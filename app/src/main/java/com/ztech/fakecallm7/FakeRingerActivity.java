package com.ztech.fakecallm7;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;

public class FakeRingerActivity extends AppCompatActivity {

    private static final int INCOMING_CALL_NOTIFICATION = 1001;
    private static final int MISSED_CALL_NOTIFICATION = 1002;

    private RelativeLayout main;

    private TextView callDuration;

    private ImageView contactPhoto;

    private Button answer;

    private Button decline;

    private Button endCall;

    private AudioManager audioManager;

    private long secs;

    private int duration;

    private String number;

    private String name;

    private String voice;

    private Ringtone ringtone;

    private Vibrator vibrator;

    private PowerManager.WakeLock wakeLock;

    private SharedPreferences appSettings;

    private boolean maxPhoto;

    private NotificationManager notificationManager;

    private ContentResolver contentResolver;

    private MediaPlayer voicePlayer;

    private Resources resources;

    private int currentRingerMode;

    private int currentRingerVolume;

    private int currentMediaVolume;

    final Handler handler = new Handler();

    private Runnable hangUP = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        appSettings = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);

        maxPhoto = appSettings.getBoolean("maxPhoto", false);

        if (maxPhoto) {

            setTheme(R.style.AppThemeFullTrans);

            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_fake_ringer_2);

        } else {

            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_fake_ringer);

            getSupportActionBar().hide();

        }

        Drawable contactImage;

        Window window = getWindow();

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);

        TextView phoneNumber = (TextView) findViewById(R.id.phoneNumber);

        TextView callerName = (TextView) findViewById(R.id.callerName);

        main = (RelativeLayout) findViewById(R.id.main);

        contactPhoto = (ImageView)findViewById(R.id.contactPhoto);

        callDuration = (TextView)findViewById(R.id.callDuration);

        answer = (Button)findViewById(R.id.answer);

        decline = (Button)findViewById(R.id.decline);

        endCall = (Button)findViewById(R.id.endCall);

        contentResolver = getContentResolver();

        resources = getResources();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "Tag");

        currentRingerMode = audioManager.getRingerMode();

        currentRingerVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

        currentMediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        name = extras.getString("name");

        voice = extras.getString("voice", "");

        duration = extras.getInt("duration");

        number = extras.getString("number");

        String contactImageString = extras.getString("contactImage");

        int hangUpAfter = extras.getInt("hangUpAfter");

        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        wakeLock.setReferenceCounted(false);

        nBuilder.setSmallIcon(R.drawable.call);

        nBuilder.setOngoing(true);

        nBuilder.setContentTitle(name);

        nBuilder.setContentText(resources.getString(R.string.incoming_call));

        notificationManager.notify(INCOMING_CALL_NOTIFICATION, nBuilder.build());

        handler.postDelayed(hangUP, hangUpAfter * 1000);

        muteAll();

        if (!(contactImageString == null)) {

            Uri contactImageUri = Uri.parse(contactImageString);

            try {

                InputStream contactImageStream = contentResolver.openInputStream(contactImageUri);

                contactImage = Drawable.createFromStream(contactImageStream, contactImageUri.toString());

            } catch (FileNotFoundException e) {

                contactImage = getDrawable(R.drawable.img_no_image);

            }

            contactPhoto.setImageDrawable(contactImage);


        }

        phoneNumber.setText("M: " + number);

        callerName.setText(name);

        Uri ringtoneURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneURI);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        ringtone.play();

        long[] pattern = {1000, 1000, 1000, 1000, 1000};

        vibrator.vibrate(pattern, 0);

    }

    private void playVoice() {

        if (!voice.equals("")) {

            Uri voiceURI = Uri.parse(voice);

            voicePlayer = new MediaPlayer();

            try {
                voicePlayer.setDataSource(this, voiceURI);
            } catch (Exception e) {
                e.printStackTrace();
            }

            voicePlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

            voicePlayer.prepareAsync();

            voicePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

        }

    }

    private void muteAll() {

        audioManager.setStreamMute(AudioManager.STREAM_ALARM, true);

        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

    }

    private void unMuteAll() {

        audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);

        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

    }

    public void onClickEndCall(View view) {

        stopVoice();

        finish();

    }

    public void onClickAnswerCall(View view) {

        handler.removeCallbacks(hangUP);

        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        stopRinging();

        playVoice();

        main.setBackground(getDrawable(R.drawable.bg2));

        answer.setVisibility(View.GONE);

        decline.setVisibility(View.GONE);

        endCall.setVisibility(View.VISIBLE);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                long min = (secs % 3600) / 60;

                long seconds = secs % 60;

                String dur = String.format(Locale.US, "%02d:%02d", min, seconds);

                secs++;

                callDuration.setText(dur);

                handler.postDelayed(this, 1000);

            }
        }, 10);

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }

        }, duration * 1000);


    }

    private void stopVoice() {

        if (voicePlayer != null && voicePlayer.isPlaying()) {
            voicePlayer.stop();
        }

    }

    private void stopRinging() {

        vibrator.cancel();

        ringtone.stop();

    }

    // adds a missed call to the log and shows a notification
    private void missedCall() {

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);

        Bitmap b = BitmapFactory.decodeResource(resources, R.drawable.missed_call);

        nBuilder.setSmallIcon(android.R.drawable.stat_notify_missed_call);

        nBuilder.setLargeIcon(b);

        nBuilder.setContentTitle(name);

        nBuilder.setContentText(resources.getString(R.string.missed_call));

        nBuilder.setAutoCancel(true);

        Intent showCallLog = new Intent(Intent.ACTION_VIEW);

        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, showCallLog, PendingIntent.FLAG_CANCEL_CURRENT);

        nBuilder.setContentIntent(pendingIntent);

        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);

        notificationManager.notify(MISSED_CALL_NOTIFICATION, nBuilder.build());

        CallLogUtilities.addCallToLog(contentResolver, number, 0, CallLog.Calls.MISSED_TYPE, System.currentTimeMillis());

    }

    private void incomingCall() {

        CallLogUtilities.addCallToLog(contentResolver, number, secs, CallLog.Calls.INCOMING_TYPE, System.currentTimeMillis());

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        stopVoice();

        notificationManager.cancel(INCOMING_CALL_NOTIFICATION);

        if (secs > 0) {

            incomingCall();

        } else {

            missedCall();

        }

        wakeLock.release();

        audioManager.setRingerMode(currentRingerMode);

        audioManager.setStreamVolume(AudioManager.STREAM_RING, currentRingerVolume, 0);

        stopRinging();

        unMuteAll();

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentMediaVolume, 0);

    }

    @Override
    public void onBackPressed() {

    }

}
