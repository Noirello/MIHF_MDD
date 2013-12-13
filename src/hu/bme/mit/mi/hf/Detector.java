package hu.bme.mit.mi.hf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * A CSV f�jl kezel�se, szenzorok gener�l�sa, hi�nyz� adatok detekt�l�sa.
 */
public class Detector {
	private String file;
	
	/**
	 * Konstruktor.
	 * @param file A log f�jl el�r�se.
	 */
	public Detector(String file) {
		this.file = file;
	}
	
	/**
	 * A k�ld� eszk�z�k azonos�t�inak begy�jt�se.
	 * @return Lista a az azonos�tokkal.
	 * @throws IOException
	 */
	public Set<String> getSenderIds() throws IOException {
		CSVParser csv = new CSVParser(file);
		Set<String> ids = new TreeSet<String>();
		for (String[] line : csv) {
			/* Adja a list�hoz, ha hexakaraktereket tartalmaz� 6 hossz�s�g� string.*/
			if (line[2].matches("[0123456789ABCDEF]{6}")) {
				ids.add(line[2]);
			}
		}
		ids.remove("000000");
		csv.close();
		return ids;
	}
	
	/**
	 * L�trehoz egy �j szenszort az id alapj�n a CSV f�jlb�l.
	 * @param id A k�ld� szenzor azonos�t�ja.
	 * @return A szenzor.
	 * @throws IOException
	 */
	public Sensor initSensor(String id) throws IOException {
		CSVParser csv = new CSVParser(file);
		Sensor sensor = new Sensor(id);
		for (String[] line : csv) {
			if (line[2].equals(id)) {
				SensorMsg msg = new SensorMsg(line);
				// Hib�s adatok sz�r�se.
				if ((msg.getTemperature() > 1600) || (msg.getLight() > 1024)) continue;
				sensor.addNewMsg(msg);
			}
		}
		csv.close();
		return sensor;
	}
	
	/**
	 * Azokat az id�pontokat keresi meg ahol k�t �zenet ideje k�z�tt nagyobb a k�l�nbs�g,
	 * mint 15 perc. 
	 * @return Lista a kezd�- �s v�gid�pontokkal az 5 percn�l nagyobb kimarad�sr�l.
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
