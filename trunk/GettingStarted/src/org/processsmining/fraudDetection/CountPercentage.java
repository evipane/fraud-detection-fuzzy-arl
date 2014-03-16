package org.processsmining.fraudDetection;
import org.processmining.fraud.model.fraud;


public class CountPercentage {
	
	private Double percentage;
	fraud fr = new fraud();
	
	//controller untuk menghitung persentase kesalahan
	public Double countPercen(Double value, String name)
	{
		
		if(name=="SkipS"||name=="wDutySec")
		{
			this.percentage = (value/fr.getMaxSkipSeq())*100;
		}
		else if(name=="SkipD"||name=="wDutyDec"||name=="wDecision")
		{
			this.percentage = (value/fr.getMaxSkipDec())*100;
		}
		else if(name=="Tmin"||name=="Tmax"||name=="wDutyCom"||name=="wPattern"||name=="wResource")
		{
			this.percentage = (value/fr.getMaxTmax())*100;
		}
		else this.percentage=value;
		return this.percentage;
	}

}
