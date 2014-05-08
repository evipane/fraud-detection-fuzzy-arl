package org.processsmining.fraudDetection;
public class CountImportance {
	
	
	private double a;
	private double b;
	private double c;
	private double d;
	
	private double a1;
	private double b1;
	private double c1;
	private double d1;
	
	
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
	
	private double aMin;
	private double bMin;
	private double cMin;
	private double dMin;
	public double getaMin() {
		return aMin;
	}
	public void setaMin(double aMin) {
		this.aMin = aMin;
	}
	public double getbMin() {
		return bMin;
	}
	public void setbMin(double bMin) {
		this.bMin = bMin;
	}
	public double getcMin() {
		return cMin;
	}
	public void setcMin(double cMin) {
		this.cMin = cMin;
	}
	public double getdMin() {
		return dMin;
	}
	public void setdMin(double dMin) {
		this.dMin = dMin;
	}
	
	public int pakars=0;
	public CountImportance() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String[][] inputpakar;
	public Object[][] tableImportance;
	public Object[][] tableViolation;
	public Object[][] tablePercent;
	public Object[][] tableMember;
	public Object[][] tableMember2;
	public Object[] fraud;
	public Object[][] tableAtribut;
	public String[] criteria = {"SkipS","SkipD","TMin","TMax","WResource","WDutyS","WDutyD","WDutyC","WPattern","WDecision"};
	public String[] membership= {"VB","BVB&B","B","BB&F","F","BF&G","G","BG&VG","VG"};
	private CountProbability cp = new CountProbability();
	public void setValueViolation(String name)
	{
		if(name=="VB")
		{
			setA1(0);
			setB1(0);
			setC1(0.1);
			setD1(0.2);
		}
		else if(name=="BVB&B")
		{
			setA1(0);
			setB1(0.1);
			setC1(0.2);
			setD1(0.3);
		}
		else if(name=="B")
		{
			setA1(0.1);
			setB1(0.2);
			setC1(0.3);
			setD1(0.4);
		}
		else if(name=="BB&F")
		{
			setA1(0.2);
			setB1(0.3);
			setC1(0.4);
			setD1(0.5);
		}
		else if(name=="F")
		{
			setA1(0.3);
			setB1(0.4);
			setC1(0.5);
			setD1(0.6);
		}
		else if(name=="BF&G")
		{
			setA1(0.4);
			setB1(0.5);
			setC1(0.6);
			setD1(0.7);
		}
		else if(name=="G")
		{
			setA1(0.5);
			setB1(0.6);
			setC1(0.7);
			setD1(0.8);
		}
		else if(name=="BG&VG")
		{
			setA1(0.7);
			setB1(0.8);
			setC1(0.9);
			setD1(1);
		}
		else if(name=="VG")
		{
			setA1(0.8);
			setB1(0.9);
			setC1(1);
			setD1(1);
		}
	}
	
	public void setValueImportance(String name)
	{
		if(name=="Very Week")
		{
			setA(0);
			setB(0);
			setC(0);
			setD(0.3);
		}
		else if(name=="Week")
		{
			setA(0);
			setB(0.3);
			setC(0.3);
			setD(0.5);
		}
		else if(name=="Fair")
		{
			setA(0.2);
			setB(0.5);
			setC(0.5);
			setD(0.8);
		}
		else if(name=="Important")
		{
			setA(0.5);
			setB(0.7);
			setC(0.7);
			setD(1);
		}
		else if(name=="Very Important")
		{
			setA(0.7);
			setB(1);
			setC(1);
			setD(1);
		}
	}
	
	public Double low(Double value)
	{
		double minValue = 0;
		
		if(value<aMin)
		{
			minValue=0;
		}
		else if(value>aMin && value<cMin ||value==cMin)
		{
			minValue=1;
		}
		else if(value>cMin && value <dMin)
		{
			minValue = (dMin-value)/(dMin-cMin);
		}
		else if(value==dMin ||value > dMin)
		{
			minValue=0;
		}
		
		return minValue;
	}
	
	//fungsi fuzzy keanggotaan mid
	public Double mid(Double value)
	{
		double midValue = 0;
		
		if(value<aMin||value==aMin)
		{
			midValue=0;
		}
		else if(value>aMin && value<bMin)
		{
			midValue = (value-aMin)/(bMin-aMin);
		}
		else if((value>bMin && value<cMin)||value==bMin ||value==cMin)
		{
			midValue = 1;
		}
		else if(value>cMin && value <dMin)
		{
			midValue = (dMin-value)/(dMin-cMin);
		}
		else if(value==dMin ||value > dMin)
		{
			midValue=0;
		}
		
		return midValue;
	}
	
	//fungsi fuzzy keanggotaan high
	public Double high(Double value)
	{
		double HighValue = 0;
		
		if(value<aMin || value==aMin)
		{
			HighValue=0;
		}
		else if(value>aMin && value<bMin)
		{
			HighValue = (value-aMin)/(bMin-aMin);
		}
		else if(value==bMin ||value > bMin)
		{
			HighValue=1;
		}
		
		return HighValue;
	}
	
	public void countWeight(int pakar, String[][] input)
	{
		tableImportance = new Object[criteria.length][4];
		inputpakar = new String[input.length][10];
		inputpakar = input;
		pakars=pakar;
		for(int i=0;i<input[0].length;i++)
		{
			double tempA=0;
			double tempB=0;
			double tempC=0;
			double tempD=0;
			for(int j=0;j<input.length;j++)
			{
				setValueImportance(input[j][i]);
				tempA+=getA();
				tempB+=getB();
				tempC+=getC();
				tempD+=getD();
			}
			tableImportance[i][0] = tempA/pakar;
			tableImportance[i][1] = tempB/pakar;
			tableImportance[i][2] = tempC/pakar;
			tableImportance[i][3] = tempD/pakar;
		}
		
		for(int i=0;i<tableImportance.length;i++)
		{
			System.out.println("lower: "+tableImportance[i][0]+" -- middle1: "+tableImportance[i][1]+" -- middle2: "+tableImportance[i][2]+" -- upper: "+tableImportance[i][3]);
		}
		
	}
	
	public void countWeight3(int index, Object[] member)
	{
		tableAtribut = new Object[criteria.length][pakars];
		tableViolation = new Object[criteria.length][4];
		for(int i=0;i<member.length;i++)
		{
			double temp1=0;
			setValueViolation((String)tableMember[index][i]);
			if(tableMember[index][i]!="null")
			{
				temp1=(getA1()+getB1()+getC1()+getD1())/4;
				for(int j=0;j<pakars;j++)
				{
					double temp2=0;
					setValueImportance(inputpakar[j][i]);
					temp2 = (getA()+getB()+getC()+getD())/4;
					tableAtribut[i][j] = (temp1+temp2)/2;
				}
			}
			else if(tableMember[index][i]=="null")
			{
				for(int k=0;k<pakars;k++)
				tableAtribut[i][k]=temp1;
			}
		}
		membership2();
		
		for(int i=0;i<member.length;i++)
		{
			tableMember2[i][pakars] = member[i];
		}
		
		for(int i=0;i<tableMember2.length;i++)
		{
			double tempA=0;
			double tempB=0;
			double tempC=0;
			double tempD=0;
			for(int j=0;j<pakars+1;j++)
			{
				if(tableMember2[i][j]!="null")
				{
					setValueViolation((String)tableMember2[i][j]);
					tempA+=getA1();
					tempB+=getB1();
					tempC+=getC1();
					tempD+=getD1();
				}
			}
			tableViolation[i][0] = tempA/(pakars+1);
			tableViolation[i][1] = tempB/(pakars+1);
			tableViolation[i][2] = tempC/(pakars+1);
			tableViolation[i][3] = tempD/(pakars+1);
		}
		
		countRating(index);
	}
	
	public void countWeight2(int index, Object[] member)
	{
		tableViolation = new Object[criteria.length][4];
		Double[] divider = new Double[4];
		setValueImportance("Fair");
		divider[0] = getA();
		divider[1] = getB();
		divider[2] = getC();
		divider[3] = getD();
		
		for(int i=0;i<member.length;i++)
		{
			double tempA=0;
			double tempB=0;
			double tempC=0;
			double tempD=0;
			
			setValueViolation((String)tableMember[index][i]);
			if(tableMember[index][i]!="null")
			{
				for(int j=0;j<pakars;j++)
				{
					
					setValueImportance(inputpakar[j][i]);
					
					tempA+=(getA()*getA1())/divider[0];
					tempB+=(getB()*getB1())/divider[1];
					tempC+=(getC()*getC1())/divider[2];
					tempD+=(getD()*getD1())/divider[3];
				}
				tempA+=getA1();
				tempB+=getB1();
				tempC+=getC1();
				tempD+=getD1();
			}
			else if(tableMember[index][i]=="null")
			{
				tempB=0;
				tempB=0;
				tempB=0;
				tempB=0;
				
			}
			tableViolation[i][0] = tempA/(pakars+1);
			tableViolation[i][1] = tempB/(pakars+1);
			tableViolation[i][2] = tempC/(pakars+1);
			tableViolation[i][3] = tempD/(pakars+1);
		}
	
		countRating(index);
	}
	
	
	public void countRating(int index)
	{
		double tempA=0;
		double tempB=0;
		double tempC=0;
		double tempD=0;
		for(int i=0;i<criteria.length;i++)
		{
			tempA+=(Double)tableImportance[i][0]*(Double)tableViolation[i][0];
			tempB+=(Double)tableImportance[i][1]*(Double)tableViolation[i][1];
			tempC+=(Double)tableImportance[i][2]*(Double)tableViolation[i][2];
			tempD+=(Double)tableImportance[i][3]*(Double)tableViolation[i][3];
		}
		fraud[index] = (tempA/10)+(tempB/10)+(tempC/10)+(tempD/10);
	}
	
	public void countFraud()
	{
		fraud = new Object[tableMember.length];
		for(int i=0;i<tableMember.length;i++)
		{
			countWeight3(i,tableMember[i]);
			System.out.println("Fraud Case: "+i+" adalah: "+fraud[i]);
		}
		
	}
	
	public void countProb(Object[][] data, String[] columname, int seq,int dec, int total)
	{
		tablePercent = new Object[data.length][columname.length-1];
		
		//isi tabel dengan nilai persentase
		for(int i=0;i<data.length;i++)
		{
			for(int j=0;j<columname.length-1;j++)
			{
				//tableDummy[i][j] = cp.countPercen((Double)ffa.tableContent[i][j], ffa.tableModel.getColumnName(j));
				String str = data[i][j].toString(); 
				String str2 = columname[j];
				double d = Double.valueOf(str).doubleValue();
				
				double percen = cp.countProb(d, str2,seq,dec,total);
				
				tablePercent[i][j] =new Double(percen);
			}
		}
	}
	
	public void membership()
	{
		tableMember = new Object[tablePercent.length][criteria.length];
		for(int i=0;i<tablePercent.length;i++)
		{
			for(int k=0;k<criteria.length;k++)
			{
				double mini = 0;
				String temp = "null";
				for(int j=0;j<membership.length;j++)
				{
					double value=0;
					if(membership[j]=="VB")
					{
						setaMin(0);
						setbMin(0);
						setcMin(0.1);
						setdMin(0.2);
						value = low((Double)tablePercent[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="BVB&B")
					{
						setaMin(0);
						setbMin(0.1);
						setcMin(0.2);
						setdMin(0.3);
						value = mid((Double)tablePercent[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="B")
					{
						setaMin(0.1);
						setbMin(0.2);
						setcMin(0.3);
						setdMin(0.4);
						value = mid((Double)tablePercent[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="BB&F")
					{
						setaMin(0.2);
						setbMin(0.3);
						setcMin(0.4);
						setdMin(0.5);
						value = mid((Double)tablePercent[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="F")
					{
						setaMin(0.3);
						setbMin(0.4);
						setcMin(0.5);
						setdMin(0.6);
						value = mid((Double)tablePercent[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="BF&G")
					{
						setaMin(0.4);
						setbMin(0.5);
						setcMin(0.6);
						setdMin(0.7);
						value = mid((Double)tablePercent[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="G")
					{
						setaMin(0.5);
						setbMin(0.6);
						setcMin(0.7);
						setdMin(0.8);
						value = mid((Double)tablePercent[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="BG&VG")
					{
						setaMin(0.7);
						setbMin(0.8);
						setcMin(0.9);
						setdMin(1);
						value = mid((Double)tablePercent[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="VG")
					{
						setaMin(0.8);
						setbMin(0.9);
						setcMin(1);
						setdMin(1);
						value = high((Double)tablePercent[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
				}
				tableMember[i][k]=temp;
			}
			
		}
		for(int i=0;i<tableMember.length;i++)
		{
			//System.out.println("C1: "+tableMember[i][0]+" -- C2: "+tableMember[i][1]+" -- C3: "+tableMember[i][2]+" -- C4: "+tableMember[i][3]+" -- C5: "+tableMember[i][4]+" -- C6: "+tableMember[i][5]+" -- C7: "+tableMember[i][6]+" -- C8: "+tableMember[i][7]+" -- C9: "+tableMember[i][8]+" -- C10: "+tableMember[i][9]);
			//System.out.println("C1: "+tableMember[i][0]+" -- middle1: "+tableMember[i][1]+" -- middle2: "+tableMember[i][2]+" -- upper: "+tableMember[i][3]);
		}
	}
	
	public void membership2()
	{
		tableMember2 = new Object[criteria.length][pakars+1];
		for(int i=0;i<criteria.length;i++)
		{
			for(int k=0;k<pakars;k++)
			{
				double mini = 0;
				String temp = "null";
				for(int j=0;j<membership.length;j++)
				{
					double value=0;
					if(membership[j]=="VB")
					{
						setaMin(0);
						setbMin(0);
						setcMin(0.1);
						setdMin(0.2);
						value = low((Double)tableAtribut[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="BVB&B")
					{
						setaMin(0);
						setbMin(0.1);
						setcMin(0.2);
						setdMin(0.3);
						value = mid((Double)tableAtribut[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="B")
					{
						setaMin(0.1);
						setbMin(0.2);
						setcMin(0.3);
						setdMin(0.4);
						value = mid((Double)tableAtribut[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="BB&F")
					{
						setaMin(0.2);
						setbMin(0.3);
						setcMin(0.4);
						setdMin(0.5);
						value = mid((Double)tableAtribut[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="F")
					{
						setaMin(0.3);
						setbMin(0.4);
						setcMin(0.5);
						setdMin(0.6);
						value = mid((Double)tableAtribut[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="BF&G")
					{
						setaMin(0.4);
						setbMin(0.5);
						setcMin(0.6);
						setdMin(0.7);
						value = mid((Double)tableAtribut[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="G")
					{
						setaMin(0.5);
						setbMin(0.6);
						setcMin(0.7);
						setdMin(0.8);
						value = mid((Double)tableAtribut[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="BG&VG")
					{
						setaMin(0.7);
						setbMin(0.8);
						setcMin(0.9);
						setdMin(1);
						value = mid((Double)tableAtribut[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
					else if(membership[j]=="VG")
					{
						setaMin(0.8);
						setbMin(0.9);
						setcMin(1);
						setdMin(1);
						value = high((Double)tableAtribut[i][k]);
						if(value>mini)
						{
							mini=value;
							temp=membership[j];
						}
					}
				}
				tableMember2[i][k]=temp;
			}
			
		}
		for(int i=0;i<tableMember2.length;i++)
		{
			//System.out.println("C1: "+tableMember[i][0]+" -- C2: "+tableMember[i][1]+" -- C3: "+tableMember[i][2]+" -- C4: "+tableMember[i][3]+" -- C5: "+tableMember[i][4]+" -- C6: "+tableMember[i][5]+" -- C7: "+tableMember[i][6]+" -- C8: "+tableMember[i][7]+" -- C9: "+tableMember[i][8]+" -- C10: "+tableMember[i][9]);
			//System.out.println("C1: "+tableMember[i][0]+" -- middle1: "+tableMember[i][1]+" -- middle2: "+tableMember[i][2]+" -- upper: "+tableMember[i][3]);
		}
	}
	
}
