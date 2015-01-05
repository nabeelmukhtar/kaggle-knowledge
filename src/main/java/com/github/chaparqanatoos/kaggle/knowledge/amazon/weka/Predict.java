package com.github.chaparqanatoos.kaggle.knowledge.amazon.weka;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;

/**
 * The Predict class uses the trained Classifier and the test data to create a
 * prediction CSV file. As noted inthe README.md, we modified the original
 * test.csv file to contain the 'survived' column. We do not actually use the
 * values of this column, weka simply requires the train and test data to match.
 * 
 * @author jbirchfield
 * 
 */
public class Predict {

	public static void main(String[] args) throws Exception {

		/*
		 * First we load the test data from our ARFF file
		 */
		ArffLoader testLoader = new ArffLoader();
		testLoader.setSource(new File("data/test.arff"));
		testLoader.setRetrieval(Loader.BATCH);
		Instances testDataSet = testLoader.getDataSet();

		/*
		 * Now we tell the data set which attribute we want to classify, in our
		 * case, we want to classify the first column: survived
		 */
		Attribute testAttribute = testDataSet.attribute(0);
		testDataSet.setClass(testAttribute);
		testDataSet.deleteStringAttributes();

		/*
		 * Now we read in the serialized model from disk
		 */
		Classifier classifier = (Classifier) SerializationHelper
				.read("data/amazon.model");

		/*
		 * Now we iterate over the test data and classify each entry and set the
		 * value of the 'survived' column to the result of the classification
		 */
		Map<Integer, Boolean> results = new TreeMap<Integer, Boolean>();
		Enumeration testInstances = testDataSet.enumerateInstances();
		while (testInstances.hasMoreElements()) {
			Instance instance = (Instance) testInstances.nextElement();
			double classification = classifier.classifyInstance(instance);
			results.put(((int) instance.value(0)), (classification > 0.5));
		}
		
		saveResults(results, "data/submission.csv");

		/*
		 * Now we want to write out our predictions. The resulting file is in a
		 * format suitable to submit to Kaggle.
		 */
//		CSVSaver predictedCsvSaver = new CSVSaver();
//		predictedCsvSaver.setFile(new File("data/predict.csv"));
//		predictedCsvSaver.setInstances(test1DataSet);
//		predictedCsvSaver.writeBatch();

//		System.out.println("Prediciton saved to predict.csv");

	}
	
	private static void saveResults(Map<Integer, Boolean> results, String outputFileName) {
		PrintWriter output = null;
		
		try {
			output = new PrintWriter(new FileWriter(outputFileName));
			
			//header
			output.println("id,ACTION");
			
			for (Integer id : results.keySet()) {
				output.println(id + "," + (results.get(id)? "1" : "0"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeWriter(output);
		}
	}
	
	/**
	 * 
	 */
	protected static void closeReader(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	protected static void closeWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
