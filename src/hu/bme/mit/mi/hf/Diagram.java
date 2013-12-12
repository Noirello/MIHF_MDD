package hu.bme.mit.mi.hf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * Diagram osztály az értékek kirajzolásához. X-Y line diagram.
 */
public class Diagram extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private int fig = 0; 
	private ArrayList<Float> TimeLine = null;
	private ArrayList<Float> Temperature = null;
	private ArrayList<Float> Light = null;
	
	private ArrayList<Float> MTimeLine = null;
	private ArrayList<Float> MTemperature = null;
	private ArrayList<Float> MLight = null;
	
	private long start_time;
	private long end_time;
	
	/**
	 * Mérési pontok és az interplollált értékek kirajzolása.
	 * @param g
	 */
	private void doDrawing(Graphics g) {
		ArrayList<Float> data = null;
		ArrayList<Float> mdata = null;
		if (TimeLine == null) return;
        Graphics2D g2d = (Graphics2D) g;
        

        /* Panel mérteének megállapítása. */
        Dimension size = getSize();
        Insets insets = getInsets();

        int w = size.width - insets.left - insets.right - 40;
        int h = size.height - insets.top - insets.bottom - 40;
        
        /* Melyik adatsort rajzolja ki.*/
        if (fig == 0) {
        	data = Temperature;
        	mdata = MTemperature;
        } else if (fig == 1) {
        	data = Light;
        	mdata = MLight;
        }
        g2d.setColor(Color.orange);
        for (int i = 0; i < TimeLine.size(); i++) {
            int x = Math.round(TimeLine.get(i) * w) + 20;
            int y = h - Math.round(data.get(i) * h) + 20;
            if (h - y < 0) {
            	continue;
            }
            g2d.drawLine(x, h, x, y);
        }
        g2d.setColor(Color.red);
        for (int i = 0; i < MTimeLine.size(); i++) {
            int x = Math.round(MTimeLine.get(i) * w) + 20;
            int y = h - Math.round(mdata.get(i) * h) + 20;
            if (h - y < 0) {
            	continue;
            }
            g2d.drawLine(x, h, x, y);
        }
        g2d.setColor(Color.black);
        g2d.drawLine(0, h + 1, w + 40, h + 1);
        g2d.drawLine(20, 0, 20, h);
        drawYScale(g, 0,  640, h);
    }
	
	private void drawYScale(Graphics g, int lowest, int highest, int size) {
		Graphics2D g2d = (Graphics2D)g;
		float ratio = size/(float)(highest - lowest);
		for (int i = lowest; i < highest; i += (highest/20)) {
			g2d.drawLine(15, size-Math.round(i*ratio), 40, size-Math.round(i*ratio));
			g2d.drawString(Integer.toString(i), 0, size-Math.round(i*ratio));
		}
	}
	
	/**
	 * Az idõsor beállítása. A long típusú timestampeket leképzi [0,1] intervallumú floatokra. 
	 * @param values A timestampek listája.
	 */
	public void setTimeLine(ArrayList<Long> values) {
		TimeLine = new ArrayList<>();
		start_time = values.get(0);
		end_time = values.get(values.size()-1);
		for (int i = 0; i < values.size(); i++) {
			float x = (float)((values.get(i) - start_time)) / (end_time - start_time);
			TimeLine.add(x);
		}
	}
	
	/**
	 * A fényerõ mérések beállítása. A fényerõ értékeket leképzi [0,1] intervallumu floatokra.
	 * @param values A fényerõ mérési értékek listája.
	 */
	public void setLight(ArrayList<Integer> values) {
		Light = new ArrayList<>();
		int x_max = 1024;
		for (int i = 0; i < values.size(); i++) {
			float x = (float)(values.get(i)) / x_max;
			Light.add(x);
		}
	}
	
	/**
	 * A hõmérsékleti mérések beállítása. A hõmérséklet értékeket leképzi [0,1] intervallumu floatokra.
	 * @param values A hõmérséklet mérési értékek listája.
	 */
	public void setTemperature(ArrayList<Integer> values) {
		Temperature = new ArrayList<>();
		int x_max = 640;
		for (int i = 0; i < values.size(); i++) {
			float x = (float)(values.get(i)) / x_max;
			Temperature.add(x);
		}
	}
	
	/**
	 * A interpollált értékeket leképzi a [0,1] intervallumra.
	 * @param values Az interpollált értékekek listája.
	 */
	public void setMissingValues(ArrayList<SensorMsg> values) {
		MTemperature = new ArrayList<>();
		MLight = new ArrayList<>();
		MTimeLine = new ArrayList<>();
		int x_tmax = 640;
		int x_lmax = 1024;
		for (int i = 0; i < values.size(); i++) {
			float x = (float)((values.get(i).getTime() - start_time)) / (end_time - start_time);
			float xt = (float)(values.get(i).getTemperature()) / x_tmax;
			float xl = (float)(values.get(i).getLight()) / x_lmax;
			MTimeLine.add(x);
			MLight.add(xl);
			MTemperature.add(xt);
		}
	}
	
	/**
	 * Beállítja melyik ábrát rajzolja ki.
	 * @param i
	 */
	public void setFigure(int i) {
		fig = i;
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}
