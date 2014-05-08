package org.processsmining.fraudDetection;
import org.processmining.fraud.model.fraud;


public class CountProbability {
	
	private Double percentage;
	fraud fr = new fraud();
	
	//controller untuk menghitung persentase kesalahan
	public Double countProb(Double value, String name, int seq,int dec, int total)
	{
		
		fr.setMaxSkipSeq(seq);
		fr.setMaxSkipDec(dec);
		fr.setMaxTmax(total);
		
		if(name=="SkipS"||name=="wDutySec")
		{
			System.out.println("maxSeq :"+fr.getMaxSkipSeq());
			this.percentage = (value/fr.getMaxSkipSeq());
		}
		else if(name=="SkipD"||name=="wDutyDec"||name=="wDecision"||name=="wDutyCom")
		{
			System.out.println("maxDec :"+fr.getMaxSkipDec());
			this.percentage = (value/fr.getMaxSkipDec());
		}
		else if(name=="Tmin"||name=="Tmax"||name=="wPattern"||name=="wResource")
		{
			System.out.println("maxTMax :"+fr.getMaxTmax());
			this.percentage = (value/fr.getMaxTmax());
		}
		else this.percentage=value;
		return this.percentage;
	}

}
