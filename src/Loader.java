import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Loader {
  private String URL_PATH = "TopURLs";
  private String MAPPING_PATH = "mapping";
  
  private ArrayList<String[]> urls = new ArrayList<String[]>();
  private ArrayList<String[]> mappings = new ArrayList<String[]>();
  
  public void loadURLs() {
    urls = loadFile(URL_PATH);
  }
  
  public void loadMappings() {
    mappings = loadFile(MAPPING_PATH);
  }
  
  private ArrayList<String[]> loadFile(String path) {
    File file = new File(path);
    ArrayList<String[]> result = new ArrayList<String[]>();
    try {
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String[] row = scanner.nextLine().split("\t");
        result.add(row);
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return result;
    
    
  }

  public ArrayList<String[]> getURLs() {
    return urls;
  }
  
  public ArrayList<String[]> getMappings() {
    return mappings;
  }

}
