package com.tubidyapp.freeapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.tubidyapp.freeapp.R;
import com.tubidyapp.freeapp.activity.SearchActivity;
import com.tubidyapp.freeapp.adapter.AdapterGenre;
import com.tubidyapp.freeapp.model.MusicAlbum;
import com.tubidyapp.freeapp.utils.Tools;
import com.tubidyapp.freeapp.widget.SpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class FragmentMusicAlbum extends Fragment {

    public FragmentMusicAlbum() {
    }


    AdapterGenre mAdapter;

    List <MusicAlbum> listgenre = new ArrayList<>();

    public static FragmentMusicAlbum newInstance() {
        FragmentMusicAlbum fragment = new FragmentMusicAlbum();
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_product_grid, container, false);


        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 4), true));
        recyclerView.setHasFixedSize(true);


        //set data and list adapter
        mAdapter = new AdapterGenre(getActivity(), listgenre);
        recyclerView.setAdapter(mAdapter);
        getallgenre();
        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterGenre.OnItemClickListener() {
            @Override
            public void onItemClick(View view, MusicAlbum obj, int position) {

                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("type","genre");
                intent.putExtra("genrename",obj.getGenrename());
                intent.putExtra("genreorigin",obj.getOrigenrename());
                startActivity(intent);

            }
        });

        return root;
    }

    public void  getallgenre (){
        listgenre.clear();
        String [] genrelist ={"Alternative Rock","Ambient","Audiobooks","Business","Classical","Comedy","Country","Dance & EDM","Dancehall","Deep House","Disco","Drum & Bass","Dubstep","Electronic","Entertainment","Folk & Singer-Songwriter","Hip Hop & Rap","House","Indie","Jazz & Blues","Latin","Learning","Metal","News & Politics","Piano","Pop","R&B & Soul","Reggae","Reggaeton","Religion & Spirituality","Rock","Science","Soundtrack","Sports","Storytelling","Techno","Technology","Trance","Trap","Trending Audio","Trending Music","Trip Hop","World"};

        for (int i = 0; i < genrelist.length; i++) {
            listgenre.add(new MusicAlbum(genrelist[i]));

        }




        mAdapter.notifyDataSetChanged();


    }
}