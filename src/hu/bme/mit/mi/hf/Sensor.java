package hu.bme.mit.mi.hf;

import java.util.ArrayList;

/**
 * Szenszor osztály.
 */
public class Sensor {
	private String ID;
	private ArrayList<SensorMsg> msgs;
	private ArrayList<SensorMsg> missing_points;
	private ArrayList<Long> loss; 
	private int missing_msg;
	private float avg_temp;
	private float avg_light;
	private int max_temp;
	private int min_temp;
	
	/**
	 * Konstruktor.
	 * @param id A szenzor azonosítója.
	 */
	public Sensor(String id) {
		ID = id;
		msgs = new ArrayList<>();
		missing_points = new ArrayList<>();
		loss = new ArrayList<>();
		min_temp = 500;
		max_temp = 0;
	}

	/**
	 * A szenzorhoz tartozó új üzenet felvétele.
	 * Új üzenet felvételével ellenõrzi, hogy az elõzõ üzenet alapján:
	 * 	 - van-e lényeges - 10 percnél nagyobb - kimaradás az üzenetek között.
	 *   - van-e a sorszám alapján hiány.
	 * Megállapítja a min és max hõmérsékletet, átlagot számol a hõmérsékletre, fényerõségre.
	 * @param msg Szenzor üzenet.
	 */
	public void addNewMsg(SensorMsg msg) {
		SensorMsg last_one; 
		if (msgs.size() != 0) {
			last_one = msgs.get(msgs.size()-1);
			/* Szenszor tartós kimaradás. */
			if (msg.getTime() - last_one.getTime() >= 600000) {
				loss.add(last_one.getTime());
				loss.add(msg.getTime());
			}
			/* Üzenet vesztés. */
			if ((msg.getNumber() - last_one.getNumber()) > 1) {
				missing_points.add(last_one);
				missing_points.add(msg);
				missing_msg += (msg.getNumber() - last_one.getNumber() - 1);
			}
		}
		if (msg.getTemperature() > max_temp) {
			max_temp = msg.getTemperature(); 
		}
		if (msg.getTemperature() < min_temp) {
			min_temp = msg.getTemperature(); 
		}
		avg_temp = avg_temp + (((float)msg.getTemperature() - avg_temp)/(msgs.size() + 1));
		avg_light = avg_light + (((float)msg.getLight() - avg_light)/(msgs.size() + 1));
		msgs.add(msg);
	}
	
	/**
	 * A hiányzó adatok határát jelõlõ két sor között linerás interpollációval kiszámolja a timestampet, a hõmérsékletet és a fényerõt. 
	 * @param start A hiányzó adatok elõtti elsõ sor.
	 * @param end A hiányzó adatok utáni elsõ sor.
	 * @return Interpollált adatok.
	 */
	private ArrayList<SensorMsg> linearInterpolation(SensorMsg start, SensorMsg end) {
		
		ArrayList<SensorMsg> data = new ArrayList<>();
		
		int start_num = start.getNumber();
		int start_temp = start.getTemperature();
		int start_light = start.getLight();
		long start_date = start.getTime();
		int end_num = end.getNumber();
		int end_temp = end.getTemperature();
		int end_light = end.getLight(); 
		long end_date = end.getTime();
		
		for (int x = start_num + 1; x < end_num; x++) {
			/* Lineráis interpolláció képletet alapján */
			long y_date = start_date + ((end_date - start_date) * (x - start_num)) / (end_num - start_num);
			int y_temp = start_temp + ((end_temp - start_temp) * (x - start_num)) / (end_num - start_num);
			int y_light = start_light + ((end_light - start_light) * (x - start_num)) / (end_num - start_num);
			
			if (y_light > 1024 || y_temp > 800) continue; 
			
			data.add(new SensorMsg(y_date, x, y_temp, y_light));
		}
		return data;
	}
	
	/**
	 * Az összes hiányzó sorszámú adatból elõállítja az interpollált értékeket tartalmazó üzeneteket.
	 * @return A hiányzó üzenetek listája.
	 */
	public ArrayList<SensorMsg> generateMissingMsgs() {
		ArrayList<SensorMsg> missing_msgs = new ArrayList<>();
		for (int i = 0; i < missing_points.size() - 1; i+=2) {
			missing_msgs.addAll(this.linearInterpolation(missing_points.get(i), missing_points.get(i+1)));
		}
		return missing_msgs;
	}
	
	/**
	 * Visszadaja az összes fogadott üzenetek számát.
	 * @return A fogadott üzenetek száma.
	 */
	public int getNumberOfReceivedMsg() {
		return msgs.size();
	}
	
	/**
	 * Visszadja az elsõ üzenet idõbélyegjét.
	 * @return Az idõbélyeg.
	 */
	public long getFirstReceivedMsgTime() {
		return msgs.get(0).getTime();
	}
	
	/**
	 * Visszadja az utolsó üzenet idõbélyegjét.
	 * @return Az idõbélyeg.
	 */
	public long getLastReceivedMsgTime() {
		return msgs.get(msgs.size() - 1).getTime();
	}
	
	/**
	 * Visszadaja a hiányzó üzenetek számát.
	 * @return A hiányzó üzenetek száma.
	 */
	public int getNumberOfMissingMsg() {
		return missing_msg;
	}
	
	/**
	 * A 10 percnél nagyobb idejû kimaradások kezdõidõpontjainak listája.
	 * @return Lista a kezdõidõpontokkal.
	 */
	public ArrayList<Long> getHeavyLoss() {
		return loss;
	}
	
	public ArrayList<SensorMsg> getMessages() {
		return msgs;
	}
	
	public float getAvarageTemp() {
		return avg_temp;
	}
	
	public int getMaxTemp() {
		return max_temp;
	}
	
	public int getMinTemp() {
		return min_temp;
	}
	
	public float getAvarageLight() {
		return avg_light;
	}
	
	public String getID() {
		return ID;
	}
}
