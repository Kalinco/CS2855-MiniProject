import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A class for loading the contents of the TopURLs and mapping files.
 * @author Arran Reedman
 *
 */
public class Loader {
  private String urlPath = "TopURLs";
  private String mappingPath = "mapping";
  
  private ArrayList<String[]> urls = new ArrayList<String[]>();
  private ArrayList<String[]> mappings = new ArrayList<String[]>();
  
  /**
   * Loads URLs into object.
   */
  public void loadUrls() {
    urls = loadFile(urlPath);
  }
  
  /**
   * Loads mappings into object.
   */
  public void loadMappings() {
    mappings = loadFile(mappingPath);
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

  /**
   * Gets URLs.
   * @return urls
   */
  public ArrayList<String[]> getUrls() {
    return urls;
  }
  
  /**
   * Gets mappings.
   * @return mappings
   */
  public ArrayList<String[]> getMappings() {
    return mappings;
  }

}
