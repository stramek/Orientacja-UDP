package pl.xdcodes.stramek.orientacjaudp;

import android.os.AsyncTask;

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
import pl.xdcodes.stramek.orientacjaudp.algorithms.Complementary;

public class UDP extends AsyncTask<String, Void, String> {

    private final String TAG = UDP.class.getName();

    private String ip = "";
    private int port = 0;

    private InetAddress local;
    private DatagramSocket s;

    private double[] newRotation;

    private float[] dataToSend = new float[10];

    private float[] dataToAnalyze = new float[17];

    public static final int REFRESH_RATE = 20;

    private ScheduledFuture result;

    private final int RAW_DATA = 1;
    private final int ACCELEROMETER = 2;
    private final int COMPLEMENTARY = 3;

//    private double time = 0;

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

        newRotation = new double[3];
        Arrays.fill(newRotation, 0);

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        result =  exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                /*Log.d(TAG, "" + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();*/

                try {
                    byte[] b;

                    if(MainActivity.rawData.isChecked()) {
                        Arrays.fill(dataToSend, 0.0f);
                        dataToSend = Arrays.copyOf(MainActivity.values, 10);
                        dataToSend[9] = RAW_DATA;
                    }

                    if(MainActivity.accelerometer.isChecked()) {
                        Arrays.fill(dataToSend, 0.0f);
                        Accelerometer aa = new Accelerometer(MainActivity.values);
                        dataToSend[0] = (float) Math.toDegrees(aa.getRadian().getAlpha());
                        dataToSend[1] = (float) Math.toDegrees(aa.getRadian().getBetta());
                        dataToSend[9] = ACCELEROMETER;
                    }

                    if(MainActivity.complementary.isChecked()) {
                        Arrays.fill(dataToSend, 0.0f);
                        Complementary ca = new Complementary(MainActivity.values, newRotation);
                        newRotation[0] = ca.getRadian().getAlpha();
                        newRotation[1] = ca.getRadian().getBetta();
                        newRotation[2] = ca.getRadian().getGamma();
                        for (int i = 0; i < 3; i++) {
                            dataToSend[i] = (float) Math.toDegrees(newRotation[i]);
                        }
                        dataToSend[9] = COMPLEMENTARY;
                    }

                    if(MainActivity.dataToAnalyze.isChecked()) {
                        Arrays.fill(dataToSend, 0.0f);

                        Complementary test = new Complementary(MainActivity.values, newRotation);

                        for (int i = 0; i < MainActivity.values.length - 1; i++) {
                            dataToAnalyze[i] = MainActivity.values[i];
                        }
                        for (int i = 0; i < test.daneDoAnalizy().getData().length; i++) {
                            dataToAnalyze[MainActivity.values.length - 1 + i] = test.daneDoAnalizy().getData()[i];
                        }
                    }

                    if(MainActivity.rawData.isChecked() || MainActivity.accelerometer.isChecked() || MainActivity.complementary.isChecked()) {
                        b = FloatArray2ByteArray(dataToSend);
                        DatagramPacket p = new DatagramPacket(b, b.length, local, port);
                        s.send(p);
                    }

                    if(MainActivity.dataToAnalyze.isChecked()) {
                        b = FloatArray2ByteArray(dataToAnalyze);
                        DatagramPacket p = new DatagramPacket(b, b.length, local, port);
                        s.send(p);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
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