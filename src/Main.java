public class Main {

  public static void main(String[] args) {
    Loader loader = new Loader();
    loader.loadUrls();
    loader.loadMappings();
    Database db = new Database("jdbc:postgresql://localhost/CS2855");
    db.login("test", "password");
    if(db.isConnected()) {
      setup(db, loader);

      
    }else {
      int i;
    }
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
        {"text","VARCHAR(5)"},
        {"meaning","VARCHAR(255)"}
    };
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
              website_data[i][2] = "null";
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
            website_data[i][2] = "null";
          }else {
            website_data[i][2] = id[0][0];
          }
          
        }
      }
    }
    
    db.dropTable("websites");
    db.createTable("websites", website_columns);
    String[] ws_headings = {"domain", "sub_dot", "last_dot"};
    db.insertRows("websites", ws_headings, website_data);
    
    db.dropTable("rankings");
    db.createTable("rankings", ranking_columns);
    String[] headings = {"ranking"};
    db.insertRows("rankings", headings, ranking_data);
    
  }

}
