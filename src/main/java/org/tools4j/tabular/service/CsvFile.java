package org.tools4j.tabular.service;

import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ben
 * Date: 24/10/17
 * Time: 6:58 AM
 */
public class CsvFile {
    public static final char DEFAULT_DELIMITER = ',';
    public static final Character DEFAULT_QUOTE = null;

    private final Reader reader;
    private final char delimiter;
    private final Character quote;

    public static CsvFile fromReader(final Reader reader){
        return new CsvFile(reader, DEFAULT_DELIMITER, DEFAULT_QUOTE);
    }

    public static CsvFile fromFileLocation(final String fileLocation){
        try {
            return new CsvFile(new FileReader(fileLocation), DEFAULT_DELIMITER, DEFAULT_QUOTE);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static CsvFile fromFile(final File file){
        try {
            return new CsvFile(new FileReader(file), DEFAULT_DELIMITER, DEFAULT_QUOTE);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private CsvFile(final Reader reader, final char delimiter, final Character quote) {
        this.reader = reader;
        this.delimiter = delimiter;
        this.quote = quote;
    }

    public List<String[]> getRows(){
        final List<String[]> rows = new ArrayList<>();
        try {
            final CSVReader csvReader;
            if(quote != null){
                csvReader = new CSVReader(reader, delimiter, quote);
            } else {
                csvReader = new CSVReader(reader, delimiter);
            }
            final CSVIterator iterator = new CSVIterator(csvReader);
            while(iterator.hasNext()) {
                rows.add(iterator.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }
}
