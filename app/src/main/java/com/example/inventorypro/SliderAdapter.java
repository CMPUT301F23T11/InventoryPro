package com.example.inventorypro;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SliderItem> sliderItems;
    private ViewPager2 viewPager2;

    public SliderAdapter(List<SliderItem> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    // Method to convert URIs to Strings
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
                        R.layout.slide_image_container,parent,false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(sliderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }


    class SliderViewHolder extends RecyclerView.ViewHolder{

        private RoundedImageView imageView;
        private ImageButton deleteImageButton;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            deleteImageButton = itemView.findViewById(R.id.deleteImageButton);

            deleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && position < sliderItems.size()) {
                        sliderItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, sliderItems.size());
                    }
                }
            });
        }
        void setImage(SliderItem sliderItem){
            imageView.setImageURI(sliderItem.getImage());
        }
    }

}
