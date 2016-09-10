package com.ztech.fakecallm7;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences appSettings;

    private SharedPreferences.Editor preferencesEditor;

    private Switch showIconSwitch;

    private EditText numberToDialInput;

    private Switch maxSwitch;

    private static final ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName("com.ztech.fakecallm7", "com.ztech.fakecallm7.Launcher");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        showIconSwitch = (Switch)findViewById(R.id.showIconSwitch);

        maxSwitch = (Switch)findViewById(R.id.maximumUserPhoto);

        numberToDialInput = (EditText)findViewById(R.id.numberToDialInput);

        appSettings = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        preferencesEditor = appSettings.edit();

        boolean showIcon = appSettings.getBoolean("showIcon", true);

        boolean maxPhoto = appSettings.getBoolean("maxPhoto", false);

        String numberToDial = appSettings.getString("numberToDial", "111");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showIconSwitch.setChecked(showIcon);

        maxSwitch.setChecked(maxPhoto);

        numberToDialInput.setEnabled(!showIcon);

        numberToDialInput.setText(numberToDial);

    }

    public void onClickShowIcon(View view) {

        PackageManager packageManager = getPackageManager();

        boolean showIcon = showIconSwitch.isChecked();

        if (!showIcon) {

            packageManager.setComponentEnabledSetting(LAUNCHER_COMPONENT_NAME, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        } else {

            packageManager.setComponentEnabledSetting(LAUNCHER_COMPONENT_NAME, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        }

        Toast.makeText(this, "You may need to restart your launcher for action to take effect", Toast.LENGTH_SHORT).show();

        preferencesEditor.putBoolean("showIcon", showIcon);

        numberToDialInput.setEnabled(!showIconSwitch.isChecked());

    }

    public void onClickMax(View view) {

        boolean maxPhoto = maxSwitch.isChecked();

        preferencesEditor.putBoolean("maxPhoto", maxPhoto);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        String numberToDial = numberToDialInput.getText().toString();

        preferencesEditor.putString("numberToDial", numberToDial);

        preferencesEditor.apply();

    }

}
