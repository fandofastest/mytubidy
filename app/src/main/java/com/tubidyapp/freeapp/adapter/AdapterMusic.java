package com.tubidyapp.freeapp.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.krossovochkin.bottomsheetmenu.BottomSheetMenu;
import com.tubidyapp.freeapp.R;
import com.tubidyapp.freeapp.model.MusicSongOnline;
import com.tubidyapp.freeapp.utils.MusicUtils;
import com.tubidyapp.freeapp.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterMusic extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MusicSongOnline> items = new ArrayList<>();

    private Context ctx;
    private  int menures;
    private OnItemClickListener mOnItemClickListener;
    private OnMoreButtonClickListener onMoreButtonClickListener;
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    public void setOnMoreButtonClickListener(final OnMoreButtonClickListener onMoreButtonClickListener) {
        this.onMoreButtonClickListener = onMoreButtonClickListener;
    }

    public AdapterMusic(Context context, List<MusicSongOnline> items, int menures) {
        this.items = items;
        this.menures=menures;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView brief;
        public  TextView duration;
        public ImageButton more;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.img);
            title = (TextView) v.findViewById(R.id.title);
            brief = (TextView) v.findViewById(R.id.brief);
            more = (ImageButton) v.findViewById(R.id.more);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            duration=v.findViewById(R.id.txt_time);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_song, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            MusicUtils utils = new MusicUtils();
            final MusicSongOnline musicSongOnline = items.get(position);
            view.title.setText(musicSongOnline.getTitle());
            view.brief.setText(musicSongOnline.getArtist());
            view.duration.setText(utils.milliSecondsToTimer(Long.parseLong(musicSongOnline.getDuration())));
            Tools.displayImageOriginal(ctx, view.image, musicSongOnline.getImageurl());
            view.lyt_parent.setOnClickListener(v -> new BottomSheetMenu.Builder(ctx, new BottomSheetMenu.BottomSheetMenuListener() {
                @Override
                public void onCreateBottomSheetMenu(MenuInflater inflater, Menu menu) {
                    inflater.inflate(menures, menu);



                }

                @Override
                public void onBottomSheetMenuItemSelected(MenuItem item) {
                    onMoreButtonClickListener.onItemClick(item,position);



                }
            }).show());

//            view.more.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (onMoreButtonClickListener == null) return;
//                    onMoreButtonClick(view, position,menures);
//                }
//            });
        }
    }

    private void onMoreButtonClick(final View view, final int pos,int menures) {

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    public interface OnMoreButtonClickListener {
        void onItemClick( MenuItem item,int pos);
    }

}