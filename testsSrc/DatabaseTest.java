import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatabaseTest {
  private Database db;
  
  @BeforeEach
  public void setUp(){
    db = new Database("jdbc:postgresql://localhost/CS2855");
  }

  @Test
  void test() {
    assertFalse(db.isConnected());
  }
  
  @Test
  void test2() {
    db.login("test", "password");
    assertTrue(db.isConnected());
  }
  
  @Test
  void test3() {
    String[][] headings = {
        {"id","int"}, 
        {"name","VARCHAR(255)"},
        
    };
    db.login("test", "password");
    db.dropTable("test");
    db.createTable("test", headings);
  }
  
  @Test
  void test4() {
    String[][] headings = {
        {"id","SERIAL", "PRIMARY KEY"}, 
        {"name","VARCHAR(255)"}
    };
    String[] headers = {
        "name"
    };
    db.login("test", "password");
    
    db.dropTable("test2");
    db.createTable("test2", headings);
    String[] row = {"test"};
    db.insertRow("test2", headers, row);
  }
  
  @Test
  void test5() {
    String[][] headings = {
        {"id","SERIAL", "PRIMARY KEY"}, 
        {"name","VARCHAR(255)"}
    };
    String[] headers = {
        "name"
    };
    db.login("test", "password");
    
    db.dropTable("test3");
    db.createTable("test3", headings);
    String[][] rows = {{"test"},{"test2"},{"test3"}};
    db.insertRows("test3", headers, rows);
    String[][] result = db.selectQuery("name", "test3", "id > 1", "");
    assertEquals(result[0][0],"test2");
    assertEquals(result[1][0],"test3");
  }

}
