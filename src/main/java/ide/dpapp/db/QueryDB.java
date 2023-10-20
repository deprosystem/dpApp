package ide.dpapp.db;

import ide.dpapp.entity.Query;
import ide.dpapp.entity.SqlParam;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryDB extends BaseDB {
    
    public QueryDB(HttpServletRequest request) {
        super(request);
    }
    
    public long createQuery(Query qu, String schema) {
        long res = -1;
        try (Connection connection = getDBConnection(); Statement statement = connection.createStatement()) {
            String str = "INSERT INTO " + schema + "._querys_meta (type_query, name_query, descr_query, origin_query, sql_query, param_query, err_1, err_2, list_where, orderBy, fields_result) VALUES ('"
                    + qu.type_query + "','" + qu.name_query + "','" + qu.descr_query + "','" + qu.origin_query + "','" + qu.sql_query + "','" + qu.param_query + "','" 
                    + qu.err_1 + "','" + qu.err_2 + "','" + qu.listWhere + "','" + qu.orderBy + "','" + qu.fields_result + "');";
//System.out.println("createQuery SQL="+str+"<<");
            int updateCount = statement.executeUpdate(str, Statement.RETURN_GENERATED_KEYS);
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
              if (generatedKeys.next()) {
                res = generatedKeys.getLong("id_query");
              }
              else {
                  System.out.println("createQuery Creating failed, no ID obtained.");
              }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("createQuery error="+ex);
        }
        return res;
    }
    
    public String changeQuery(Query qu, String schema) {
        String res = "";
        String strUpd = "UPDATE " + schema + "._querys_meta SET ";
        String nn = "";
        if (qu.id_query > 3) {
            nn = "', name_query='" + qu.name_query + "', descr_query='" + qu.descr_query;
        }
        strUpd += "type_query ='" + qu.type_query +  nn +  "', origin_query='" + qu.origin_query + "', fields_result='" + qu.fields_result
                + "', sql_query='" + qu.sql_query +  "', param_query='" + qu.param_query +  "', err_1='" + qu.err_1 + "', list_where='" + qu.listWhere + "', orderBy='" + qu.orderBy 
                + "' WHERE id_query = " + qu.id_query;
//System.out.println("changeQuery strUpd="+strUpd+"<<");
        try (Connection connection = getDBConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(strUpd);
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("changeQuery error="+ex);
            res = "changeQuery error="+ex;
        }
        return res;
    }
    
    public SqlParam getSqlForMobile(String sql) {
        SqlParam res = new SqlParam();
        try (Connection connection = getDBConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                res.sql_query = result.getString("sql_query");
                res.param_query = result.getString("param_query");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("getSqlForMobile error="+ex);
        }
        return res;
    }
    
    public Query getQueryMobile(String sql) {
        Query res = new Query();
        try (Connection connection = getDBConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                res.id_query = result.getLong("id_query");
                res.type_query = result.getString("type_query");
                res.name_query = result.getString("name_query");
                res.origin_query = result.getString("origin_query");
                res.sql_query = result.getString("sql_query");
                res.param_query = result.getString("param_query");
                res.err_1 = result.getString("err_1");
                res.err_2 = result.getString("err_2");
                res.listWhere = result.getString("list_where");
                res.orderBy = result.getString("orderBy");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("getQueryMobile error="+ex);
        }
        return res;
    }
    
    public String deleteQuery(String schema, long id) {
        try (Connection connection = getDBConnection(); Statement statement = connection.createStatement()) {
// delete describe query
            statement.executeUpdate("DELETE FROM " + schema + "._querys_meta" + " WHERE id_query=" + id);
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("deleteQuery error="+ex);
            return "deleteQuery error="+ex;
        }
        return "";
    }
}
