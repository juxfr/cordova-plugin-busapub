/*
 Widespace SDK for Cordova
 www.widespace.com

  Together with Encode in collaboration with LehtuMedia
 Encode - http://encode.fi
 LehtuMedia - http://lehtumedia.com
 */

var argscheck = require('cordova/argscheck');
var exec = require('cordova/exec');

var widespaceAd = {};

widespaceAd.setWidespaceSettings =
  function(options, successCallback, failureCallback) {
    if(typeof options === 'object'
      && typeof options.theBannerSid === 'string'
        && options.theBannerSid.length > 0) {
      cordova.exec(
            successCallback,
            failureCallback,
            'Widespace',
            'setWidespaceSettings',
            [options]
        );
    } else {
      if(typeof failureCallback === 'function') {
        failureCallback('options.theBannerSid is not valid.')
      }
    }
  };

//Interstitial ad space (full screen)
widespaceAd.showInterstitialAd =
function(options, successCallback, failureCallback) {
  if(typeof options === 'undefined' || options == null) options = {};
  cordova.exec(
      successCallback,
      failureCallback,
      'Widespace',
      'showInterstitialAd',
      [ options ]
  );
};

//Flexible ad space that could contain, Panorama(320x50), PanoramaXL(320x160) & Takeover(320x320)
widespaceAd.showBannerAd =
function(options, successCallback, failureCallback) {
  if(typeof options === 'undefined' || options == null) options = {};
  cordova.exec(
      successCallback,
      failureCallback,
      'Widespace',
      'showBannerAd',
      [ options ]
  );
};

//Close ad
widespaceAd.closeAd =
function(options, successCallback, failureCallback) {
  if(typeof options === 'undefined' || options == null) options = {};
  cordova.exec(
      successCallback,
      failureCallback,
      'Widespace',
      'closeAd',
      [ options ]
  );
};

module.exports = widespaceAd;
