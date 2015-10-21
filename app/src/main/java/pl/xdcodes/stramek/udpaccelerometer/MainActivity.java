package pl.xdcodes.stramek.udpaccelerometer;

import android.content.Context;
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

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = MainActivity.class.getName();

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senMagnetometer;

    private TextView accelerometerX;
    private TextView accelerometerY;
    private TextView accelerometerZ;
    private TextView magnetometerA;
    private TextView magnetometerB;
    private TextView magnetometerC;

    private FloatingActionButton fab;
    private long lastUpdate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Context context = getApplicationContext();
        senSensorManager = (SensorManager) getSystemService(context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senMagnetometer = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        senSensorManager.registerListener(this, senMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        accelerometerX = (TextView) findViewById(R.id.accelerometerX);
        accelerometerY = (TextView) findViewById(R.id.accelerometerY);
        accelerometerZ = (TextView) findViewById(R.id.accelerometerZ);
        magnetometerA = (TextView) findViewById(R.id.magnetometerA);
        magnetometerB = (TextView) findViewById(R.id.magnetometerB);
        magnetometerC = (TextView) findViewById(R.id.magnetometerC);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectDialog dialog = new ConnectDialog();
                dialog.show(getSupportFragmentManager(), null);
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

            //TODO Dialog jakiś

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
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        senSensorManager.registerListener(this, senMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] accelerometer = event.values;

            Long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                lastUpdate = curTime;

                accelerometerX.setText(String.valueOf(Math.round(accelerometer[0] * 100.0) / 100.0));
                accelerometerY.setText(String.valueOf(Math.round(accelerometer[1] * 100.0) / 100.0));
                accelerometerZ.setText(String.valueOf(Math.round(accelerometer[2] * 100.0) / 100.0));
            }
        }

        if (mySensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float[] magnetometer = event.values;

                magnetometerA.setText(String.valueOf(Math.round(magnetometer[0] * 100.0) / 100.0));
                magnetometerB.setText(String.valueOf(Math.round(magnetometer[1] * 100.0) / 100.0));
                magnetometerC.setText(String.valueOf(Math.round(magnetometer[2] * 100.0) / 100.0));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
