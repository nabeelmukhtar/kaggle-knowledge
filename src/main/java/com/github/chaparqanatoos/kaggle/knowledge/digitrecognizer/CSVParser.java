package com.github.chaparqanatoos.kaggle.knowledge.digitrecognizer;



public class CSVParser {
	
	
	public TitanicRecord parse(String[] tokens, boolean training) {
		
		TitanicRecord record = new TitanicRecord();
		int index = 0;
		if (training) {
			record.setSurvived("1".equals(tokens[index++]));
		}
		record.setpClass(parseInt(tokens[index++]));
		record.setName(tokens[index++]);
		record.setSex(TitanicRecord.parseSex(tokens[index++]));
		record.setAge(parseDouble(tokens[index++]));
		record.setSibsp(parseInt(tokens[index++]));
		record.setParch(parseInt(tokens[index++]));
		record.setTicket(tokens[index++]);
		record.setFare(parseDouble(tokens[index++]));
		record.setCabin(tokens[index++]);
		record.setEmbarked(TitanicRecord.parseEmbarked(tokens[index++]));
		
		return record;
	}

	private double parseDouble(String string) {
		return (string == null || string.isEmpty())? 0.0 : Double.parseDouble(string);
	}

	private int parseInt(String string) {
		return (string == null || string.isEmpty())? 0 : Integer.parseInt(string);
	}
}
