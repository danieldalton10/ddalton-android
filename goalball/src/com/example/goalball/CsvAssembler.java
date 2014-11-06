package com.example.goalball;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CsvAssembler {

    public void produceCSV(final BufferedWriter writer, String header, List<HashMap<String, String>> contents) throws IOException {
        //StringBuilder sb = new StringBuilder ();
        writer.append(header.trim());
        writer.newLine ();
        for (HashMap<String, String> row : contents) {
            appendRow(writer, header.split(","), row);
        }
    }

    private void appendRow(BufferedWriter writer, String[] header, HashMap<String, String> row) throws IOException {
        StringBuilder sb = new StringBuilder ();
        for (String field : header) {
            String value = row.get(field);
            if (value != null && !value.equals("")) {
                sb.append(value);
            }
            sb.append(",");
        }

        String csvRow = "";
        if (sb.length() > 0) {
            csvRow = sb.substring(0, sb.length() - 1);
        }
        if (!csvRow.replace(",", "").trim().equals("")) {
            writer.append(csvRow);
            writer.newLine ();
        }
    }
}
