package org.tools4j.tabular.service;

import java.util.Collections;
import java.util.List;

/**
 * User: ben
 * Date: 3/11/17
 * Time: 6:39 AM
 */
public class MutablePartIndex<R extends Row> implements Pretty {
    private PartIndex<R> partIndex = null;

    public MutablePartIndex() {
    }

    public Results<R> search(final String query){
        if(partIndex == null) {
            return new Results<R>();
        } else {
            return partIndex.search(query);
        }
    }

    public List<Result<R>> returnAll(){
        if(partIndex == null){
            return Collections.emptyList();
        } else {
            return partIndex.returnAll().asList();
        }
    }

    public void update(TableWithColumnHeadings<R> newContent){
        partIndex = new PartIndex<>(newContent);
    }

    @Override
    public String toPrettyString(final String indent) {
        if(partIndex == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("PartIndex{\n");
            sb.append("    <empty>\n");
            sb.append("}");
            return sb.toString();
        } else {
            return partIndex.toPrettyString(indent);
        }
    }
}
