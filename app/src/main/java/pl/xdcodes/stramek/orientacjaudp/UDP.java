package pl.xdcodes.stramek.orientacjaudp;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class UDP {

    private final String TAG = MainActivity.class.getName();

    String ip = "";
    int port = 0;

    UDP(String ip, int port) {
        this.ip = ip;
        this.port = port;
        new SendingOperation().execute();
    }

    private class SendingOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                byte[] b = FloatArray2ByteArray(MainActivity.values);

                DatagramSocket s = new DatagramSocket();
                InetAddress local = InetAddress.getByName(ip);

                DatagramPacket p = new DatagramPacket(b, b.length, local, port);
                s.send(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
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