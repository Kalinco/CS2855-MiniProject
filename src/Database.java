import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
  private String url;
  private Connection conn;
  
  public Database(String url){
    this.url = url;
  }
  
  public boolean login(String username, String password) {
    try {
      conn = DriverManager.getConnection(url, username, password);
      return true;
    } catch (SQLException e) {
      return false;

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
      System.exit(0);

    }
  }

  public void dropTable(String table) {
    try {
      Statement st = conn.createStatement();
      st.execute("DROP TABLE IF EXISTS "+ table);
      st.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(0);

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
        if (row[i] == "null") {
          sql += row[i];
        }
        else {
          sql += "'" +row[i] + "'";
        }
      }
      sql += ")";
      //System.out.println(sql);
      st.executeUpdate(sql);
      st.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  public void insertRows(String tableName, String[] headers, String[][] rows) {
    for (int i = 0; i < rows.length; i++) {
      insertRow(tableName, headers, rows[i]);
    }
  }
  
  public String[][] selectQuery(String select, String from, String where, String post){
    String sql = "SELECT " + select + " FROM " + from + " WHERE " + where + " " + post;
    return query(sql);
    
  }
  
  private String[][] query(String sql) {
    try {
      //System.out.println(sql);
      Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      if (st.execute(sql)) {
        String[][] result = processResultSet(st.getResultSet());
        return result;
      }else {
        return null;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(0);
    }
    return null;
  }
  
  private String[][] processResultSet(ResultSet rs) {
    try {
      int columns = rs.getMetaData().getColumnCount();
      int rows = 0;
      if (rs.last()) {
        rows = rs.getRow();
        rs.beforeFirst();
      }
      String[][] contents = new String[rows][columns];
      for (int j = 1; rs.next() && j<=rows;j++) {
        for (int i = 1; i <= columns; i++) {
          contents[j-1][i-1] = rs.getString(i);
        }
      }
      rs.close();
      return contents;
    }catch (SQLException e) {
      e.printStackTrace();
      System.exit(0);
    }
    return null;
    
  }
}
