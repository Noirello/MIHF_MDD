package hu.bme.mit.mi.hf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A szenzor �zeneteket t�rol� oszt�ly.
 * Tartalmazza az �zenet k�ld�si idej�t, a sorsz�m�t, a h�m�rts�kelet �s a f�nyer�ss�get.
 */
public class SensorMsg {
	private Long time;
	private int number;
	private int temperature;
	private int light;
	
	/**
	 * Konstruktor CSV f�jl alapj�n
	 * @param line A CSV f�jl egy sora.
	 */
	public SensorMsg(String[] line) {
		time = dateToTimeStamp(line[0]);
		number = Integer.parseInt(line[7]);
		temperature = Integer.parseInt(line[9]);
		light = Integer.parseInt(line[10]);
	}
	/**
	 * Konstruktor manu�lisan.
	 * @param t Az �zenet id�pontja
	 * @param num Az �zenet sorsz�ma.
	 * @param temp A h�m�rs�klet.
	 * @param l A f�nyer�ss�g.
	 */
	public SensorMsg(Long t, int num, int temp, int l) {
		time = t;
		number = num;
		temperature = temp;
		light = l;
	}
	
	/**
	 * D�tum string konvert�l�sa timestampp�.
	 * @param dateString A d�tum string.
	 * @return A timestamp.
	 */
	public static long dateToTimeStamp(String dateString) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date parsed = format.parse(dateString);
			return parsed.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Timestamp konver�l�sa D�tum stringg�.
	 * @param timestamp Az id�b�lyeg.
	 * @return A d�tum string.
	 */
	public static String timeStampToDate(long timestamp) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(timestamp);
		return format.format(date);
	}
	
	/**
	 * Visszadja az �zenet k�ld�s�nek idej�t.
	 * @return Az id�pont timestampk�nt.
	 */
	public Long getTime() {
		return time;
	}
	
	/**
	 * Visszadaja az �zenet sorsz�m�t.
	 * @return Az �zenet sorsz�ma.
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Visszadaja az �zenethez tartoz� h�m�rs�kletet.
	 * @return A h�m�rts�klet.
	 */
	public int getTemperature() {
		return temperature;
	}
	
	/**
	 * Visszadaja az �zenethez tartoz� f�nyer�ss�get.
	 * @return A f�nyer�ss�g.
	 */
	public int getLight() {
		return light;
	}
}
