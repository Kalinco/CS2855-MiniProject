import java.util.Scanner;

public class Main {
  private static int queryNumber = 1;
  
  /**
   * Main method.
   * @param args initialised with
   */
  public static void main(String[] args) {
    Loader loader = new Loader();
    Scanner scan = new Scanner(System.in);
    loader.loadUrls();
    loader.loadMappings();
    Database db;
    do {
      System.out.println("Enter database to connect to (of format jdbc:postgresql://<url>)");
      System.out.print("> ");
      String databaseAddress = scan.nextLine();
      if (databaseAddress.equals("debug")) {
        db = new Database("jdbc:postgresql://localhost/CS2855");
        db.login("test", "password");
      } else {
        System.out.println("Enter database username");
        System.out.print("> ");
        String username = scan.nextLine();
        System.out.print("Enter database password\n> ");
        String password = scan.nextLine();
        
        db = new Database(databaseAddress);
        if (!db.login(username, password)) {
          System.out.println("Connection failed to establish, please check and try again\n");
        }
      }
    } while (!db.isConnected());
    scan.close();
    if (db.isConnected()) {
      setup(db, loader);
      String[][] result = db.selectQuery(
          "rankings.ranking,websites.domain,websites.sub_dot,dots.text",
          "websites,rankings,dots",
          "rankings.ws_id=websites.ws_id AND"
          + "websites.last_dot=dots.dot_id AND rankings.ranking <= 10",
          "ORDER BY rankings.ranking ASC");
      display(result);
      
      result = db.selectQuery("websites.sub_dot,dots.text",
          "websites,rankings,dots",
          "rankings.ws_id=websites.ws_id AND websites.last_dot=dots.dot_id",
          "GROUP BY websites.sub_dot,dots.text ORDER BY MIN(rankings.ranking) LIMIT 10");
      display(result);
      
      result = db.selectQuery("dots.meaning",
          "websites,rankings,dots",
          "rankings.ws_id=websites.ws_id AND"
          + "websites.last_dot=dots.dot_id AND dots.meaning!='Not supplied'",
          "GROUP BY dots.meaning ORDER BY MIN(rankings.ranking) LIMIT 10");
      display(result);
      
      result = db.selectQuery("websites.domain",
          "websites,rankings,dots",
          "rankings.ws_id=websites.ws_id AND websites.last_dot=dots.dot_id",
          "GROUP BY websites.domain"
          + "HAVING COUNT(websites.domain)>1 ORDER BY MIN(rankings.ranking) LIMIT 10");
      display(result);
      
    }
  }

  private static void display(String[][] data) {
    System.out.println("################# Query " + queryNumber++ + " ################");
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data[i].length; j++) {
        if (!data[i][j].equals("null")) {
          System.out.print(data[i][j] + "\t");
        }
      }
      System.out.println();
    }
    System.out.println("\n");
  }

  private static void setup(Database db, Loader loader) {
    String[][] dotColumns = {
        {"dot_id","SERIAL", "PRIMARY KEY"}, 
        {"text","VARCHAR(10)"},
        {"meaning","VARCHAR(255)"}
    };
    db.dropTable("rankings");
    db.dropTable("websites");
    db.dropTable("dots");
    
    db.createTable("dots", dotColumns);
    String[] dotHeadings = {"text", "meaning"};
    db.insertRows("dots", dotHeadings, loader.getMappings());
    
    String[][] websiteDataCombined = loader.getUrls();
    String[][] websiteData = new String[websiteDataCombined.length][3];
    String[][] rankingData = new String[websiteDataCombined.length][1];
    for (int i = 0; i < websiteDataCombined.length; i++) {
      for (int j = 0; j < websiteDataCombined[i].length; j++) {
        switch (j) {
          case 0:
            rankingData[i][0] = websiteDataCombined[i][j];
            break;
          case 1:
            websiteData[i][0] = websiteDataCombined[i][j];
            break;
          case 2:
            if (websiteDataCombined[i].length == 3) {
              String[][] id = db.selectQuery("dot_id", "dots", "text = '"
+ websiteDataCombined[i][j] + "'", "");
              if (id.length == 0) {
                String[] data = {websiteDataCombined[i][j], "Not supplied"};
                db.insertRow("dots", dotHeadings, data);
                websiteData[i][2] = db.selectQuery("dot_id", "dots", "text = '"
                + websiteDataCombined[i][j] + "'", "")[0][0];
              } else {
                websiteData[i][2] = id[0][0];
              }
            } else {
              websiteData[i][1] = websiteDataCombined[i][j];
            }
            break;
          case 3:
            String[][] id = db.selectQuery("dot_id", "dots", "text = '"
          + websiteDataCombined[i][j] + "'", "");
            if (id.length == 0) {
              String[] data = {websiteDataCombined[i][j], "Not supplied"};
              db.insertRow("dots", dotHeadings, data);
              websiteData[i][2] = db.selectQuery("dot_id", "dots", "text = '"
              + websiteDataCombined[i][j] + "'", "")[0][0];
            } else {
              websiteData[i][2] = id[0][0];
            }
            break;
          default:
            break;
        }
      }
    }
    String[][] websiteColumns = {
        {"ws_id","SERIAL", "PRIMARY KEY"}, 
        {"domain","VARCHAR(50)"},
        {"sub_dot","VARCHAR(11)"},
        {"last_dot","INT REFERENCES dots(dot_id)"}
    };
    String[][] rankingColumns = {
        {"ranking","INT", "PRIMARY KEY"}, 
        {"ws_id","SERIAL REFERENCES websites(ws_id)"}
    };
    
    db.createTable("websites", websiteColumns);
    String[] wsHeadings = {"domain", "sub_dot", "last_dot"};
    db.insertRows("websites", wsHeadings, websiteData);
    
    db.createTable("rankings", rankingColumns);
    String[] headings = {"ranking"};
    db.insertRows("rankings", headings, rankingData);
    
  }

}
