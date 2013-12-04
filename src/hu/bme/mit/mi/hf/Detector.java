package hu.bme.mit.mi.hf;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;


/**
 * A hi�nyz� adatok detekt�l�sa
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
	 * Visszaadja az adott ID-hez tartoz� �s az (i+1). oszlopban tal�lhat� �rt�keket 
	 * a CSV f�jlb�l, ami - lehet�leg - eg�sz sz�mokat tartalmaz.
	 * @param id A k�ld� azonos�t�ja.
	 * @param i Az oszlop sz�m 
	 * @return Az �rt�keke list�ja.
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
	 * Visszadja az adott ID-hez tartoz� timestampeket a CSV f�jlb�l.
	 * @param id A k�ld� azonos�t�ja.
	 * @return A timestampek list�ja.
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
	 * Visszadja azokat a sorokat a CSV f�jlb�l, ami k�z�tt az adott ID-hez tartoz� 
	 * az �zenet sorsz�ma alapj�n hi�nyoznak sorok.
	 * @param id a k�ld� azonos�t�ja.
	 * @return A hi�nyz� �rt�kek hat�r�t jel�l� sorok.
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
					/* Ha a jelenlegi �s az el�z� sor k�z�tt nagyobb a k�l�nbs�g a sorsz�mban 1-n�l, akkor hi�nyzik onnan sor. */
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
	 * D�tum string konvert�l�sa timestampp�.
	 * @param dateString A d�tum string.
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
	 * A hi�nyz� adatok hat�r�t jel�l� k�t sor k�z�tt liner�s interpoll�ci�val kisz�molja a timestampet, a h�m�rs�kletet �s a f�nyer�t. 
	 * @param start A hi�nyz� adatok el�tti els� sor.
	 * @param end A hi�nyz� adatok ut�ni els� sor.
	 * @return Interpoll�lt {timestamp, h�m�rs�klet, f�nyer�} h�rmasok list�ja.
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
			/* Liner�is interpoll�ci� k�pletet alapj�n */
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
