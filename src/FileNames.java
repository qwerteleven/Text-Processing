package textprocessing;

import java.util.Queue;
import java.util.LinkedList;


public class FileNames {
    
    private Queue<String> queue;
    private volatile boolean moreNames = true;
    
    
    public FileNames () {
        queue = new LinkedList();
    }
    
    
    public void addName(String fileName) {
        queue.add(fileName);
    }
    
    
    public synchronized String getName() {
        while(moreNames) {}
        return queue.poll();
    }
    
    
    public void noMoreNames() {
        moreNames = false;
    }
    
}