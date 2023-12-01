package com.example.inventorypro.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.inventorypro.Activities.SignInActivity;
import com.example.inventorypro.FilterSettings;
import com.example.inventorypro.Helpers;
import com.example.inventorypro.R;
import com.example.inventorypro.User;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Fragment which updates the UserPreferences FilterSettings.
 */
public class UserProfileFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle bundle) {
        String[] userDetails = new String[] {
                "USER ID\n" + User.getInstance().getUserID() + "\n",
                "EMAIL ID\n" + User.getInstance().getEmailID() + "\n",
                "DISPLAY NAME\n" + User.getInstance().getDisplayName()
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Profile")
                .setItems(userDetails, null)
                .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent signInActivity = new Intent(getContext(), SignInActivity.class);
                        signInActivity.putExtra(getString(R.string.logout_token), true);
                        startActivity(signInActivity);

                        Helpers.toast(getContext(),"You have been signed out.");
                    }
                })
                .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it.
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

