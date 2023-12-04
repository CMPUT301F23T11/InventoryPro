package com.example.inventorypro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Adapter for the displaying of images for an item.
 */
public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SliderItem> sliderItems;
    private ViewPager2 viewPager2;
    private boolean showDeleteButton;

    public SliderAdapter(List<SliderItem> sliderItems, ViewPager2 viewPager2, boolean showDeleteButton) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
        this.showDeleteButton = showDeleteButton;
    }

    /**
     * Convert URIs of currently displayed images to a String array.
     *
     * @return String array containing URIs of displayed images.
     */
    public String[] convertUrisToStringArray() {
        String[] stringArray = new String[sliderItems.size()];

        for (int i = 0; i < sliderItems.size(); i++) {
            Uri uri = sliderItems.get(i).getImage();
            if (uri != null) {
                stringArray[i] = uri.toString();
            }
        }

        return stringArray;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slide_image_container, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(sliderItems.get(position));

        // Show or hide the delete button based on the 'showDeleteButton' flag
        if (showDeleteButton) {
            holder.deleteImageButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteImageButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;
        private ImageButton deleteImageButton;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            deleteImageButton = itemView.findViewById(R.id.deleteImageButton);

            // Click listener for the delete button
            deleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && position < sliderItems.size()) {
                        showDeleteConfirmationDialog(position);
                    }
                }
            });
        }

        /**
         * Set the image in the ImageView using Glide.
         *
         * @param sliderItem The SliderItem containing the image URI.
         */
        void setImage(SliderItem sliderItem) {
            Glide.with(itemView.getContext())
                    .load(sliderItem.getImage()) // Assuming getImage() returns a Uri
                    .placeholder(R.drawable.baseline_downloading) // Optional placeholder
                    .into(imageView);
        }

        /**
         * Display a confirmation dialog for deleting an image.
         *
         * @param position The position of the image to be deleted.
         */
        private void showDeleteConfirmationDialog(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Confirm Deletion");
            builder.setMessage("Are you sure you want to delete this image?");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Delete the image
                    sliderItems.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, sliderItems.size());
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
