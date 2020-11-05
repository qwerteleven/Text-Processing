package textprocessing;


public class FileReader extends Thread{
    
    private FileNames fn;
    private FileContents fc;
    
    public FileReader(FileNames fn, FileContents fc){
        this.fn = fn;
        this.fc = fc;
    }
    
    
    @Override
    public void run(){
        
        String text;
        fc.registerWriter();
        
        while((text = fn.getName()) != null) {
            
            String [] data = Tools.getContents(text).split("\n");
            
            for(String linea : data) {
                linea = linea.replaceAll("\\b", " ");
                linea = linea.replaceAll("[,-.:;?¿¡'!\" \\t«»() ]", " ");
                linea = linea.replaceAll("  ", " ");
                if (! "".equals(linea)) { 
                    fc.addContents(linea);
                }
            }
        }
        
        fc.unregisterWriter();
    }
    
}
