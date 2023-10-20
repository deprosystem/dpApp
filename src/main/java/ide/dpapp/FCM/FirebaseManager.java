package ide.dpapp.FCM;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Message.Builder;
import ide.dpapp.servlets.Constants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class FirebaseManager {
    
    public void FcmClient(String serviceAccountFile) {
        if (Constants.isFirebaseAPI) return;
System.out.println("FcmClient serviceAccountFile="+serviceAccountFile);
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(serviceAccountFile.getBytes("UTF-8"));
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(is))
                .build();
            FirebaseApp.initializeApp(options);
            Constants.isFirebaseAPI = true;
        } catch (IOException e) {
            System.out.println("FcmClient error=" + e);
        }
    }

    public void subscribeToTopic(String token, String topic) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().subscribeToTopic(Arrays.asList(token), topic);
    }

    public void unsubscribeFromTopic(String token, String topic) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Arrays.asList(token), topic);
    }
    
    public void sendMessageToTopic(String topic, String title, String body, Map<String, String> data) throws FirebaseMessagingException {
        Builder messageBuilder;
        if (data == null) {
            messageBuilder = Message.builder();
        } else {
            messageBuilder = Message.builder().putAllData(data);
        }
        Message message = messageBuilder
                .putData("title", title)
                .putData("body", body)
                .putData("message", body)
                //.setNotification(new Notification(title, body))
                //.setAndroidConfig(AndroidConfig.builder()
                //        .setNotification(AndroidNotification.builder()
                //                //.setIcon(ANDROID_NEWS_ICON_RESOURCE)
                //                .build())
                //        .build())
                //.setApnsConfig(ApnsConfig.builder()
                //        .setAps(Aps.builder()
                //                //.setBadge(APNS_NEWS_BADGE_RESOURCE)
                //                .build())
                //        .build())
                //.setWebpushConfig(WebpushConfig.builder()
                //        //.setNotification(new WebpushNotification(null, null, WEBPUSH_NEWS_ICON_URL))
                //        .build())
                .setTopic(topic)
                .build();
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully send message: " + response);
    }
    
    public void sendMessageToToken(String token, String title, String body, Map<String, String> data) throws FirebaseMessagingException {
        Builder messageBuilder;
        if (data == null) {
            messageBuilder = Message.builder();
        } else {
            messageBuilder = Message.builder().putAllData(data);
        }
        Message message = messageBuilder
                .putData("title", title)
                .putData("body", body)
                .putData("message", body)
                //.setNotification(new Notification(title, body))
                //.setAndroidConfig(AndroidConfig.builder()
                //        .setNotification(AndroidNotification.builder()
                //                //.setIcon(ANDROID_NEWS_ICON_RESOURCE)
                //                .build())
                //        .build())
                //.setApnsConfig(ApnsConfig.builder()
                //        .setAps(Aps.builder()
                //                //.setBadge(APNS_NEWS_BADGE_RESOURCE)
                //                .build())
                //        .build())
                //.setWebpushConfig(WebpushConfig.builder()
                //        //.setNotification(new WebpushNotification(null, null, WEBPUSH_NEWS_ICON_URL))
                //        .build())
                .setToken(token)
                .build();
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully send message: " + response);
    }
}