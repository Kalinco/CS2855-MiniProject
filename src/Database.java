import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
  private String url;
  private Connection conn;
  
  public Database(String url) {
    this.url = url;
  }
  
  /**
   * Logs into the database.
   * @param username to login
   * @param password to login
   * @return result
   */
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
  
  /**
   * Creates a database table.
   * @param tableName to create
   * @param headings to use
   */
  public void createTable(String tableName, String[][] headings) {
    try {
      String sql = "CREATE TABLE " + tableName + " (";
      for (int i = 0; i < headings.length; i++)  {
        for (int j = 0; j < headings[i].length; j++) {
          if (i != 0 || j != 0) {
            sql += " ";
          }
          sql += headings[i][j];
        }
        sql += ",";
      }
      sql = sql.substring(0, sql.length() - 1);
      sql += ")";
      Statement st = conn.createStatement();
      st.execute(sql);
      st.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.exit(0);

    }
  }

  /**
   * Removes a table.
   * @param table name to remove
   */
  public void dropTable(String table) {
    try {
      Statement st = conn.createStatement();
      st.execute("DROP TABLE IF EXISTS " +  table);
      st.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(0);

    }
  }

  /**
   * Inserts a row of data into a table.
   * @param tableName to insert into
   * @param headings being inserted
   * @param row being inserted
   */
  public void insertRow(String tableName,String[] headings, String[] row) {
    try {
      String sql = "INSERT INTO " + tableName + " (";
      for (int i = 0; i < headings.length; i++) {
        if (i != 0) {
          sql += ", ";
        }
        sql += headings[i];
      }
          
      sql += ") VALUES (";
      for (int i = 0; i < row.length; i++) {
        if (i != 0) {
          sql += ", ";
        }
        if (row[i] == "null") {
          sql += row[i];
        } else {
          sql += "'" + row[i] + "'";
        }
      }
      sql += ")";
      //System.out.println(sql);
      Statement st = conn.createStatement();
      st.executeUpdate(sql);
      st.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  /**
   * Inserts multiple rows into table.
   * @param tableName to insert into
   * @param headers to insert
   * @param rows to insert
   */
  public void insertRows(String tableName, String[] headers, String[][] rows) {
    for (int i = 0; i < rows.length; i++) {
      insertRow(tableName, headers, rows[i]);
    }
  }
  
  /**
   * Queries the database with a select query.
   * @param select portion
   * @param from portion
   * @param where portion
   * @param post portion
   * @return result
   */
  public String[][] selectQuery(String select, String from, String where, String post) {
    String sql = "SELECT " + select + " FROM " + from + " WHERE " + where + " " + post;
    return query(sql);
    
  }
  
  private String[][] query(String sql) {
    try {
      //System.out.println(sql);
      Statement st = conn.createStatement(
          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      if (st.execute(sql)) {
        String[][] result = processResultSet(st.getResultSet());
        return result;
      } else {
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
      for (int j = 1; rs.next() && j <= rows; j++) {
        for (int i = 1; i <= columns; i++) {
          contents[j - 1][i - 1] = rs.getString(i);
        }
      }
      rs.close();
      return contents;
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(0);
    }
    return null;
    
  }
}
