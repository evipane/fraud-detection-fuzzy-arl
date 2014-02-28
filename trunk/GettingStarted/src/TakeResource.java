import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.pnml.controller.ReadPNML;


public class TakeResource {
	
	public String[] columname = {"Transition","Role","Resource","Time"};
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
			if(i==0)
			{
				tableTransition[c][0] = str[0];
				tableTransition[c][1] = pnml.transitions.get(i).getRole();
				tableTransition[c][2] = pnml.transitions.get(i).getResource();
				tableTransition[c][3] = pnml.transitions.get(i).getTime();	
				continue;
			}
			else
			{
				for(int j=0;j<tableTransition.length;j++)
				{
					if(str[0].equals(tableTransition[j][0]))
					{
						flag=true;
					}
				}
			}
			if(flag==false)
			{
				c++;
				tableTransition[c][0] = str[0];
				tableTransition[c][1] = pnml.transitions.get(i).getRole();
				tableTransition[c][2] = pnml.transitions.get(i).getResource();
				tableTransition[c][3] = pnml.transitions.get(i).getTime();	
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
