package in.UsCoolLabs.currencyconvertor.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import in.UsCoolLabs.currencyconvertor.R;

/**
 * Created by ujjawal on 9/10/17.
 *
 */

public class ErrorDialogFragment extends DialogFragment {

    private static final String TAG = "ErrorDialogFragment";

    private static final String ARG_MESSAGE = "message";
    private static final String ARG_TITLE = "title";

    public static ErrorDialogFragment newInstance(String title, String errorMessage) {
        ErrorDialogFragment fragment = new ErrorDialogFragment();

        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, errorMessage);
        args.putString(ARG_TITLE, title);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getArguments().getString(ARG_TITLE);
        String errorMessage = getArguments().getString(ARG_MESSAGE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setIcon(R.mipmap.ic_action_alert_warning)
                .setMessage(errorMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();

    }
}
