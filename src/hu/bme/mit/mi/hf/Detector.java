package hu.bme.mit.mi.hf;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;


/**
 * A hiányzó adatok detektálása
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
	 * Visszaadja az adott ID-hez tartozó és az (i+1). oszlopban található értékeket 
	 * a CSV fájlból, ami - lehetõleg - egész számokat tartalmaz.
	 * @param id A küldõ azonosítója.
	 * @param i Az oszlop szám 
	 * @return Az értékeke listája.
	 * @throws IOException
	 */
	public ArrayList<Integer> getIntValues(String id, int i) throws IOException {
		CSVParser csv = new CSVParser(file);
		ArrayList<Integer> valueList = new ArrayList<>();
		for (String[] line : csv) {
			if (line[2].equals(id)) {
				int seq_num = Integer.parseInt(line[i]);
				valueList.add(seq_num);
			}
		}
		csv.close();
		return valueList;
	}
	
	/**
	 * Visszadja az adott ID-hez tartozó timestampeket a CSV fájlból.
	 * @param id A küldõ azonosítója.
	 * @return A timestampek listája.
	 * @throws IOException
	 */
	public ArrayList<Long> getTimeLine(String id) throws IOException {
		CSVParser csv = new CSVParser(file);
		ArrayList<Long> timeline = new ArrayList<>();
		for (String[] line : csv) {
			if (line[2].equals(id)) {
				timeline.add(dateToTimeStamp(line[0]));
			}
		}
		csv.close();
		return timeline;
	}
	
	/**
	 * Visszadja azokat a sorokat a CSV fájlból, ami között az adott ID-hez tartozó 
	 * az üzenet sorszáma alapján hiányoznak sorok.
	 * @param id a küldõ azonosítója.
	 * @return A hiányzó értékek határát jelölõ sorok.
	 * @throws IOException
	 */
	public ArrayList<String[]> getMissingIntervalls(String id) throws IOException {
		CSVParser csv = new CSVParser(file);
		ArrayList<String[]> valueList = new ArrayList<>();
		int past = 0;
		String[] past_values = null;
		for (String[] line : csv) {
			if (line[2].equals(id)) {
				int seq_num = Integer.parseInt(line[7]);
				if (past != 0) {
					/* Ha a jelenlegi és az elõzõ sor között nagyobb a különbség a sorszámban 1-nél, akkor hiányzik onnan sor. */
					if ((seq_num - past) > 1) {
						valueList.add(past_values);
						valueList.add(line);
					}
				}
				past_values = line;
	            past = seq_num;
			}
		}
		csv.close();
		return valueList;
	}
	
	/**
	 * Dátum string konvertálása timestamppé.
	 * @param dateString A dátum string.
	 * @return A timestamp.
	 */
	private long dateToTimeStamp(String dateString) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date parsed = format.parse(dateString);
			return parsed.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	@SuppressWarnings("unused")
	private String timeStampToDate(long timestamp) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(timestamp);
		return format.format(date);
	}
	
	/**
	 * A hiányzó adatok határát jelõlõ két sor között linerás interpollációval kiszámolja a timestampet, a hõmérsékletet és a fényerõt. 
	 * @param start A hiányzó adatok elõtti elsõ sor.
	 * @param end A hiányzó adatok utáni elsõ sor.
	 * @return Interpollált {timestamp, hõmérséklet, fényerõ} hármasok listája.
	 */
	public ArrayList<Long[]> linearInterpolation(String[] start, String[] end) {
		
		ArrayList<Long[]> data = new ArrayList<>();
		
		int start_num = Integer.parseInt(start[7]); 
		int start_temp = Integer.parseInt(start[9]);
		int start_light = Integer.parseInt(start[10]); 
		long start_date = dateToTimeStamp(start[0]);
		int end_num = Integer.parseInt(end[7]); 
		int end_temp = Integer.parseInt(end[9]); 
		int end_light = Integer.parseInt(end[10]); 
		long end_date = dateToTimeStamp(end[0]);
		
		for (int x = start_num + 1; x < end_num; x++) {
			/* Lineráis interpolláció képletet alapján */
			long y_date = start_date + ((end_date - start_date) * (x - start_num)) / (end_num - start_num);
			int y_temp = start_temp + ((end_temp - start_temp) * (x - start_num)) / (end_num - start_num);
			int y_light = start_light + ((end_light - start_light) * (x - start_num)) / (end_num - start_num);
			
			if (y_light > 1024 || y_temp > 800) continue; 
			
			/*String[] line = {timeStampToDate(y_date), start[1],
						start[2], start[3], start[4], start[5], 
						start[6], Integer.toString(x), start[8],
						Integer.toString(y_temp), Integer.toString(y_light), 
						start[11], start[12], start[13]};*/
			Long[] line = {y_date, Long.valueOf(y_temp), Long.valueOf(y_light)};
			data.add(line);
		}
		return data;
	}
}
