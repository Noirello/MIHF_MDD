package hu.bme.mit.mi.hf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * A CSV fájl kezelése, szenzorok generálása, hiányzó adatok detektálása.
 */
public class Detector {
	private String file;
	
	/**
	 * Konstruktor.
	 * @param file A log fájl elérése.
	 */
	public Detector(String file) {
		this.file = file;
	}
	
	/**
	 * A küldõ eszközök azonosítóinak begyûjtése.
	 * @return Lista a az azonosítokkal.
	 * @throws IOException
	 */
	public Set<String> getSenderIds() throws IOException {
		CSVParser csv = new CSVParser(file);
		Set<String> ids = new TreeSet<String>();
		for (String[] line : csv) {
			/* Adja a listához, ha hexakaraktereket tartalmazó 6 hosszúságú string.*/
			if (line[2].matches("[0123456789ABCDEF]{6}")) {
				ids.add(line[2]);
			}
		}
		ids.remove("000000");
		csv.close();
		return ids;
	}
	
	/**
	 * Létrehoz egy új szenszort az id alapján a CSV fájlból.
	 * @param id A küldõ szenzor azonosítója.
	 * @return A szenzor.
	 * @throws IOException
	 */
	public Sensor initSensor(String id) throws IOException {
		CSVParser csv = new CSVParser(file);
		Sensor sensor = new Sensor(id);
		for (String[] line : csv) {
			if (line[2].equals(id)) {
				SensorMsg msg = new SensorMsg(line);
				// Hibás adatok szûrése.
				if ((msg.getTemperature() > 1600) || (msg.getLight() > 1024)) continue;
				sensor.addNewMsg(msg);
			}
		}
		csv.close();
		return sensor;
	}
	
	/**
	 * Azokat az idõpontokat keresi meg ahol két üzenet ideje között nagyobb a különbség,
	 * mint 15 perc. 
	 * @return Lista a kezdõ- és végidõpontokkal az 5 percnél nagyobb kimaradásról.
	 * @throws IOException
	 */
	public ArrayList<Long> getSystemDownTime() throws IOException {
		CSVParser csv = new CSVParser(file);
		ArrayList<Long> data = new ArrayList<>();
		long prev_date = Long.MAX_VALUE;
		for (String[] line : csv) {
			long curr_date = SensorMsg.dateToTimeStamp(line[0]);
			if ((curr_date - prev_date) > 900000) {
				data.add(prev_date);
				data.add(curr_date);
			}
			prev_date = curr_date;
		}
		csv.close();
		return data;
	}
}
