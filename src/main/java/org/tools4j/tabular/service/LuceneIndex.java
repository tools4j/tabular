package org.tools4j.tabular.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.datasets.Row;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LuceneIndex<T extends Row> {
    private final static Logger LOG = LoggerFactory.getLogger(LuceneIndex.class);
    private Analyzer analyzer;
    private QueryParser queryParser;
    private Directory index;
    private IndexReader reader;
    private IndexSearcher searcher;
    private List<T> table;

    public LuceneIndex(List<T> table){
        this(table, columnName -> true);
    }

    public LuceneIndex(List<T> table, Predicate<String> columnsToIndexPredicate){
        try {
            this.table = table;
            index = new RAMDirectory();
            analyzer = new StandardAnalyzer();
            queryParser = new QueryParser("data", analyzer);
            queryParser.setAllowLeadingWildcard(true);
            queryParser.setDefaultOperator(QueryParser.Operator.AND);
            loadData(columnsToIndexPredicate);
            reader = DirectoryReader.open(index);
            searcher = new IndexSearcher(reader);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void loadData(Predicate<String> columnsToIndexPredicate) throws IOException {
        IndexWriter writer;
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setMaxBufferedDocs(100);
        config.setRAMBufferSizeMB(50.0);
        writer = new IndexWriter(index, config);
        for (Document doc: new DocsFromTable(table, columnsToIndexPredicate).getDocs()) {
            writer.addDocument(doc);
        }
        writer.commit();
        writer.close();
    }

    public List<T> search(String query) {
        if(query.isEmpty()){
            return getAll();
        }
        try {
            String cleanQuery = cleanQueryString(query);
            final Query q = queryParser.parse(cleanQuery);
            TopDocs topDocs = searcher.search(q, 1024);
            LOG.info("Searched Lucene with query '" + cleanQuery + "', Results [" + topDocs.totalHits + "]");
            return convertTopDocsToItems(topDocs);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private List<T> convertTopDocsToItems(TopDocs topDocs) throws IOException {
        List<T> results = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            results.add(table.get(Integer.parseInt(doc.get("id"))));
        }
        return results;
    }

    private String cleanQueryString(String query) {
        query = query.replace("*", "");
        query = Arrays.stream(query.split("\\s+")).map(t -> "*" + t + "*").collect(Collectors.joining(" "));
        return query;
    }

    public List<T> getAll() {
        try {
            return convertTopDocsToItems(searcher.search(new MatchAllDocsQuery(), 1024));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
