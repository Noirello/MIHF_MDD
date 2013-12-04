package hu.bme.mit.mi.hf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * CSV f�jl feldolgoz�.
 */
public class CSVParser implements Iterable<String[]>{
	
	private FileReader file;
	private BufferedReader reader;

	/**
	 * Konstruktor.
	 * @param path - f�jl el�r�si �tja.
	 */
	CSVParser(String path) {
		try {
			file = new FileReader(path);
			reader = new BufferedReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Streamek lez�r�sa.
	 * @throws IOException
	 */
	public void close() throws IOException {
		reader.close();
		file.close();
	}
	
	/**
	 * Iter�tor a sorok beolvas�s�hoz.
	 */
	@Override
	public Iterator<String[]> iterator() {
		Iterator<String[]> it = new Iterator<String[]>() {
			private String currentLine = null;
			private String[] values = null;

            @Override
            public boolean hasNext() {
            	try {
					currentLine = reader.readLine();
					if (currentLine == null) {
						return false;
	            	}
    	        	values = currentLine.split(";");
    	        	/* Ha nem siker�lt feldarabolni a ';' ment�n, akkor val�sz�n�leg hib�s a f�jl. */
    	        	if (values.length <= 1) {
    	        		return false;
    	        	}
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
                return true;
            }

            @Override
            public String[] next() {
            	if (currentLine != null) {
    	        	ArrayList<String> list = new ArrayList<String>();
    	        	for(String v : values) {
    	        		list.add(v.trim());
    	        	}
    	        	return list.toArray(values);
    	        }
				return null;
            }

			@Override
			public void remove() {
				// TODO Auto-generated method stub
			}
        };
        return it;
	}
}
