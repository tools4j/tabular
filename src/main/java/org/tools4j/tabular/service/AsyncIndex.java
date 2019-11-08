package org.tools4j.tabular.service;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class AsyncIndex<T extends Row> {
    private final static Logger LOG = Logger.getLogger(LuceneIndex.class);
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
            try {
                while(!Thread.currentThread().isInterrupted()){
                    String query = this.queryQueue.take();
                    List<T> results = luceneIndex.search(query);
                    if(queryQueue.isEmpty()) {
                        this.callback.accept(results);
                        Thread.sleep(500);
                    } else {
                        LOG.info("Skipping sending back results as another query is in the pipeline...");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
