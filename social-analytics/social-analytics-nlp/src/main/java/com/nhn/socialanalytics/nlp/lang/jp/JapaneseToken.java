package com.nhn.socialanalytics.nlp.lang.jp;

import java.util.ArrayList;
import java.util.List;

import com.nhn.socialanalytics.nlp.morpheme.Token;

public class JapaneseToken extends Token {

	private int cost;
	private String basicForm;
	private String conjugationalForm;
	private String conjugationalType;
	private String partOfSpeech;
	private List<String> pronunciations = new ArrayList<String>();
	private List<String> readings = new ArrayList<String>();
	
	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public String getBasicForm() {
		return basicForm;
	}

	public void setBasicForm(String basicForm) {
		this.basicForm = basicForm;
	}

	public String getConjugationalForm() {
		return conjugationalForm;
	}

	public void setConjugationalForm(String conjugationalForm) {
		this.conjugationalForm = conjugationalForm;
	}

	public String getConjugationalType() {
		return conjugationalType;
	}

	public void setConjugationalType(String conjugationalType) {
		this.conjugationalType = conjugationalType;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	public List<String> getPronunciations() {
		return pronunciations;
	}

	public void setPronunciations(List<String> pronunciations) {
		this.pronunciations = pronunciations;
	}

	public List<String> getReadings() {
		return readings;
	}

	public void setReadings(List<String> readings) {
		this.readings = readings;
	}

	@ Override
	public boolean containsTagOf(String[] tags) {
		return false;
	}
	
	public void makeObject(int index, net.java.sen.dictionary.Token token) {
		this.setIndex(index);
		this.setSource(token.getSurface());
		
		if (token.getMorpheme().getBasicForm().equals("*"))
			this.setTerm(token.getSurface());
		else
			this.setTerm(token.getMorpheme().getBasicForm());
		
		this.setCost(token.getCost());
		this.setBasicForm(token.getMorpheme().getBasicForm());
		this.setConjugationalForm(token.getMorpheme().getConjugationalForm());
		this.setConjugationalType(token.getMorpheme().getConjugationalType());
		this.setPartOfSpeech(token.getMorpheme().getPartOfSpeech());
		this.setPronunciations(token.getMorpheme().getPronunciations());
		this.setReadings(token.getMorpheme().getReadings());		
	}
	
	private char convertPos(String partOfSpeech) {
		
		return 'V';
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer()
		.append("[").append(index).append("]")
		.append(" source=").append(source)
		.append("| term=").append(term)
		.append("| cost=").append(cost)		
		.append("| basicForm=").append(basicForm)
		.append("| conjugationalForm=").append(conjugationalForm)
		.append("| conjugationalType=").append(conjugationalType)
		.append("| pos=").append(pos)
		.append("| partOfSpeech=").append(partOfSpeech)
		.append("| pronunciations=").append(pronunciations)
		.append("| readings=").append(readings);
	
		return sb.toString();	
	}

}