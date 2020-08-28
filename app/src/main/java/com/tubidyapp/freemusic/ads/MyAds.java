package com.tubidyapp.freemusic.ads;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.tubidyapp.freemusic.utils.Tools;

import static android.content.ContentValues.TAG;

public class MyAds {
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    com.facebook.ads.AdView adViewfb;
    AdView mAdView;

    com.google.android.gms.ads.InterstitialAd mInterstitialAd;
    KProgressHUD hud;
    public interface MyCustomObjectListener {
        public void onAdsfinish();
        public void onRewardOk();
    }

    private MyCustomObjectListener listener;

    public void setCustomObjectListener(MyCustomObjectListener listener) {
        this.listener = listener;
    }


    public void showreward(Context context, String rewardads){
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading")
                .setCancellable(true)
                .setDetailsLabel("Please Wait")
                .setMaxProgress(100)

                .show();

        rewardedAd = new RewardedAd(context,
                rewardads);

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            boolean getreward=false;
            @Override
            public void onRewardedAdLoaded() {
                RewardedAdCallback adCallback = new RewardedAdCallback() {
                    @Override
                    public void onRewardedAdOpened() {
                        // Ad opened.
                    }

                    @Override
                    public void onRewardedAdClosed() {
                        if (!getreward){
                            hud.dismiss();
                            listener.onAdsfinish();
                        }

                        // Ad closed.
                    }

                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem reward) {

                        getreward=true;

                        listener.onRewardOk();
                        hud.dismiss();

                        // User earned reward.
                    }


                };
                rewardedAd.show((Activity) context, adCallback);

                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }

        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);


    }

    public void showinterfb(final Context context, String interfb){

        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading")
                .setCancellable(true)
                .setDetailsLabel("Please Wait")
                .setMaxProgress(100)

                .show();

        interstitialAd = new InterstitialAd(context, interfb);
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
                listener.onAdsfinish();
                hud.dismiss();



            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                listener.onAdsfinish();
                hud.dismiss();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                hud.dismiss();
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();

    }


    public  void showinter(final Context context, String inter) {
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading")
                .setCancellable(true)
                .setDetailsLabel("Please Wait")
                .setMaxProgress(100)

                .show();

        hud.show();


        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(context);
        mInterstitialAd.setAdUnitId(inter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
                hud.dismiss();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                listener.onAdsfinish();
                hud.dismiss();

                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                listener.onAdsfinish();
                hud.dismiss();
                // Code to be executed when the interstitial ad is closed.
            }
        });


    }

    public  void showBannerAds(Context context, LinearLayout mAdViewLayout, String bannerfb, String banneradmob, Display display) {



        adViewfb = new com.facebook.ads.AdView(context, bannerfb, com.facebook.ads.AdSize.BANNER_HEIGHT_50);

        adViewfb.setAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {


            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        adViewfb.loadAd();


        mAdView  = new AdView(context);
        AdSize adSize = getAdSize(context,display);

        mAdView.setAdSize(adSize);
        mAdView.setAdUnitId(banneradmob);

        AdRequest.Builder builder = new AdRequest.Builder();



        mAdView.loadAd(builder.build());
//        mAdViewLayout.addView(mAdView);
        System.out.println(bannerfb);
        if (Tools.ads.equals("admob")){
            mAdViewLayout.addView(mAdView);

        }
        else {
            mAdViewLayout.addView(adViewfb);
        }






    }


    private AdSize getAdSize(Context context,Display display) {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.

        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }


}