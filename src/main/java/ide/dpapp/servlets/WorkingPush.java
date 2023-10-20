package ide.dpapp.servlets;

import com.google.firebase.messaging.FirebaseMessagingException;
import ide.dpapp.FCM.FirebaseManager;
import ide.dpapp.db.PushDB;
import ide.dpapp.entity.DataPush;
import ide.dpapp.entity.DataServlet;
import ide.dpapp.entity.ForSubsctibePush;
import ide.dpapp.entity.Push;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "WorkingPush", urlPatterns = {"/push/*"})
public class WorkingPush extends BaseServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, DataServlet ds) {
            PushDB pushDB = new PushDB(request);
            Push push;
            String schema = ds.schema;
            String res;
            String data = null;
            Record rec = null;
            switch (ds.query) {
                case "/push/save":
                    push = null;
                    try {
                        data = getStringRequest(request);
//                        push = gson.fromJson(data, Push.class);
                    } catch (IOException e) {
                        sendError(response, ERR.PROF_ERR + e.toString());
                    }
                    if (data != null) {
                        res = pushDB.savePush(data, schema);
                        if (res.length() > 0) {
                            sendError(response, "Error save push: " + res);
                        } else {
                            sendResultOk(response);
                        }
                    }
                    break;
                case "/push/subscribe": 
                    String token = request.getHeader("push-token");
                    String topic;
                    try {
                        data = getStringRequest(request);
//                        push = gson.fromJson(data, Push.class);
                    } catch (IOException e) {
                        sendError(response, "subscribe error: " + e.toString());
                    }
                    if (data != null && data.length() > 0) {
                        DataPush dp = gson.fromJson(data, DataPush.class);
                        topic = dp.name;
                        schema = dp.schema;
//System.out.println("subscribe topic="+topic+"<< token="+token+"<< data="+data+"<<");
                        ForSubsctibePush fsp= pushDB.getParamForSubscribe(schema, topic);
//System.out.println("subscribe topic="+topic+"<< token="+token+"<< fsp.key="+fsp.key);
                        FirebaseManager fm = new FirebaseManager();
                        fm.FcmClient(fsp.key);
                        try {
                            fm.subscribeToTopic(token, topic);
                        } catch (FirebaseMessagingException ex) {
                            sendError(response, "subscribeToTopic error: "+ex);
                        }
                        sendResultOk(response);
                    } else {
                        sendError(response, "subscribeToTopic error: No data");
                    }
                    break;

                case "/push/unsubscribe":
                    token = request.getHeader("push-token");
                    try {
                        data = getStringRequest(request);
//                        push = gson.fromJson(data, Push.class);
                    } catch (IOException e) {
                        sendError(response, "subscribe error: " + e.toString());
                    }
                    if (data != null && data.length() > 0) {
                        DataPush dp = gson.fromJson(data, DataPush.class);
                        topic = dp.name;
                        schema = dp.schema;
//System.out.println("subscribe topic="+topic+"<< token="+token+"<< data="+data+"<<");
                        ForSubsctibePush fsp= pushDB.getParamForSubscribe(schema, topic);
//System.out.println("subscribe topic="+topic+"<< token="+token+"<< fsp.key="+fsp.key);
                        FirebaseManager fm = new FirebaseManager();
                        fm.FcmClient(fsp.key);
                        try {
                            fm.unsubscribeFromTopic(token, topic);
                        } catch (FirebaseMessagingException ex) {
                            sendError(response, "subscribeToTopic error: "+ex);
                        }
                        sendResultOk(response);
                    } else {
                        sendError(response, "subscribeToTopic error: No data");
                    }
                    break;
            }
    }
}
