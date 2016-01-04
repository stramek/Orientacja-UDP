package pl.xdcodes.stramek.orientacjaudp;

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

import pl.xdcodes.stramek.orientacjaudp.algorithms.Algorithms.Algorithm;
import pl.xdcodes.stramek.orientacjaudp.algorithms.Algorithms.AlgorithmFactory;

public class UDP {

    @SuppressWarnings("Unused")
    private final String TAG = UDP.class.getName();

    private String ip = "";
    private int port = 0;

    private InetAddress local;
    private DatagramSocket s;

    private float[] dataToSend = new float[10];

    public static final long TIMES_FASTER = 1;
    public static final long REFRESH_RATE = 20 / TIMES_FASTER;

    private ScheduledFuture result;

    private final int RAW_DATA = 1;
    private final int ACCELEROMETER = 2;
    private final int COMPLEMENTARY = 3;
    private final int MADGWICK = 4;
    private final int MADGWICK_IMU = 5;
    private final int MADGWICK_IMU_KAT = 6;

    private int lastAlgorithm = 0;

    private int counter = 0;

    UDP(String ip, int port) {
        this.ip = ip;
        this.port = port;
        run();
    }

    public void run() {
        try {
            local = InetAddress.getByName(ip);
            s = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlgorithmFactory algorithmFactory = new AlgorithmFactory();

        final Algorithm accelerometer = algorithmFactory.getAlgorithm("ACCELEROMETER");
        final Algorithm complementary = algorithmFactory.getAlgorithm("COMPLEMENTARY");
        final Algorithm madgwickAMG = algorithmFactory.getAlgorithm("MADGWICKAMG");
        final Algorithm madgwickAG = algorithmFactory.getAlgorithm("MADGWICKAG");

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
                    float[] a = accelerometer.calculate(MainActivity.values);
                    for (int i = 0; i < a.length; i++)
                        dataToSend[i] = (float) Math.toDegrees(a[i]);
                    dataToSend[9] = ACCELEROMETER;
                }

                if(MainActivity.complementary.isChecked()) {
                    float[] a = complementary.calculate(MainActivity.values);
                    for (int i = 0; i < a.length; i++)
                        dataToSend[i] = (float) Math.toDegrees(a[i]);
                    dataToSend[9] = COMPLEMENTARY;
                }

                if(MainActivity.madgwick.isChecked()) {
                    float[] a = madgwickAMG.calculate(MainActivity.getValues());
                    for (int i = 0; i < a.length; i++)
                        dataToSend[i] = a[i];
                    dataToSend[9] = MADGWICK;
                }

                if(MainActivity.madgwickIMU.isChecked()) {
                    float[] a = madgwickAG.calculate(MainActivity.getValues());
                    for (int i = 0; i < a.length; i++)
                        dataToSend[i] = a[i];
                    dataToSend[9] = MADGWICK_IMU;
                }

                if(MainActivity.madgwickIMUKat.isChecked()) {
                    float[] a = madgwickAG.calculate(MainActivity.getValues());
                    for (int i = 0; i < a.length; i++)
                        dataToSend[i] = a[i];
                    dataToSend[9] = MADGWICK_IMU_KAT;
                }

                if(counter >= TIMES_FASTER) {
                    byte[] b = FloatArray2ByteArray(dataToSend);
                    DatagramPacket p = new DatagramPacket(b, b.length, local, port);
                    try {
                        s.send(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    counter = 0;

                }
                counter++;
            }
        }, 0, REFRESH_RATE, TimeUnit.MILLISECONDS);
    }

    public void stopUDP() {
        result.cancel(false);
    }

    public byte[] FloatArray2ByteArray(float[] values) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);

        for (float value : values) {
            buffer.putFloat(value);
        }

        return buffer.array();
    }
}