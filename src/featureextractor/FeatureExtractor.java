/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author Matteo
 */
public class FeatureExtractor extends JFrame {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String fileName = args[0];
        
        String fileSeparator = System.getProperty("file.separator");
        String userDir = System.getProperty("user.dir");
        
        String sourceFileString = userDir.concat(fileSeparator).concat("data")
                .concat(fileSeparator).concat(fileName).concat(".txt");
        
        FileContentExtractor extractor = new FileContentExtractor(sourceFileString);
        
        ArrayList<TimeDataValues> valuesExtracted = extractor.extractValueFromFile();
        
        DataAnalyzer analyzer = new DataAnalyzer(valuesExtracted);
        
        analyzer.searchForMaxOrMin(); analyzer.normalize(); analyzer.evaluateDeltaTimes();
        analyzer.calculateFeatures();
        
        System.out.println("Risultati: ");
        System.out.println(analyzer);
        
        DeltaTimesGraph graph = new DeltaTimesGraph(analyzer.startingData.get(0));
        graph.setVisible(true);
        
        AxisValuesGraph timeGraph = new AxisValuesGraph(analyzer.startingData);
        timeGraph.setVisible(true);
    }
}
