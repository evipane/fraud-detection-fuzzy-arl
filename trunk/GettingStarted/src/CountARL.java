
public class CountARL {
	
	Fuzzy fuzzy = new Fuzzy();
	public Object[] tableSupport;
	public String[] columnsNameS;
	public void countSupport()
	{
		fuzzy.FuzzyTabel();
		
		for(int i=0;i<fuzzy.columnsName2.length;i++)
		{
			double supp=0;
			for(int j=0;j<fuzzy.tableFuzzy.length;j++)
			{
				supp+=(Double)fuzzy.tableFuzzy[j][i];
				
			}
			tableSupport[i] = supp;
		}
	}
	
	public void selection(double param)
	{
		for(int i=0;i<tableSupport.length;i++)
		{
			
		}
	}

}
