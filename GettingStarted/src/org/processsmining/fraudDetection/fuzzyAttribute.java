package org.processsmining.fraudDetection;

public class fuzzyAttribute {
	
	private double a;
	private double b;
	private double c;
	private double d;
	private double e;
	private double a1;
	private double b1;
	private double c1;
	private double d1;
	
	public double getA() {
		return a;
	}
	public void setA(double a) {
		this.a = a;
	}
	public double getB() {
		return b;
	}
	public void setB(double b) {
		this.b = b;
	}
	public double getC() {
		return c;
	}
	public void setC(double c) {
		this.c = c;
	}
	public double getD() {
		return d;
	}
	public void setD(double d) {
		this.d = d;
	}
	public double getE() {
		return e;
	}
	public void setE(double e) {
		this.e = e;
	}
	public double getA1() {
		return a1;
	}
	public void setA1(double a1) {
		this.a1 = a1;
	}
	public double getB1() {
		return b1;
	}
	public void setB1(double b1) {
		this.b1 = b1;
	}
	public double getC1() {
		return c1;
	}
	public void setC1(double c1) {
		this.c1 = c1;
	}
	public double getD1() {
		return d1;
	}
	public void setD1(double d1) {
		this.d1 = d1;
	}
	
	public fuzzyAttribute(double a, double b, double c, double d, double e) {
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
	
	public void importance()
	{
		setA(0);
		setB(25);
		setC(50);
		setD(75);
		setE(100);
	}
	
	public void membership(String name)
	{
		if(name=="low")
		{
			setA1(getA());
			setB1(getA());
			setC1(getB()/getE());
			setD1(getC()/getE());
			
			System.out.println("A: "+getA()+" -- B: "+getB()+" -- C: "+getC()+" -- D: "+getD()+" -- E: "+getE());
		}
		else if(name=="medium")
		{
			setA1(getB()/getE());
			setB1(getC()/getE());
			setC1(getD()/getE());
			setD1(getE()/getE());
		}
		else if(name=="high")
		{
			setA1(getD()/getE());
			setB1(getE()/getE());
			setC1(getE()/getE());
			setD1(getE()/getE());
		}
		
	}
	

}
