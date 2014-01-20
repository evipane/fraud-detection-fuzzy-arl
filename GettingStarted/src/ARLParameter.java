import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.fluxicon.slickerbox.factory.SlickerFactory;


public class ARLParameter {
	
	
	//UI untuk memasukkan parameter ARL
	public double parameter;
	
	public JPanel ARLParam()
	{
		JPanel panel4 = new JPanel();
		
		final JLabel label1 = SlickerFactory.instance().createLabel("Support Threshold (0 - 1)");
		final JTextField inputParam = new JTextField("0.15");
		
        

        GroupLayout layout = new GroupLayout(panel4);
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label1)
                    .addComponent(inputParam, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(269, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(label1)
                .addGap(18, 18, 18)
                .addComponent(inputParam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(216, Short.MAX_VALUE))
        );
        
        parameter = Double.parseDouble(inputParam.getText());

		return panel4;
	}

	public double getParameter() {
		return parameter;
	}

	public void setParameter(double parameter) {
		this.parameter = parameter;
	}
	
	

}
