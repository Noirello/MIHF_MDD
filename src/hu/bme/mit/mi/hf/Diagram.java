package hu.bme.mit.mi.hf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
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
 * Diagram oszt�ly az �rt�kek kirajzol�s�hoz. X-Y line diagram.
 */
public class Diagram extends JPanel {
	private static final Shape circle = new Ellipse2D.Double(-1, -1, 2, 2);
	private static final Shape big_circle = new Ellipse2D.Double(-2, -2, 4, 4);
	private static final long serialVersionUID = 1L;
	
	private ChartPanel chart_panel = new ChartPanel(null);
	private Sensor sensor = null;
	private ArrayList<Long> sysdown;
	
	public Diagram() {
		add(chart_panel);
	}
	
	/**
	 * Szenszor be�ll�t�sa.
	 * @param s A szenzor.
	 */
	public void setSensor(Sensor s) {
		sensor = s;
	}
	
	/**
	 * A hosszabb idej� rendszerkimarad�s id�pontjainak be�ll�t�sa.
	 * @param data Lista az id�pontokkal.
	 * @param value A hozz�adott �rt�k.
	 */
	public TimeSeries setSysDown(ArrayList<Long> data, float value) {
		TimeSeries ts_sys = new TimeSeries("SysDownTime");
		for(Long time : data) {
			ts_sys.addOrUpdate(new Millisecond(new Date(time)), value);
		}
		return ts_sys;
	}
	
	/**
	 * Visszaadja a h�m�rs�kleti adatok gy�jtem�ny�t.
	 * @return A m�rt �s interpol�lt h�m�rs�klet sorozat gy�jtem�nye.
	 */
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
	
	/**
	 * Visszaadja a f�nyer�ss�g adatok gy�jtem�ny�t.
	 * @return A m�rt �s interpol�lt f�nyer�ss�gek gy�jtem�nye.
	 */
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
	
	/**
	 * Be�ll�tja a megjelen�t�shez haszn�lt adat kollekci�kat.
	 * @param title A digram c�me.
	 * @param yleg Az Y tengely magyar�zata.
	 * @param tsc Az adatgy�jtem�ny.
	 */
	public void setData(String title, String yleg, final TimeSeriesCollection tsc) {		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				title, "Date", yleg, tsc, true, true, false);
		XYPlot plot = (XYPlot) chart.getPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(){
			private static final long serialVersionUID = 1L;
            @Override
            public Paint getItemPaint(int row, int column) {
            	Paint col;
            	if (row == 2) return Color.black;
            	if (row == 0) {
            		col = Color.pink;
            	} else {
            		col = Color.cyan;
            	}
            	
                long x = (long)tsc.getXValue(row, column);
                // Ha az adott id�pont beleesik a szenzor tart�s kimarad�sa intervallumba a jel�l� pont szine lecser�l�dik.
            	for (int i = 0; i < (sensor.getHeavyLoss().size() - 1); i += 2) {
            		if ((x > sensor.getHeavyLoss().get(i)) && (x < sensor.getHeavyLoss().get(i+1))) {
            			return col;
            		}
            	}
            	return super.getItemPaint(row, column);
            }
            @Override
            public boolean getItemLineVisible(int series, int item) {
            	// �sszek�t� vonal csak a m�rt �rt�kek k�z�tt.
            	if (series == 1) return true;
            	else return false;
            }
        };
        renderer.setSeriesShape(0, circle);
        renderer.setSeriesShape(1, circle);
        renderer.setSeriesShape(2, big_circle);
        renderer.getBaseLinesVisible();
        renderer.setBaseStroke(new BasicStroke(1));
        plot.setRenderer(renderer);
		chart_panel.setChart(chart);
	}
}
