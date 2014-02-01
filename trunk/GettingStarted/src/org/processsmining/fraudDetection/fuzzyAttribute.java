package org.processsmining.fraudDetection;

public class fuzzyAttribute {
	
	private int a;
	private int b;
	private int c;
	private int d;
	private int e;
	
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	public int getC() {
		return c;
	}
	public void setC(int c) {
		this.c = c;
	}
	public int getD() {
		return d;
	}
	public void setD(int d) {
		this.d = d;
	}
	public int getE() {
		return e;
	}
	public void setE(int e) {
		this.e = e;
	}
	
	
	public fuzzyAttribute(int a, int b, int c, int d, int e) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
	}
	
	
	public fuzzyAttribute() {
		super();
		// TODO Auto-generated constructor stub
	}
	public void fuzzySeq()
	{
		setA(0);
		setB(3);
		setC(5);
		setD(8);
		setE(10);
	}
	
	public void fuzzyDec()
	{
		setA(0);
		setB(2);
		setC(3);
		setD(5);
		setE(6);
	}
	
	public void fuzzyAll()
	{
		setA(0);
		setB(4);
		setC(8);
		setD(12);
		setE(16);
	}

}
