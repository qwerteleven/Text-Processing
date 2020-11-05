package textprocessing;

import java.util.Map;
import java.util.HashMap;

public class FileProcessor extends Thread{
    
    private FileContents fc;
    private WordFrequencies wf;

    public FileProcessor(FileContents fc, WordFrequencies wf){
        this.fc = fc;
        this.wf = wf;
    }
    
    @Override
    public void run(){
        HashMap<String,Integer> data = new HashMap<String,Integer>();
        
        String text;
        
        while ( (text = fc.getContents()) != null) {
            String [] palabras = text.split(" ");
            
            for (String palabra: palabras) {
                 if (! "".equals(palabra)) {
                    if (data.containsKey(palabra)) {
                            
                        data.put(palabra, data.get(palabra) + 1);
                            
                    } else {
                        data.put(palabra, 1);
                    }
                }
            }
        }
        
        wf.addFrequencies(data);
    }
    
}
