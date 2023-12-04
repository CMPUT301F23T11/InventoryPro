package com.example.inventorypro.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.inventorypro.Activities.MainActivity;
import com.example.inventorypro.Activities.SignInActivity;
import com.example.inventorypro.Helpers;
import com.example.inventorypro.Item;
import com.example.inventorypro.ItemList;
import com.example.inventorypro.R;
import com.example.inventorypro.User;

import java.util.ArrayList;

/**
 * Fragment which gets confirmation from user before deleting selected items.
 */
public class DeleteItemsConfirmationDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle bundle) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Profile")
                .setCancelable(false)
                .setMessage("Are you sure you want to delete these items?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ItemList.getInstance().deleteSelectedItems();
                        dialog.dismiss();
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

