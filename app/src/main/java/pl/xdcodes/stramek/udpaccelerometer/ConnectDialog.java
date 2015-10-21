package pl.xdcodes.stramek.udpaccelerometer;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class ConnectDialog extends DialogFragment {

    private static final String TAG = MainActivity.class.getName();
    private EditText address;
    private EditText port;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View connectView = View.inflate(getContext(), R.layout.connect_dialog, null);

        address = (EditText) connectView.findViewById(R.id.address);
        port = (EditText) connectView.findViewById(R.id.port);

        builder.setTitle(getString(R.string.connect_settings));
        builder.setView(connectView);
        builder.setPositiveButton(getString(R.string.connect), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Log.d(TAG, "Adres: " + address.getText().toString() + " Port: " + port.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

}