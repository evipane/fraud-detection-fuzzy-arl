import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.fraud.model.InsertFraudData;


public class readPNML {
	
	public String[] columname = {"Transition","Jumlah"};
	public String[] columname2 = {"Decision","Sequence"};
	public String[] columname3 = {"Transition","Role","Resource","Time"};
	public String[] columname4 = {"Case","SkipS","SkipD","Tmin","Tmax","WResource","WdutyS","WdutyD","WdutyC","WPattern","Wdecision","Fraud"};
	public Object[][] tableTransition;
	public Object[][] tableTransition2;
	public Object[][] tableTransition3;
	public Object[][] tableTransition4;
	
	public DefaultTableModel tableModelTransition ;
	public DefaultTableModel tableModelTransition2 ;
	@Plugin(
			name="Read PNML File",
			parameterLabels = {},
			returnLabels ={"Decision Event"},
			returnTypes = {JPanel.class},
			userAccessible = true
			)
	@UITopiaVariant(
			affiliation = "Fernandes Sinaga",
			author = "Fernandes Sinaga",
			email = "nandes.02@gmail.com"
			)
	public JPanel ModelTabel(final UIPluginContext context, InsertFraudData fraud)
	{
		JPanel panel = new JPanel();
		System.out.println("Size: "+fraud.frauds.size());
		tableTransition = new Object[fraud.frauds.size()][columname4.length];
		for(int i=0;i<tableTransition.length;i++)
		{
			tableTransition[i][0] = fraud.frauds.get(i).getCase();
			tableTransition[i][1] = fraud.frauds.get(i).getSkipSeq();
			tableTransition[i][2] = fraud.frauds.get(i).getSkipDec();
			tableTransition[i][3] = fraud.frauds.get(i).getTmin();
			tableTransition[i][4] = fraud.frauds.get(i).getTmax();
			tableTransition[i][5] = fraud.frauds.get(i).getWResource();
			tableTransition[i][6] = fraud.frauds.get(i).getWDutySeq();
			tableTransition[i][7] = fraud.frauds.get(i).getWDutyDec();
			tableTransition[i][8] = fraud.frauds.get(i).getWDutyCom();
			tableTransition[i][9] = fraud.frauds.get(i).getwPattern();
			tableTransition[i][10] = fraud.frauds.get(i).getwDecision();
			tableTransition[i][11] = fraud.frauds.get(i).getFraud();
			
		}
		Object[][] table = new Object[fraud.frauds.size()][];
		tableModelTransition = new DefaultTableModel(table,columname4);
		
		for(int i=0;i<tableTransition.length;i++)
		{
			for(int j=0;j<tableModelTransition.getColumnCount();j++)
			{
				tableModelTransition.setValueAt(tableTransition[i][j], i, j);
			}
			
		}
		/*System.out.println("label: "+net.getLabel());
		tableTransition = new Object[net.getTransitions().size()][columname.length];
		Object[][] table = new Object[net.getTransitions().size()][];
		tableModelTransition = new DefaultTableModel(table,columname2);
		int c=0;
		int temp;
		boolean flag=false;
		for(Transition t : net.getTransitions())
		{
			flag=false;
			if(tableTransition.length==0)
			{
				tableTransition[c][0] = t.getLabel();
				tableTransition[c][1] = 1;
				c++;
			}
			else
			{
				for(int i=0;i<tableTransition.length;i++)
				{
					if(t.getLabel().equals(tableTransition[i][0]))
					{
						System.out.println("masuk sini");
						temp = (Integer) tableTransition[i][1];
						tableTransition[i][1]=temp+1;
						flag=true;
					}
				}
			}
			if(flag==false)
			{
				tableTransition[c][0] = t.getLabel();
				tableTransition[c][1] = 1;
				c++;
			}
		}
		
		tableTransition2 = new Object[c][columname2.length];
		tableTransition3 = new Object[c][columname2.length];
		c=0;
		int d=0;
		for(int i=0;i<tableTransition2.length;i++)
		{
			String temp1 = (String)tableTransition[i][0];
			String[] str = temp1.split(" ");
			if((Integer)tableTransition[i][1]>1 && str[1].equals("Complete"))
			{
				System.out.println("masuk sini donk");
				tableTransition2[c][0]=str[0];
				c++;
			}
			else if((Integer)tableTransition[i][1]<2 && str[1].equals("Complete"))
			{
				tableTransition3[d][0]=str[0];
				d++;
			}
		}
		
		System.out.println("table3: "+d);
		
		for(int i=0;i<tableTransition2.length;i++)
		{
			tableModelTransition.setValueAt(tableTransition2[i][0], i, 0);
		}
		
		for(int i=0;i<tableTransition3.length;i++)
		{
			tableModelTransition.setValueAt(tableTransition3[i][0], i, 1);
		}
				
		ProMTable Ptabel = new ProMTable(tableModelTransition);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panel.add(Ptabel);
		context.showConfiguration("Tabel Transisi",panel);
		*/
		
		ProMTable Ptabel = new ProMTable(tableModelTransition);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panel.add(Ptabel);
		context.showConfiguration("Tabel Data Fraud",panel);
		return panel;
	}
	

}
