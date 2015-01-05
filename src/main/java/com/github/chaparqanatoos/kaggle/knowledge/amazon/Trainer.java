package com.github.chaparqanatoos.kaggle.knowledge.amazon;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameter;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterPoint;
import edu.berkeley.compbio.jlibsvm.SVM;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationProblem;
import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;
import edu.berkeley.compbio.jlibsvm.binary.BooleanClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.binary.C_SVC;
import edu.berkeley.compbio.jlibsvm.kernel.GaussianRBFKernel;
import edu.berkeley.compbio.jlibsvm.kernel.LinearKernel;
import edu.berkeley.compbio.jlibsvm.scaler.LinearScalingModelLearner;
import edu.berkeley.compbio.jlibsvm.util.SparseVector;

public class Trainer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err
					.println("Usage: java com.kaggle.nabeelmukhtar.amazon.Trainer data/train.csv.train.split.csv data/train.csv.test.split.csv");
			System.exit(-1);
		}

		System.out.println("Started training.....");
		BinaryModel<Boolean, SparseVector> model = train(args[0]);
		System.out.println("Finished training.....");

//		System.out.println("Starting evalation.....");
//		double score = evaluate(args[1], model);
//		System.out.println("Finished evaluation.....");
//		System.out.println("Score: " + score);

//		System.out.println("Starting prediction.....");
//		Map<Integer, Boolean> results = predict(args[1], model);
//		System.out.println("Finished prediction.....");
//
//		saveResults(results, "data/submission.csv");
	}

	public static BinaryModel<Boolean, SparseVector> train(String fileName) {
		float gamma = 0.2f;

		GaussianRBFKernel kernel = new GaussianRBFKernel(gamma);

		Set<SparseVector> trueExamples = new HashSet<SparseVector>();
		Set<SparseVector> falseExamples = new HashSet<SparseVector>();
		Map<SparseVector, Integer> exampleIds = new HashMap<SparseVector, Integer>();

		loadTrainingData(fileName, trueExamples, falseExamples, exampleIds);

		BinaryClassificationProblem<Boolean, SparseVector> problem = new BooleanClassificationProblemImpl<Boolean, SparseVector>(
				Boolean.class, Boolean.TRUE, trueExamples, Boolean.FALSE,
				falseExamples, exampleIds);
		
		SVM<Boolean, SparseVector, BinaryClassificationProblem<Boolean, SparseVector>> svm = new C_SVC<Boolean, SparseVector>();

		double C_arr[] = {0.01, 0.1, 0.5, 1.0, 10.0, 100.0};
//		double p_arr[] = {0.1, 0.5, 1.0, 10.0, 100.0};
//		double eps_arr[] = {10.0, 1.0, 0.1, 0.01, 0.001, 0.0001, 0.00001};
//		double nu_arr[] = {0.1, 0.5, 1.0, 10.0, 100.0};
		
		for (int i = 0; i < C_arr.length; i++) {
//			for (int j = 0; j < p_arr.length; j++) {
//				for (int k = 0; k < eps_arr.length; k++) {
//					for (int l = 0; l < nu_arr.length; l++) {
						ImmutableSvmParameterPoint.Builder<Boolean, SparseVector> builder = new ImmutableSvmParameterPoint.Builder<Boolean, SparseVector>();
						builder.kernel = kernel;
//						builder.nu = (float) nu_arr[l];
						builder.cache_size = 100;
						builder.eps = 0.0001f;
//						builder.p = (float) p_arr[j];
						builder.C = (float) C_arr[i];
						builder.shrinking = true;
						builder.probability = false;
						builder.redistributeUnbalancedC = true;
						// builder.gridsearchBinaryMachinesIndependently = true;
						builder.scaleBinaryMachinesIndependently = true;
						builder.scalingModelLearner = new LinearScalingModelLearner(500,
								true);
						ImmutableSvmParameter<Boolean, SparseVector> param = builder.build();
						
						System.out.println("C:" + C_arr[i]);
						
						do_cross_validation(svm, problem, param);
//					}
					
//				}
//			}
		}
		
		// set svm parameters

		
//		BinaryModel<Boolean, SparseVector> model = (BinaryModel<Boolean, SparseVector>) svm.train(problem, param);
//
		return null;
	}

	public static double evaluate(String testFileName,
			BinaryModel<Boolean, SparseVector> model) {
		Set<SparseVector> trueExamples = new HashSet<SparseVector>();
		Set<SparseVector> falseExamples = new HashSet<SparseVector>();
		Map<SparseVector, Integer> exampleIds = new HashMap<SparseVector, Integer>();

		loadTrainingData(testFileName, trueExamples, falseExamples, exampleIds);

		int tp = 0, tn = 0, fp = 0, fn = 0;

		for (SparseVector x : falseExamples) {
			if (model.predictLabel(x)) {
				fp++;
			} else {
				tn++;
			}
		}

		for (SparseVector x : trueExamples) {
			if (model.predictLabel(x)) {
				tp++;
			} else {
				fn++;
			}
		}

		double prec = (double) tp / (double) (tp + fp);
		double recall = (double) tp / (double) (tp + fn);
		double f = (2 * prec * recall) / (prec + recall);

		System.out.println("prec:" + prec + ", recall:" + recall
				+ ", f-measure:" + f);

		return f;
	}

	public static Map<Integer, Boolean> predict(String testFileName,
			BinaryModel<Boolean, SparseVector> model) {
		Map<Integer, SparseVector> tests = new HashMap<Integer, SparseVector>();
		loadTestData(testFileName, tests);

		Map<Integer, Boolean> results = new TreeMap<Integer, Boolean>();

		for (Integer id : tests.keySet()) {
			results.put(id, model.predictLabel(tests.get(id)));
		}

		return results;
	}

	private static void loadTrainingData(String fileName,
			Set<SparseVector> trueExamples, Set<SparseVector> falseExamples,
			Map<SparseVector, Integer> exampleIds) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(fileName));
			Scanner scanner = new Scanner(reader);

			CSVParser parser = new CSVParser();

			int id = 1;

			while (scanner.hasNextLine()) {
				AmazonEacRecord record = parser.parse(scanner.nextLine(), true);
				record.setId(id);

				SparseVector vector = convertToSparseVector(record);
				if (record.isAction()) {
					trueExamples.add(vector);
				} else {
					falseExamples.add(vector);
				}
				exampleIds.put(vector, id);
				id++;
			}

			System.out.println("Loaded " + id + " records....");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeReader(reader);
		}

	}

	private static void loadTestData(String fileName,
			Map<Integer, SparseVector> tests) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(fileName));
			Scanner scanner = new Scanner(reader);

			CSVParser parser = new CSVParser();

			boolean header = true;
			while (scanner.hasNextLine()) {
				if (header) {
					// skip....
					scanner.nextLine();
					header = false;
				} else {
					AmazonEacRecord record = parser.parse(scanner.nextLine(),
							false);
					tests.put(record.getId(), convertToSparseVector(record));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeReader(reader);
		}

	}

	private static void saveResults(Map<Integer, Boolean> results,
			String outputFileName) {
		PrintWriter output = null;

		try {
			output = new PrintWriter(new FileWriter(outputFileName));

			// header
			output.println("id,ACTION");

			for (Integer id : results.keySet()) {
				output.println(id + "," + (results.get(id) ? "1" : "0"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeWriter(output);
		}
	}

	private static SparseVector convertToSparseVector(AmazonEacRecord record) {
		SparseVector vector = new SparseVector(10);
		vector.indexes[0] = 0;
		vector.values[0] = record.getRoleCode();
		vector.indexes[1] = 1;
		vector.values[1] = record.getManagerId();
		vector.indexes[2] = 2;
		vector.values[2] = record.getRoleRollup1();
		vector.indexes[3] = 3;
		vector.values[3] = record.getRoleRollup2();
		vector.indexes[4] = 4;
		vector.values[4] = record.getRoleDepartmentName();
		vector.indexes[5] = 5;
		vector.values[5] = record.getRoleTitle();
		vector.indexes[6] = 6;
		vector.values[6] = record.getResource();
		vector.indexes[7] = 7;
		vector.values[7] = record.getRoleFamily();
//		vector.indexes[8] = 8;
//		vector.values[8] = record.getRoleFamilyDescription();
		return vector;
	}

	private static void do_cross_validation(SVM<Boolean, SparseVector, BinaryClassificationProblem<Boolean, SparseVector>> svm, BinaryClassificationProblem<Boolean, SparseVector> problem, ImmutableSvmParameter<Boolean, SparseVector> param) {
		// int i;
		int total_correct = 0;
		int total_unknown = 0;
		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		// double[] target = new double[problem.l];

		int numExamples = problem.getNumExamples();

		Map cvResult = svm.discreteCrossValidation(problem, param);
		for (SparseVector p : problem.getBooleanExamples().keySet())
		// for (i = 0; i < numExamples; i++)
		{
			Object prediction = cvResult.get(p);
			if (prediction == null) {
				++total_unknown;
			} else if (prediction.equals(problem.getTargetValue(p))) {
				++total_correct;
			}
		}

		int classifiedExamples = numExamples - total_unknown;
		System.out.print("Cross Validation Classified = " + 100.0
				* classifiedExamples / numExamples + "%\n");
		System.out.print("Cross Validation Accuracy (of those classified) = "
				+ 100.0 * total_correct / classifiedExamples + "%\n");
		System.out.print("Cross Validation Accuracy (of total) = " + 100.0
				* total_correct / numExamples + "%\n");
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
