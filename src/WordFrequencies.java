package textprocessing;

import java.util.Map;
import java.util.HashMap;


public class WordFrequencies {
    
    private static HashMap<String,Integer> data = new HashMap<String,Integer>();
    
    public synchronized void addFrequencies(Map<String,Integer> f){
        
        for (Map.Entry <String, Integer> e : f.entrySet()) {
            
            if (data.containsKey(e.getKey())) {
                data.put(e.getKey(), e.getValue() + data.get(e.getKey()));
                
            } else {
                data.put(e.getKey(), e.getValue());
            }
        }
    }
    
    public Map <String,Integer> getFrequencies(){
        return data;
    }
    
}
