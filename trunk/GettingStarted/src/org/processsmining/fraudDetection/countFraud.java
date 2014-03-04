package org.processsmining.fraudDetection;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.processmining.framework.util.ui.widgets.ProMTable;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class countFraud {
	
	newFuzzy nf = new newFuzzy();
	FraudwithFuzzyARL2 ffa = new FraudwithFuzzyARL2();
	fuzzyAttribute fa = new fuzzyAttribute();
	fuzzyAttribute fa2 = new fuzzyAttribute();
	public String[] columnsName1 = {"SkipS","SkipD","Tmin","Tmax","wResource","wDutySec","wDutyDec","wDutyCom","wPattern","wDecision"};
	public String[] columnCocok = {"Index 1","Index 2","Index 3","Index 4"};
	public Object[][] tableDummy;
	Object[][] tabel2 = new Object[25][];
	Object[][] tabel3 = new Object[25][];
	public String cmbType ;
	public Object[][] tableKecocokan;
	public DefaultTableModel tableModelFraud = new DefaultTableModel(tabel2,ffa.columnsName);
	public DefaultTableModel tableModel = new DefaultTableModel(tabel3,columnsName1);
	public DefaultTableModel tableModelKecocokan = new DefaultTableModel(tabel3,columnCocok);
	public DefaultTableModel tableModelbobotFraud = new DefaultTableModel(tabel3,ffa.columnsName);
	public JTextField jTextField1 = new javax.swing.JTextField();
	public JComboBox jComboBox1 = new javax.swing.JComboBox();
    public JComboBox jComboBox2 = new javax.swing.JComboBox();
    public JComboBox jComboBox3 = new javax.swing.JComboBox();
    public JComboBox jComboBox4 = new javax.swing.JComboBox();
    public JComboBox jComboBox5 = new javax.swing.JComboBox();
    public JComboBox jComboBox6 = new javax.swing.JComboBox();
    public JComboBox jComboBox7 = new javax.swing.JComboBox();
    public JComboBox jComboBox8 = new javax.swing.JComboBox();
    public JComboBox jComboBox9 = new javax.swing.JComboBox();
    public JComboBox jComboBox10 = new javax.swing.JComboBox();
	
	
	public JPanel TabelFuzzyMADM()
	{
		JPanel panel = new JPanel();
		
		tableDummy = new Object[ffa.tableContent.length][ffa.columnsName.length];
		nf.FuzzyTabel2();
		
		for(int i=0;i<ffa.tableContent.length;i++)
		{
			int j=0;
				
			double max=0;
			String result="null";
			//System.out.println("length: "+nf.columnsName2.length);
			for(int l=0;l<nf.columnsName2.length-1;l++)
			{
				
				//System.out.println("index: "+l);
				if(l%3==0)
				{
					//System.out.println("Masuk1");
					max= (Double) nf.tableFuzzy[i][l];
					result="low";
				}
				else if(l%3==1)
				{
					//System.out.println("Masuk2");
					if((Double) nf.tableFuzzy[i][l]>max || (Double) nf.tableFuzzy[i][l]==max)
					{
						max=(Double) nf.tableFuzzy[i][l];
						result = "medium";
					}
					
				}
				else if(l%3==2)
				{
					//System.out.println("Masuk3");
					if((Double) nf.tableFuzzy[i][l]>max || (Double) nf.tableFuzzy[i][l]==max)
					{
						max=(Double) nf.tableFuzzy[i][l];
						if(max==0)
						{
							result="null";
						}
						else
						{
							result="high";
						}
						
					}
					tableDummy[i][j] =result;
					j++;
					
				}
				
			}
		
		}
		
		//isi tabel percent ke tabel model
		for(int i=0;i<tableDummy.length;i++)
		{
			for(int j=0;j<tableModelFraud.getColumnCount();j++)
			{
				tableModelFraud.setValueAt(tableDummy[i][j], i, j);
			}
		}
		
		
		ProMTable Ptabel = new ProMTable(tableModelFraud);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panel.add(Ptabel);
		//context.showConfiguration("Tabel Fraud",panel2);
		
		return panel;
	}
	
	public JPanel InputJumlahPakar(final Integer[] jumlahPakar)
	{
		JPanel panel = new JPanel();
		
		JLabel jLabel1 = SlickerFactory.instance().createLabel("Jumlah Pakar: ");
        JButton jButton1 = SlickerFactory.instance().createButton("Input");

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel1)
                .addComponent(jButton1)
                .addGap(32, 32, 32)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(142, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        
        jButton1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				jumlahPakar[0] = Integer.parseInt(jTextField1.getText());
			}
		});
        
        panel.setLayout(layout);
		return panel;
	}
	
	
	public JPanel InputKepentingan(final String[] var)
	{
		JPanel panel3 = new JPanel();
		
		JLabel jLabel1 = new javax.swing.JLabel();
		JLabel jLabel2 = new javax.swing.JLabel();
		JLabel jLabel3 = new javax.swing.JLabel();
		JLabel jLabel4 = new javax.swing.JLabel();
		JLabel jLabel5 = new javax.swing.JLabel();
		JLabel jLabel6 = new javax.swing.JLabel();
		JLabel jLabel7 = new javax.swing.JLabel();
		JLabel jLabel8 = new javax.swing.JLabel();
		JLabel jLabel9 = new javax.swing.JLabel();
		JLabel jLabel10 = new javax.swing.JLabel();
        
        JLabel jLabel11 = new javax.swing.JLabel();

        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Skip Sequence");

        jLabel2.setText("Skip Decision");

        jLabel3.setText("Throughput Time Min");

        jLabel4.setText("Throughput Time Max");

        jLabel5.setText("Wrong Resource");

        jLabel6.setText("Wrong Duty Sequence");

        jLabel7.setText("Wrong Duty Decision");

        jLabel8.setText("Wrong Duty Combine");

        jLabel9.setText("Wrong Pattern");

        jLabel10.setText("Wrong Decision");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"", "Very Week", "Week", "Fair", "Important", "Very Important" }));
        jComboBox1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "","Very Week", "Week", "Fair", "Important", "Very Important" }));
        jComboBox2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"", "Very Week", "Week", "Fair", "Important", "Very Important" }));
        jComboBox3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"", "Very Week", "Week", "Fair", "Important", "Very Important" }));
        jComboBox4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"", "Very Week", "Week", "Fair", "Important", "Very Important" }));
        jComboBox5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "","Very Week", "Week", "Fair", "Important", "Very Important" }));
        jComboBox6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"", "Very Week", "Week", "Fair", "Important", "Very Important" }));
        jComboBox7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "","Very Week", "Week", "Fair", "Important", "Very Important" }));
        jComboBox8.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "","Very Week", "Week", "Fair", "Important", "Very Important" }));
        jComboBox9.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"", "Very Week", "Week", "Fair", "Important", "Very Important" }));
        jComboBox10.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel11.setText("Derajat Kepentingan");

        GroupLayout jPanel1Layout = new GroupLayout(panel3);
        panel3.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(48, 48, 48)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(139, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jComboBox10,javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jComboBox1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox jcmbType = (JComboBox) e.getSource();
				cmbType = (String) jcmbType.getSelectedItem();
				var[0] = cmbType;
			}
		});
        
        jComboBox6.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox jcmbType = (JComboBox) e.getSource();
				cmbType = (String) jcmbType.getSelectedItem();
				var[1] = cmbType;
			}
		});
        
        jComboBox7.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox jcmbType = (JComboBox) e.getSource();
				cmbType = (String) jcmbType.getSelectedItem();
				var[2] = cmbType;
			}
		});
        
        jComboBox5.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox jcmbType = (JComboBox) e.getSource();
				cmbType = (String) jcmbType.getSelectedItem();
				var[3] = cmbType;
			}
		});
        
        jComboBox4.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox jcmbType = (JComboBox) e.getSource();
				cmbType = (String) jcmbType.getSelectedItem();
				var[4] = cmbType;
			}
		});
        
        jComboBox3.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox jcmbType = (JComboBox) e.getSource();
				cmbType = (String) jcmbType.getSelectedItem();
				var[5] = cmbType;
			}
		});
        
        jComboBox2.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox jcmbType = (JComboBox) e.getSource();
				cmbType = (String) jcmbType.getSelectedItem();
				var[6] = cmbType;
			}
		});
        
        jComboBox8.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox jcmbType = (JComboBox) e.getSource();
				cmbType = (String) jcmbType.getSelectedItem();
				var[7] = cmbType;
			}
		});
        
        jComboBox9.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox jcmbType = (JComboBox) e.getSource();
				cmbType = (String) jcmbType.getSelectedItem();
				var[8] = cmbType;
			}
		});
        
        jComboBox10.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox jcmbType = (JComboBox) e.getSource();
				cmbType = (String) jcmbType.getSelectedItem();
				var[9] = cmbType;
			}
		});
        
        System.out.println("isi :"+var.length);
       
        
        
        panel3.setLayout(jPanel1Layout);
        
        return panel3;
	}
	
	public JPanel DerajatKepentingan(String[] var)
	{
		
		System.out.println("tes :"+jComboBox1.getSelectedItem());
		JPanel panelan = new JPanel();
		System.out.println("isi2 :"+var.length);
		for(int i=0;i<1;i++)
		{
			for(int j=0;j<var.length;j++)
			{
				tableModel.setValueAt(var[j], i, j);
			}
		}
		ProMTable Ptabel = new ProMTable(tableModel);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panelan.add(Ptabel);
		
		return panelan;
	}

	public JPanel TabelBobotFraud(String[] var)
	{
		JPanel panel9 = new JPanel();
		
		TabelFuzzyMADM();
		fa2.importance();
		tableKecocokan = new Object[tableDummy.length][columnCocok.length];
		for(int i=0;i<tableDummy.length;i++)
		{
			double i1 = 0;
			double i2 = 0;
			double i3 = 0;
			double i4 = 0;
			for(int j=0;j<ffa.columnsName.length-1;j++)
			{
				if(ffa.columnsName[j]=="SkipS" || ffa.columnsName[j]=="wDutySec")
				{
					fa.fuzzySeq();
					if(tableDummy[i][j]=="low")
					{
						System.out.println("Dummy: "+tableDummy[i][j]+"-- var: "+var[j]);
						fa.membership("low");
						if(var[j]=="Normal")
						{
							fa2.membership("low");
						}
						else if(var[j]=="Penting")
						{
							fa2.membership("medium");
						}
						else if(var[j]=="Sangat Penting")
						{
							fa2.membership("high");
						}
						System.out.println("a: "+fa.getA()+" -- b: "+fa.getB()+" -- c: "+fa.getC()+" -- d: "+fa.getD());
						System.out.println("a2: "+fa2.getA()+" -- b2: "+fa2.getB()+" -- c2: "+fa2.getC()+" -- d2: "+fa2.getD());
						i1 += fa.getA1()*fa2.getA1();
						i2 += fa.getB1()*fa2.getB1();
						i3 += fa.getC1()*fa2.getC1();
						i4 += fa.getD1()*fa2.getD1();
					}
					else if(tableDummy[i][j]=="medium")
					{
						fa.membership("medium");
						if(var[j]=="Normal")
						{
							fa2.membership("low");
						}
						else if(var[j]=="Penting")
						{
							fa2.membership("medium");
						}
						else if(var[j]=="Sangat Penting")
						{
							fa2.membership("high");
						}
						i1 += fa.getA1()*fa2.getA1();
						i2 += fa.getB1()*fa2.getB1();
						i3 += fa.getC1()*fa2.getC1();
						i4 += fa.getD1()*fa2.getD1();
					}
					else if(tableDummy[i][j]=="high")
					{
						fa.membership("high");
						if(var[j]=="Normal")
						{
							fa2.membership("low");
						}
						else if(var[j]=="Penting")
						{
							fa2.membership("medium");
						}
						else if(var[j]=="Sangat Penting")
						{
							fa2.membership("high");
						}
						i1 += fa.getA1()*fa2.getA1();
						i2 += fa.getB1()*fa2.getB1();
						i3 += fa.getC1()*fa2.getC1();
						i4 += fa.getD1()*fa2.getD1();
					}
				}
				else if(ffa.columnsName[j]=="SkipD" || ffa.columnsName[j]=="wDutyDec"||ffa.columnsName[j]=="wDutyCom" || ffa.columnsName[j]=="wDecision")
				{
					fa.fuzzyDec();
					if(tableDummy[i][j]=="low")
					{
						fa.membership("low");
						if(var[j]=="Normal")
						{
							fa2.membership("low");
						}
						else if(var[j]=="Penting")
						{
							fa2.membership("medium");
						}
						else if(var[j]=="Sangat Penting")
						{
							fa2.membership("high");
						}
						i1 += fa.getA1()*fa2.getA1();
						i2 += fa.getB1()*fa2.getB1();
						i3 += fa.getC1()*fa2.getC1();
						i4 += fa.getD1()*fa2.getD1();
					}
					else if(tableDummy[i][j]=="medium")
					{
						fa.membership("medium");
						if(var[j]=="Normal")
						{
							fa2.membership("low");
						}
						else if(var[j]=="Penting")
						{
							fa2.membership("medium");
						}
						else if(var[j]=="Sangat Penting")
						{
							fa2.membership("high");
						}
						i1 += fa.getA1()*fa2.getA1();
						i2 += fa.getB1()*fa2.getB1();
						i3 += fa.getC1()*fa2.getC1();
						i4 += fa.getD1()*fa2.getD1();
					}
					else if(tableDummy[i][j]=="high")
					{
						fa.membership("high");
						if(var[j]=="Normal")
						{
							fa2.membership("low");
						}
						else if(var[j]=="Penting")
						{
							fa2.membership("medium");
						}
						else if(var[j]=="Sangat Penting")
						{
							fa2.membership("high");
						}
						i1 += fa.getA1()*fa2.getA1();
						i2 += fa.getB1()*fa2.getB1();
						i3 += fa.getC1()*fa2.getC1();
						i4 += fa.getD1()*fa2.getD1();
					}
				}
				else if(ffa.columnsName[j]=="Tmin" || ffa.columnsName[j]=="Tmax"||ffa.columnsName[j]=="wResource" || ffa.columnsName[j]=="wPattern")
				{
					fa.fuzzyAll();
					if(tableDummy[i][j]=="low")
					{
						fa.membership("low");
						if(var[j]=="Normal")
						{
							fa2.membership("low");
						}
						else if(var[j]=="Penting")
						{
							fa2.membership("medium");
						}
						else if(var[j]=="Sangat Penting")
						{
							fa2.membership("high");
						}
						i1 += fa.getA1()*fa2.getA1();
						i2 += fa.getB1()*fa2.getB1();
						i3 += fa.getC1()*fa2.getC1();
						i4 += fa.getD1()*fa2.getD1();
					}
					else if(tableDummy[i][j]=="medium")
					{
						fa.membership("medium");
						if(var[j]=="Normal")
						{
							fa2.membership("low");
						}
						else if(var[j]=="Penting")
						{
							fa2.membership("medium");
						}
						else if(var[j]=="Sangat Penting")
						{
							fa2.membership("high");
						}
						i1 += fa.getA1()*fa2.getA1();
						i2 += fa.getB1()*fa2.getB1();
						i3 += fa.getC1()*fa2.getC1();
						i4 += fa.getD1()*fa2.getD1();
					}
					else if(tableDummy[i][j]=="high")
					{
						fa.membership("high");
						if(var[j]=="Normal")
						{
							fa2.membership("low");
						}
						else if(var[j]=="Penting")
						{
							fa2.membership("medium");
						}
						else if(var[j]=="Sangat Penting")
						{
							fa2.membership("high");
						}
						i1 += fa.getA1()*fa2.getA1();
						i2 += fa.getB1()*fa2.getB1();
						i3 += fa.getC1()*fa2.getC1();
						i4 += fa.getD1()*fa2.getD1();
					}
				}
				
			}
			tableKecocokan[i][0] = i1/columnsName1.length;
			tableKecocokan[i][1] = i2/columnsName1.length;
			tableKecocokan[i][2] = i3/columnsName1.length;
			tableKecocokan[i][3] = i4/columnsName1.length;
		}
		for(int i=0;i<tableKecocokan.length;i++)
		{
			for(int j=0;j<columnCocok.length;j++)
			{
				tableModelKecocokan.setValueAt(tableKecocokan[i][j], i, j);
			}
		}
		ProMTable Ptabel = new ProMTable(tableModelKecocokan);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panel9.add(Ptabel);
		return panel9;
	}
	
	public JPanel HasilBobotFraud(String[] var)
	{
		JPanel panels = new JPanel();
		TabelBobotFraud(var);
		
		for(int i=0;i<tableDummy.length;i++)
		{
			tableDummy[i][ffa.columnsName.length-1] = (((Double)tableKecocokan[i][3]+(Double)tableKecocokan[i][2]+(Double)tableKecocokan[i][1])/2)*100;
		}
		
		for(int i=0;i<tableDummy.length;i++)
		{
			for(int j=0;j<ffa.columnsName.length;j++)
			{
				tableModelbobotFraud.setValueAt(tableDummy[i][j], i, j);
			}
		}
		ProMTable Ptabel = new ProMTable(tableModelbobotFraud);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panels.add(Ptabel);
		
		return panels;
	}
	
}
