package org.tools4j.tabular.service;

import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ben
 * Date: 24/10/17
 * Time: 6:58 AM
 */
public class CsvFile {
    private final Reader reader;
    private final char delimiter;
    private final Character quote;

    public CsvFile(final Reader reader) throws FileNotFoundException {
        this(reader, ',', null);
    }

    public CsvFile(final String fileLocation) throws FileNotFoundException {
        this(new FileReader(fileLocation), ',', null);
    }

    public CsvFile(final String fileLocation, final char delimiter) throws FileNotFoundException {
        this(new FileReader(fileLocation), delimiter, null);
    }

    public CsvFile(final String fileLocation, final char delimiter, final Character quote) throws FileNotFoundException {
        this(new FileReader(fileLocation), delimiter, quote);
    }

    public CsvFile(final Reader reader, final char delimiter, final Character quote) {
        this.reader = reader;
        this.delimiter = delimiter;
        this.quote = quote;
    }

    public List<String[]> getData(){
        final List<String[]> data = new ArrayList<>();
        try {
            final CSVReader csvReader;
            if(quote != null){
                csvReader = new CSVReader(reader, delimiter, quote);
            } else {
                csvReader = new CSVReader(reader, delimiter);
            }
            final CSVIterator iterator = new CSVIterator(csvReader);
            while(iterator.hasNext()) {
                data.add(iterator.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
