package com.example.group20restaurantapp.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.group20restaurantapp.R;

// source: https://developer.android.com/guide/topics/ui/dialogs
public class PleaseWaitDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(R.layout.dialog_please_wait);
        builder.setMessage(R.string.please_wait_dialog_message)
                .setNegativeButton(R.string.please_wait_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send negative button event back to the host activity
                        listener.onPleaseWaitDialogNegativeClick(PleaseWaitDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface PleaseWaitDialogListener {
        public void onPleaseWaitDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    PleaseWaitDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the AskUserToUpdateDialogListener so we can send events to the host
            listener = (PleaseWaitDialogListener) context;
        } catch (ClassCastException e) {
            // Activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement AskUserToUpdateDialogListener");
        }
    }
}
