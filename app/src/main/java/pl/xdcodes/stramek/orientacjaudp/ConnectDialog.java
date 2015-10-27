package pl.xdcodes.stramek.orientacjaudp;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import java.util.Timer;
import java.util.TimerTask;

import pl.xdcodes.stramek.udpaccelerometer.R;


public class ConnectDialog extends DialogFragment {

    private final String TAG = MainActivity.class.getName();
    private final int refreshRateInMs = 100;

    private EditText address;
    private EditText port;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View connectView = View.inflate(getContext(), R.layout.connect_dialog, null);

        address = (EditText) connectView.findViewById(R.id.address);
        port = (EditText) connectView.findViewById(R.id.port);

        address.setText(getString(R.string.default_ip));
        port.setText(getString(R.string.default_port));

        address.requestFocus();

        builder.setTitle(getString(R.string.connect_settings));
        builder.setView(connectView);
        builder.setPositiveButton(getString(R.string.connect), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        if (!address.getText().toString().equals("") &&
                                !port.getText().toString().equals("")) {
                            MainActivity.status.setText(R.string.sending);
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    new UDP(address.getText().toString(),
                                            Integer.parseInt(port.getText().toString()));
                                }
                            }, 0, refreshRateInMs);
                        }
                    }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }
}