package pl.xdcodes.stramek.orientacjaudp;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import pl.xdcodes.stramek.orientacjaudp.algorithms.Accelerometer;
import pl.xdcodes.stramek.orientacjaudp.algorithms.Angles;
import pl.xdcodes.stramek.orientacjaudp.algorithms.Complementary;
import pl.xdcodes.stramek.orientacjaudp.algorithms.MadgwickAHRS;
import pl.xdcodes.stramek.orientacjaudp.algorithms.MadgwickIMU;

public class UDP extends AsyncTask<String, Void, String> {

    @SuppressWarnings("Unused")
    private final String TAG = UDP.class.getName();

    private String ip = "";
    private int port = 0;

    private InetAddress local;
    private DatagramSocket s;

    private double[] newRotation = new double[3];

    private float[] dataToSend = new float[10];

    public static final long TIMES_FASTER = 5;
    public static final long REFRESH_RATE = 20 / TIMES_FASTER;

    private ScheduledFuture result;
    private ScheduledFuture madg;

    private final int RAW_DATA = 1;
    private final int ACCELEROMETER = 2;
    private final int COMPLEMENTARY = 3;
    private final int MADGWICK = 4;
    private final int MADGWICK_IMU = 5;

    private int lastAlgorithm = 0;

    private float[] madgwickResult = new float[4];

    private int multiply = 0;

    UDP(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void stopUDP() {
        result.cancel(false);
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            local = InetAddress.getByName(ip);
            s = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Accelerometer aa = new Accelerometer();
        final Complementary ca = new Complementary();
        final MadgwickAHRS mahrs = new MadgwickAHRS();
        final MadgwickIMU mimu = new MadgwickIMU();

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        result =  exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                if(dataToSend[9] != lastAlgorithm)
                    Arrays.fill(dataToSend, 0.0f);
                lastAlgorithm = (int) dataToSend[9];

                if(MainActivity.rawData.isChecked()) {
                    dataToSend = Arrays.copyOf(MainActivity.values, 10);
                    dataToSend[9] = RAW_DATA;
                }

                if(MainActivity.accelerometer.isChecked()) {
                    Angles a = aa.doMath(MainActivity.getValues());
                    dataToSend[0] = (float) Math.toDegrees(a.getAlpha());
                    dataToSend[1] = (float) Math.toDegrees(a.getBetta());
                    dataToSend[9] = ACCELEROMETER;
                }

                if(MainActivity.complementary.isChecked()) {
                    Angles a = ca.doMath(MainActivity.getValues());
                    newRotation[0] = a.getAlpha();
                    newRotation[1] = a.getBetta();
                    newRotation[2] = a.getGamma();

                    for (int i = 0; i < 3; i++)
                        dataToSend[i] = (float) Math.toDegrees(newRotation[i]);

                    dataToSend[9] = COMPLEMENTARY;
                }

                if(MainActivity.madgwick.isChecked()) {
                    madgwickResult = mahrs.doMath(MainActivity.getValues());
                    for (int i = 0; i < madgwickResult.length; i++) {
                        dataToSend[i] = madgwickResult[i];
                    }
                    dataToSend[9] = MADGWICK;
                }

                if(MainActivity.madgwickIMU.isChecked()) {
                    madgwickResult = mimu.doMath(MainActivity.getValues());
                    for (int i = 0; i < madgwickResult.length; i++) {
                        dataToSend[i] = madgwickResult[i];
                    }
                    dataToSend[9] = MADGWICK_IMU;
                }

                if(multiply >= TIMES_FASTER) {
                    byte[] b = FloatArray2ByteArray(dataToSend);
                    DatagramPacket p = new DatagramPacket(b, b.length, local, port);
                    try {
                        s.send(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    multiply = 0;
                }
                multiply++;
            }
        }, 0, REFRESH_RATE, TimeUnit.MILLISECONDS);

        return "Executed";
    }

    public byte[] FloatArray2ByteArray(float[] values) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);

        for (float value : values) {
            buffer.putFloat(value);
        }

        return buffer.array();
    }
}