package com.github.chaparqanatoos.kaggle.knowledge.sentimentanalysis;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameter;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterPoint;
import edu.berkeley.compbio.jlibsvm.SVM;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationProblem;
import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;
import edu.berkeley.compbio.jlibsvm.binary.BooleanClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.binary.C_SVC;
import edu.berkeley.compbio.jlibsvm.kernel.LinearKernel;
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

		System.out.println("Starting prediction.....");
		Map<TitanicRecord, Boolean> results = predict(args[1], model);
		System.out.println("Finished prediction.....");

		saveResults(results, "data/titanic/submission.csv");
	}

	public static BinaryModel<Boolean, SparseVector> train(String fileName) {
		float gamma = 1.0f;

		LinearKernel kernel = new LinearKernel();

		Set<SparseVector> trueExamples = new HashSet<SparseVector>();
		Set<SparseVector> falseExamples = new HashSet<SparseVector>();
		Map<SparseVector, Integer> exampleIds = new HashMap<SparseVector, Integer>();

		loadTrainingData(fileName, trueExamples, falseExamples, exampleIds);

		BinaryClassificationProblem<Boolean, SparseVector> problem = new BooleanClassificationProblemImpl<Boolean, SparseVector>(
				Boolean.class, Boolean.TRUE, trueExamples, Boolean.FALSE,
				falseExamples, exampleIds);

		SVM<Boolean, SparseVector, BinaryClassificationProblem<Boolean, SparseVector>> svm = new C_SVC<Boolean, SparseVector>();

		ImmutableSvmParameterPoint.Builder<Boolean, SparseVector> builder = new ImmutableSvmParameterPoint.Builder<Boolean, SparseVector>();
		// set svm parameters
		builder.kernel = kernel;
		builder.nu = 0.5f;
		builder.cache_size = 100;
		builder.eps = 1e-3f;
		builder.p = 0.1f;
		builder.C = 1.0f;
		builder.shrinking = true;
		builder.probability = false;
		builder.redistributeUnbalancedC = true;
		// builder.gridsearchBinaryMachinesIndependently = true;
//		builder.scaleBinaryMachinesIndependently = true;
//		builder.scalingModelLearner = new LinearScalingModelLearner(500,
//		true);

		ImmutableSvmParameter<Boolean, SparseVector> param = builder.build();
		
		BinaryModel<Boolean, SparseVector> model = (BinaryModel<Boolean, SparseVector>) svm.train(problem, param);
//		do_cross_validation(svm, problem, param);

		return model;
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

	public static Map<TitanicRecord, Boolean> predict(String testFileName,
			BinaryModel<Boolean, SparseVector> model) {
		List<TitanicRecord> tests = new ArrayList<TitanicRecord>();
		loadTestData(testFileName, tests);

		Map<TitanicRecord, Boolean> results = new LinkedHashMap<TitanicRecord, Boolean>();
		
		for (TitanicRecord record : tests) {
			results.put(record, model.predictLabel(convertToSparseVector(record)));
		}

		return results;
	}

	private static void loadTrainingData(String fileName,
			Set<SparseVector> trueExamples, Set<SparseVector> falseExamples,
			Map<SparseVector, Integer> exampleIds) {
		CSVReader reader = null;

		try {
			reader = new CSVReader(new FileReader(fileName));

			CSVParser parser = new CSVParser();

			int id = 1;
			
			boolean header = true;
			
			String [] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
				if (header) {
					// skip....
					header = false;
				} else {
					TitanicRecord record = parser.parse(nextLine, true);

					SparseVector vector = convertToSparseVector(record);
					if (record.isSurvived()) {
						trueExamples.add(vector);
					} else {
						falseExamples.add(vector);
					}
					exampleIds.put(vector, id);
					id++;
				}
			}

			System.out.println("Loaded " + id + " records....");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeReader(reader);
		}

	}

	private static void loadTestData(String fileName,
			List<TitanicRecord> tests) {
		CSVReader reader = null;

		try {
			reader = new CSVReader(new FileReader(fileName));

			CSVParser parser = new CSVParser();

			boolean header = true;
			String [] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
				if (header) {
					// skip....
					header = false;
				} else {
					TitanicRecord record = parser.parse(nextLine,
							false);
					tests.add(record);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeReader(reader);
		}

	}

	private static void saveResults(Map<TitanicRecord, Boolean> results,
			String outputFileName) {
		PrintWriter output = null;

		try {
			output = new PrintWriter(new FileWriter(outputFileName));

			// header
			output.println("survived ,pclass, name, sex, age, sibsp, parch, ticket, fare, cabin, embarked");

			for (TitanicRecord id : results.keySet()) {
				output.println((results.get(id) ? "1" : "0") + "," + id.getpClass()  + "," + id.getName() + "," + TitanicRecord.GENDERS[id.getSex()] + "," + id.getAge() + "," + id.getSibsp() + "," + id.getParch() + "," + id.getTicket() + "," + id.getFare() + "," + id.getCabin() + "," + TitanicRecord.EMBARKED[id.getEmbarked()]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeWriter(output);
		}
	}

	private static SparseVector convertToSparseVector(TitanicRecord record) {
		SparseVector vector = new SparseVector(10);
		vector.indexes[0] = 0;
		vector.values[0] = record.getpClass();
		vector.indexes[1] = 1;
		vector.values[1] = record.getParch();
//		vector.indexes[2] = 2;
//		vector.values[2] = record.getEmbarked();
		vector.indexes[3] = 3;
		vector.values[3] = record.getSex();
//		vector.indexes[4] = 4;
//		vector.values[4] = record.getSibsp();
		vector.indexes[5] = 5;
		vector.values[5] = (int) record.getAge();
//		vector.indexes[6] = 6;
//		vector.values[6] = (int) record.getFare();
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
		for (SparseVector p : problem.getExamples().keySet())
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
	protected static void closeReader(CSVReader reader) {
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
