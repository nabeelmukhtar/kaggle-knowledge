package com.github.chaparqanatoos.kaggle.knowledge.amazon;

public class CSVParser {
	
	public AmazonEacRecord parse(String line, boolean training) {
		if (line == null) {
			return null;
		}
		
		String[] tokens = line.split(",");
		AmazonEacRecord record = new AmazonEacRecord();
		if (training) {
			record.setAction("1".equals(tokens[0]));
		} else {
			record.setId(Integer.parseInt(tokens[0]));
		}
		record.setResource(Integer.parseInt(tokens[1]));
		record.setManagerId(Integer.parseInt(tokens[2]));
		record.setRoleRollup1(Integer.parseInt(tokens[3]));
		record.setRoleRollup2(Integer.parseInt(tokens[4]));
		record.setRoleDepartmentName(Integer.parseInt(tokens[5]));
		record.setRoleTitle(Integer.parseInt(tokens[6]));
		record.setRoleFamilyDescription(Integer.parseInt(tokens[7]));
		record.setRoleFamily(Integer.parseInt(tokens[8]));
		record.setRoleCode(Integer.parseInt(tokens[9]));
		
		return record;
	}
}
