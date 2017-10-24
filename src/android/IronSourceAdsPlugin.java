package com.deineagentur.cordova.plugin.ironsource;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.ironsource.adapters.supersonicads.SupersonicConfig;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.InterstitialPlacement;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;

public class IronSourceAdsPlugin extends CordovaPlugin implements RewardedVideoListener, OfferwallListener, InterstitialListener {
    private static final String EVENT_INTERSTITIAL_LOADED = "interstitialLoaded";
    private static final String EVENT_INTERSTITIAL_LOAD_FAILED = "interstitialLoadFailed";
    private static final String EVENT_INTERSTITIAL_SHOWN = "interstitialShown";
    private static final String EVENT_INTERSTITIAL_SHOW_FAILED = "interstitialShowFailed";
    private static final String EVENT_INTERSTITIAL_CLICKED = "interstitialClicked";
    private static final String EVENT_INTERSTITIAL_CLOSED = "interstitialClosed";

    private static final String EVENT_OFFERWALL_CLOSED = "offerwallClosed";
    private static final String EVENT_OFFERWALL_CREDIT_FAILED = "offerwallCreditFailed";
    private static final String EVENT_OFFERWALL_CREDITED = "offerwallCreditReceived";
    private static final String EVENT_OFFERWALL_SHOW_FAILED = "offerwallShowFailed";
    private static final String EVENT_OFFERWALL_OPENED = "offerwallOpened";
    private static final String EVENT_OFFERWALL_READY = "offerwallReady";

    private static final String EVENT_REWARDED_VIDEO_FAILED = "rewardedVideoFailed";
    private static final String EVENT_REWARDED_VIDEO_REWARDED = "rewardedVideoRewardReceived";
    private static final String EVENT_REWARDED_VIDEO_ENDED = "rewardedVideoEnded";
    private static final String EVENT_REWARDED_VIDEO_STARTED = "rewardedVideoStarted";
    private static final String EVENT_REWARDED_VIDEO_AVAILABILITY_CHANGED = "rewardedVideoAvailabilityChanged";
    private static final String EVENT_REWARDED_VIDEO_CLOSED = "rewardedVideoClosed";
    private static final String EVENT_REWARDED_VIDEO_OPENED = "rewardedVideoOpened";



    private static final String TAG = "[IronSourceAdsPlugin]";

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.d(TAG, "Initializing IronSourceAdsPlugin");
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        Log.d(TAG, "onPause");
        IronSource.onPause(this.cordova.getActivity());
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        Log.d(TAG, "onResume");
        IronSource.onResume(this.cordova.getActivity());
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "Execute: " + action);

        if(action.equals("init")) {
            final String appKey = args.getString(0);
            final String userId = args.getString(1);
            final IronSourceAdsPlugin vthis = this;
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Boolean s = vthis.init(appKey, userId);
                    callbackContext.success();
                }
            });
            return true;
        } else if(action.equals("showRewardedAd")) {
            String placementName = "DefaultRewardedVideo";
            if (args.length() == 1) {
                placementName = args.getString(0);
            }
            return this.showRewardedVideo(placementName);
        } else if(action.equals("getRewardedVideoPlacementInfo")) {
            String placementName = "DefaultRewardedVideo";
            if (args.length() == 1) {
                placementName = args.getString(0);
            }
            return this.getRewardedVideoPlacementInfo(placementName, callbackContext);
        } else if(action.equals("isRewardedVideoPlacementCapped")) {
            String placementName = "DefaultRewardedVideo";
            if (args.length() == 1) {
                placementName = args.getString(0);
            }
            return this.isRewardedVideoPlacementCapped(placementName, callbackContext);
        }else if (action.equals("isRewardedVideoAvailable")){

            return this.isRewardedVideoAvailable(callbackContext);

        } else if(action.equals("setDynamicUserId")) {
            String userId = args.getString(0);
            return this.setDynamicUserId(userId);
        } else if(action.equals("loadInterstitial")) {
            return this.loadInterstitial();
        } else if(action.equals("isInterstitialReady")) {
            return this.isInterstitialReady(callbackContext);
        } else if(action.equals("getInterstitialPlacementInfo")) {
            String placementName = "DefaultInterstitial";
            if (args.length() == 1) {
                placementName = args.getString(0);
            }
            return this.getInterstitialPlacementInfo(placementName, callbackContext);
        }  else if (action.equals("validateIntegration")) {
            IntegrationHelper.validateIntegration(this.cordova.getActivity());
            callbackContext.success();
            return true;
        } else if(action.equals("showInterstitial")) {
            String placementName = "DefaultInterstitial";
            if (args.length() == 1) {
                placementName = args.getString(0);
            }
            return this.showInterstitial(placementName);
        } else if(action.equals("showOfferwall")) {
            String placementName = "DefaultOfferWall";
            if (args.length() == 1) {
                placementName = args.getString(0);
            }
            return this.showOfferwall(placementName);
        }else if(action.equals("isInterstitialPlacementCapped")){
            String placementName = "DefaultRewardedVideo";
            if (args.length() == 1) {
                placementName = args.getString(0);
            }
            return this.isInterstitialPlacementCapped(placementName, callbackContext);
        }else if(action.equals("onResume")){
            return this.onActivityResumeCordova(callbackContext);
        }else if(action.equals("onPause")){
            return this.onActivityPauseCordova(callbackContext);
        }
        return false;
    }

    private boolean isRewardedVideoAvailable(CallbackContext callbackContext) {
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, IronSource.isRewardedVideoAvailable()));

        return true;
    }

    private void fireEvent(final String event) {
        final CordovaWebView view = this.webView;
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.loadUrl("javascript:cordova.fireWindowEvent('" + event + "');");
            }
        });
    }

    private void fireEvent(final String event, final JSONObject data) {
        final CordovaWebView view = this.webView;
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.loadUrl(String.format("javascript:cordova.fireWindowEvent('%s', %s);", event, data.toString()));
            }
        });
    }

    private boolean init(String appKey, String userId) {
        IronSource.setRewardedVideoListener(this);
        IronSource.setOfferwallListener(this);
        SupersonicConfig.getConfigObj().setClientSideCallbacks(true);
        IronSource.setInterstitialListener(this);
        IronSource.setUserId(userId);
        IronSource.init(this.cordova.getActivity(), appKey);
        return true;
    }

    private boolean showRewardedVideo(String placementName) {
        IronSource.showRewardedVideo(placementName);
        return true;
    }

    private boolean getRewardedVideoPlacementInfo(String placementName, CallbackContext callbackContext) {
        Placement placement = IronSource.getRewardedVideoPlacementInfo(placementName);
        if (placement != null) {
            JSONObject event = new JSONObject();
            try {
                event.put("placementName", placement.getPlacementName());
                event.put("rewardName", placement.getRewardName());
                event.put("rewardAmount", placement.getRewardAmount());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callbackContext.success(event);
        }
        else {
            callbackContext.error("placementName_invalid");
        }
        return true;
    }

    private boolean isRewardedVideoPlacementCapped(String placementName, CallbackContext callbackContext) {
        boolean isCapped = IronSource.isRewardedVideoPlacementCapped(placementName);
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, isCapped));

        return true;
    }

    private boolean setDynamicUserId(String userId) {
        IronSource.setDynamicUserId(userId);
        return true;
    }

    private boolean loadInterstitial() {
        IronSource.loadInterstitial();
        return true;
    }

    private boolean isInterstitialReady(CallbackContext callbackContext) {
        boolean isReady = IronSource.isInterstitialReady();
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, isReady));

        return true;
    }

    private boolean getInterstitialPlacementInfo(String placementName, CallbackContext callbackContext) {
        InterstitialPlacement placement = IronSource.getInterstitialPlacementInfo(placementName);
        if (placement != null) {
            JSONObject event = new JSONObject();
            try {

                event.put("placementName", placement.getPlacementName());
                event.put("placementId", placement.getPlacementId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callbackContext.success(event);
        }
        else {
            callbackContext.error("placementName_invalid");
        }
        return true;
    }

    private boolean isInterstitialPlacementCapped(String placementName, CallbackContext callbackContext){
        boolean isCapped = IronSource.isInterstitialPlacementCapped(placementName);
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, isCapped));


        return true;
    }


    private boolean showInterstitial(String placementName) {
        IronSource.showInterstitial(placementName);
        return true;
    }

    private boolean showOfferwall(String placementName) {
        IronSource.showOfferwall(placementName);
        return true;
    }


    private boolean onActivityResumeCordova(CallbackContext callbackContext){
        IronSource.onResume(this.cordova.getActivity());
        return true;
    }

    private boolean onActivityPauseCordova(CallbackContext callbackContext){
        IronSource.onPause(this.cordova.getActivity());
        return true;
    }

    // --------- IronSource Rewarded Video Listener ---------
    @Override
    public void onRewardedVideoAdOpened() {
        this.fireEvent(EVENT_REWARDED_VIDEO_OPENED);
    }

    @Override
    public void onRewardedVideoAdClosed() {
        this.fireEvent(EVENT_REWARDED_VIDEO_CLOSED);
    }

    @Override
    public void onRewardedVideoAvailabilityChanged(boolean b) {
        final JSONObject data = new JSONObject();
        try {
            data.put("available", b);
        } catch (JSONException e) {
        }
        this.fireEvent(EVENT_REWARDED_VIDEO_AVAILABILITY_CHANGED, data);
    }

    @Override
    public void onRewardedVideoAdStarted() {
        this.fireEvent(EVENT_REWARDED_VIDEO_STARTED);
    }

    @Override
    public void onRewardedVideoAdEnded() {
        this.fireEvent(EVENT_REWARDED_VIDEO_ENDED);
    }

    @Override
    public void onRewardedVideoAdRewarded(Placement placement) {
        final JSONObject data = new JSONObject();
        try {
            data.put("placementName", placement.getPlacementName());
            data.put("rewardName", placement.getRewardName());
            data.put("rewardAmount", placement.getRewardAmount());
        } catch (JSONException e) {
        }
        this.fireEvent(EVENT_REWARDED_VIDEO_REWARDED, data);
    }

    @Override
    public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {
        final JSONObject data = new JSONObject();
        try {
            data.put("errorCode", ironSourceError.getErrorCode());
            data.put("errorMessage", ironSourceError.getErrorMessage());
        } catch (JSONException e) {
        }
        this.fireEvent(EVENT_REWARDED_VIDEO_FAILED, data);
    }

    // --------- IronSource Offerwall Listener ---------


    @Override
    public void onOfferwallAvailable(boolean b) {
        final JSONObject data = new JSONObject();
        try {
            data.put("available", b);
        } catch (JSONException e) {
        }
        this.fireEvent(EVENT_OFFERWALL_READY, data);
    }

    @Override
    public void onOfferwallOpened() {
        this.fireEvent(EVENT_OFFERWALL_OPENED);
    }

    @Override
    public void onOfferwallShowFailed(IronSourceError ironSourceError) {
        final JSONObject data = new JSONObject();
            try {
                data.put("errorCode", ironSourceError.getErrorCode());
                data.put("errorMessage", ironSourceError.getErrorMessage());
            } catch (JSONException e) {}
        this.fireEvent(EVENT_OFFERWALL_SHOW_FAILED, data);
    }

    @Override
    public boolean onOfferwallAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {
        final JSONObject data = new JSONObject();
        try {
            data.put("credits", credits);
            data.put("totalCredits", totalCredits);
            data.put("totalCreditsFlag", totalCreditsFlag);
        } catch (JSONException e) {
        }
        this.fireEvent(EVENT_OFFERWALL_CREDITED, data);
        return false;
    }

    @Override
    public void onGetOfferwallCreditsFailed(IronSourceError ironSourceError) {
        final JSONObject data = new JSONObject();
        try {
            data.put("errorCode", ironSourceError.getErrorCode());
            data.put("errorMessage", ironSourceError.getErrorMessage());
        } catch (JSONException e) {
        }
        this.fireEvent(EVENT_OFFERWALL_CREDIT_FAILED, data);
    }

    @Override
    public void onOfferwallClosed() {
        this.fireEvent(EVENT_OFFERWALL_CLOSED);
    }

    // --------- IronSource Interstitial Listener ---------


    @Override
    public void onInterstitialAdReady() {
        this.fireEvent(EVENT_INTERSTITIAL_LOADED);
    }

    @Override
    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
        final JSONObject data = new JSONObject();
        try {
            data.put("errorCode", ironSourceError.getErrorCode());
            data.put("errorMessage", ironSourceError.getErrorMessage());
        } catch (JSONException e) {
        }
        this.fireEvent(EVENT_INTERSTITIAL_LOAD_FAILED, data);
    }

    @Override
    public void onInterstitialAdOpened() {

    }

    @Override
    public void onInterstitialAdClosed() {
        this.fireEvent(EVENT_INTERSTITIAL_CLOSED);
    }

    @Override
    public void onInterstitialAdShowSucceeded() {
        this.fireEvent(EVENT_INTERSTITIAL_SHOWN);
    }

    @Override
    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
        final JSONObject data = new JSONObject();
        try {
            data.put("errorCode", ironSourceError.getErrorCode());
            data.put("errorMessage", ironSourceError.getErrorMessage());
        } catch (JSONException e) {
        }
        this.fireEvent(EVENT_INTERSTITIAL_SHOW_FAILED, data);
    }

    @Override
    public void onInterstitialAdClicked() {
        this.fireEvent(EVENT_INTERSTITIAL_CLICKED);
    }

}