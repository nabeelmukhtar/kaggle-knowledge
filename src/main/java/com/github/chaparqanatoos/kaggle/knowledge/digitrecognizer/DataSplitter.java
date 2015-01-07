package com.github.chaparqanatoos.kaggle.knowledge.digitrecognizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Scanner;

public class DataSplitter {
	
	public static final double TRAINING_PROPORTION = 0.70;
	public static final double TEST_PROPORTION = 1.0;
	public static final double X_VALIDATION_PROPORTION = 1.0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: java com.kaggle.nabeelmukhtar.amazon.DataSplitter data/train.csv");
			System.exit(-1);
		}
		BufferedReader reader = null;
		PrintWriter trainSplit = null, testSplit = null, xValidationSplit = null;
		try {
			reader = new BufferedReader(new FileReader(args[0]));
			Scanner scanner = new Scanner(reader);
			trainSplit = new PrintWriter(new FileWriter(args[0] + ".train.split.csv"));
			testSplit = new PrintWriter(new FileWriter(args[0] + ".test.split.csv"));
			xValidationSplit = new PrintWriter(new FileWriter(args[0] + ".xvalid.split.csv"));
			
			boolean header = true;
			while (scanner.hasNextLine()) {
				if (header) {
					// skip....
					scanner.nextLine();
					header = false;
				} else {
					double rand = Math.random();
					
					if (rand < TRAINING_PROPORTION) {
						trainSplit.println(scanner.nextLine());
					} else if (rand < TEST_PROPORTION) {
						testSplit.println(scanner.nextLine());
					} else {
						xValidationSplit.println(scanner.nextLine());
					}
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeReader(reader);
			closeWriter(trainSplit);
			closeWriter(testSplit);
			closeWriter(xValidationSplit);
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
