package textprocessing;
import java.util.*;
import java.io.*;

public class Main{
    public static void main(String[] args) {
        FileNames fileNames = new FileNames();
        FileContents fileContent = new FileContents(30, 100 * 1024);
        WordFrequencies wordFrequencies = new WordFrequencies();
        
        
        FileProcessor hilo1 = new FileProcessor(fileContent, wordFrequencies);
        FileProcessor hilo2 = new FileProcessor(fileContent, wordFrequencies);
        FileProcessor hilo3 = new FileProcessor(fileContent, wordFrequencies);
        
        FileReader hilo4 = new FileReader(fileNames, fileContent);
        FileReader hilo5 = new FileReader(fileNames, fileContent);
        
        hilo1.start();
        hilo2.start();
        hilo3.start();
        hilo4.start();
        hilo5.start();
        

        Tools.fileLocator(fileNames, "datos");
        fileNames.noMoreNames();
        
        
        try{
            
            hilo1.join();
            hilo2.join();
            hilo3.join();
            hilo4.join();
            hilo5.join();
            
            }catch(Exception e){ };
        
        for( String palabra : Tools.wordSelector(wordFrequencies.getFrequencies())) {
            System.out.println(palabra);
        }
    }
}

class Tools {
    public static void fileLocator(FileNames fn, String dirname){
        File dir = new File(dirname);
        if ( ! dir.exists() ){
            return;
        }
        if ( dir.isDirectory() ) {
            String[] dirList = dir.list();
            for ( String name: dirList ) {
                if ( name.equals(".") || name.equals("..") ) {
                    continue;
                }
                fileLocator(fn, dir + File.separator + name);
            }
        } else {
            fn.addName(dir.getAbsolutePath());
            System.out.println(dir.getAbsolutePath());
        }
    }
    public static class Order implements Comparator< Map.Entry<String,Integer> > {
        public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
            if ( o1.getValue().compareTo(o2.getValue()) == 0 ) {
                return o1.getKey().compareTo(o2.getKey());
            }
            return o2.getValue().compareTo(o1.getValue());
        }
    }
    
    public static List<String> wordSelector(Map<String,Integer> wf){
        Set<Map.Entry<String,Integer>> set = wf.entrySet();
        Set<Map.Entry<String,Integer>> orderSet = new TreeSet<Map.Entry<String,Integer>>(
            new Order());
        orderSet.addAll(set);
        List<String> l = new LinkedList<String>();
        int i=0;
        for ( Map.Entry<String,Integer> pair: orderSet){
            l.add(pair.getValue() + " -" + pair.getKey() + "-" );
            if (++i >= 10 ) break;
        }
        return l;
    }

    public static  String getContents(String fileName){
        StringBuilder text = new StringBuilder();
		try {
		    FileInputStream fis = new FileInputStream( fileName );
	        BufferedReader br = new BufferedReader( new InputStreamReader(fis, "ISO8859-1") );
			String line;
			while (( line = br.readLine()) != null) {
				text.append(line);
				text.append("\n");
			}
		} catch (IOException e) {
			return "Error: " + e.getMessage();
		}
        return text.toString();
    }
    
}
