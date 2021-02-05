/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for parsing CSV files that can have embedded complexity allowed by tools like Microsoft Excel.
 * For example:
 * <ul>
 *     <li>embedded commas within a cell (escaped by a double quote)</li>
 *     <li>double quotes within a cell (escaped by a secondary double quote)</li>
 *     <li>newlines within a cell (escaped by a double quote)</li>
 * </ul>
 */
public class CSVParser {

    private int expectedColumnCount;
    private String sReadTokenCSV;
    private String sLineCSV;
    private BufferedReader readerCSV;

    private CSVParser() {
        expectedColumnCount = -1;
    }

    /**
     * Construct a new parser using the provided parameters.
     * @param sInputFilePath the path to the input file to parse
     * @param sEncoding the encoding of the input file
     * @throws FileNotFoundException if the file is not found
     * @throws IOException if the file cannot be accessed or read
     */
    public CSVParser(String sInputFilePath, String sEncoding) throws FileNotFoundException, IOException {
        this();
        readerCSV = new BufferedReader(new InputStreamReader(new FileInputStream(sInputFilePath), sEncoding));
    }

    /**
     * Construct a new parser using the provided parameters.
     * @param inputFile the input file to parse
     * @param sEncoding the encoding of the input file
     * @throws IOException if the file cannot be accessed or read
     */
    public CSVParser(File inputFile, String sEncoding) throws IOException {
        this();
        readerCSV = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), sEncoding));
    }

    /**
     * Construct a new parser using the provided parameters.
     * @param sInputFilePath the path to the input file to parse
     * @param sEncoding the encoding of the input file
     * @param expectedColumnCount the expected number of columns per row of data
     * @throws FileNotFoundException if the file is not found
     * @throws IOException if the file cannot be accessed or read
     */
    public CSVParser(String sInputFilePath, String sEncoding, int expectedColumnCount) throws FileNotFoundException, IOException {
        this(sInputFilePath, sEncoding);
        this.expectedColumnCount = expectedColumnCount;
    }

    /**
     * Returns the next non-empty, non-blank line, or null if the end of the stream has been reached.
     * @return String
     * @throws IOException on any error reading
     */
    private String nextLine() throws IOException {
        do {
            sLineCSV = readerCSV.readLine();
            if (sLineCSV == null)
                return null;
        } while (sLineCSV.trim().equals("") || sLineCSV.startsWith("#") || sLineCSV.replace(",", "").replace("\"\"", "").equals(""));
        return sLineCSV;
    }

    /**
     * Locates the index of the next comma (as this could be more than a line away given in-cell newlines).
     * @param p current position from which to scan
     * @return int
     * @throws IOException on any error reading
     */
    private int findNextComma(int p) throws IOException {

        int i;
        sReadTokenCSV = "";
        char c = sLineCSV.charAt(p);

        // empty field
        if (c == ',') {
            sReadTokenCSV = "";
            return p;
        }

        // not escape char
        if (c != '"') {
            i = sLineCSV.indexOf(',', p);
            if (i == -1)
                i = sLineCSV.length();
            sReadTokenCSV = sLineCSV.substring(p, i);
            return i;
        }

        // start with "
        p++;

        StringBuilder sb = new StringBuilder(200);
        while (true) {
            c = readNextChar(p);
            p++;

            // not a "
            if (c != '"') {
                sb.append(c);
                continue;
            }

            // ", last char -> ok
            if (p == sLineCSV.length()) {
                sReadTokenCSV = sb.toString();
                return p;
            }

            c = readNextChar(p);
            p++;

            // "" -> just print one
            if (c == '"') {
                sb.append('"');
                continue;
            }

            // ", -> return
            if (c == ',') {
                sReadTokenCSV = sb.toString();
                return p - 1;
            }

        }
    }

    /**
     * Read the next character.
     * @param p the position from which to start scanning
     * @return char
     * @throws IOException on any error reading
     */
    private char readNextChar(int p) throws IOException {

        if (p == sLineCSV.length()) {
            String newLine = readerCSV.readLine();
            if (newLine == null)
                throw new IOException("CSV parsing: went overboard");
            sLineCSV += "\n" + newLine;
        }
        return sLineCSV.charAt(p);
    }

    /**
     * Retrieve the next line as a list of values (one per column).
     * @return {@code List<String>} of values
     * @throws IOException on any error reading
     */
    public List<String> splitLine() throws IOException {

        List<String> al = new ArrayList<>();
        sLineCSV = nextLine();

        if (sLineCSV == null)
            return null;

        int pos = 0;

        while (pos < sLineCSV.length()) {
            pos = findNextComma(pos);
            al.add(sReadTokenCSV);
            pos++;
        }

        // If the CSV string ends with a ',', we need an empty token at the end...
        if (sLineCSV.length() > 0 && sLineCSV.charAt(sLineCSV.length() - 1) == ',') {
            al.add("");
        }

        // If the length of the array isn't full, pad with empty cells...
        while (al.size() < expectedColumnCount) {
            al.add("");
        }

        return al;

    }

    /**
     * Attempts to translate the provided string into a boolean, where any of the following (case-insensitive) are
     * considered to be "true" and anything else is considered to be "false": {@literal yes}, {@literal true},
     * {@literal x}, and {@literal y}.
     * @param s the string to translate into a boolean value
     * @return boolean
     */
    public static boolean checkBoolean(String s) {
        if (s == null || s.equals("")) {
            return false;
        } else {
            String sInsensitive = s.toUpperCase();
            return (sInsensitive.equals("YES") || sInsensitive.equals("TRUE") || sInsensitive.equals("X") || sInsensitive.equals("Y"));
        }
    }

    /**
     * Attempts to translate the provided string into an integer, or returns the specified default value if the string
     * is empty.
     * @param s the string to translate into an integer value
     * @param defaultValue the default value to use if the string is empty
     * @return Integer
     */
    public static Integer checkInteger(String s, Integer defaultValue) {
        if (s == null || s.equals("")) {
            return defaultValue;
        } else {
            return Integer.parseInt(s);
        }
    }

    /**
     * Attempts to translate the provided string into a list, based on splitting it by the provided delimiter.  If no
     * values are found, it will return an empty list.
     * @param s the string to translate into a list
     * @param delimiter the delimiter on which to split the string
     * @return {@code List<String>}
     */
    public static List<String> checkList(String s, String delimiter) {
        String[] array = s.split(",");
        List<String> target = new ArrayList<>();
        if ( !(array.length == 1 && array[0].equals("")) ) {
            target = Arrays.asList(array);
        }
        return target;
    }

}
