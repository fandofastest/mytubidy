package com.tubidyapp.freemusic.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.tubidyapp.freemusic.R;
import com.tubidyapp.freemusic.ads.MyAds;
import com.tubidyapp.freemusic.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.tubidyapp.freemusic.utils.Tools.KEYSC;
import static com.tubidyapp.freemusic.utils.Tools.ads;

public class SplashScreenActivity extends AppCompatActivity implements Animation.AnimationListener {

    Animation animFadeIn;
    RelativeLayout relativeLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splas_screen);

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        ThreeBounce doubleBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);





        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getstatus();
                //Do something after 100ms
            }
        }, 3000);


        View decorView = getWindow().getDecorView();

        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.

        // load the animation
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_fade_in);

        // set animation listener
        animFadeIn.setAnimationListener(this);

        // animation for image
        relativeLayout = findViewById(R.id.splashLayout);

        // start the animation
        relativeLayout.setVisibility(View.VISIBLE);
        relativeLayout.startAnimation(animFadeIn);
        getkey();

    }

    public void getstatus(){
        String url=Tools.urlstatus;
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {


                    Tools.redirect = response.getString("redirect");
                    Tools.pops = response.getString("pops");
                    Tools.apk = response.getString("apk");
                    Tools.ads = response.getString("ads");
                    Tools.admobbanner = response.getString("admobbanner");
                    Tools.admobinter = response.getString("admobinter");
                    Tools.admobreward = response.getString("admobreward");
                    Tools.fanbanner = response.getString("fanbanner");
                    Tools.faninter = response.getString("faninter");
                    Tools.popstitle = response.getString("popstitle");
                    Tools.popsdesc = response.getString("popsdesc");
                    Tools.popsimage = response.getString("popsimage");
                    Tools.api = response.getString("api");
                    Tools.appid=response.getString("appid");


                    MyAds myAds = new MyAds();
                    if (Tools.ads.equals("admob")){

                        myAds.showinter(SplashScreenActivity.this,Tools.admobinter);
                    }
                    else if (Tools.ads.equals("fan")) {
                        myAds.showinterfb(SplashScreenActivity.this,Tools.faninter);
                    }
                    else {
                        Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    myAds.setCustomObjectListener(new MyAds.MyCustomObjectListener() {
                        @Override
                        public void onAdsfinish() {
                            Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onRewardOk() {

                        }
                    });


                    if (Tools.redirect.equals("y")){

                        showdialogredirect(Tools.apk);

                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }





            }
        }, error -> Log.e("err",error.getMessage()));

        Volley.newRequestQueue(SplashScreenActivity.this).add(jsonObjectRequest);


    }
    private void  showdialogredirect(String appupdate){
        new SweetAlertDialog(SplashScreenActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("App Was Discontinue")
                .setContentText("Please Install Our New Music App")
                .setConfirmText("Install")

                .setConfirmClickListener(sDialog -> {
                    sDialog
                            .setTitleText("Install From Playstore")
                            .setContentText("Please Wait, Open Playstore")
                            .setConfirmText("Go")


                            .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(
                                "https://play.google.com/store/apps/details?id="+appupdate));
                        intent.setPackage("com.android.vending");
                        startActivity(intent);
//                                Do something after 100ms

                    },3000);


                })
                .show();
    }
    public void getkey(){
        String url="https://fando.id/soundcloud/getapi.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        KEYSC=response.replaceAll("^\"|\"$", "");
                        System.out.println(KEYSC);
                        // Display the first 500 characters of the response string.

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });



        Volley.newRequestQueue(SplashScreenActivity.this).add(stringRequest);


    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        //under Implementation
    }

    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        //under Implementation
    }

}
