FeatureExtractorAccelBench
==========================

Java program to extract sample features from Accelerometer data. 

How to use: 
To Run the program, the command is 
  java -jar FeatureExtractor.jar file_name_without_extension
  
The input data has to be a series of data with this requirements:
  (time,xValue,yValue,zValue)(time,xValue,yValue,zValue)
  
The system normalize data and computes standard deviation, mean value, variation and shows two graphs about time 
interval between one sample and the next one and another one that show the acquired data.
