package com.example.goalball;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

public class CsvAssemblerTest extends TestCase {
    private CsvAssembler subject;

    public CsvAssemblerTest() {
        subject = new CsvAssembler();
    }

    //@Test
    public void testProduceCSV() {
        String header = "Player Number,Throws,Goal";
        HashMap<String, String> row = new HashMap<String, String>();
        row.put("Player Number", "3");
        row.put("Throws", "25");
        List<HashMap<String, String>> contents = new ArrayList<HashMap<String, String>>();
        contents.add(row);
        Assert.assertEquals("Player Number, Throws,Goals\n3,25,\n", subject.produceCSV(header, contents));
    }
}
