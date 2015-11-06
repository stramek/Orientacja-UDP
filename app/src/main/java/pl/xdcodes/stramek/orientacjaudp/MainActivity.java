package pl.xdcodes.stramek.orientacjaudp;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;


import pl.xdcodes.stramek.udpaccelerometer.R;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final String TAG = MainActivity.class.getName();

    private SensorManager senSensorManager;

    private Sensor sAccelerometer;
    private Sensor sMagnetometer;
    private Sensor sGyroscope;

    protected static TextView status;

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

    protected static float[] values = new float[9];

    private FloatingActionButton fab;

    protected static boolean sending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Context context = getApplicationContext();
        senSensorManager = (SensorManager) getSystemService(context.SENSOR_SERVICE);

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
                    dialog = new ConnectDialog();
                    dialog.show(getSupportFragmentManager(), null);
                } else {
                    dialog.cancelSending();
                    status.setText(getString(R.string.disconnected));
                    sending = false;
                }
            }
        });
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
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, sAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        senSensorManager.registerListener(this, sMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        senSensorManager.registerListener(this, sGyroscope, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        senSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }

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
