import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
  private String url;
  private Connection conn;
  
  public Database(String url){
    this.url = url;
  }
  
  public void login(String username, String password) {
    try {
      conn = DriverManager.getConnection(url, username, password);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  public boolean isConnected() {
    return (conn != null);
  }

  public void createTable(String tableName, String[][] headings){
    try {
      Statement st = conn.createStatement();
      String sql = "CREATE TABLE " + tableName + " (";
      for (int i = 0; i<headings.length;i++) {
        for (int j = 0; j <headings[i].length;j++) {
          if (i != 0 || j != 0) {
            sql += " ";
          }
          sql += headings[i][j];
        }
        sql += ",";
      }
      sql = sql.substring(0, sql.length() - 1);
      sql += ")";

      st.execute(sql);
      st.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void dropTable(String table) {
    try {
      Statement st = conn.createStatement();
      st.execute("DROP TABLE IF EXISTS "+ table);
      st.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void insertRow(String tableName,String[] headings, String[] row) {
    try {
      Statement st = conn.createStatement();
      String sql = "INSERT INTO " + tableName + " (";
      for (int i = 0; i<headings.length;i++) {
        if (i != 0) {
          sql += ", ";
        }
        sql += headings[i];
      }
          
      sql += ") VALUES (";
      for (int i = 0; i<row.length;i++) {
        if (i != 0) {
          sql += ", ";
        }
        sql += row[i];
      }
      sql += ")";
      System.out.println(sql);
      st.execute(sql);
      st.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  

}
