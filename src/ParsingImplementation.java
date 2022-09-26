import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * ParsingImplementation.java - parsing csv files columns into arrays.
 */
public class ParsingImplementation {
    /**
     * Path to file.
     */
    private static final String FILE_PATH = "test1.csv";
    /**
     * Maximal column index for good print.
     */
    private static final int COLUMN_INDEX = 0;
    /**
     * Quote counter needed for count number of quotes in each element. Default value is 0.
     */
    static int quoteCounter = 0;
    /**
     * Item length needed for count item length. Default value is 0.
     */
    static int itemLength = 0;
    /**
     * Boolean variable that need to define is letters or separator exists behind.
     */
    static boolean isLettersOrSeparatorBehind = false;

    /**
     * Printing all columns until reach COLUMN_INDEX index from FILE_PATH.
     */
    public static void main(String[] args) {
        for (int columnIndex = 0; columnIndex <= COLUMN_INDEX; columnIndex++) {
            System.out.println(extractColumn(FILE_PATH, columnIndex));
        }
    }

    /**
     * Extracting column with columnIndex index from filename file.
     *
     * @param filename    String with path to file.
     * @param columnIndex Index of column that needed to extract to array.
     * @return Array of all values of column.
     */
    private static ArrayList<String> extractColumn(String filename, int columnIndex) {
        // creating two arrays for column items and to get input lines.
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> allLines = readFile(filename);
        // if allLines array equals null, then result is null.
        if (allLines == null) {
            return null;
        }
        // going through all lines.
        for (String line : allLines) {
            // getting column item at line.
            String columnItem = getColumnItemAtLine(line, columnIndex);
            // adding column item to array.
            result.add(columnItem);
        }
        return result;
    }

    /**
     * Reads all lines in the file and puts its in array.
     *
     * @param filename String name of file.
     * @return Array of all lines in file.
     */
    private static ArrayList<String> readFile(String filename) {
        // creating arraylist for all lines in file.
        ArrayList<String> allLines = new ArrayList<>();
        try {
            // reading all lines and putting its in arraylist.
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                allLines.add(scanner.nextLine());
            }
        } catch (FileNotFoundException ex) { // if file not found, returning null.
            System.out.println("File not found.");
            return null;
        } catch (Exception ex) {
            System.out.println("Problem.");
        }
        return allLines;
    }

    /**
     * Getting element of row with columnIndex index.
     *
     * @param line        String of one row of table.
     * @param columnIndex Index of column that needed to extract.
     * @return Table row element string.
     */
    private static String getColumnItemAtLine(String line, int columnIndex) {
        if (!line.contains("\"")) { // if line does not contain quotes, then returning item from
            return line.split(",")[columnIndex];                        // line that has column index.
        } else { // if line contain quotes, then, at first, finding separator indexes.
            int[] separatorIndexes = findSeparatorIndexes(line);
            if (separatorIndexes.length == 0) { // if separators not find, then returning all line without edge quotes.
                return line.substring(0, line.length() - 1);
            } else { // if separators exists, then returning item from line with column index.
                return columnItemIfExistsSeparators(line, columnIndex, separatorIndexes);
            }
        }
    }

    /**
     * Finding all separator indexes excepting separators in quotes.
     *
     * @param line String in which needed to find separators.
     * @return Array with separator indexes.
     */
    private static int[] findSeparatorIndexes(String line) {
        // setting default values to class variables.
        quoteCounter = 0;
        itemLength = 0;
        isLettersOrSeparatorBehind = false;
        // creating char array of line and separator indexes array.
        char[] lineCharArray = line.toCharArray();
        ArrayList<Integer> separatorIndexes = new ArrayList<>();
        // going through all line char-by-char.
        for (int i = 0; i < lineCharArray.length; i++) {
            // makes some actions depending on char
            charHandler(lineCharArray[i], separatorIndexes, i);
            itemLength++;
        }
        return convertToArray(separatorIndexes);
    }

    /**
     * Handles one char index.
     *
     * @param element Char that need to be handled
     * @param separatorIndexes ArrayList of separator indexes.
     * @param charIndex Index of char.
     */
    private static void charHandler(char element, ArrayList<Integer> separatorIndexes, int charIndex) {
        if (element == '\"') { // if char is quote, then change number of quotes.
            quoteCounter = quoteCounterHandler(quoteCounter, isLettersOrSeparatorBehind);
        } else if (element == ',') { // if char is comma, then checking if number of quotes is less than 0 or element
            // consist of quotes, if true, then writing up char index and setting element variables to default.
            if (quoteCounter <= 0 || itemLength == quoteCounter) {
                separatorIndexes.add(charIndex);
                isLettersOrSeparatorBehind = false;
                quoteCounter = 0;
                itemLength = -1; // -1 because in end of every iteration item length is increments by 1.
            } else { // if char is comma, but another checks is false, then set that letters or separator behind to true.
                isLettersOrSeparatorBehind = true;
            }
        } else { // if char is not comma or quote, then set that letters or separator behind to true.
            isLettersOrSeparatorBehind = true;
        }
    }

    /**
     * Counts the number of quotes.
     *
     * @param quoteCounter               Counter of quotes, number of quotes if its were before in line.
     * @param isLettersOrSeparatorBehind True if letters or separators were behind,
     *                                   false if were not.
     * @return quoteCounter +1 or -1 depending on the isLettersOrSeparatorBehind value.
     */
    private static int quoteCounterHandler(int quoteCounter, boolean isLettersOrSeparatorBehind) {
        if (isLettersOrSeparatorBehind) {
            return quoteCounter - 1;
        } else {
            return quoteCounter + 1;
        }
    }

    /**
     * Converting ArrayList of integers into integer array.
     *
     * @param separatorIndexes ArrayList of integers with separators indexes.
     * @return Array of integers with separators indexes.
     */
    private static int[] convertToArray(ArrayList<Integer> separatorIndexes) {
        int[] separatorIndexesArray = new int[separatorIndexes.size()];
        for (int i = 0; i < separatorIndexes.size(); i++) {
            separatorIndexesArray[i] = separatorIndexes.get(i);
        }
        return separatorIndexesArray;
    }

    /**
     * Finding column item in row in case that at least one separator exists.
     *
     * @param line             String of one row of table.
     * @param columnIndex      Index of column that needed to extract.
     * @param separatorIndexes Indexes of separators(in our case comma).
     * @return Table row element string.
     */
    private static String columnItemIfExistsSeparators(String line, int columnIndex, int[] separatorIndexes) {
        String columnItem;
        if (columnIndex == 0) { // if column index is 0, then getting first item from line.
            columnItem = line.substring(0, separatorIndexes[0]);
        } else { // if column index is not 0, then getting item with columnIndex index.
            columnItem = line.substring(separatorIndexes[columnIndex - 1] + 1,
                    columnIndex >= separatorIndexes.length ?
                            line.length() : separatorIndexes[columnIndex]);
        }
        if (columnItem.contains("\"")) { // if column item contains quotes,
            // then removing edge quotes and all double quotes replacing on single quotes
            columnItem = columnItem.substring(1, columnItem.length() - 1);
            columnItem = columnItem.replaceAll("\"\"", "\"");
        }
        return columnItem;
    }
}
