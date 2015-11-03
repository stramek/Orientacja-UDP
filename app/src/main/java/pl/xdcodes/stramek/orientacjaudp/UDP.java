package pl.xdcodes.stramek.orientacjaudp;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UDP {

    private final String TAG = MainActivity.class.getName();

    private String ip = "";
    private int port = 0;

    private InetAddress local;
    private DatagramSocket s;

    UDP(String ip, int port) {
        this.ip = ip;
        this.port = port;
        new SendingOperation().execute();
    }

    private class SendingOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

            try {
                local = InetAddress.getByName(ip);
                s = new DatagramSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }

            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] b = FloatArray2ByteArray(MainActivity.values);
                        DatagramPacket p = new DatagramPacket(b, b.length, local, port);
                        s.send(p);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 10, TimeUnit.MILLISECONDS);

            return "Executed";
        }
    }

    public byte[] FloatArray2ByteArray(float[] values) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);

        for (float value : values) {
            buffer.putFloat(value);
        }

        return buffer.array();
    }
}