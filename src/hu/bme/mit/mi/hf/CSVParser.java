package hu.bme.mit.mi.hf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class CSVParser implements Iterable<String[]>{
	
	private FileReader file;
	private BufferedReader reader;

	CSVParser(String path) {
		try {
			file = new FileReader(path);
			reader = new BufferedReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void close() throws IOException {
		file.close();
	}
	
	@Override
	public Iterator<String[]> iterator() {
		try {
			reader.ready();
		} catch (IOException ex) {
			reader = new BufferedReader(file);
		}
		Iterator<String[]> it = new Iterator<String[]>() {
			private String currentLine = null;
			private String[] values = null;

            @Override
            public boolean hasNext() {
            	try {
					currentLine = reader.readLine();
					if (currentLine == null) {
						reader.close();
						return false;
	            	}
    	        	values = currentLine.split(";");
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
