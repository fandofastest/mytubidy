package com.tubidyapp.freeapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.tubidyapp.freeapp.R;
import com.tubidyapp.freeapp.adapter.AdapterMusic;
import com.tubidyapp.freeapp.adapter.SliderAdapter;
import com.tubidyapp.freeapp.ads.Ads;
import com.tubidyapp.freeapp.fragment.FragmentMusicAlbum;
import com.tubidyapp.freeapp.fragment.FragmentMusicSong;
import com.tubidyapp.freeapp.fragment.LocalFragment;
import com.tubidyapp.freeapp.fragment.PlaylistsFragment;
import com.tubidyapp.freeapp.fragment.RecentFragment;
import com.tubidyapp.freeapp.model.MusicSongOffline;
import com.tubidyapp.freeapp.model.MusicSongOnline;
import com.tubidyapp.freeapp.model.SliderModel;
import com.tubidyapp.freeapp.servicemusic.PlayerService;
import com.tubidyapp.freeapp.utils.MusicUtils;
import com.tubidyapp.freeapp.utils.RealmHelper;
import com.tubidyapp.freeapp.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.fando.GDPRChecker;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    public View parent_view;
    TextView hometitle,homeartist;
    LinearLayout bannerlayout;

    private ViewPager view_pager;
    private TabLayout tab_layout;

    private ImageButton bt_play;
    private ProgressBar song_progressbar;
    private AdapterMusic mAdapter;

    // Media Player
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();

    //private SongsManager songManager;
    private MusicUtils utils;
    SectionsPagerAdapter adapter;
    Realm realm;
    RealmHelper realmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parent_view = findViewById(R.id.parent_view);




        initToolbar();
        initComponent();
        initrealm();
        initSlider();
        if (PlayerService.PLAYERSTATUS.equals("PLAYING")){
            bt_play.setImageResource(R.drawable.ic_pause);
            hometitle.setText(PlayerService.currenttitle);
            homeartist.setText(PlayerService.currentartist);
        }
        else {
            bt_play.setImageResource(R.drawable.ic_play_arrow);
            hometitle.setText("No Song");
            homeartist.setText("");
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            }
        }
    refreshtab();

    }



    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.appbartitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(this);






    }

    private void initComponent() {
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(view_pager);
        hometitle=findViewById(R.id.titlehomeplayer);
        homeartist=findViewById(R.id.artishomeplayer);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);

        bt_play = (ImageButton) findViewById(R.id.bt_play);
        song_progressbar = (ProgressBar) findViewById(R.id.song_progressbar);

        // set Progress bar values
        song_progressbar.setProgress(0);
        song_progressbar.setMax(MusicUtils.MAX_PROGRESS);
        getlocalbroadcaster();
        bannerlayout=findViewById(R.id.banner_container);

        Display display = MainActivity.this.getDisplay();
        Ads ads= new Ads(MainActivity.this,false);
        ads.ShowBannerAds(bannerlayout,display);

        new GDPRChecker()
                .withContext(getApplicationContext())
                .withActivity(MainActivity.this)
                .withAppId("your admob appid")
                .withDebug()
                .check();





        utils = new MusicUtils();

        buttonPlayerAction();






    }

    /**
     * Play button click event plays a song and changes button to pause image
     * pauses a song and changes button to play image
     */





    private  void refreshtab(){
        tab_layout.getTabAt(0).setIcon(R.drawable.ic_music);
        tab_layout.getTabAt(1).setIcon(R.drawable.ic_adjust);
        tab_layout.getTabAt(2).setIcon(R.drawable.ic_person);
        tab_layout.getTabAt(3).setIcon(R.drawable.ic_queue_music);
        tab_layout.getTabAt(4).setIcon(R.drawable.ic_baseline_library_music_24);


        tab_layout.getTabAt(0).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);

    }









    private void buttonPlayerAction() {
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (PlayerService.PLAYERSTATUS.equals("PLAYING")) {
                   pause();
                } else {
                    resume();
                    // Resume song

                }

            }
        });
    }

    public void controlClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_expand: {
                if (PlayerService.PLAYERSTATUS.equals("PLAYING")){
                    Intent intent = new Intent(MainActivity.this, PlayerMusicActivity.class);
                    intent.putExtra("from","player");
                    startActivity(intent);
                }
                else {
                    Snackbar.make(parent_view, R.string.snackbarnomusic, Snackbar.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            // Running this thread after 10 milliseconds
            if (PlayerService.PLAYERSTATUS.equals("PLAYING")) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateTimerAndSeekbar() {
        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "getduration");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // Updating progress bar
        int progress = (int) (utils.getProgressSeekBar(PlayerService.currentduraiton, PlayerService.totalduration));
        song_progressbar.setProgress(progress);
    }

    // stop player when destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);

    }

    private void setupViewPager(final ViewPager viewPager) {
       adapter = new SectionsPagerAdapter(getSupportFragmentManager());


        adapter.addFragment(FragmentMusicSong.newInstance(), "SONGS");
        adapter.addFragment(FragmentMusicAlbum.newInstance(), "GENRE");
        adapter.addFragment(RecentFragment.newInstance(), "RECENT");
        adapter.addFragment(PlaylistsFragment.newInstance(), "PLAYLIST");
        adapter.addFragment(LocalFragment.newInstance(), "LOCAL MUSIC");


        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.getAdapter().notifyDataSetChanged();
                refreshtab();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }



    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle(R.string.quittitle)
                .setMessage(R.string.quitmessage)
                .addButton(getString(R.string.quitbutton), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        finishAffinity();
                        System.exit(0);

                    }
                });

// Show the alert
        builder.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_setting, menu);
        MenuItem searchIem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchIem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextSubmit(String query) {

               Intent intent = new Intent(MainActivity.this,SearchActivity.class);
               intent.putExtra("type","search");
               intent.putExtra("q",query);
               startActivity(intent);

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {



        }
        return super.onOptionsItemSelected(item);
    }



    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE ;
        }
    }


    public void playmusic (int position ,List<MusicSongOnline> listsong){

        PlayerService.currentlist=listsong;





        Intent intent = new Intent(MainActivity.this, PlayerMusicActivity.class);
        intent.putExtra("from","online");
        intent.putExtra("pos",position);


        Ads ads= new Ads(MainActivity.this,true);
        ads.setCustomObjectListener(new Ads.MyCustomObjectListener() {
            @Override
            public void onAdsfinish() {
                startActivity(intent);
            }

            @Override
            public void onRewardOk() {

            }
        });










    }

    public void playmusicoffline (int position ,List<MusicSongOffline> listsong){

        PlayerService.currentlistoffline=listsong;


        Intent intent = new Intent(MainActivity.this, PlayerMusicActivity.class);
        intent.putExtra("from","offline");
        intent.putExtra("pos",position);
        Ads ads= new Ads(MainActivity.this,true);
        ads.setCustomObjectListener(new Ads.MyCustomObjectListener() {
            @Override
            public void onAdsfinish() {
                startActivity(intent);
            }

            @Override
            public void onRewardOk() {

            }
        });





    }
    public void  initrealm(){
        Realm.init(MainActivity.this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);

    }


    public List<MusicSongOnline> getrecent(){
        realmHelper = new RealmHelper(realm,getApplication());
        PlayerService.listrecent=  realmHelper.getAllSongsrecent();

        return  PlayerService.listrecent;

    }

    public List<MusicSongOnline> getplaylists(){
        realmHelper = new RealmHelper(realm,getApplication());
        PlayerService.listplaylist=  realmHelper.getallplaylists();

        return  PlayerService.listplaylist;

    }



    public void getlocalbroadcaster(){
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("status");
                if (status.equals("playing")){

                    bt_play.setVisibility(View.VISIBLE);
                    bt_play.setImageResource(R.drawable.ic_pause);
                    hometitle.setText(PlayerService.currenttitle);
                    homeartist.setText(PlayerService.currentartist);
                    mHandler.post(mUpdateTimeTask);

                }
                else if (status.equals("pause")){
                    bt_play.setImageResource(R.drawable.ic_play_arrow);
                }

            }
        }, new IntentFilter("musicplayer"));

    }

    public void pause (){
        bt_play.setImageResource(R.drawable.ic_play_arrow);
        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "pause");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    public void resume (){
        bt_play.setImageResource(R.drawable.ic_pause);
        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "resume");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        mHandler.post(mUpdateTimeTask);

    }

    public  void addtoplaylits(MusicSongOnline musicSongOnline){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.saveplaylists(musicSongOnline);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"Added to Playlists",LENGTH_LONG).show();


    }


    public  void removerecent(MusicSongOnline musicSongOnline){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.removefromrecent(musicSongOnline);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"Removed",LENGTH_LONG).show();


    }

    public  void removeplaylists(MusicSongOnline musicSongOnline){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.removefromplaylists(musicSongOnline);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"Removed",LENGTH_LONG).show();


    }

    void  initSlider(){
        List<SliderModel> list = new ArrayList<>();
        SliderView sliderView = findViewById(R.id.imageSlider);

        SliderAdapter adapter = new SliderAdapter(MainActivity.this,list);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);

        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        list.clear();

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, getString(R.string.url), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("promo");
                    for (int i = 0; i <jsonArray.length() ; i++) {
                        JSONObject jsonObject= jsonArray.getJSONObject(i);

                        SliderModel sliderModel= new SliderModel();
                        sliderModel.setTitle(jsonObject.getString("title"));
                        sliderModel.setDesc(jsonObject.getString("desc"));
                        sliderModel.setImageurl(jsonObject.getString("imageurl"));
                        sliderModel.setUrltarget(jsonObject.getString("linkurl"));
                        list.add(sliderModel);
                    }
                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, error -> Log.e("err","test"));

        Volley.newRequestQueue(MainActivity.this).add(jsonObjectRequest);


    }
}

