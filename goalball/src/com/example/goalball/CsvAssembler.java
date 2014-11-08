package com.example.goalball;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CsvAssembler {

    public void produceCSV(final BufferedWriter writer, String header, List<Map<String, String>> contents)
            throws IOException {
        writer.append(header.trim());
        writer.newLine();
        for (Map<String, String> row : contents) {
            appendRow(writer, header.split(","), row);
        }
    }

    private void appendRow(BufferedWriter writer, String[] header, Map<String, String> row)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String field : header) {
            String value = row.get(field); // try to get field with raw name
            if (value == null && field.length() > 0) { // try stripping the
                                                       // quotes then getting it
                value = row.get(field.substring(1, field.length() - 1));
            }
            if (value != null && !value.equals("")) {
                sb.append(value);
            }
            sb.append(",");
        }

        String csvRow = "";
        if (sb.length() > 0) { // remove extra comma at the end
            csvRow = sb.substring(0, sb.length() - 1);
        }

        // Don't add an empty row
        if (!csvRow.replace(",", "").trim().equals("")) {
            writer.append(csvRow);
            writer.newLine();
        }
    }
}
