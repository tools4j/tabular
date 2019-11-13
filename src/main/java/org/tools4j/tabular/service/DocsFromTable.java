package org.tools4j.tabular.service;

import org.apache.lucene.document.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DocsFromTable {
    private final List<? extends Row> table;
    private final Predicate<String> columnsToIndexPredicate;

    public DocsFromTable(List<? extends Row> table, Predicate<String> columnsToIndexPredicate){
        this.table = table;
        this.columnsToIndexPredicate = columnsToIndexPredicate;
    }

    public List<Document> getDocs() {
        List<Document> docs = new ArrayList<>();
        int rowId=0;
        for(Row row: table){
            Document doc = new Document();
            doc.add(new StoredField("id", rowId++));
            doc.add(new TextField("data",
                    row.entrySet()
                            .stream()
                            .filter(e -> columnsToIndexPredicate.test(e.getKey()))
                            .map(e -> e.getValue())
                            .collect(Collectors.joining(" ")), Field.Store.NO));
            docs.add(doc);
        }
        return docs;
    }
}
