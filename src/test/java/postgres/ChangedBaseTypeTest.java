package postgres;

import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangedBaseTypeTest {
  @Test
  public void showChange() throws Exception {
    String url = "jdbc:postgresql://localhost:5432/it_ais_longterm?user=integrationtest&password=integrationtest";
    url = "jdbc:postgresql://localhost:5432/?user=postgres&password=postgres&loggerLevel=TRACE&loggerFile=pgjdbc-trace.log ";
    Connection connection = DriverManager.getConnection(url);
    try {
      connection.createStatement().executeUpdate("DROP TABLE IF EXISTS X");
      connection.createStatement().executeUpdate("DROP TYPE IF EXISTS flag ");
      connection.createStatement().executeUpdate("CREATE TYPE flag AS ENUM('duplicate','spike')");
      connection.createStatement().executeUpdate("CREATE table X(id INT primary key , flags flag[] NULL)");
      connection.createStatement().executeUpdate("insert into X values (1, '{duplicate}')");

      ResultSet result = connection.createStatement().executeQuery("select * from X");
      ResultSetMetaData metaData = result.getMetaData();
      System.out.println("Index: $i, column name: [${metadata.getColumnName(i)}] type: [ " +
          result.getMetaData().getColumnType(2) + ", typeName: [" + metaData.getColumnTypeName(2));
      result.next();
      Array array = result.getArray(2);
      System.out.println("Array: base type name: " + array.getBaseTypeName() + ", base type: " + array.getBaseType());
      Object[] arr = (Object[]) array.getArray();
      for (Object a : arr) {
        System.out.println("Element: Value=" + a + ", Type: " + a.getClass());
      }
      assertEquals(String.class, arr[0].getClass());
    } finally {
      connection.close();
    }
  }
}
