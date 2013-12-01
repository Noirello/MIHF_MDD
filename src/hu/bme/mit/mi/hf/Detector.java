package hu.bme.mit.mi.hf;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;


public class Detector {
	private String file;
	
	public Detector(String file) {
		this.file = file;
	}
	
	public Set<String> getSenderIds() throws IOException {
		CSVParser csv = new CSVParser(file);
		Set<String> ids = new TreeSet<String>();
		for (String[] line : csv) {
			if (line[2].matches("[0123456789ABCDEF]{6}")) {
				ids.add(line[2]);
			}
		}
		ids.remove("000000");
		csv.close();
		return ids;
	}
	
	public ArrayList<String[]> getMissingIntervalls(String id) throws IOException {
		CSVParser csv = new CSVParser(file);
		ArrayList<String[]> valueList = new ArrayList<>();
		int past = 0;
		String[] past_values = null;
		for (String[] line : csv) {
			if (line[2].equals(id)) {
				int seq_num = Integer.parseInt(line[7]);
				if (past != 0) {
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
	
	private String timeStampToDate(long timestamp) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(timestamp);
		return format.format(date);
	}
	
	public ArrayList<String[]> linearInterpolation(String[] start, String[] end) {
		
		ArrayList<String[]> data = new ArrayList<>();
		
		int start_num = Integer.parseInt(start[7]); 
		int start_temp = Integer.parseInt(start[9]);
		int start_light = Integer.parseInt(start[10]); 
		long start_date = dateToTimeStamp(start[0]);
		int end_num = Integer.parseInt(end[7]); 
		int end_temp = Integer.parseInt(end[9]); 
		int end_light = Integer.parseInt(end[10]); 
		long end_date = dateToTimeStamp(end[0]);
		
		for (int x = start_num + 1; x < end_num; x++) {
			long y_date = start_date + ((end_date - start_date) * (x - start_num)) / (end_num - start_num);
			int y_temp = start_temp + ((end_temp - start_temp) * (x - start_num)) / (end_num - start_num);
			int y_light = start_light + ((end_light - start_light) * (x - start_num)) / (end_num - start_num);
			
			String[] line = {timeStampToDate(y_date), start[1],
						start[2], start[3], start[4], start[5], 
						start[6], Integer.toString(x), start[8],
						Integer.toString(y_temp), Integer.toString(y_light), 
						start[11], start[12], start[13]};
			data.add(line);
		}
		return data;
	}
}
