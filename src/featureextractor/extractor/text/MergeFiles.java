/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.extractor.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author po
 */
public class MergeFiles {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String fileName = args[0];
        
        String fileSeparator = System.getProperty("file.separator");
        String userDir = System.getProperty("user.dir");
        
        String sourceFileXString = userDir.concat(fileSeparator).concat("data")
                .concat(fileSeparator).concat(fileName).concat("asseX.txt");
        String sourceFileYString = userDir.concat(fileSeparator).concat("data")
                .concat(fileSeparator).concat(fileName).concat("asseY.txt");
        String sourceFileZString = userDir.concat(fileSeparator).concat("data")
                .concat(fileSeparator).concat(fileName).concat("asseZ.txt");
        
        ArrayList<String> filesContent = new ArrayList<String>(3);
        filesContent.add(0, ""); filesContent.add(1, ""); filesContent.add(2, "");
        
        try {
            
            BufferedReader readerX = new BufferedReader(new FileReader(sourceFileXString));
            String readLine;
            
            while ((readLine = readerX.readLine()) != null) {
                
                filesContent.add(0,filesContent.get(0).concat(readLine));
            }
            readerX.close();
            
            BufferedReader readerY = new BufferedReader(new FileReader(sourceFileYString));
            
            while ((readLine = readerY.readLine()) != null) {
                
                filesContent.add(1, filesContent.get(1).concat(readLine));
            }
            readerY.close();
            
            BufferedReader readerZ = new BufferedReader(new FileReader(sourceFileZString));
            
            while ((readLine = readerZ.readLine()) != null) {
                
                filesContent.add(2, filesContent.get(2).concat(readLine));
            }
            readerZ.close();
        }
        catch(Exception exc) {
            System.out.println(exc);
        }
        
        String outputFileName = userDir.concat(fileSeparator).concat("data")
                .concat(fileSeparator).concat(fileName).concat(".txt");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outputFileName));
            
            int indexStartPharenthesisX = filesContent.get(0).indexOf("(");
            int indexStartPharenthesisY = filesContent.get(1).indexOf("(");
            int indexStartPharenthesisZ = filesContent.get(2).indexOf("(");
            
            while (indexStartPharenthesisX != -1) {
                
                int indexEndPharenthesisX = filesContent.get(0).indexOf(")", 
                        indexStartPharenthesisX);
                int indexEndPharenthesisY = filesContent.get(1).indexOf(")",
                        indexStartPharenthesisY);
                int indexEndPharenthesisZ = filesContent.get(2).indexOf(")", 
                        indexStartPharenthesisZ);
                
                String[] elements = filesContent.get(0)
                        .substring(indexStartPharenthesisX + 1, 
                        indexEndPharenthesisX).split(",");
                
                Double time = new Double(elements[0]);
                
                Double xValue = new Double(elements[1]);
                
                elements = filesContent.get(1)
                        .substring(indexStartPharenthesisY+1, 
                        indexEndPharenthesisY).split(",");
                
                Double yValue = new Double(elements[1]);
                
                elements = filesContent.get(2)
                        .substring(indexStartPharenthesisZ+1, 
                        indexEndPharenthesisZ).split(",");
                
                Double zValue = new Double(elements[1]);
                
                out.write("(" + time + "," + xValue + "," + yValue + "," + zValue + ")");
                
                indexStartPharenthesisX = filesContent.get(0).indexOf("(", 
                        indexEndPharenthesisX);
                indexStartPharenthesisY = filesContent.get(1).indexOf("(", 
                        indexEndPharenthesisY);
                indexStartPharenthesisZ = filesContent.get(2).indexOf("(", 
                        indexEndPharenthesisZ);
            }
            
            out.close();
        }
        catch(IOException exc) {
            System.out.println("Unable to create output file");
        }
    }
}
