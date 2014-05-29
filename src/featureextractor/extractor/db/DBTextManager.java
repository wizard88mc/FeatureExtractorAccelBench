package featureextractor.extractor.db;

import featureextractor.App;
import featureextractor.model.DataTime;
import featureextractor.model.SingleCoordinateSet;
import featureextractor.model.SlidingWindow;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matteo Ciman
 */
public class DBTextManager {
    
    private static String BASE_FOLDER;
    private static String BASE_FOLDER_INPUT = "textualDB/";
    private static String BASE_FOLDER_TEST_DB = "testDB/";
    private static String DB_FILE = "databasesInserted.txt";
    private static String DB_DATA;
    private static String DB_DATA_INPUT = "samplesSlidingWindows.dsw";
    private static String DB_DATA_TEST = "testSamplesSlidingWindows.dsw";
    
    private PrintWriter outputDatabase;
    private PrintWriter outputSamples;
    private static int lastTrunkId = 0;
    
    public DBTextManager(boolean testDB) throws IOException {
        
        if (testDB) {
            DB_DATA = DB_DATA_TEST;
            BASE_FOLDER = BASE_FOLDER_TEST_DB;
        }
        else {
            DB_DATA = DB_DATA_INPUT;
            BASE_FOLDER = BASE_FOLDER_INPUT;
        }
        
        File file = new File(BASE_FOLDER + DB_DATA);
        
        if (!file.exists()) {
            printHeaderFile(true);
        }

        outputDatabase = new PrintWriter(new BufferedWriter(new FileWriter(BASE_FOLDER + DB_FILE, true)));
        outputSamples = new PrintWriter(new BufferedWriter(new FileWriter(BASE_FOLDER + DB_DATA, true)));
        
        getLastTrunkIds();
    }
    
    private void printHeaderFile(boolean append) throws IOException {
        outputSamples = new PrintWriter(new BufferedWriter(new FileWriter(BASE_FOLDER + DB_DATA, append)));
        outputSamples.println("@FILE_FORMAT");
        outputSamples.println("@timestamp: double");outputSamples.println("@x: double");
        outputSamples.println("@y: double");outputSamples.println("@z: double");
        outputSamples.println("@xPMitzell: double");outputSamples.println("@yPMitzell: double");
        outputSamples.println("@zPMitzell: double");outputSamples.println("@xHMitzell: double");
        outputSamples.println("@yHMitzell: double");outputSamples.println("@zHMitzell: double");
        outputSamples.println("@sex: string");outputSamples.println("@height: string");
        outputSamples.println("@shoes: string");outputSamples.println("@mode: string");
        outputSamples.println("@action: string");
        outputSamples.println("@trunk: int"); outputSamples.println("@isLinear: boolean");
        outputSamples.println("@DATA");
        outputSamples.flush();
    }
    
    public void resetOutputFile() {
        try {
        printHeaderFile(true);
        }
        catch(IOException exc) {
            exc.printStackTrace();
        }
    }
    
    /**
     * Is responsible to get the last IDs for the inserted trunks, either for the
     * linear case or the accelerometer case
     */
    private void getLastTrunkIds() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(BASE_FOLDER + DB_DATA)));
            String line;
            while((line = reader.readLine()) != null) {
                if (!line.contains("@")) {
                    line = line.replace("(", "").replace(")", "");
                    
                    String[] pieces = line.split(";");
                    int idTrunk = Integer.valueOf(pieces[pieces.length - 2]);
                    
                    if (idTrunk > lastTrunkId) {
                        lastTrunkId = idTrunk;
                    }
                }
            }
        }
        catch(FileNotFoundException exc) {
            System.out.println("File Not Found exception samplesSlidingWindows.dsw");
            exc.printStackTrace();
        }
        catch(IOException exc) {
            System.out.println("IOException form samplesSlidingWindows.dsw");
        }
    }
    
    /**
     * Checks if the database has been already analyzed and all the data inserted
     * @param databaseName: the name of the database to check
     * @return if the database already inserted
     */
    public boolean checkIfDatabaseAlreadyInserted(String databaseName) {
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(BASE_FOLDER + DB_FILE)));
            String line;
            
            boolean found = false;
            
            while (!found && (line = reader.readLine()) != null) {
                String[] databases = line.split(",");


                for (int i = 0; i < databases.length && !found; i++) {
                    if (databases[i].equals(databaseName)) {
                        found = true;
                    }
                }
            }
            
            return found;
        }
        catch(FileNotFoundException exc) {
            System.out.println("File Not Found (databasesInserted)");
            exc.printStackTrace();
            return false;
        }
        catch(IOException exc) {
            System.out.println("IOException (databasesInserted)");
            exc.printStackTrace();
            return false;
        }
    }
    
    /**
     * Adds a new database to the file of the inserted databases
     * @param databaseName the name of the database
     */
    public void insertNewDatabase(String databaseName) {
        
        outputDatabase.println(databaseName);
        outputDatabase.flush();
    }
    
    /**
     * Adds a new window to the file. Each sliding window is recognized
     * by the trunkID field that is the same for the values belonging to the window
     * @param window: the window to insert
     * @param newAction: the new action assigned to the SlidingWindow
     * @param linear: if the window represents linear values or not
     */
    public void addNewSlidingWindow(SlidingWindow window, String newAction, boolean linear) {
    
        if (newAction != null) {
            window.setSupposedAction(newAction);
        }
        
        List<SingleCoordinateSet> values = window.getValues(),
                valuesPMitzell = window.getPMitzellValues(),
                valuesHMitzell = window.getHMitzellValues();
        
        int lengthSlidingWindow = values.get(0).getValues().size();
        
        for (int i = 0; i < lengthSlidingWindow; i++) {
            
            Double timestamp = values.get(0).getValues().get(i).getTime(),
                    x = values.get(0).getValues().get(i).getValue(),
                    y = values.get(1).getValues().get(i).getValue(),
                    z = values.get(2).getValues().get(i).getValue(),
                    xPMitzell = linear?null:valuesPMitzell.get(0).getValues().get(i).getValue(),
                    yPMitzell = linear?null:valuesPMitzell.get(1).getValues().get(i).getValue(),
                    zPMitzell = linear?null:valuesPMitzell.get(2).getValues().get(i).getValue(),
                    xHMitzell = linear?null:valuesHMitzell.get(0).getValues().get(i).getValue(),
                    yHMitzell = linear?null:valuesHMitzell.get(1).getValues().get(i).getValue(),
                    zHMitzell = linear?null:valuesHMitzell.get(2).getValues().get(i).getValue();
            
            String finalString = "(" + timestamp + ";" + x + ";" + y + ";" + z + ";" +
                    (!linear?xPMitzell:"NULL") + ";" + (!linear?yPMitzell.toString():"NULL") + ";"
                    + (!linear?zPMitzell.toString():"NULL") + ";" + (!linear?xHMitzell.toString():"NULL") + ";" 
                    + (!linear?yHMitzell.toString():"NULL") + ";" + (!linear?zHMitzell.toString():"NULL") + ";" 
                    + window.getSex() + ";" + window.getHeight() + ";" 
                    + window.getShoes() + ";" + window.getMode() + ";"
                    + window.getSupposedAction() + ";" + 
                    (lastTrunkId) + ";" + (linear?1:0) + ")";
            
            outputSamples.println(finalString);
        }
        outputSamples.flush();
        
        lastTrunkId++;
    }
    
    /**
     * Retrieves all the sliding window from the textual database, dividing 
     * them depending on the action and if are linear or not
     * 
     * @param noGravityUpstairs: list containing upstairs with no gravity movements
     * @param linearUpstairs: list containing upstairs using linear data
     * @param noGravityDownstairs: list containing downstairs with no gravity movements
     * @param LinearDownstairs: list containing downstairs using linear data
     * @param noGravityNoStairs: list containing no stairs with no gravity movements
     * @param linearNoStairs: list containing no stairs using linear data
     */
    public void retrieveAllSlidingWindows(List<SlidingWindow> noGravityUpstairs, 
            List<SlidingWindow> linearUpstairs, List<SlidingWindow> noGravityDownstairs,
            List<SlidingWindow> LinearDownstairs, List<SlidingWindow> noGravityNoStairs,
            List<SlidingWindow> linearNoStairs) {
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(BASE_FOLDER + DB_DATA)));
            
            String line;
            
            while ((line=reader.readLine()).contains("@")) {}
            
            // devo definire lista List<SingleCoordinateSet> per gli x y e z 
            // che fanno parte del trunk. Se trunk di quello che leggo e' diverso
            // dal precedente allora devo creare window e schiaffarla dentro
            int lastTrunkId = -1;
            SlidingWindow window = null;
            List<SingleCoordinateSet> valuesForWindow = new ArrayList<SingleCoordinateSet>(),
                    vectorPMitzell = null,
                    vectorHMitzell = null;
            
            initializeList(valuesForWindow); 
            
            line = line.replace("(", "").replace(")", "");
            String[] elements = line.split(";");
            
            if (elements[elements.length - 1].equals("0")) {
                vectorPMitzell = new ArrayList<SingleCoordinateSet>();
                vectorHMitzell = new ArrayList<SingleCoordinateSet>();
                initializeList(vectorHMitzell); initializeList(vectorPMitzell);
            }
            
            window = new SlidingWindow(elements[10], elements[11], elements[12],
                    elements[13], elements[14], valuesForWindow, 
                    vectorPMitzell, vectorHMitzell, 
                    elements[elements.length - 1].equals("1"), 
                    Integer.valueOf(elements[elements.length - 2]));
            
            insertNewThreeDataTime(valuesForWindow, elements[0], elements[1], elements[2], elements[3]);
            
            if (!window.isLinear()) {
                insertNewThreeDataTime(vectorPMitzell, elements[0], elements[4], elements[5], elements[6]);
                insertNewThreeDataTime(vectorHMitzell, elements[0], elements[7], elements[8], elements[9]);
            }
            
            while ((line = reader.readLine()) != null) {
                if (!line.contains("@")) {
                    
                    line = line.replace("(", "").replace(")", "");
                    
                    elements = line.split(";");
                    /**
                     * elements[0]: timestamp
                     * elements[1,2,3]: x,y,z
                     * elements[4,5,6]: xPMitzell, yPMitzell, zPMitzell
                     * elements[7,8,9]: xHMitzell, yHMitzell, zHMitzell
                     * elements[10]]: sex
                     * elements[11]: height
                     * elements[12]: shoes
                     * elements[13]: mode
                     * elements[14]: action
                     * elements[15]: trunk
                     * elements[16]: isLinear
                     */
                    
                    /**
                     * Sliding window completed, I have to create a new one and
                     * initialize the three lists
                     */
                    if (Integer.valueOf(elements[elements.length - 2]) != lastTrunkId) {
                        
                        //window.completeSlidingWindow();
                        if (window.isLinear()) {
                            if (window.getSupposedAction().equals(App.NO_STAIR)) {
                                linearNoStairs.add(window);
                            }
                            else if (window.getSupposedAction().equals(App.STAIR_DOWNSTAIRS)) {
                                LinearDownstairs.add(window);
                            }
                            else if (window.getSupposedAction().equals(App.STAIR_UPSTAIRS)) {
                                linearUpstairs.add(window);
                            }
                        }
                        else {
                            if (window.getSupposedAction().equals(App.NO_STAIR)) {
                                noGravityNoStairs.add(window);
                            }
                            else if (window.getSupposedAction().equals(App.STAIR_DOWNSTAIRS)) {
                                noGravityDownstairs.add(window);
                            }
                            else if (window.getSupposedAction().equals(App.STAIR_UPSTAIRS)) {
                                noGravityUpstairs.add(window);
                            }
                        }
                        
                        /**
                         * Initialize the new window
                         */
                        valuesForWindow = new ArrayList<SingleCoordinateSet>();
                        initializeList(valuesForWindow);
                        if (elements[elements.length - 1].equals("0")) {
                            vectorPMitzell = new ArrayList<SingleCoordinateSet>();
                            vectorHMitzell = new ArrayList<SingleCoordinateSet>();
                            
                            initializeList(vectorHMitzell); initializeList(vectorPMitzell);
                        }
                        else {
                            vectorPMitzell = null; vectorHMitzell = null;
                        }
                        
                        window = new SlidingWindow(elements[10], elements[11], 
                                elements[12], elements[13], elements[14],
                                valuesForWindow, vectorPMitzell, vectorHMitzell, 
                                elements[elements.length - 1].equals("1"), 
                                Integer.valueOf(elements[elements.length - 2]));
                        
                        lastTrunkId = Integer.valueOf(elements[elements.length - 2]);
                    }
                    /**
                     * Add data to the window, either new or old
                     */
                        
                    insertNewThreeDataTime(valuesForWindow, elements[0], elements[1], elements[2], elements[3]);
                    if (!window.isLinear() && vectorHMitzell==null) {
                        System.out.println("errore");
                    }
                            
                    if (!window.isLinear()) {
                        insertNewThreeDataTime(vectorPMitzell, elements[0], elements[4], elements[5], elements[6]);
                        insertNewThreeDataTime(vectorHMitzell, elements[0], elements[7], elements[8], elements[9]);
                    }
                }
            }
        }
        catch(IOException exc) {
            System.out.println("IOException for " + DB_DATA);
            exc.printStackTrace();
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
    }
    
    private void initializeList(List<SingleCoordinateSet> list) {
        list.add(new SingleCoordinateSet("X")); list.add(new SingleCoordinateSet("Y"));
        list.add(new SingleCoordinateSet("Z"));
    }
    
    private void insertNewThreeDataTime(List<SingleCoordinateSet> values, String timestamp, String x, String y, String z) {
        values.get(0).addValue(new DataTime(Double.valueOf(timestamp), Double.valueOf(x)));
        values.get(1).addValue(new DataTime(Double.valueOf(timestamp), Double.valueOf(y)));
        values.get(2).addValue(new DataTime(Double.valueOf(timestamp), Double.valueOf(z)));
    }
}
