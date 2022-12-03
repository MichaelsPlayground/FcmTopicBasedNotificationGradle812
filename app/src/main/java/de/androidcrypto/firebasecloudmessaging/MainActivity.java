package de.androidcrypto.firebasecloudmessaging;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.json.JSONException;

import java.io.IOException;

/**
 * This class was manually converted from Kotlin to Java
 * original code: MainActivity.kts
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    com.google.android.material.textfield.TextInputLayout topic_subscribe, topic_unsubscribe;
    com.google.android.material.textfield.TextInputEditText topic_toSubscribe, topic_toUnsubscribe;
    com.google.android.material.textfield.TextInputEditText title_edt, content_edt, topic_toSend;
    com.google.android.material.button.MaterialButton send_btn;

    MyFirebaseMessagingService myFirebaseMessagingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topic_subscribe = findViewById(R.id.topic_subscribe);
        topic_unsubscribe = findViewById(R.id.topic_unsubscribe);
        topic_toSubscribe = findViewById(R.id.topic_toSubscribe);
        topic_toUnsubscribe = findViewById(R.id.topic_toUnsubscribe);
        send_btn = findViewById(R.id.send_btn);
        title_edt = findViewById(R.id.title_edt);
        content_edt = findViewById(R.id.content_edt);
        topic_toSend = findViewById(R.id.topic_toSend);

        myFirebaseMessagingService = new MyFirebaseMessagingService();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"));
                startActivityForResult(intent, 1);
            }
        }

        /**
         * The MyFirebaseMessagingService is actualy running on main thread and is blocked therefore.
         * The following 2 code lines are enabling the sending but in real production scenarios you
         * should run the code in a background thread.
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        topic_subscribe.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "subscribe to topic");
                if (!TextUtils.isEmpty(topic_toSubscribe.getText().toString())) {
                    myFirebaseMessagingService.subscribeTopic(MainActivity.this, topic_toSubscribe.getText().toString());
                }
            }
        });

        topic_unsubscribe.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "unsubscribe from topic");
                if (!TextUtils.isEmpty(topic_toUnsubscribe.getText().toString())) {
                    myFirebaseMessagingService.unsubscribeTopic(MainActivity.this, topic_toUnsubscribe.getText().toString());
                }
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "sendMessage");
                if (TextUtils.isEmpty(title_edt.getText().toString())) {
                    title_edt.setError("Fill this field");
                    return;
                }
                if (TextUtils.isEmpty(content_edt.getText().toString())) {
                    content_edt.setError("Fill this field");
                    return;
                }
                try {
                    myFirebaseMessagingService.sendMessage(
                            title_edt.getText().toString(),
                            content_edt.getText().toString(),
                            topic_toSend.getText().toString()
                    );
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}