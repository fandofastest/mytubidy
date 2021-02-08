package com.tubidyapp.freeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.tubidyapp.freeapp.R;
import com.tubidyapp.freeapp.model.SliderModel;
import com.tubidyapp.freeapp.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<SliderModel> mSliderItems = new ArrayList<>();

    public SliderAdapter(Context context, List<SliderModel> list) {
        this.context = context;
        this.mSliderItems=list;
    }

    public void renewItems(List<SliderModel> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(SliderModel sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promo, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        SliderModel sliderItem = mSliderItems.get(position);

        viewHolder.title.setText(sliderItem.getTitle());
        viewHolder.desc.setText(sliderItem.getDesc());
        Tools.displayImage(context,viewHolder.image,sliderItem.getImageurl(),R.drawable.icon);
        viewHolder.install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sliderItem.getUrltarget())));
            }
        });


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

   public   class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView image;
        Button install;
        TextView title;
        TextView desc;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imagepromotion);
            title = itemView.findViewById(R.id.titlepromo);
            desc = itemView.findViewById(R.id.descpromo);
            install = itemView.findViewById(R.id.install);
            this.itemView = itemView;
        }
    }

}