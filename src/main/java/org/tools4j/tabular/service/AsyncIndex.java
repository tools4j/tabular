package org.tools4j.tabular.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.datasets.Row;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class AsyncIndex<T extends Row> {
    private final static Logger LOG = LoggerFactory.getLogger(LuceneIndex.class);
    private final LuceneIndex<T> luceneIndex;
    private final BlockingQueue<String> queryQueue;
    private final Consumer<List<T>> callback;

    public AsyncIndex(LuceneIndex<T> luceneIndex, Consumer<List<T>> callback) {
        this.luceneIndex = luceneIndex;
        this.queryQueue = new LinkedBlockingQueue<>(1);
        this.callback = callback;
    }

    public void search(String query){
        try {
            this.queryQueue.poll();
            this.queryQueue.put(query);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void init(){
        Thread t = new Thread(() -> {
            String query = "";
            while(!Thread.currentThread().isInterrupted()){
                try {
                    query = this.queryQueue.take().trim();
                    List<T> results = luceneIndex.search(query);
                    if(queryQueue.isEmpty()) {
                        this.callback.accept(results);
                        Thread.sleep(500);
                    } else {
                        LOG.info("Skipping sending back results as another query is in the pipeline...");
                    }
                } catch (Exception e) {
                    LOG.error("Error whilst searching using query [" + query + "]", e);
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
