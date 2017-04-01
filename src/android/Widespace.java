package com.encode.widespace;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.widespace.AdInfo.AdType;
import com.widespace.AdSpace;
import com.widespace.adspace.models.AnimationDirection;
import com.widespace.adspace.models.MediaType;
import com.widespace.adspace.models.PrefetchStatus;
import com.widespace.exception.ExceptionTypes;
import com.widespace.interfaces.AdAnimationEventListener;
import com.widespace.interfaces.AdErrorEventListener;
import com.widespace.interfaces.AdEventListener;
import com.widespace.interfaces.AdMediaEventListener;

import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;

public class Widespace extends CordovaPlugin {

    private static final String TAG = "Widespace";

    private AdSpace adSpace;
    private RelativeLayout adSpaceLayout = null;

    private String bannerSid;
    private String interstitialSid;
    private boolean displayBannerTop = false;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        PluginResult result = null;

        if ("setWidespaceSettings".equals(action)) {
            JSONObject options = args.optJSONObject(0);
            result = WidespaceSettings(options, callbackContext);

        } else if ("showBannerAd".equals(action)) {
            JSONObject options = args.optJSONObject(0);
            result = ShowBannerAd(options, callbackContext);
            Log.d(TAG, "showBannerAd");

        } else if ("showInterstitialAd".equals(action)) {
            JSONObject options = args.optJSONObject(0);
            result = showInterstitialAd(options, callbackContext);

        } else if ("closeAd".equals(action)) {
            result = willCloseAd(callbackContext);
        }

        if (result != null) callbackContext.sendPluginResult(result);

        return true;
    }

    private PluginResult WidespaceSettings(JSONObject options, CallbackContext callbackContext) {

        this.setOptions(options);

        callbackContext.success();
        return null;
    }

    private void setOptions(JSONObject options) {
        if (options == null) return;

        Log.d(TAG, "Option value: " + options);

        try {
            this.interstitialSid = options.getString("theInterstitialSid");
            this.bannerSid = options.getString("theBannerSid");
            this.displayBannerTop = options.optBoolean("displayBannerTop");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d(TAG, "interstitialSid value: " + interstitialSid);
    }

    private PluginResult ShowBannerAd(JSONObject options, final CallbackContext callbackContext) {

        Log.d(TAG, "execute showBannerAd");

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {

                Log.d(TAG, "adspace = null showBannerAd");

                adSpace = new AdSpace(cordova.getActivity(), bannerSid, false, false);
                adSpace.setAdEventListener(adEventListener);
                adSpace.setAdErrorEventListener(adErrorListener);
                adSpace.setAdAnimationEventListener(adAnimationListener);
                adSpace.setAdMediaEventListener(adMediaEventListener);

                if (adSpace.getParent() != null) {
                    ((ViewGroup) adSpace.getParent()).removeView(adSpace);
                }

                if (adSpaceLayout == null) {
                    Log.d(TAG, "adViewLayout = null showBannerAd");

                    adSpaceLayout = new RelativeLayout(cordova.getActivity());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    ((ViewGroup) webView.getView().getRootView()).addView(adSpaceLayout, params);
                }

                adSpace.runAd();

                Log.d(TAG, "runAd showBannerAd");


                if (adSpace.getParent() != null) {
                    ((ViewGroup) adSpace.getParent()).removeView(adSpace);
                }

                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params2.setMargins(0, 50, 0, 0);
                params2.addRule(displayBannerTop ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.ALIGN_PARENT_BOTTOM);
                adSpaceLayout.addView(adSpace, params2);
                adSpaceLayout.bringToFront();

                ViewGroup parentView = ((ViewGroup) webView.getView().getRootView());

                parentView.bringToFront();

                callbackContext.success();
            }
        });

        return null;
    }

    //

    private PluginResult showInterstitialAd(JSONObject options, final CallbackContext callbackContext) {


        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {

                adSpace = new AdSpace(cordova.getActivity(), interstitialSid, false, false);
                adSpace.setAdEventListener(adEventListener);
                adSpace.setAdErrorEventListener(adErrorListener);
                adSpace.setAdAnimationEventListener(adAnimationListener);
                adSpace.setAdMediaEventListener(adMediaEventListener);

                adSpace.runAd();

                callbackContext.success();
            }
        });

        return null;
    }

    private PluginResult willCloseAd(CallbackContext callbackContext) {

        final CallbackContext delayCallback = callbackContext;
        cordova.getActivity().runOnUiThread(new Runnable() {

            public void run() {

                if (adSpace != null) {
                    adSpace.closeAd();
                }

                delayCallback.success();
            }
        });

        return null;
    }


    private AdEventListener adEventListener = new AdEventListener() {

        public void onNoAdRecieved(AdSpace sender) {
            Log.d(TAG, "onNoAdRecieved SID:" + sender.getSID());
            webView.loadUrl("javascript:cordova.fireDocumentEvent('didReceiveNoAd');");
        }

        public void onAdLoading(AdSpace sender) {
            Log.d(TAG, "onAdLoading SID:" + sender.getSID());
            webView.loadUrl("javascript:cordova.fireDocumentEvent('willLoadAd');");
        }

        public void onAdLoaded(AdSpace sender, AdType adType) {
            Log.d(TAG, "onAdLoaded SID:" + sender.getSID());
            webView.loadUrl("javascript:cordova.fireDocumentEvent('didLoadAd');");
        }

        public void onAdClosing(AdSpace sender, AdType adType) {
            Log.d(TAG, "onAdClosing SID:" + sender.getSID());
            webView.loadUrl("javascript:cordova.fireDocumentEvent('willCloseAd');");
        }

        public void onAdClosed(AdSpace sender, AdType adType) {
            Log.d(TAG, "onAdClosed SID:" + sender.getSID());
            webView.loadUrl("javascript:cordova.fireDocumentEvent('didCloseAd');");
        }

        public void onAdDismissed(AdSpace arg0, boolean arg1, AdType arg2) {
            // TODO Auto-generated method stub
        }

        public void onAdDismissing(AdSpace arg0, boolean arg1, AdType arg2) {
            // TODO Auto-generated method stub
        }

        public void onAdPresented(AdSpace arg0, boolean arg1, AdType arg2) {
            // TODO Auto-generated method stub
        }

        public void onAdPresenting(AdSpace arg0, boolean arg1, AdType arg2) {
            // TODO Auto-generated method stub
        }

        public void onPrefetchAd(AdSpace sender, com.widespace.adspace.models.PrefetchStatus arg1) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onPrefetchAd SID:" + sender.getSID());
        }
    };

    private AdErrorEventListener adErrorListener = new AdErrorEventListener() {
        public void onFailedWithError(Object sender, ExceptionTypes type, String message, Exception exeception) {
            Log.d(TAG, "onFailedWithError : error message # " + message);
        }
    };

    private AdAnimationEventListener adAnimationListener = new AdAnimationEventListener() {

        public void onAnimatingIn(AdSpace sender, int finalWidth, int finalHeight, int xPosition, int yPosition) {
            Log.d(TAG, "onAnimatingIn SID:" + sender.getSID());
            webView.loadUrl("javascript:cordova.fireDocumentEvent('willAnimateIn');");
        }

        public void onAnimatedIn(AdSpace sender, int finalWidth, int finalHeight, int xPosition, int yPosition) {
            Log.d(TAG, "onAnimatedIn SID:" + sender.getSID());
            webView.loadUrl("javascript:cordova.fireDocumentEvent('didAnimateIn');");
        }

        public void onAnimatingOut(AdSpace sender, int finalWidth, int finalHeight, int xPosition, int yPosition) {
            Log.d(TAG, "onAnimatingOut SID:" + sender.getSID());
            webView.loadUrl("javascript:cordova.fireDocumentEvent('willAnimateOut');");
        }

        public void onAnimatedOut(AdSpace sender, int finalWidth, int finalHeight, int xPosition, int yPosition) {
            Log.d(TAG, "onAnimatedOut SID:" + sender.getSID());
            webView.loadUrl("javascript:cordova.fireDocumentEvent('didAnimateOut');");
        }


        public void onAdResized(AdSpace sender, int finalWidth, int finalHeight) {
            Log.d(TAG, "onAdResized SID:" + sender.getSID());
        }

        public void onAdCollapsed(AdSpace sender, com.widespace.adspace.models.AnimationDirection arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onCollapseAd SID:" + sender.getSID());
        }

        public void onAdExpanded(AdSpace sender, com.widespace.adspace.models.AnimationDirection arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onExpandAd SID:" + sender.getSID());
        }
    };

    // If there is any audio or video in Ad then the following methods for media
    // events will be handy
    private AdMediaEventListener adMediaEventListener = new AdMediaEventListener() {

        public void onMediaStarting(AdSpace sender, com.widespace.adspace.models.MediaType mediaType) {
            Log.d(TAG, "onMediaStarting SID:" + sender.getSID());
            webView.loadUrl("javascript:cordova.fireDocumentEvent('willStartMedia');");
        }

        public void onMediaCompleted(AdSpace sender, com.widespace.adspace.models.MediaType mediaType) {
            Log.d(TAG, "onMediaCompleted SID:" + sender.getSID());
            webView.loadUrl("javascript:cordova.fireDocumentEvent('didCompleteMedia');");
        }

        public void onMediaStopped(AdSpace sender, com.widespace.adspace.models.MediaType mediaType) {
            Log.d(TAG, "onMediaStopped SID:" + sender.getSID());
            webView.loadUrl("javascript:cordova.fireDocumentEvent('didStopMedia');");
        }
    };
}