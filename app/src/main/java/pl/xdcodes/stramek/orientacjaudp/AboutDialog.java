package pl.xdcodes.stramek.orientacjaudp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import pl.xdcodes.stramek.udpaccelerometer.R;


public class AboutDialog extends DialogFragment {

    private final String TAG = MainActivity.class.getName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View connectView = View.inflate(getContext(), R.layout.about_dialog, null);

        builder.setTitle(getString(R.string.author));
        builder.setView(connectView);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
        });
        return builder.create();
    }
}