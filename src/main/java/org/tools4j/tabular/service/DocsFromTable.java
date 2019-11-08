package org.tools4j.tabular.service;

import org.apache.lucene.document.*;

import java.util.ArrayList;
import java.util.List;

public class DocsFromTable {
    private final List<? extends Row> table;

    public DocsFromTable(List<? extends Row> table){
        this.table = table;
    }

    public List<Document> getDocs() {
        List<Document> docs = new ArrayList<>();
        int rowId=0;
        for(Row row: table){
            Document doc = new Document();
            doc.add(new StoredField("id", rowId++));
            doc.add(new TextField("data", String.join(" ", row.values()), Field.Store.NO));
            docs.add(doc);
        }
        return docs;
    }
}
