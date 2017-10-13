#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>
#import <IronSource/IronSource.h>

@interface IronSourceAdsPlugin : CDVPlugin <ISRewardedVideoDelegate ,ISInterstitialDelegate ,ISOfferwallDelegate>

- (void)init:(CDVInvokedUrlCommand *)command;

- (void)showRewardedAd:(CDVInvokedUrlCommand *)command;

- (void)isRewardedVideoPlacementCapped:(CDVInvokedUrlCommand *)command;
    
- (void)isRewardedVideoAvailable:(CDVInvokedUrlCommand *)command;

- (void)showInterstitial:(CDVInvokedUrlCommand *)command;

- (void)showOfferwall:(CDVInvokedUrlCommand *)command;

- (void)loadInterstitial:(CDVInvokedUrlCommand *)command;

- (void)isInterstitialReady:(CDVInvokedUrlCommand *)command;

- (void)isInterstitialPlacementCapped:(CDVInvokedUrlCommand *)command;

- (void)validateIntegration:(CDVInvokedUrlCommand *)command;

- (void)getInterstitialPlacementInfo:(CDVInvokedUrlCommand *)command;

- (void)setDynamicUserId:(CDVInvokedUrlCommand *)command;






    
@end
