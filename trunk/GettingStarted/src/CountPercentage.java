import org.processmining.fraud.model.fraud;


public class CountPercentage {
	
	private Double percentage;
	fraud fr = new fraud();
	
	//controller untuk menghitung persentase kesalahan
	public Double countPercen(Double value, String name)
	{
		System.out.println("Masuk Sini !!!");
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
		System.out.println("Persen: "+percentage);
		return this.percentage;
	}

}
