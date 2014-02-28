package org.processmining.fraud.model;

//model untuk fraud
public class fraud {
	private String Case;
	private int SkipSeq;
	private int SkipDec;
	private int Tmin;
	private int Tmax;
	private int WResource;
	private int Fraud;
	private int WDutySeq;
	private int WDutyDec;
	private int WDutyCom; 
	private int wPattern; 
	private int wDecision;
	private int maxSkipSeq=16;
	private int maxSkipDec=8;
	private int maxTmin=24;
	private int maxTmax=24;
	private int maxWResource=24;
	private int maxWDutySeq=16;
	private int maxWDutyDec=8;
	private int maxWDutyCom=24; 
	private int maxwPattern=24; 
	private int maxwDecision=8;
	
	
	public fraud() {
		super();
	}

	
	
	public fraud(String case1, int skipSeq, int skipDec, int tmin, int tmax, int wResource, int wDutySeq,
			int wDutyDec, int wDutyCom, int wPattern, int wDecision, int fraud) {
		super();
		Case = case1;
		SkipSeq = skipSeq;
		SkipDec = skipDec;
		Tmin = tmin;
		Tmax = tmax;
		WResource = wResource;
		Fraud = fraud;
		WDutySeq = wDutySeq;
		WDutyDec = wDutyDec;
		WDutyCom = wDutyCom;
		this.wPattern = wPattern;
		this.wDecision = wDecision;
	}



	public int getMaxSkipSeq() {
		return maxSkipSeq;
	}

	public void setMaxSkipSeq(int maxSkipSeq) {
		this.maxSkipSeq = maxSkipSeq;
	}

	public int getMaxSkipDec() {
		return maxSkipDec;
	}

	public void setMaxSkipDec(int maxSkipDec) {
		this.maxSkipDec = maxSkipDec;
	}

	public int getMaxTmin() {
		return maxTmin;
	}

	public void setMaxTmin(int maxTmin) {
		this.maxTmin = maxTmin;
	}

	public int getMaxTmax() {
		return maxTmax;
	}

	public void setMaxTmax(int maxTmax) {
		this.maxTmax = maxTmax;
	}

	public int getMaxWResource() {
		return maxWResource;
	}

	public void setMaxWResource(int maxWResource) {
		this.maxWResource = maxWResource;
	}

	public int getMaxWDutySeq() {
		return maxWDutySeq;
	}

	public void setMaxWDutySeq(int maxWDutySeq) {
		this.maxWDutySeq = maxWDutySeq;
	}

	public int getMaxWDutyDec() {
		return maxWDutyDec;
	}

	public void setMaxWDutyDec(int maxWDutyDec) {
		this.maxWDutyDec = maxWDutyDec;
	}

	public int getMaxWDutyCom() {
		return maxWDutyCom;
	}

	public void setMaxWDutyCom(int maxWDutyCom) {
		this.maxWDutyCom = maxWDutyCom;
	}

	public int getMaxwPattern() {
		return maxwPattern;
	}

	public void setMaxwPattern(int maxwPattern) {
		this.maxwPattern = maxwPattern;
	}

	public int getMaxwDecision() {
		return maxwDecision;
	}

	public void setMaxwDecision(int maxwDecision) {
		this.maxwDecision = maxwDecision;
	}

	public int getWDutySeq() {
		return WDutySeq;
	}

	public void setWDutySeq(int wDutySeq) {
		WDutySeq = wDutySeq;
	}

	public int getWDutyDec() {
		return WDutyDec;
	}

	public void setWDutyDec(int wDutyDec) {
		WDutyDec = wDutyDec;
	}

	public int getWDutyCom() {
		return WDutyCom;
	}

	public void setWDutyCom(int wDutyCom) {
		WDutyCom = wDutyCom;
	}

	public int getwPattern() {
		return wPattern;
	}

	public void setwPattern(int wPattern) {
		this.wPattern = wPattern;
	}

	public int getwDecision() {
		return wDecision;
	}

	public void setwDecision(int wDecision) {
		this.wDecision = wDecision;
	}

	public String getCase() {
		return Case;
	}
	public void setCase(String case1) {
		Case = case1;
	}
	public int getSkipSeq() {
		return SkipSeq;
	}
	public void setSkipSeq(int skipSeq) {
		SkipSeq = skipSeq;
	}
	public int getSkipDec() {
		return SkipDec;
	}
	public void setSkipDec(int skipDec) {
		SkipDec = skipDec;
	}
	public int getTmin() {
		return Tmin;
	}
	public void setTmin(int tmin) {
		Tmin = tmin;
	}
	public int getTmax() {
		return Tmax;
	}
	public void setTmax(int tmax) {
		Tmax = tmax;
	}
	public int getWResource() {
		return WResource;
	}
	public void setWResource(int wResource) {
		WResource = wResource;
	}
	public int getFraud() {
		return Fraud;
	}
	public void setFraud(int fraud) {
		Fraud = fraud;
	}
	
	
	

}
