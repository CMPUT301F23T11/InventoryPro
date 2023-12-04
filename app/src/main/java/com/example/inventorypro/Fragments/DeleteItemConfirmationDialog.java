package com.example.inventorypro.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.inventorypro.Item;
import com.example.inventorypro.ItemList;

/**
 * Fragment which gets confirmation from user before deleting item.
 */
public class DeleteItemConfirmationDialog extends DialogFragment {
    private ViewItemFragment viewItemFragment;
    private Item item;

    public DeleteItemConfirmationDialog(ViewItemFragment viewItemFragment, Item item) {
        this.viewItemFragment = viewItemFragment;
        this.item = item;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle bundle) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete item")
                .setCancelable(false)
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ItemList.getInstance().removeItem(item);
                        viewItemFragment.dismiss();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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