package org.tools4j.tabular.merger

import com.opencsv.CSVIterator
import com.opencsv.CSVReader
import com.opencsv.CSVWriter

/**
 * User: ben
 * Date: 2/02/2018
 * Time: 8:34 AM
 */
class CsvMerger {
    private final static String KEY_COLUMN = "Name"
    private final static String[] OUTPUT_COLUMNS = ["Host", "OS", "FullHost", "Env", "LogicalName", "App", "HomeDir", "LogsDir", "Country", "Name"]

    public static void main(String[] args) {
        new CsvMerger().go();
    }

    void go() {
        final List<Map<String,String>> originalFile =  new CsvFile("src/test/groovy/org/tools4j/tabular/merger/original.csv").getDataAsListOfMaps()
        List<Map<String,String>> newFile =  new CsvFile("src/test/groovy/org/tools4j/tabular/merger/new.csv").getDataAsListOfMaps()
        newFile = expandRowsWithDelimitedCell(newFile, "Names", "Name", '\\|')
        final List<Map<String,String>> merged = merge(originalFile, newFile);
        final List<Map<String,String>> withDesiredRows = filterDesiredRows(merged, OUTPUT_COLUMNS)
        prettyPrint(withDesiredRows)
        save(withDesiredRows, new File("src/test/groovy/org/tools4j/tabular/merger/merged.csv"))
    }

    List<Map<String, String>> filterDesiredRows(final List<Map<String, String>> rows, final String[] columnNames) {
        List<Map<String, String>> results = new ArrayList<>()
        rows.each {final Map<String, String> row ->
            final Map<String, String> newRow = new LinkedHashMap<>();
            results.add(newRow)
            columnNames.each {final String columnName ->
                newRow.put(columnName, row.get(columnName))
            }
        }
        return results;
    }

    List<Map<String, String>> merge(final List<Map<String, String>> one, final List<Map<String, String>> two) {
        //Copy the first map into the results
        Map<String, Map<String, String>> result = new LinkedHashMap<>()
        one.each {
            final String keyColumnValue = it[KEY_COLUMN]
            if(keyColumnValue == null){
                throw new IllegalStateException("Cannot find value for key $KEY_COLUMN in row: $it")
            }
            result.put(keyColumnValue, new LinkedHashMap<String, String>(it))
        }

        final List<String> errors = new ArrayList<>()

        two.each { Map<String, String> newRow ->
            final String keyColumnValue = newRow[KEY_COLUMN]
            if(keyColumnValue == null){
                throw new IllegalStateException("Cannot find value for key $KEY_COLUMN in row: $newRow")
            }
            final Map<String, String> existingRow = result[keyColumnValue];
            if(existingRow != null){
                newRow.keySet().each { String key ->
                    final String existingCellValue = existingRow[key]
                    final String newCellValue = newRow[key]
                    if (existingCellValue != null && !existingCellValue.equals(newCellValue)) {
                        errors.add("$keyColumnValue: Existing cell value $existingCellValue does not equal new cell value $newCellValue in row $newRow")
                    } else {
                        existingRow.put(key, newCellValue)
                    }
                }
            } else {
                result.put(keyColumnValue, new LinkedHashMap<String, String>(newRow))
            }
        }
        if(!errors.isEmpty()){
            println "==========================================================="
            println "-----------------------------------------------------------"
            println "ERRORS"
            println "-----------------------------------------------------------"
            errors.each {
                println it;
            }
            throw new IllegalArgumentException("Found errors!")
        } else {
            return new ArrayList<Map<String,String>>(result.values())
        }
    }

    void save(final List<Map<String, String>> rows, final File file) {
        if(file.exists()){
            throw new IllegalArgumentException("File already exists at: " + file)
        }
        printAsCsv(rows, new PrintStream(file))
    }

    void prettyPrint(final List<Map<String, String>> rows) {
        printAsCsv(rows, System.out)
    }

    protected void printAsCsv(List<Map<String, String>> rows, PrintStream out) {
        PrintWriter writer = new PrintWriter(out);
        final CSVWriter csvWriter = new CSVWriter(writer, (char) ',')
        rows.each { final Map<String, String> map ->
            try {
                final List<String> cells = map.values().asList()
                csvWriter.writeNext(cells.toArray(new String[cells.size()]));
            } catch (Throwable e) {
                println e
            }
        }
        csvWriter.flush()
    }

    List<Map<String, String>> expandRowsWithDelimitedCell(final List<Map<String, String>> file, final String oldCsvColumnName, final String newColumnName, final String delimiter) {
        List<Map<String, String>> newRows = new ArrayList<>();
        file.each { row ->
            final String csvColStr = row[oldCsvColumnName];
            if(csvColStr == null){
                newRows.add(row);
            } else {
                final String[] csvColValues = csvColStr.split(delimiter).each {it.trim()}
                row.remove(oldCsvColumnName);
                csvColValues.each {
                    final Map<String, String> rowCopy = new LinkedHashMap<>(row);
                    rowCopy.put(newColumnName, it);
                    newRows.add(rowCopy)
                }
            }
        }
        return newRows;
    }

    public static class CsvFile {
        private final Reader reader;
        private final char delimiter;
        private final Character quote;

        public CsvFile(final String fileLocation) throws FileNotFoundException {
            this(new FileReader(fileLocation), (char) ',');
        }

        public CsvFile(final Reader reader, final char delimiter) {
            this.reader = reader;
            this.delimiter = delimiter;
            this.quote = quote;
        }

        public List<String[]> getDataAsListOfArrays(){
            final List<String[]> data = new ArrayList<>();
            try {
                final CSVReader csvReader;
                csvReader = new CSVReader(reader, delimiter);
                final CSVIterator iterator = new CSVIterator(csvReader);
                while(iterator.hasNext()) {
                    data.add(iterator.next());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        public List<Map<String,String>> getDataAsListOfMaps(){
            final List<String[]> listOfArrays = getDataAsListOfArrays();
            final List<Map<String, String>> listOfMaps = new ArrayList<>();
            final List<String> columnNames = Arrays.asList(listOfArrays.first());
            for(int i=1; i<listOfArrays.size(); i++){
                final Map<String,String> row = new LinkedHashMap<>();
                for(int c=0; c<columnNames.size(); c++){
                    row.put(columnNames.get(c), listOfArrays.get(i)[c]);
                }
                listOfMaps.add(row);
            }
            return listOfMaps;
        }
    }
}
