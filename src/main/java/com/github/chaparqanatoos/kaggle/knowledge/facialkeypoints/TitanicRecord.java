package com.github.chaparqanatoos.kaggle.knowledge.facialkeypoints;

import java.io.Serializable;
import java.util.Arrays;

public class TitanicRecord implements Serializable {
	
	public static final String[] GENDERS = {"female", "male"};
	public static final String[] EMBARKED = {"C", "Q", "S"};

	/**
	 * 
	 */
	private static final long serialVersionUID = -7652126961955548367L;
	
	private boolean survived;
	private int pClass;
	private String name;
	private int sex;
	private double age;
	private int sibsp;
	private int parch;
	private String ticket;
	private double fare;
	private String cabin;
	private int embarked;
	public boolean isSurvived() {
		return survived;
	}
	public void setSurvived(boolean survived) {
		this.survived = survived;
	}
	public int getpClass() {
		return pClass;
	}
	public void setpClass(int pClass) {
		this.pClass = pClass;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public double getAge() {
		return age;
	}
	public void setAge(double age) {
		this.age = age;
	}
	public int getSibsp() {
		return sibsp;
	}
	public void setSibsp(int sibsp) {
		this.sibsp = sibsp;
	}
	public int getParch() {
		return parch;
	}
	public void setParch(int parch) {
		this.parch = parch;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public double getFare() {
		return fare;
	}
	public void setFare(double fare) {
		this.fare = fare;
	}
	public String getCabin() {
		return cabin;
	}
	public void setCabin(String cabin) {
		this.cabin = cabin;
	}
	public int getEmbarked() {
		return embarked;
	}
	public void setEmbarked(int embarked) {
		this.embarked = embarked;
	}
	public static int parseSex(String string) {
		return Arrays.binarySearch(GENDERS, string);
	}
	public static int parseEmbarked(String string) {
		return Arrays.binarySearch(EMBARKED, string);
	}
}
