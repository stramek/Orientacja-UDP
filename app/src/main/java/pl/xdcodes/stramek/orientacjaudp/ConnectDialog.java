package pl.xdcodes.stramek.orientacjaudp;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import pl.xdcodes.stramek.udpaccelerometer.R;


public class ConnectDialog extends DialogFragment {

    private final String TAG = ConnectDialog.class.getName();

    private EditText address;
    private EditText port;

    private TextInputLayout addressIL;
    private TextInputLayout portIL;

    private UDP udp;

    public void cancelSending() {
        udp.stopUDP();
        //udp.cancel(false);
    }

    public interface StatusDialogListener {
        void onFinishDialog(boolean status);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View connectView = View.inflate(getContext(), R.layout.connect_dialog, null);

        address = (EditText) connectView.findViewById(R.id.address);
        port = (EditText) connectView.findViewById(R.id.port);

        addressIL = (TextInputLayout) connectView.findViewById(R.id.address_input_layout);
        portIL = (TextInputLayout) connectView.findViewById(R.id.port_input_layout);

        SharedPreferences prefs = getContext().getSharedPreferences("lastIpPort", Context.MODE_PRIVATE);

        address.setText(prefs.getString("lastIp", getString(R.string.default_ip)));
        port.setText(prefs.getString("lastPort", getString(R.string.default_port)));

        address.requestFocus();

        builder.setTitle(getString(R.string.connect_settings));
        builder.setView(connectView);
        builder.setPositiveButton(getString(R.string.connect), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // Button overrided below
                    }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                StatusDialogListener activity = (StatusDialogListener) getActivity();
                activity.onFinishDialog(false);
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address.getText().length() == 0) {
                    addressIL.setErrorEnabled(true);
                    addressIL.setError(getString(R.string.blank_address));
                    address.requestFocus();
                }

                if (port.getText().length() == 0) {
                    portIL.setErrorEnabled(true);
                    portIL.setError(getString(R.string.blank_port));
                    port.requestFocus();
                }

                if (address.getText().length() > 0 && port.getText().length() > 0) {

                    udp = new UDP(address.getText().toString(), Integer.parseInt(port.getText().toString()));

                    SharedPreferences.Editor editor =  getContext().getSharedPreferences("lastIpPort", Context.MODE_PRIVATE).edit();
                    editor.putString("lastIp", address.getText().toString());
                    editor.putString("lastPort", port.getText().toString());
                    editor.commit();

                    StatusDialogListener activity = (StatusDialogListener) getActivity();
                    activity.onFinishDialog(true);

                    dialog.dismiss();
                }
            }
        });
        return dialog;
    }
}