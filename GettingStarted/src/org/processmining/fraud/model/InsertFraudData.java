package org.processmining.fraud.model;

import java.util.ArrayList;
import java.util.List;

public class InsertFraudData {
	
	public List<fraud>frauds = new ArrayList<fraud>();
	
	
	public InsertFraudData() {
		super();
		// TODO Auto-generated constructor stub
	}


	public void insert(List<fraud>fs)
	{
		frauds = fs;
	}
}
