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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.data.time.TimeSeriesCollection;

/**
 * Az alkalmazás fõablaka.
 */
public class MainWindow extends JFrame {
	
	private JFileChooser fileChooser = new JFileChooser();
	private Sensor selected_sensor = null;
	private ArrayList<Long> sysdown = null;
	private static final long serialVersionUID = -7018142967304183953L;
	
	public MainWindow() {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
	    panel.setLayout(layout);
		
	    final Diagram diag = new Diagram();
		JButton btn = new JButton("Select logfile!");
		JButton draw = new JButton("Draw!");
		final JLabel all_msg_num = new JLabel("Number of received msg: ");
		final JLabel missin_msg_num = new JLabel("Number of missing msg: ");
		final JLabel avg_l = new JLabel("Avg light value: ");
		final JLabel avg_t = new JLabel("Avg temperature: ");
		final JLabel min_t = new JLabel("Min temperature: ");
		final JLabel max_t = new JLabel("Max temperature: ");
		
		final JComboBox<String> box = new JComboBox<>();
		String[] series_labels = {"Temperature", "Light"};
		final JComboBox<String> series = new JComboBox<>(series_labels);
		
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				switch (fileChooser.showOpenDialog(MainWindow.this)) {
					case JFileChooser.APPROVE_OPTION:
						Detector detector = new Detector(fileChooser.getSelectedFile().toString());
						try {
							/* A logfájl kiválasztása után a küldõ ID-kal feltölti a legördülõ menüt.*/
							Set<String> ids = detector.getSenderIds();
							DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
							model.addElement("<Select sensor ID!>");
							for (String id : ids) {
								model.addElement(id);
							}
							box.setModel(model);
							sysdown = detector.getSystemDownTime();
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					case JFileChooser.CANCEL_OPTION:
						break;
					case JFileChooser.ERROR_OPTION:
						break;
				}
			}
		});
		
		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				Detector detector = new Detector(fileChooser.getSelectedFile().toString());
				try {
					if (box.getSelectedIndex() != 0) {
						selected_sensor = detector.initSensor((String)box.getSelectedItem());
						all_msg_num.setText("Number of received msg: " + selected_sensor.getNumberOfReceivedMsg());
						missin_msg_num.setText("Number of missing msg: " + selected_sensor.getNumberOfMissingMsg());
						avg_l.setText("Avg light value: " + selected_sensor.getAvarageLight());
						avg_t.setText("Avg temperature: " + selected_sensor.getAvarageTemp()/8);
						max_t.setText("Max temperature: " + (float)selected_sensor.getMaxTemp()/8);
						min_t.setText("Min temperature: " + (float)selected_sensor.getMinTemp()/8);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		draw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String id = (String)box.getSelectedItem();
				TimeSeriesCollection tsc;
				if (id != null) {
					diag.setSensor(selected_sensor);
					switch (series.getSelectedIndex()) {
					case 0:
						tsc = diag.getTemperatureData();
						tsc.addSeries(diag.setSysDown(sysdown, selected_sensor.getAvarageTemp()));
						diag.setData("Temperature", "Celsius", tsc);
						break;
					case 1:
						tsc = diag.getLightData();
						tsc.addSeries(diag.setSysDown(sysdown, selected_sensor.getAvarageLight()));
						diag.setData("Light", "Lux", diag.getLightData());
						break;
					}
					diag.repaint();
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
			    .addGroup(layout.createSequentialGroup()
			    		.addComponent(all_msg_num, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			    		.addComponent(missin_msg_num, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			    .addGroup(layout.createSequentialGroup()
			    		.addComponent(avg_l, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			    		.addComponent(avg_t, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			    		.addComponent(max_t, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			    		.addComponent(min_t, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			        .addComponent(diag, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			        .addComponent(btn).addComponent(box).addComponent(series).addComponent(draw))
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    	.addComponent(all_msg_num).addComponent(missin_msg_num))
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    	.addComponent(avg_l).addComponent(avg_t).addComponent(max_t).addComponent(min_t))
			    .addComponent(diag));
		add(panel);
		setTitle("BME - MIHF - MDD");
		setSize(650, 600);
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
