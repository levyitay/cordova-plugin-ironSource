var exec = require("cordova/exec");
function IronSourceAds(appKey, userId, successCallback) {
    exec(successCallback, null, 'IronSourceAdsPlugin', 'init', [appKey, userId]);
   
    this.showRewardedVideo = function(placementName, successCallback, failureCallback) {
        exec(successCallback, null, 'IronSourceAdsPlugin', 'showRewardedAd', [placementName]);
    };
    this.getRewardedVideoPlacementInfo = function(placementName, successCallback, failureCallback) {
        exec(successCallback, failureCallback, 'IronSourceAdsPlugin', 'getRewardedVideoPlacementInfo', [placementName]);
    };
    this.isRewardedVideoPlacementCapped = function(placementName, successCallback, failureCallback) {
        exec(successCallback, failureCallback, 'IronSourceAdsPlugin', 'isRewardedVideoPlacementCapped', [placementName]);
    };
    this.isRewardedVideoReady = function(successCallback, failureCallback) {
       exec(successCallback, failureCallback, 'IronSourceAdsPlugin', 'isRewardedVideoAvailable', []);
    };
    this.validateIntegration = function(successCallback, failureCallback) {
        exec(successCallback, failureCallback, 'IronSourceAdsPlugin', 'validateIntegration', []);
    };
    this.setDynamicUserId = function(userId) {
        exec(null, null, 'IronSourceAdsPlugin', 'setDynamicUserId', [userId]);
    };
    this.loadInterstitial = function() {
        exec(null, null, 'IronSourceAdsPlugin', 'loadInterstitial', []);
    };
    this.isInterstitialReady = function(successCallback, failureCallback) {
       exec(successCallback, failureCallback, 'IronSourceAdsPlugin', 'isInterstitialReady', []);
    };
    this.getInterstitialPlacementInfo = function(placementName, successCallback, failureCallback) {
        exec(successCallback, failureCallback, 'IronSourceAdsPlugin', 'getInterstitialPlacementInfo', [placementName]);
    };
    this.showInterstitial = function(placementName) {
        exec(null, null, 'IronSourceAdsPlugin', 'showInterstitial', [placementName]);
    };
    this.showOfferwall = function(placementName) {
        exec(null, null, 'IronSourceAdsPlugin', 'showOfferwall', [placementName]);
    };
    this.subscribeOnNotifications = function(successCallback) {
        exec(successCallback, null, 'IronSourceAdsPlugin', 'subscribeOnNotifications', []);
    };
  

}


    module.exports = IronSourceAds;
