package hu.bme.mit.mi.hf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A szenzor üzeneteket tároló osztály.
 * Tartalmazza az üzenet küldési idejét, a sorszámát, a hõmértsékelet és a fényerõsséget.
 */
public class SensorMsg {
	private Long time;
	private int number;
	private int temperature;
	private int light;
	
	/**
	 * Konstruktor CSV fájl alapján
	 * @param line A CSV fájl egy sora.
	 */
	public SensorMsg(String[] line) {
		time = dateToTimeStamp(line[0]);
		number = Integer.parseInt(line[7]);
		temperature = Integer.parseInt(line[9]);
		light = Integer.parseInt(line[10]);
	}
	/**
	 * Konstruktor manuálisan.
	 * @param t Az üzenet idõpontja
	 * @param num Az üzenet sorszáma.
	 * @param temp A hõmérséklet.
	 * @param l A fényerõsség.
	 */
	public SensorMsg(Long t, int num, int temp, int l) {
		time = t;
		number = num;
		temperature = temp;
		light = l;
	}
	
	/**
	 * Dátum string konvertálása timestamppé.
	 * @param dateString A dátum string.
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
	 * Timestamp konverálása Dátum stringgé.
	 * @param timestamp Az idõbélyeg.
	 * @return A dátum string.
	 */
	public static String timeStampToDate(long timestamp) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(timestamp);
		return format.format(date);
	}
	
	/**
	 * Visszadja az üzenet küldésének idejét.
	 * @return Az idõpont timestampként.
	 */
	public Long getTime() {
		return time;
	}
	
	/**
	 * Visszadaja az üzenet sorszámát.
	 * @return Az üzenet sorszáma.
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Visszadaja az üzenethez tartozó hõmérsékletet.
	 * @return A hõmértséklet.
	 */
	public int getTemperature() {
		return temperature;
	}
	
	/**
	 * Visszadaja az üzenethez tartozó fényerõsséget.
	 * @return A fényerõsség.
	 */
	public int getLight() {
		return light;
	}
}
