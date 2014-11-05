package com.example.goalball;

import java.util.HashMap;
import java.util.List;

public class CsvAssembler {

    public String produceCSV(String header, List<HashMap<String, String>> contents) {
        StringBuilder sb = new StringBuilder ();
        sb.append(header.trim() + "\n");
        for (HashMap<String, String> row : contents) {
            appendRow(sb, header.split(","), row);
        }
        return sb.toString ();
    }

    private void appendRow(StringBuilder writer, String[] header, HashMap<String, String> row) {
        StringBuilder sb = new StringBuilder();
        for (String field : header) {
            String value = row.get(field);
            if (value != null && !value.equals("")) {
                sb.append(value);
            }
            sb.append(",");
        }

        String csvRow = "";
        if (sb.length() > 0) {
            csvRow = sb.substring(0, sb.length() - 1) + "\n";
        }
        if (!csvRow.replace(",", "").trim().equals("")) {
            writer.append(csvRow);
        }
    }
}
