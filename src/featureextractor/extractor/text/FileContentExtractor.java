/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.extractor.text;

import featureextractor.model.Sample;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author Matteo
 */
public class FileContentExtractor {
    
    private String fileName;
    private String fileContent = new String();
    
    public FileContentExtractor(String fileName) {
        this.fileName = fileName;
        
        try {
            
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String readLine;
            
            while ((readLine = reader.readLine()) != null) {
                
                fileContent = fileContent.concat(readLine);
            }
            
            reader.close();
        }
        catch(Exception exc) {
            System.out.println(exc);
        }
    }
    
    public String getFileContent() {
        return fileContent;
    }
    
    public ArrayList<Sample> extractValueFromFile() {
        
        ArrayList<Sample> valuesExtracted = new ArrayList();
        
        int positionOpenParenthesis = -1, positionCloseParenthesis = -1;
        
        positionOpenParenthesis = this.fileContent.indexOf("(");
        while (positionOpenParenthesis != -1) {
            
            positionCloseParenthesis = this.fileContent.indexOf(")", positionOpenParenthesis);
            String substring = this.fileContent.substring(positionOpenParenthesis + 1, positionCloseParenthesis);
            
            String[] elements = substring.split(",");
            
            if (valuesExtracted.isEmpty() || (Double.parseDouble(elements[0]) != valuesExtracted.get(valuesExtracted.size() - 1).time )) {
                valuesExtracted.add(new Sample(new Double(elements[0]), 
                    new Double(elements[1]), new Double(elements[2]), 
                        new Double(elements[3])));
            }
            
            positionOpenParenthesis = this.fileContent.indexOf("(", positionCloseParenthesis);
        }
        
        return valuesExtracted;
    }
}
