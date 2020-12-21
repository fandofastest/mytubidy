package com.tubidyapp.freeapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tubidyapp.freeapp.BuildConfig;
import com.tubidyapp.freeapp.R;
import com.tubidyapp.freeapp.activity.MainActivity;
import com.tubidyapp.freeapp.adapter.AdapterMusic;
import com.tubidyapp.freeapp.model.MusicSongOnline;
import com.tubidyapp.freeapp.servicemusic.PlayerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.tubidyapp.freeapp.utils.Tools.KEYSC;

public class FragmentMusicSong extends Fragment {
    AdapterMusic mAdapter;
    Context context;

    public FragmentMusicSong() {
    }

    public static FragmentMusicSong newInstance() {
        FragmentMusicSong fragment = new FragmentMusicSong();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_music_song, container, false);


        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        context=getContext();

        

        //set data and list adapter
         mAdapter = new AdapterMusic(getActivity(), PlayerService.listtopsong,R.menu.menu_song_more);
        recyclerView.setAdapter(mAdapter);





        mAdapter.setOnMoreButtonClickListener(new AdapterMusic.OnMoreButtonClickListener() {
            @Override
            public void onItemClick(MenuItem item, int pos) {
                final int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.action_playlist:

                        MusicSongOnline musicSongOnline =PlayerService.listtopsong.get(pos);
                        if (context instanceof MainActivity) {
                            ((MainActivity)context).addtoplaylits(musicSongOnline);
                        }



                        break;

                    case R.id.action_play:
                        if (context instanceof MainActivity) {
                            ((MainActivity)context).playmusic(pos,PlayerService.listtopsong);
                        }

                        break;
                    case  R.id.action_share:
                        try {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                            String shareMessage= "\nLet me recommend you this application\n\n";
                            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            startActivity(Intent.createChooser(shareIntent, "choose one"));
                        } catch(Exception e) {
                            //e.toString();
                        }
                        break;

                    default:
                }


            }

         


        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gettopchart();
    }

    public void gettopchart(){
        String url="https://api-v2.soundcloud.com/charts?charts-top:all-music&&high_tier_only=false&kind=top&limit=100&client_id="+KEYSC;
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {



                try {
                    JSONArray jsonArray1=response.getJSONArray("collection");

                    for (int i = 0;i<jsonArray1.length();i++){
                        JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                        JSONObject jsonObject=jsonObject1.getJSONObject("track");
                        MusicSongOnline musicSongOnline = new MusicSongOnline();
                        musicSongOnline.setId(jsonObject.getInt("id"));
                        musicSongOnline.setTitle(jsonObject.getString("title"));
                        musicSongOnline.setImageurl(jsonObject.getString("artwork_url"));
                        musicSongOnline.setDuration(jsonObject.getString("full_duration"));
                        musicSongOnline.setType("online");


                        try {
                            JSONObject jsonArray3=jsonObject.getJSONObject("publisher_metadata");
                            musicSongOnline.setArtist(jsonArray3.getString("artist"));

                        }
                        catch (JSONException e){
                            musicSongOnline.setArtist("Artist");

                        }


//                        System.out.println(jsonArray3);


                        PlayerService.listtopsong.add(musicSongOnline);
//



//                        Toast.makeText(getActivity(),id,Toast.LENGTH_LONG).show();


                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
//                songAdapter.notifyDataSetChanged();
                //    System.out.println("update"+listsongModalSearch);




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);


    }

}