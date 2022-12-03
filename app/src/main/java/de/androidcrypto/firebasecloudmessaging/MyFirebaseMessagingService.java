package de.androidcrypto.firebasecloudmessaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class was manually converted from Kotlin to Java
 * original code: MyFirebaseMessagingService.kts
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private static String token = "";

    public void subscribeTopic(Context context, String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, "subscribeTopic success: " + topic);
                        Toast.makeText(context, "subscribed to topic: " + topic, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "subscribeTopic failure: " + topic);
                        Toast.makeText(context, "failure on subscribing to topic: " + topic, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void unsubscribeTopic(Context context, String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, "unsubscribeTopic success: " + topic);
                        Toast.makeText(context, "unsubscribed from topic: " + topic, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "unsubscribeTopic failure: " + topic);
                        Toast.makeText(context, "failure on unsubscribing fromo topic: " + topic, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void sendMessage(String title, String content, String topic) throws IOException, JSONException {
        Log.i(TAG, "send message title: " + title
        + " content: " + content
                + " topic: " + topic);
        String apiEndpoint = "https://fcm.googleapis.com/fcm/send";
        String key = "AAAA94XS4eA:APA91bFHSFNsjdEOlFiIKcko2Y_vDRohRG_zTKUx1vVE-zChNi1roi7DDODLF_Cobi1jkEstGn5EfV45t6Jvn3Dh1mjRN71h9fpC-BlePVKrrKSEflCFmF1FgJDWUb4S3fTbxOudqZEm";
        //try {
            URL url = new URL(apiEndpoint);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setReadTimeout(10000);
            httpsURLConnection.setConnectTimeout(15000);
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);

            // Adding the necessary headers
        String keyString = "key=" + key;
            httpsURLConnection.setRequestProperty("authorization", keyString);
            httpsURLConnection.setRequestProperty("Content-Type", "application/json");

            // Creating the JSON with post params
            String topicString = "/topics/" + topic;
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("title", title);
            data.put("content", content);
            body.put("data", data);
            body.put("to", topicString);

            OutputStream outputStream = new BufferedOutputStream(httpsURLConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
            Log.i(TAG, "body: " + body.toString(2));
            writer.write(body.toString());
            writer.flush();
            writer.close();
            outputStream.close();
            int responseCode = httpsURLConnection.getResponseCode();
            String responseMessage = httpsURLConnection.getResponseMessage();
            Log.d(TAG, "responseMessage: " + responseMessage);
            InputStream inputStream = null;
            if (400 <= responseCode) {
                if (499 >= responseCode) {
                    httpsURLConnection.getErrorStream();
                    return;
                }
            }
            httpsURLConnection.getInputStream();
            if (responseCode == 200) {
                Log.e("Success:", "notification sent $title \n $content");
                // The details of the user can be obtained from the result variable in JSON format
            } else {
                Log.e("Error", "Error Response");
            }
/*        } catch (Exception e) {
            Log.e(TAG, "error on sending message: " + e.getMessage());
        }

 */
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("onMessageReceived: ", remoteMessage.getData().toString());
        String title = (String) remoteMessage.getData().get("title");
        String content = (String) remoteMessage.getData().get("content");
        Uri defaultSound = RingtoneManager.getDefaultUri(2);
        Intent intent = new Intent((Context) this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(getApplicationContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= 26) {
            this.checkNotificationChannel("1");
        }

        //Person person = new Person.Builder().setName("test").build();
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this.getApplicationContext(), "1");
        notification.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(defaultSound);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification.build());
    }

    @RequiresApi(26)
    private final void checkNotificationChannel(String CHANNEL_ID) {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription("CHANNEL_DESCRIPTION");
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        NotificationManager notificationManager = (NotificationManager)this.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        token = s;
        super.onNewToken(s);
    }
}
