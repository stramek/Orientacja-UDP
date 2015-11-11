package pl.xdcodes.stramek.orientacjaudp;

import android.os.AsyncTask;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class UDP extends AsyncTask<String, Void, String> {

    private final String TAG = UDP.class.getName();

    private String ip = "";
    private int port = 0;

    private InetAddress local;
    private DatagramSocket s;

    //private double d = 0;

    private ScheduledFuture result;

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

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        result =  exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //Log.d(TAG, "" + (System.currentTimeMillis() - d));
                //d = System.currentTimeMillis();
                try {
                    byte[] b = FloatArray2ByteArray(MainActivity.values);
                    DatagramPacket p = new DatagramPacket(b, b.length, local, port);
                    s.send(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 33, TimeUnit.MILLISECONDS);

        /*while(!isCancelled()) {
            try {
                byte[] b = FloatArray2ByteArray(MainActivity.values);
                DatagramPacket p = new DatagramPacket(b, b.length, local, port);
                s.send(p);
                Thread.sleep(33);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

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