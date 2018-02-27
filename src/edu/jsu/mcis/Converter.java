package edu.jsu.mcis;

import java.io.*;
import java.util.*;
import com.opencsv.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Converter {
    
    /*
    
        Consider the following CSV data:
        
        "ID","Total","Assignment 1","Assignment 2","Exam 1"
        "111278","611","146","128","337"
        "111352","867","227","228","412"
        "111373","461","96","90","275"
        "111305","835","220","217","398"
        "111399","898","226","229","443"
        "111160","454","77","125","252"
        "111276","579","130","111","338"
        "111241","973","236","237","500"
        
        The corresponding JSON data would be similar to the following (tabs and other whitespace
        have been added for clarity).  Note the curly braces, square brackets, and double-quotes!
        
        {
            "colHeaders":["ID","Total","Assignment 1","Assignment 2","Exam 1"],
            "rowHeaders":["111278","111352","111373","111305","111399","111160","111276","111241"],
            "data":[[611,146,128,337],
                    [867,227,228,412],
                    [461,96,90,275],
                    [835,220,217,398],
                    [898,226,229,443],
                    [454,77,125,252],
                    [579,130,111,338],
                    [973,236,237,500]
            ]
        }
    
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String results = "";
        
        try {
            
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> full = reader.readAll();
            Iterator<String[]> iterator = full.iterator();
            
            JSONObject jsonObject = new JSONObject();
            
            //colHeaders
            String[] cols;        
            
            JSONArray colHeaders = new JSONArray();
            cols = iterator.next();
            
            for (String field: cols) 
            {
                colHeaders.add(field);
            } 
            
            jsonObject.put("colHeaders", colHeaders);
            
            //rowHeaders & data
            boolean loop = true;
            String[] rows;
            
            rows = iterator.next();
            
            JSONArray rowHeaders = new JSONArray();
            JSONArray data = new JSONArray();
            JSONArray dataIt = new JSONArray();
            
            while(loop)
            {
                if (iterator.hasNext() == false)
                {
                    rowHeaders.add(rows[0]);
                    
                    for (int i = 1; i < rows.length; i++)
                    {
                        dataIt.add(rows[i]);
                    }
                    
                    loop = false;
                }
                else
                {
                    rowHeaders.add(rows[0]);
                    
                    for (int i = 1; i < rows.length; i++)
                    {
                        dataIt.add(rows[i]);
                    }
                    
                    rows = iterator.next();
                }
            }
            data.add(dataIt);
            
            jsonObject.put("rowHeaders", rowHeaders);
            jsonObject.put("data", data);
            
            results = JSONValue.toJSONString(jsonObject);
        }
        
        catch(IOException e) { return e.toString(); }
        
        return results.trim();
        
    }
    
    public static String jsonToCsv(String jsonString) {
        
        String results = "";
        
        try {
            
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject)parser.parse(jsonString);
            
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\n');
            
            //columns
         
            JSONArray cols = (JSONArray)jsonObject.get("colHeaders");
            String[] colHeaders = new String[5];
            String columns;
            
            for(int i = 0; i < cols.size(); i++)
            {
                columns = (String)cols.get(i);
                colHeaders[i] = columns;
            }
            csvWriter.writeNext(colHeaders);
            
            //rows & data
            boolean loop = true;
            int counter = 0;
            
            JSONArray rows = (JSONArray)jsonObject.get("rowHeaders");
            JSONArray data = (JSONArray)jsonObject.get("data");
            JSONArray dataIt;
            String[] rowHeaders = new String[5];
            String r;
            String d;
            
            for(int j = 0; j < rows.size(); j++)
            {
                r = (String)rows.get(j);
                rowHeaders[0] = r;
                
                while(loop)
                {
                    if (counter == 8)
                        loop = false;
                    else
                    {
                        dataIt = (JSONArray)data.get(counter);
                    
                        for (int k = 0; k < dataIt.size(); k++)
                        {
                            d = (String)dataIt.get(k).toString();
                            rowHeaders[k+1] = d;
                        }
                        csvWriter.writeNext(rowHeaders);
                        counter++;
                    }
                }
            }
            results = writer.toString();
        }
        
        catch(ParseException e) { return e.toString(); }
        
        return results.trim();
        
    }
	
}