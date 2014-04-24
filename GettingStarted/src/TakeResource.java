import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.pnml.controller.ReadPNML;


public class TakeResource {
	
	public String[] columname = {"Transition","Role","Resource","Time","Sebelum","Status"};
	public Object[][] tableTransition;
	
	public DefaultTableModel tableModelTransition2 ;
	@Plugin(
			name="Take Resource",
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
	public JPanel ModelTabel(final UIPluginContext context, ReadPNML pnml)
	{
		JPanel panel = new JPanel();
		tableTransition = new Object[pnml.transitions.size()][columname.length];
		
		Object[][] table = new Object[pnml.transitions.size()][];
		int c=0;
		for(int i=0;i<pnml.transitions.size();i++)
		{
			boolean flag=false;
			String [] str = pnml.transitions.get(i).getName().split(" ");
			if(str[1].equals("Start"))
			{
				tableTransition[c][0] = str[0];
				tableTransition[c][1] = pnml.transitions.get(i).getRole();
				tableTransition[c][2] = pnml.transitions.get(i).getResource();
				tableTransition[c][3] = pnml.transitions.get(i).getTime(); 
				tableTransition[c][4] = pnml.transitions.get(i).getSebelum();
				tableTransition[c][5] =str[1];
				c++;
			}
				
			if(i>0)
			{
				for(int j=0;j<tableTransition.length;j++)
				{
						//System.out.println("str1: "+str[1]);
					if(str[0].equals(tableTransition[j][0]) && str[1].equals(tableTransition[c][5]))
					{
						System.out.println("trans: "+tableTransition[j][0]+" -- str1: "+str[1]);
						tableTransition[j][4] = tableTransition[j][4]+"-"+pnml.transitions.get(i).getSebelum();
						
					}
					
				}
			}
			
					
		}
		
		tableModelTransition2 = new DefaultTableModel(table,columname);
		
		for(int i=0;i<tableTransition.length;i++)
		{
			for(int j=0;j<columname.length;j++)
			{
				tableModelTransition2.setValueAt(tableTransition[i][j], i, j);
			}
		}
		
		ProMTable Ptabel = new ProMTable(tableModelTransition2);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panel.add(Ptabel);
		context.showConfiguration("Tabel Transisi",panel);
		
		return panel;
	}
}
