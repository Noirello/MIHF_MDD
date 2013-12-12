package hu.bme.mit.mi.hf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.Date;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * Diagram osztály az értékek kirajzolásához. X-Y line diagram.
 */
public class Diagram extends JPanel {
	private static final Shape circle = new Ellipse2D.Double(-1, -1, 2, 2);
	private static final long serialVersionUID = 1L;
	
	private ChartPanel chart_panel = new ChartPanel(null);
	private Sensor sensor = null;
	
	public Diagram() {
		add(chart_panel);
	}
	
	public void setSensor(Sensor s) {
		sensor = s;
	}
	
	public TimeSeriesCollection getTemperatureData() {
		if (sensor == null) return null;
		
		TimeSeriesCollection tsc = new TimeSeriesCollection();
		TimeSeries ts_emp = new TimeSeries("Empirical");
		for(SensorMsg msg : sensor.getMessages()) {
			ts_emp.addOrUpdate(new Millisecond(new Date(msg.getTime())), (float)msg.getTemperature()/8);
		}
		TimeSeries ts_int = new TimeSeries("Interpolated");
		for(SensorMsg msg : sensor.generateMissingMsgs()) {
			ts_int.addOrUpdate(new Millisecond(new Date(msg.getTime())), (float)msg.getTemperature()/8);
		}
		tsc.addSeries(ts_int);
		tsc.addSeries(ts_emp);
		return tsc;
	}
	
	public TimeSeriesCollection getLightData() {
		if (sensor == null) return null;
		
		TimeSeriesCollection tsc = new TimeSeriesCollection();
		TimeSeries ts_emp = new TimeSeries("Empirical");
		for(SensorMsg msg : sensor.getMessages()) {
			ts_emp.addOrUpdate(new Millisecond(new Date(msg.getTime())), (float)msg.getLight());
		}
		TimeSeries ts_int = new TimeSeries("Interpolated");
		for(SensorMsg msg : sensor.generateMissingMsgs()) {
			ts_int.addOrUpdate(new Millisecond(new Date(msg.getTime())), (float)msg.getLight());
		}
		tsc.addSeries(ts_int);
		tsc.addSeries(ts_emp);
		return tsc;
	}
	
	public void setData(String title, String yleg, final TimeSeriesCollection tsc) {		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				title, "Date", yleg, tsc, true, true, false);
		XYPlot plot = (XYPlot) chart.getPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(){
			private static final long serialVersionUID = 1L;
            @Override
            public Stroke getItemStroke(int row, int column) {
                return super.getItemStroke(row, column);
            }
            @Override
            public Paint getItemPaint(int row, int column) {
            	Paint col;
            	if (row == 0) {
            		col = Color.pink;
            	} else {
            		col = Color.cyan;
            	}
            	
                long x = (long)tsc.getXValue(row, column);
            	for (int i = 0; i < (sensor.getHeavyLoss().size() - 1); i += 2) {
            		if ((x > sensor.getHeavyLoss().get(i)) && (x < sensor.getHeavyLoss().get(i+1))) {
            			return col;
            		}
            	}
            	return super.getItemPaint(row, column);
            }
        };
        renderer.setSeriesShape(0, circle);
        renderer.setSeriesShape(1, circle);
        renderer.setBaseLinesVisible(false);
        renderer.setBaseStroke(new BasicStroke(1));
        plot.setRenderer(renderer);
		chart_panel.setChart(chart);
	}
}
