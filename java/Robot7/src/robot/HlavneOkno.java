package robot;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bluetooth.Bluetooth;

public class HlavneOkno {
	
	private Bluetooth bluetooth = Bluetooth.priprav();
	private JFrame frmOvladacRobota;
	private JTextField textVzdialenost;
	private DefaultListModel listModelSpravy;
	private JList listSpravy;
	private JScrollPane scrollPaneSpravy;
	private JCheckBox chckbxZastavRolovanie;
	private JCheckBox chckbxZastavPrimanie;
	private JSlider sliderCitlivostLightAssist;
	private JSlider sliderKedyZastat;
	private JSlider sliderRychlostMerania;
	private JCheckBox chckbxStretavacieSvetla;
	private JCheckBox chckbxDialkoveSvetla;
	private JCheckBox chckbxLightAssist;
	private JCheckBox chckbxDiagnostickeVypisy;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HlavneOkno window = new HlavneOkno();
					window.frmOvladacRobota.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public HlavneOkno() {
		initialize();
		Zobrazovac zobrazovac = new Zobrazovac(bluetooth, this);
	    Thread thread = new Thread(zobrazovac);
	    thread.setDaemon(true);
	    thread.start();
	}

	private void initialize() {
		frmOvladacRobota = new JFrame();
		frmOvladacRobota.setTitle("Ovl\u00E1da\u010D robota");
		frmOvladacRobota.setForeground(Color.WHITE);
		frmOvladacRobota.setBounds(100, 100, 1009, 650);
		frmOvladacRobota.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnNewButton = new JButton("STOP");
		btnNewButton.setMnemonic('q');
		btnNewButton.setToolTipText("zastavit robota");
		btnNewButton.setBounds(835, 501, 78, 44);
		btnNewButton.setBackground(Color.RED);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bluetooth.posli("E");
			}
		});
		frmOvladacRobota.getContentPane().setLayout(null);
		frmOvladacRobota.getContentPane().add(btnNewButton);
		
		JButton btnVl = new JButton("vl");
		btnVl.setMnemonic('a');
		btnVl.setToolTipText("vlavo");
		btnVl.setBounds(761, 501, 64, 44);
		btnVl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bluetooth.posli("B");
			}
		});
		btnVl.setBackground(Color.YELLOW);
		frmOvladacRobota.getContentPane().add(btnVl);
		
		JButton btnVp = new JButton("vp");
		btnVp.setMnemonic('d');
		btnVp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bluetooth.posli("C");
			}
		});
		btnVp.setToolTipText("vpravo");
		btnVp.setBounds(923, 501, 64, 44);
		btnVp.setBackground(Color.YELLOW);
		frmOvladacRobota.getContentPane().add(btnVp);
		
		JButton btnDz = new JButton("dz");
		btnDz.setMnemonic('s');
		btnDz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bluetooth.posli("D");
			}
		});
		btnDz.setToolTipText("dozadu");
		btnDz.setBounds(845, 556, 64, 44);
		btnDz.setBackground(Color.YELLOW);
		frmOvladacRobota.getContentPane().add(btnDz);
		
		JButton btnDp = new JButton("dp");
		btnDp.setMnemonic('w');
		btnDp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bluetooth.posli("A");
			}
		});
		btnDp.setToolTipText("dopredu");
		btnDp.setBounds(845, 446, 64, 44);
		btnDp.setBackground(Color.YELLOW);
		frmOvladacRobota.getContentPane().add(btnDp);
		
		textVzdialenost = new JTextField();
		textVzdialenost.setBounds(176, 577, 599, 23);
		textVzdialenost.setEditable(false);
		frmOvladacRobota.getContentPane().add(textVzdialenost);
		textVzdialenost.setColumns(10);
		
		JLabel lblVzdialenostOdPrekazky = new JLabel("Vzdialenos\u0165 od prek\u00E1\u017Eky:");
		lblVzdialenostOdPrekazky.setBounds(10, 578, 165, 21);
		frmOvladacRobota.getContentPane().add(lblVzdialenostOdPrekazky);
	    
	    final JRadioButton rdbtnR1 = new JRadioButton("najr\u00FDchlej\u0161ie");
	    rdbtnR1.setMnemonic('x');
	    rdbtnR1.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (rdbtnR1.isSelected()) {
	    			bluetooth.posli("H");
	    		}
	    	}
	    });
	    rdbtnR1.setToolTipText("zapnut rychlost \"najrychlejsie\"");
	    rdbtnR1.setBounds(10, 257, 150, 37);
	    frmOvladacRobota.getContentPane().add(rdbtnR1);
	    
	    final JRadioButton rdbtnR2 = new JRadioButton("ve\u013Emi r\u00FDchlo");
	    rdbtnR2.setMnemonic('c');
	    rdbtnR2.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (rdbtnR2.isSelected()) {
	    			bluetooth.posli("I");
	    			} else {
	    		}
	    	}
	    });
	    rdbtnR2.setToolTipText("zapn\u00FA\u0165 r\u00FDchlos\u0165 \"ve\u013Emi r\u00FDchlo\"");
	    rdbtnR2.setBounds(162, 257, 140, 37);
	    frmOvladacRobota.getContentPane().add(rdbtnR2);
	    
	    final JRadioButton rdbtnR3 = new JRadioButton("r\u00FDchlo");
	    rdbtnR3.setMnemonic('v');
	    rdbtnR3.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (rdbtnR3.isSelected()) {
	    			bluetooth.posli("J");
	    			} else {
	    		}
	    		
	    	}
	    });
	    rdbtnR3.setToolTipText("zapn\u00FA\u0165 r\u00FDchlos\u0165 \"r\u00FDchlo\"");
	    rdbtnR3.setBounds(304, 257, 119, 37);
	    frmOvladacRobota.getContentPane().add(rdbtnR3);
	    
	    final JRadioButton rdbtnR4 = new JRadioButton("viac ne\u017E stredne");
	    rdbtnR4.setMnemonic('b');
	    rdbtnR4.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (rdbtnR4.isSelected()) {
	    			bluetooth.posli("K");
	    			} else {
	    		}
	    	}
	    });
	    rdbtnR4.setToolTipText("zapn\u00FA\u0165 r\u00FDchlos\u0165 \"viac ne\u017E stredne r\u00FDchlo\"");
	    rdbtnR4.setBounds(425, 257, 165, 37);
	    frmOvladacRobota.getContentPane().add(rdbtnR4);
	    
	    final JRadioButton rdbtnR5 = new JRadioButton("stredne");
	    rdbtnR5.setMnemonic('n');
	    rdbtnR5.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (rdbtnR5.isSelected()) {
	    			bluetooth.posli("L");
	    			} else {
	    		}
	    	}
	    });
	    rdbtnR5.setToolTipText("zapnut rychlost \"stredne\"");
	    rdbtnR5.setBounds(10, 301, 126, 37);
	    frmOvladacRobota.getContentPane().add(rdbtnR5);
	    
	    final JRadioButton rdbtnR6 = new JRadioButton("menej ne\u017E stredne");
	    rdbtnR6.setMnemonic('m');
	    rdbtnR6.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (rdbtnR6.isSelected()) {
	    			bluetooth.posli("M");
	    			} else {
	    		}
	    	}
	    });
	    rdbtnR6.setToolTipText("zapn\u00FA\u0165 r\u00FDchlos\u0165 \"menej ne\u017E stredne\"");
	    rdbtnR6.setBounds(138, 301, 182, 37);
	    frmOvladacRobota.getContentPane().add(rdbtnR6);
	    
	    final JRadioButton rdbtnR7 = new JRadioButton("pomaly");
	    rdbtnR7.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (rdbtnR7.isSelected()) {
	    			bluetooth.posli("N");
	    			} else {
	    		}
	    	}
	    });
	    rdbtnR7.setToolTipText("zapn\u00FA\u0165 r\u00FDchlos\u0165 \"pomaly\"");
	    rdbtnR7.setBounds(322, 301, 119, 37);
	    frmOvladacRobota.getContentPane().add(rdbtnR7);
	    
	    final JRadioButton rdbtnR8 = new JRadioButton("najpomal\u0161ie");
	    rdbtnR8.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (rdbtnR8.isSelected()) {
	    			bluetooth.posli("O");
	    			} else {
	    		}
	    	}
	    });
	    rdbtnR8.setToolTipText("zapn\u00FA\u0165 r\u00FDchlos\u0165 \"najpomal\u0161ie\"");
	    rdbtnR8.setBounds(443, 297, 147, 37);
	    frmOvladacRobota.getContentPane().add(rdbtnR8);
	    
	    final JRadioButton rdbtnAcp = new JRadioButton("Vyh\u00FDbanie sa prek\u00E1\u017Ekam doprava");
	    rdbtnAcp.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (rdbtnAcp.isSelected()) {
	    			bluetooth.posli("P");
	    			} else {
	    		}
	    	}
	    });
	    rdbtnAcp.setToolTipText("");
	    rdbtnAcp.setBounds(18, 404, 416, 23);
	    frmOvladacRobota.getContentPane().add(rdbtnAcp);
	    
	    final JRadioButton rdbtnAcl = new JRadioButton("Vyh\u00FDbanie sa prek\u00E1\u017Ekam do\u013Eava");
	    rdbtnAcl.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (rdbtnAcl.isSelected()) {
	    			bluetooth.posli("Q");
	    			} else {
	    		}
	    	}
	    });
	    rdbtnAcl.setToolTipText("zapnut Acc dolava");
	    rdbtnAcl.setBounds(18, 430, 423, 23);
	    frmOvladacRobota.getContentPane().add(rdbtnAcl);
	    
	    final JRadioButton rdbtnAcof = new JRadioButton("Vypn\u00FA\u0165 vyh\u00FDbanie sa prek\u00E1\u017Ekam");
	    rdbtnAcof.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (rdbtnAcof.isSelected()) {
	    			bluetooth.posli("R");
	    			} else {
	    		}
	    	}
	    });
	    rdbtnAcof.setToolTipText("");
	    rdbtnAcof.setBounds(19, 456, 404, 23);
	    frmOvladacRobota.getContentPane().add(rdbtnAcof);
	    
	    ButtonGroup group3 = new ButtonGroup();
	    group3.add(rdbtnAcl);
	    group3.add(rdbtnAcp);
	    group3.add(rdbtnAcof);
	    
	    final JCheckBox chckbxUltzSenzor = new JCheckBox("ultrazvukov\u00FD senzor");
	    chckbxUltzSenzor.setSelected(true);
	    chckbxUltzSenzor.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (chckbxUltzSenzor.isSelected()) {
	    			bluetooth.posli("F");
	    		} else {
	    			bluetooth.posli("G");
	    		}
	    	}
	    });
	    chckbxUltzSenzor.setBounds(10, 231, 165, 23);
	    frmOvladacRobota.getContentPane().add(chckbxUltzSenzor);
	    
	    scrollPaneSpravy = new JScrollPane();
	    scrollPaneSpravy.setBounds(10, 10, 973, 214);
	    
	    frmOvladacRobota.getContentPane().add(scrollPaneSpravy);

	    listModelSpravy = new DefaultListModel();
	    listSpravy = new JList(listModelSpravy);
	    scrollPaneSpravy.setViewportView(listSpravy);
	    
	    chckbxZastavRolovanie = new JCheckBox("zastav rolovanie");
	    chckbxZastavRolovanie.setBounds(871, 231, 112, 23);
	    frmOvladacRobota.getContentPane().add(chckbxZastavRolovanie);
	    
	    chckbxZastavPrimanie = new JCheckBox("zastav prij\u00EDmanie");
	    chckbxZastavPrimanie.setBounds(734, 231, 135, 23);
	    frmOvladacRobota.getContentPane().add(chckbxZastavPrimanie);
	    
	    ButtonGroup group2 = new ButtonGroup();
	    group2.add(rdbtnR1);
	    group2.add(rdbtnR2);
	    group2.add(rdbtnR5);
	    group2.add(rdbtnR6);
	    group2.add(rdbtnR8);
	    group2.add(rdbtnR4);
	    group2.add(rdbtnR7);
	    group2.add(rdbtnR3);
	    
	    chckbxStretavacieSvetla = new JCheckBox("stret\u00E1vacie svetl\u00E1");
	    chckbxStretavacieSvetla.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (chckbxStretavacieSvetla.isSelected()) {
	    			bluetooth.posli("S");
	    			chckbxLightAssist.setSelected(false);
	    		} else {
	    			bluetooth.posli("T");
	    		}
	    	}
	    });
	    chckbxStretavacieSvetla.setBounds(734, 250, 135, 50);
	    frmOvladacRobota.getContentPane().add(chckbxStretavacieSvetla);
	    
	    chckbxDialkoveSvetla = new JCheckBox("dia\u013Ekov\u00E9 svetl\u00E1");
	    chckbxDialkoveSvetla.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
		    		if (chckbxDialkoveSvetla.isSelected()) {
		    			bluetooth.posli("U");
		    			chckbxStretavacieSvetla.setSelected(true);
		    			chckbxLightAssist.setSelected(false);
		    		} else {
		    			bluetooth.posli("V");
		    		}
	    	}
	    });
	    chckbxDialkoveSvetla.setBounds(871, 250, 112, 50);
	    frmOvladacRobota.getContentPane().add(chckbxDialkoveSvetla);
	    
	    chckbxLightAssist = new JCheckBox("Light assist");
	    chckbxLightAssist.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (chckbxLightAssist.isSelected()) {
	    			posliLightAssist();
	    			chckbxStretavacieSvetla.setSelected(false);
	    			chckbxDialkoveSvetla.setSelected(false);
	    		} else {
	    			bluetooth.posli("X");
	    		}
	    	}
	    });
	    chckbxLightAssist.setBounds(592, 250, 140, 50);
	    frmOvladacRobota.getContentPane().add(chckbxLightAssist);
	    
	    sliderCitlivostLightAssist = new JSlider();
	    sliderCitlivostLightAssist.addChangeListener(new ChangeListener() {
	    	public void stateChanged(ChangeEvent e) {
	    		if (chckbxLightAssist.isSelected()) {
	    			posliLightAssist();
	    		}
	    	}
	    });
	    sliderCitlivostLightAssist.setMinimum(0);
	    sliderCitlivostLightAssist.setMaximum(9);
	    sliderCitlivostLightAssist.setBounds(703, 307, 147, 31);
	    frmOvladacRobota.getContentPane().add(sliderCitlivostLightAssist);
	    
	    chckbxDiagnostickeVypisy = new JCheckBox("diagnostick\u00E9 v\u00FDpisy");
	    chckbxDiagnostickeVypisy.setSelected(true);
	    chckbxDiagnostickeVypisy.setBounds(592, 231, 140, 23);
	    frmOvladacRobota.getContentPane().add(chckbxDiagnostickeVypisy);
	    
	    sliderKedyZastat = new JSlider();
	    sliderKedyZastat.addChangeListener(new ChangeListener() {
	    	public void stateChanged(ChangeEvent e) {
	    		if (chckbxUltzSenzor.isSelected()) {
	    			posliKedyZastat();
	    		}
	    	}
	    });
	    sliderKedyZastat.setValue(50);
	    sliderKedyZastat.setToolTipText("kedy  brzdi\u0165");
	    sliderKedyZastat.setMinimum(0);
	    sliderKedyZastat.setMaximum(51);
	    sliderKedyZastat.setBounds(199, 231, 147, 31);
	    frmOvladacRobota.getContentPane().add(sliderKedyZastat);
	    
	    sliderRychlostMerania = new JSlider();
	    sliderRychlostMerania.addChangeListener(new ChangeListener() {
	    	public void stateChanged(ChangeEvent e) {
	    		if (chckbxUltzSenzor.isSelected()) {
	    			posliRychlostMerania();	    		}
	    	}
	    });
	    sliderRychlostMerania.setValue(141);
	    sliderRychlostMerania.setToolTipText("rychlost merania");
	    sliderRychlostMerania.setMinimum(10);
	    sliderRychlostMerania.setMaximum(33);
	    sliderRychlostMerania.setBounds(408, 231, 147, 31);
	    frmOvladacRobota.getContentPane().add(sliderRychlostMerania);
	    

	}
	
	private void posliLightAssist() {
		char citlivost = (char) ('0' + sliderCitlivostLightAssist.getValue()); 
		bluetooth.posli("W" + Character.valueOf(citlivost));
	}
	private void posliKedyZastat() {
		char vzdialenost = (char) ('0' + sliderKedyZastat.getValue()); 
		bluetooth.posli("Y" + Character.valueOf(vzdialenost));
	}
	private void posliRychlostMerania() {
		char rychlostMerania = (char) ('0' + sliderRychlostMerania.getValue()); 
		bluetooth.posli("Z" + Character.valueOf(rychlostMerania));
	}

	public void prisielRiadok(String riadok) {
		String x = null;
		int i = riadok.indexOf("[D=");
		if (i >= 0) {
			int j = riadok.indexOf(" ");
			if (j >= 0) {
				x = riadok.substring(i+3, j);
				textVzdialenost.setText(x + " cm");
			}
			
			try {
				PrintWriter pw = new PrintWriter(new FileWriter("xyz15.csv", true));
				pw.println(riadok);
				pw.close();
			} catch (IOException e) {
				System.out.println("Chyba: " + e);
			}
		}
		
		if (!chckbxZastavPrimanie.isSelected() &&
				(chckbxDiagnostickeVypisy.isSelected() || !riadok.startsWith("["))) {
			listModelSpravy.addElement(riadok);
			if (!chckbxZastavRolovanie.isSelected()) {
				JScrollBar sb = scrollPaneSpravy.getVerticalScrollBar();
				sb.setValue(sb.getMaximum());
			}
		}
	}
}
