package hu.bme.mit.mi.hf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Az alkalmazás fõablaka.
 */
public class MainWindow extends JFrame {
	
	private JFileChooser fileChooser = new JFileChooser();
	private static final long serialVersionUID = -7018142967304183953L;
	
	public MainWindow() {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
	    panel.setLayout(layout);
		
	    final Diagram diag = new Diagram();
		JButton btn = new JButton("Select logfile!");
		JButton draw = new JButton("Draw!");
		final JComboBox<String> box = new JComboBox<>();
		String[] series_labels = {"Temperature", "Light"};
		final JComboBox<String> series = new JComboBox<>(series_labels);
		btn.setSize(100, 20);
		ActionListener file_pick_al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				switch (fileChooser.showOpenDialog(MainWindow.this)) {
					case JFileChooser.APPROVE_OPTION:
						Detector detector = new Detector(fileChooser.getSelectedFile().toString());
						try {
							/* A logfájl kiválasztása után a küldõ ID-kal feltölti a legördülõ menüt.*/
							Set<String> ids = detector.getSenderIds();
							DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
							for (String id : ids) {
								model.addElement(id);
							}
							box.setModel(model);
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
		btn.addActionListener(file_pick_al);
		draw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String id = (String)box.getSelectedItem();
				if (id != null) {
					Detector detector = new Detector(fileChooser.getSelectedFile().toString());
					try {
						ArrayList<Long[]> intpols = new ArrayList<>();
						/* Hiányzó adatok megkeresése a kiválasztott eszköznél */
						ArrayList<String[]> missings = detector.getMissingIntervalls(id);
						/* Hiányzó adatok interpollálása. */ 
						for (int i = 0; i < missings.size()-1; i+=2) {
							intpols.addAll(detector.linearInterpolation(missings.get(i), missings.get(i+1)));
						}
						/* Idõsor beállítása a diagramon. */
						diag.setTimeLine(detector.getTimeLine(id));
						/* Hõmérséklet vagy fényerõ rajzolása? */
						switch (series.getSelectedIndex()) {
							case 0:
								diag.setTemperature(detector.getIntValues(id, 9));
								diag.setFigure(0);
								break;
							case 1:
								diag.setLight(detector.getIntValues(id, 10));
								diag.setFigure(1);
								break;
						}
						/* Hiányzó adatok beállítása a diagramon. */
						diag.setMissingValues(intpols);
						diag.repaint();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		layout.setHorizontalGroup(layout
			    .createParallelGroup(GroupLayout.Alignment.LEADING)
			    .addGroup(layout.createSequentialGroup()
			        .addComponent(btn, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			        .addComponent(box, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			        .addComponent(series, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			        .addComponent(draw, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			        .addComponent(diag, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			        .addComponent(btn).addComponent(box).addComponent(series).addComponent(draw))
			        .addComponent(diag));
		add(panel);
		setTitle("BME - MIHF - MDD");
		setSize(600, 600);
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
