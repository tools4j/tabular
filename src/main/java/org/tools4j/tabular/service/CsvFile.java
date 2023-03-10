package org.tools4j.tabular.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ben
 * Date: 24/10/17
 * Time: 6:58 AM
 */
public class CsvFile {
    private final static Logger LOG = LoggerFactory.getLogger(CsvFile.class);
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
            CSVParserBuilder csvParserBuilder = new CSVParserBuilder().withSeparator(delimiter);
            if(quote != null){
                csvParserBuilder.withQuoteChar(quote);
            }
            final CSVParser csvParser = csvParserBuilder.build();
            try(CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(csvParser).build()){
                while(csvReader.peek() != null) {
                    rows.add(csvReader.readNext());
                }
            } catch (Exception e){
                throw new IllegalStateException(e);        
            }
        } catch (Exception e) {
            LOG.error("Exception whilst parsing CSV file", e);
        }
        return rows;
    }
}
