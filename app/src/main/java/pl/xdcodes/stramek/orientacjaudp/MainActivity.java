package pl.xdcodes.stramek.orientacjaudp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

import pl.xdcodes.stramek.udpaccelerometer.R;

public class MainActivity extends AppCompatActivity implements SensorEventListener, ConnectDialog.StatusDialogListener {

    private final String TAG = MainActivity.class.getName();

    private SensorManager senSensorManager;

    private Sensor sAccelerometer;
    private Sensor sMagnetometer;
    private Sensor sGyroscope;

    private TextView status;

    private TextView accelerometerX;
    private TextView accelerometerY;
    private TextView accelerometerZ;

    private TextView magnetometerX;
    private TextView magnetometerY;
    private TextView magnetometerZ;

    private TextView gyroscopeX;
    private TextView gyroscopeY;
    private TextView gyroscopeZ;

    private ConnectDialog dialog;

    public static float[] values = new float[10];

    private FloatingActionButton fab;

    private boolean sending = false;

    private PowerManager.WakeLock mWakeLock;

    private RelativeLayout algorithm;
    private View algorithmShadow;

    public static RadioButton rawData;
    public static RadioButton accelerometer;
    public static RadioButton complementary;

    @Override
    public void onFinishDialog(boolean status) {
        if(status) {
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_not_interested_white_24dp));
            this.status.setText(R.string.sending);
            algorithm.setVisibility(View.VISIBLE);
            algorithmShadow.setVisibility(View.VISIBLE);
            sending = true;
            preventFromSleep(true);
        } else {
            sending = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        algorithm = (RelativeLayout) findViewById(R.id.algorithm);
        algorithmShadow = findViewById(R.id.main_shadow_2);
        rawData = (RadioButton) findViewById(R.id.algorithm_raw_data);
        accelerometer = (RadioButton) findViewById(R.id.algorithm_accelerometer);
        complementary = (RadioButton) findViewById(R.id.algorithm_complementary);

        setSupportActionBar(toolbar);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock (PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

        senSensorManager = (SensorManager) getSystemService(getApplicationContext().SENSOR_SERVICE);

        sAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sMagnetometer = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sGyroscope = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        senSensorManager.registerListener(this, sAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        senSensorManager.registerListener(this, sMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        senSensorManager.registerListener(this, sGyroscope, SensorManager.SENSOR_DELAY_GAME);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        accelerometerX = (TextView) findViewById(R.id.accelerometerX);
        accelerometerY = (TextView) findViewById(R.id.accelerometerY);
        accelerometerZ = (TextView) findViewById(R.id.accelerometerZ);

        magnetometerX = (TextView) findViewById(R.id.magnetometerX);
        magnetometerY = (TextView) findViewById(R.id.magnetometerY);
        magnetometerZ = (TextView) findViewById(R.id.magnetometerZ);

        gyroscopeX = (TextView) findViewById(R.id.gyroscopeX);
        gyroscopeY = (TextView) findViewById(R.id.gyroscopeY);
        gyroscopeZ = (TextView) findViewById(R.id.gyroscopeZ);

        status = (TextView) findViewById(R.id.status);
        
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sending) {
                    if (isWifiConnected() || isWifiSharing()) {
                        dialog = new ConnectDialog();
                        dialog.show(getSupportFragmentManager(), null);
                    } else if (isWifiEnabled()){
                        Snackbar.make(view, getString(R.string.wait_wifi), Snackbar.LENGTH_SHORT).show();
                    } else {
                        setWifiEnabled(view);
                    }
                } else {
                    stopSending();
                    preventFromSleep(false);
                    sending = false;
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_settings_ethernet_white_24dp));
                }
            }
        });
    }

    private boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    private void setWifiEnabled(View view) {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
        Snackbar.make(view, getString(R.string.on_and_wait_wifi), Snackbar.LENGTH_LONG).show();
    }

    private boolean isWifiEnabled() {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    private boolean isWifiSharing() {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        try {
            final Method method = wifi.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifi);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void preventFromSleep(boolean b) {
        if (b) {
            mWakeLock.acquire();
        } else {
            mWakeLock.release();
        }
    }

    private void stopSending() {
        dialog.cancelSending();
        status.setText(getString(R.string.disconnected));
        algorithm.setVisibility(View.GONE);
        algorithmShadow.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about_author) {

            AboutDialog dialog = new AboutDialog();
            dialog.show(getSupportFragmentManager(), null);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sending) {
            stopSending();
            preventFromSleep(false);
            sending = false;
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_settings_ethernet_white_24dp));
            senSensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, sAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        senSensorManager.registerListener(this, sMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        senSensorManager.registerListener(this, sGyroscope, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        /*if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Toast.makeText(getApplicationContext(), "UNRELIABLE sensor!", Toast.LENGTH_SHORT).show();
            return;
        }*/

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] accelerometer = event.values;

            for (int i = 0; i < 3; i++)
                values[i] = accelerometer[i];

            accelerometerX.setText(round(accelerometer[0], 2));
            accelerometerY.setText(round(accelerometer[1], 2));
            accelerometerZ.setText(round(accelerometer[2], 2));
        }

        if (mySensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float[] magnetometer = event.values;

            for (int i = 0; i < 3; i++)
                values[i + 3] = magnetometer[i];

            magnetometerX.setText(round(magnetometer[0], 2));
            magnetometerY.setText(round(magnetometer[1], 2));
            magnetometerZ.setText(round(magnetometer[2], 2));
        }

        if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float[] gyroscope = event.values;

            for (int i = 0; i < 3; i++)
                values[i + 6] = gyroscope[i];

            gyroscopeX.setText(round(gyroscope[0], 2));
            gyroscopeY.setText(round(gyroscope[1], 2));
            gyroscopeZ.setText(round(gyroscope[2], 2));
        }
    }

    private String round(float value, int digits) {
        float pom = 1;
        assert digits >= 1;
        for (int i = 0; i < digits; i++) pom *= 10;
        return String.valueOf((Math.round(value * pom)) / pom);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}