# cordova-plugin-widespace-sdk

Plugin used to present ads from Widespace.


## Installation

+You can install our plugin through both urls below, pick the one that suits your development environment. If you are unsure use the first one with `https`.
+`$ cordova plugin add https://bitbucket.org/widespacegit/cordova-plugin-widespace-sdk.git`
`$ cordova plugin add git@bitbucket:widespacegit/cordova-plugin-widespace-sdk`


## Supported Platforms

- Android
- iOS


## Methods

- widespace.setWidespaceSettings
- widespace.showBannerAd
- widespace.showInterstitialAd
- widespace.closeAd 


## widespace.setWidespaceSettings

Run this before any other method, this is used to configure the Widespace plugin so that you can present ads.

```js
                    
window.plugins.widespace.setWidespaceSettings({
   theBannerSid: widespaceSid.bannerSid,
   theInterstitialSid: widespaceSid.interstitialSid,
   displayBannerTop: false //Choose betweend true/false to display banner at top or bottom of the view. 
});

```

## widespace.showBannerAd

Presents a banner in the top or bottom portion of the screen.

```js
                    
window.plugins.widespace.showBannerAd();

```


## widespace.showInterstitialAd

Presents an interstitial ad covering the whole screen.

```js
                    
window.plugins.widespace.showInterstitialAd();

```


## widespace.closeAd

Closes/dismisses all/any presented ad(s).

```js
                    
window.plugins.widespace.closeAd();

```

