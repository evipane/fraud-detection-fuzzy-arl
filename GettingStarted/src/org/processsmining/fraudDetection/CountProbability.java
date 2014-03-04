package org.processsmining.fraudDetection;
import org.processmining.fraud.model.fraud;


public class CountProbability {
	
	private Double percentage;
	fraud fr = new fraud();
	
	//controller untuk menghitung persentase kesalahan
	public Double countProb(Double value, String name)
	{
		
		if(name=="SkipS"||name=="wDutySec")
		{
			this.percentage = (value/fr.getMaxSkipSeq());
		}
		else if(name=="SkipD"||name=="wDutyDec"||name=="wDecision"||name=="wDutyCom")
		{
			this.percentage = (value/fr.getMaxSkipDec());
		}
		else if(name=="Tmin"||name=="Tmax"||name=="wPattern"||name=="wResource")
		{
			this.percentage = (value/fr.getMaxTmax());
		}
		else this.percentage=value;
		return this.percentage;
	}

}
