public class Main {
  private static int queryNumber = 1;
  public static void main(String[] args) {
    Loader loader = new Loader();
    loader.loadUrls();
    loader.loadMappings();
    Database db = new Database("jdbc:postgresql://localhost/CS2855");
    db.login("test", "password");
    if(db.isConnected()) {
      //setup(db, loader);
      String[][] result = db.selectQuery("rankings.ranking,websites.domain,websites.sub_dot,dots.text",
          "websites,rankings,dots",
          "rankings.ws_id=websites.ws_id AND websites.last_dot=dots.dot_id AND rankings.ranking <= 10",
          "ORDER BY rankings.ranking ASC");
      display(result);
      
      result = db.selectQuery("websites.sub_dot,dots.text",
          "websites,rankings,dots",
          "rankings.ws_id=websites.ws_id AND websites.last_dot=dots.dot_id",
          "GROUP BY websites.sub_dot,dots.text ORDER BY MIN(rankings.ranking) LIMIT 10");
      display(result);
      
      result = db.selectQuery("dots.meaning",
          "websites,rankings,dots",
          "rankings.ws_id=websites.ws_id AND websites.last_dot=dots.dot_id AND dots.meaning!='Not supplied'",
          "GROUP BY dots.meaning ORDER BY MIN(rankings.ranking) LIMIT 10");
      display(result);
      
      result = db.selectQuery("websites.domain",
          "websites,rankings,dots",
          "rankings.ws_id=websites.ws_id AND websites.last_dot=dots.dot_id",
          "GROUP BY websites.domain HAVING COUNT(websites.domain)>1 ORDER BY MIN(rankings.ranking) LIMIT 10");
      display(result);
      
    }else {
      int i;
    }
  }

  private static void display(String[][] data) {
    System.out.println("################# Query "+ queryNumber++ +" ################");
    for (int i=0; i < data.length; i++) {
      for (int j=0; j < data[i].length; j++) {
        if (!data[i][j].equals("null")) {
          System.out.print(data[i][j] + "\t");
        }
      }
      System.out.println();
    }
    System.out.println("\n");
  }

  private static void setup(Database db, Loader loader) {
    String[][] website_columns = {
        {"ws_id","SERIAL", "PRIMARY KEY"}, 
        {"domain","VARCHAR(50)"},
        {"sub_dot","VARCHAR(11)"},
        {"last_dot","INT REFERENCES dots(dot_id)"}
    };
    String[][] ranking_columns = {
        {"ranking","INT", "PRIMARY KEY"}, 
        {"ws_id","SERIAL REFERENCES websites(ws_id)"}
    };
    String[][] dot_columns = {
        {"dot_id","SERIAL", "PRIMARY KEY"}, 
        {"text","VARCHAR(10)"},
        {"meaning","VARCHAR(255)"}
    };
    db.dropTable("rankings");
    db.dropTable("websites");
    db.dropTable("dots");
    
    db.createTable("dots", dot_columns);
    String[] dot_headings = {"text", "meaning"};
    db.insertRows("dots", dot_headings, loader.getMappings());
    
    String[][] website_data_combined = loader.getUrls();
    String[][] website_data = new String[website_data_combined.length][3];
    String[][] ranking_data = new String[website_data_combined.length][1];
    for (int i = 0; i < website_data_combined.length; i++) {
      for (int j=0; j < website_data_combined[i].length; j++) {
        switch(j) {
        case 0:
          ranking_data[i][0] = website_data_combined[i][j];
          break;
        case 1:
          website_data[i][0] = website_data_combined[i][j];
          break;
        case 2:
          if(website_data_combined[i].length == 3) {
            String[][] id = db.selectQuery("dot_id", "dots", "text = '" + website_data_combined[i][j] +"'", "");
            if (id.length == 0) {
              String[] data = {website_data_combined[i][j], "Not supplied"};
              db.insertRow("dots", dot_headings, data);
              website_data[i][2] = db.selectQuery("dot_id", "dots", "text = '" + website_data_combined[i][j] +"'", "")[0][0];
            }else {
              website_data[i][2] = id[0][0];
            }
          }else {
            website_data[i][1] = website_data_combined[i][j];
          }
          break;
        case 3:
          String[][] id = db.selectQuery("dot_id", "dots", "text = '" + website_data_combined[i][j] +"'", "");
          if (id.length == 0) {
            String[] data = {website_data_combined[i][j], "Not supplied"};
            db.insertRow("dots", dot_headings, data);
            website_data[i][2] = db.selectQuery("dot_id", "dots", "text = '" + website_data_combined[i][j] +"'", "")[0][0];
          }else {
            website_data[i][2] = id[0][0];
          }
          
        }
      }
    }
    
    
    db.createTable("websites", website_columns);
    String[] ws_headings = {"domain", "sub_dot", "last_dot"};
    db.insertRows("websites", ws_headings, website_data);
    
    db.createTable("rankings", ranking_columns);
    String[] headings = {"ranking"};
    db.insertRows("rankings", headings, ranking_data);
    
  }

}
