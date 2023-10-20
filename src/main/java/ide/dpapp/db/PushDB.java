package ide.dpapp.db;

import com.google.firebase.messaging.FirebaseMessagingException;
import ide.dpapp.FCM.FirebaseManager;
import ide.dpapp.entity.ForSubsctibePush;
import ide.dpapp.entity.Notif;
import ide.dpapp.entity.Push;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PushDB extends BaseDB {

    public PushDB(HttpServletRequest request) {
        super(request);
    }
    
    public String getPush(String schema) {
        String result = "";
        try (Connection connection = getDBConnection(); Statement statement = connection.createStatement()) {
            String sql = "SELECT * FROM " + schema + "._push_meta WHERE id_push = 1";
            ResultSet res = statement.executeQuery(sql);
            while (res.next()) {
                result = res.getString("push_data");
            }
            return result;
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("getPush error="+ex.getMessage());
            return "";
        }
    }
    
    public String savePush(String push, String schema) {
        String res = "";
        String strUpd = "UPDATE " + schema + "._push_meta SET push_data ='" + push + "' WHERE id_push = 1";
        try (Connection connection = getDBConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(strUpd);
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("changeQuery error="+ex);
            res = "changeQuery error="+ex;
        }
        return res;
    }
    
    public ForSubsctibePush getParamForSubscribe(String schema, String topic) {
        ForSubsctibePush fsp = null;
        try (Connection connection = getDBConnection(); Statement statement = connection.createStatement()) {
            String sql = "SELECT * FROM " + schema + "._push_meta WHERE id_push = 1";
            ResultSet res = statement.executeQuery(sql);
            res.next();
            String result = res.getString("push_data");
            Notif item;
            Push push;
            List<Notif> lp;
            if (result != null && result.length() > 0) {
                push = gson.fromJson(result, Push.class);
                lp = push.listPush;
                int ik = lp.size();
                int iNotif = -1;
                for (int i = 0; i < ik; i++) {
                    item = lp.get(i);
                    boolean bb = (item.name.equals(topic));
                    if (bb) {
                        iNotif = i;
                        break;
                    }
                }
                if (iNotif > -1) {
                    item = lp.get(iNotif);
                    fsp = new ForSubsctibePush();
                    fsp.key = push.json;
                    fsp.type_push = item.type;
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("sendPush error="+ex.getMessage());
        }
        return fsp;
    }
    
    public String sendPush(String schema, String table) {
        String result = "";
        try (Connection connection = getDBConnection(); Statement statement = connection.createStatement()) {
            String sql = "SELECT * FROM " + schema + "._push_meta WHERE id_push = 1";
            ResultSet res = statement.executeQuery(sql);
            res.next();
            result = res.getString("push_data");
            Notif item;
            Push push;
            List<Notif> lp;
            if (result != null && result.length() > 0) {
                push = gson.fromJson(result, Push.class);
                lp = push.listPush;
                int ik = lp.size();
                int iNotif = -1;
                for (int i = 0; i < ik; i++) {
                    item = lp.get(i);
                    boolean bb = (item.table.equals(table));
                    if (bb) {
                        iNotif = i;
                        break;
                    }
                }
                if (iNotif < 0) {
                    return "";
                }
                item = lp.get(iNotif);
                sql = "SELECT * FROM " + schema + "." + table + " ORDER BY " + item.primaryKay + " DESC LIMIT 1";
                res = statement.executeQuery(sql);
                res.next();
                String mes = item.message;
                if (item.param != null && item.param.length() > 0) {
                    mes = String.format(item.message, setFormatParam(res, item.param.split(",")));
                }
                String push_data = "";
                if (item.data != null && item.data.length() > 0){
                    push_data = setDataParam(res, item.data.split(","));
                }
                FirebaseManager fm = new FirebaseManager();
                fm.FcmClient(push.json);
                Map<String, String> dataPush = new HashMap<>();
                dataPush.put("push_type", item.name);
                dataPush.put("push_data", push_data);
                try {
                    fm.sendMessageToTopic(item.name, item.title, mes, dataPush);
                    return "";
                } catch (FirebaseMessagingException ex) {
                    System.out.println("sendPush error:" + ex.toString());
                    return "sendPush error:" + ex.toString();
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("sendPush error="+ex.getMessage());
            result = "sendPush error="+ex.getMessage();
        }
        return result;
    }
    
    public Object[] setFormatParam(ResultSet res, String[] args) {
        Object[] st = new String[args.length];
//        Object[] st = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            try {
                st[i] = res.getString(arg);
//                st[i] = res.getObject(arg);
            } catch (SQLException ex) {
                st[i] = " ";
            }
        }
        return st;
    }

    public String setDataParam(ResultSet res, String[] args) {
        String pd = "{";
        String sep = "";
        for (String arg : args) {
            try {
                String val = res.getString(arg);
                pd += sep + quote + arg + "\":\"" + val +quote;
                sep = ",";
            } catch (SQLException ex) {
                System.out.println("setDataParam error: " + ex);
            }
        }
        return pd + "}";
    }
}
