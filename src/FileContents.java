package textprocessing;

import java.util.Queue;
import java.util.LinkedList;


public class FileContents {
    
    private Queue<String> queue;
    private volatile int registerCount;
    private volatile boolean closed = false;
    private final int maxFiles;
    private final int maxChars;
    private volatile int actualChars;
    
    
    public FileContents(int maxFiles, int maxChars) {
        queue = new LinkedList();
        this.maxFiles = maxFiles;
        this.maxChars = maxChars;
    }
    
    public synchronized void registerWriter() {
        registerCount ++;
    }
    
    public synchronized void unregisterWriter() {
        registerCount --;
        if (registerCount == 0) closed = true;
    }
    
    
    private synchronized String getData() {
        String data = queue.poll();
        if (null == data) return "";
        actualChars -= data.length();
        return data;
    }
    
    
    private synchronized void addData(String contents) {
        actualChars += contents.length();
        queue.add(contents);
    }
    
    
    public void addContents(String contents) {
        while (maxFiles < queue.size() + 1 && maxChars < contents.length() + actualChars) {}
        
        addData(contents);
    }
    
    public String getContents() {
        if (actualChars == 0 && closed) return null;
        
        while(actualChars == 0) {
            if (closed) return null;
        }
        
        return getData();
    }
    
}

