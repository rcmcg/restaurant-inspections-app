package com.example.group20restaurantapp.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.group20restaurantapp.R;
//This activity is reviewed
/**
 * Dialog to ask user if they want to update. MapsActivity implements listener
 */

// source: https://developer.android.com/guide/topics/ui/dialogs
public class AskUserToUpdateDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.ask_user_to_update_message)
                .setPositiveButton(R.string.ask_user_to_update_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send positive button event back to the host activity
                        listener.onAskUserToUpdateDialogPositiveClick(AskUserToUpdateDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.ask_user_to_update_decline, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send negative button event back to the host activity
                        listener.onAskUserToUpdateDialogNegativeClick(AskUserToUpdateDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface AskUserToUpdateDialogListener {
        public void onAskUserToUpdateDialogPositiveClick(DialogFragment dialog);
        public void onAskUserToUpdateDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    AskUserToUpdateDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the AskUserToUpdateDialogListener so we can send events to the host
            listener = (AskUserToUpdateDialogListener) context;
        } catch (ClassCastException e) {
            // Activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement AskUserToUpdateDialogListener");
        }
    }
}
