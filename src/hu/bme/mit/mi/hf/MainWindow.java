package hu.bme.mit.mi.hf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class MainWindow extends JFrame {
	
	private JFileChooser fileChooser = new JFileChooser();
	private static final long serialVersionUID = -7018142967304183953L;
	
	public MainWindow() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JButton btn = new JButton("Pick a file!");
		btn.setSize(100, 20);
		ActionListener al;
		al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				switch (fileChooser.showOpenDialog(MainWindow.this)) {
					case JFileChooser.APPROVE_OPTION:
						Detector d = new Detector(fileChooser.getSelectedFile().toString());
						try {
							Object[] x = d.getSenderIds().toArray();
							for (String[] y : d.linearInterpolation(d.getMissingIntervalls((String)x[1]).get(10), d.getMissingIntervalls((String)x[1]).get(13))) {
								System.out.println(Arrays.deepToString(y));
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case JFileChooser.CANCEL_OPTION:
						break;
					case JFileChooser.ERROR_OPTION:
						break;
				}
			}
		};
		btn.addActionListener(al);
		panel.add(btn);
		setContentPane(panel);
		setTitle("BME - MIHF - MDD");
		setSize(400, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	MainWindow ex = new MainWindow();
                ex.setVisible(true);
            }
        });

	}

}
