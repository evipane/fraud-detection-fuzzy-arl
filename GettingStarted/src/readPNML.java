import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;


public class readPNML {
	
	public String[] columname = {"Transition","Jumlah"};
	public String[] columname2 = {"Decision","Sequence"};
	public Object[][] tableTransition;
	public Object[][] tableTransition2;
	public Object[][] tableTransition3;
	public DefaultTableModel tableModelTransition ;
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
	public JPanel ModelTabel(final UIPluginContext context, PetrinetGraph net)
	{
		JPanel panel = new JPanel();
		
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
		
		return panel;
	}

}
