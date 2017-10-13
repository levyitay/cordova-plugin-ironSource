#import "IronSourcePlugin.h"

static NSString *const EVENT_INTERSTITIAL_LOADED = @"interstitialLoaded";
static NSString *const EVENT_INTERSTITIAL_LOAD_FAILED = @"interstitialLoadFailed";
static NSString *const EVENT_INTERSTITIAL_SHOWN = @"interstitialShown";
static NSString *const EVENT_INTERSTITIAL_SHOW_FAILED = @"interstitialShowFailed";
static NSString *const EVENT_INTERSTITIAL_CLICKED = @"interstitialClicked";
static NSString *const EVENT_INTERSTITIAL_CLOSED = @"interstitialClosed";

static NSString *const EVENT_OFFERWALL_CLOSED = @"offerwallClosed";
static NSString *const EVENT_OFFERWALL_CREDIT_FAILED = @"offerwallCreditFailed";
static NSString *const EVENT_OFFERWALL_CREDITED = @"offerwallCreditReceived";
static NSString *const EVENT_OFFERWALL_SHOW_FAILED = @"offerwallShowFailed";
static NSString *const EVENT_OFFERWALL_OPENED = @"offerwallOpened";
static NSString *const EVENT_OFFERWALL_READY = @"offerwallReady";

static NSString *const EVENT_REWARDED_VIDEO_FAILED = @"rewardedVideoFailed";
static NSString *const EVENT_REWARDED_VIDEO_REWARDED = @"rewardedVideoRewardReceived";
static NSString *const EVENT_REWARDED_VIDEO_ENDED = @"rewardedVideoEnded";
static NSString *const EVENT_REWARDED_VIDEO_STARTED = @"rewardedVideoStarted";
static NSString *const EVENT_REWARDED_VIDEO_AVAILABILITY_CHANGED = @"rewardedVideoAvailabilityChanged";
static NSString *const EVENT_REWARDED_VIDEO_CLOSED = @"rewardedVideoClosed";
static NSString *const EVENT_REWARDED_VIDEO_OPENED = @"rewardedVideoOpened";


@implementation IronSourceAdsPlugin


#pragma mark - CDVPlugin

- (void)pluginInitialize {

}

- (void)init:(CDVInvokedUrlCommand *)command {

    NSString *appKey = [command argumentAtIndex:0];
    NSString *userId = [command argumentAtIndex:1];

    //
    // Initialize 'Rewarded Video'
    
    [IronSource setRewardedVideoDelegate:self];
    [IronSource setOfferwallDelegate:self];
    [IronSource setInterstitialDelegate:self];

    [IronSource setUserId:userId];
    [IronSource initWithAppKey:appKey];


    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)validateIntegration:(CDVInvokedUrlCommand *)command{
        [ISIntegrationHelper validateIntegration];
        
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];

}

- (void)getRewardedVideoPlacementInfo:(CDVInvokedUrlCommand *)command{
    NSString *placementName = [command argumentAtIndex:0];

    
    ISPlacementInfo *placement = [IronSource rewardedVideoPlacementInfo:placementName];
    NSDictionary *data = @{
            @"placement": @{
                    @"name": placement.placementName,
                    @"reward": placement.rewardName,
                    @"amount": placement.rewardAmount
            }
    };
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary: data];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    

}
    
    - (void)isRewardedVideoAvailable:(CDVInvokedUrlCommand *)command{
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:[IronSource hasRewardedVideo]];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }

- (void)setDynamicUserId:(CDVInvokedUrlCommand *)command{
    NSString *userId = [command argumentAtIndex:0];

    [IronSource setUserId:userId];

    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)isRewardedVideoPlacementCapped:(CDVInvokedUrlCommand *)command{
    NSString *placementName = [command argumentAtIndex:0];

    BOOL capped = [IronSource isRewardedVideoCappedForPlacement:placementName];

    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool: capped];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];

}
- (void)isInterstitialReady:(CDVInvokedUrlCommand *)command{
    BOOL ready = [IronSource hasInterstitial];

    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool: ready];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)isInterstitialPlacementCapped:(CDVInvokedUrlCommand *)command{
    NSString *placementName = [command argumentAtIndex:0];

    BOOL capped = [IronSource isInterstitialCappedForPlacement:placementName];

    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool: capped];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];

}


- (void)getInterstitialPlacementInfo:(CDVInvokedUrlCommand *)command{
    
    NSString *placementName = [command argumentAtIndex:0];

    
    ISPlacementInfo *placement = [IronSource rewardedVideoPlacementInfo:placementName];
    NSDictionary *data = @{
            @"placement": @{
                    @"name": placement.placementName,
                    @"reward": placement.rewardName,
                    @"amount": placement.rewardAmount
            }
    };
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary: data];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    
    [self fireEvent:EVENT_REWARDED_VIDEO_REWARDED withData:data];

}

- (void)showRewardedAd:(CDVInvokedUrlCommand *)command {

    NSString *placementName = [command argumentAtIndex:0];
    UIViewController *vc = [[[UIApplication sharedApplication] keyWindow] rootViewController]; 

    if (placementName == nil) {
        [IronSource showRewardedVideoWithViewController:vc];
    } else {
        [IronSource showRewardedVideoWithViewController:vc placement:placementName];
    }
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)showInterstitial:(CDVInvokedUrlCommand *)command {
    
    NSString *placementName = [command argumentAtIndex:0];
    UIViewController *vc = [[[UIApplication sharedApplication] keyWindow] rootViewController]; 

    if (placementName == nil) {
        [IronSource showInterstitialWithViewController:vc];
    }else{
        [IronSource showInterstitialWithViewController:vc placement:placementName];

    }
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)showOfferwall:(CDVInvokedUrlCommand *)command {
    UIViewController *vc = [[[UIApplication sharedApplication] keyWindow] rootViewController];

     [IronSource showOfferwallWithViewController:vc];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)loadInterstitial:(CDVInvokedUrlCommand *)command { 

    [IronSource loadInterstitial];
     CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void) fireEvent: (NSString *) event {
    NSString *js = [NSString stringWithFormat:@"cordova.fireWindowEvent('%@')", event];
    [self.commandDelegate evalJs:js];
}

- (void) fireEvent: (NSString *) event withData: (NSDictionary *) data {
    NSError *error = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:data options:kNilOptions error:&error];

    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    NSString *js = [NSString stringWithFormat:@"cordova.fireWindowEvent('%@', %@)", event, jsonString];
    NSLog(@"firing %@:",js);
    [self.commandDelegate evalJs:js];

}

#pragma mark - ISRewardedVideoDelegate



- (void)rewardedVideoHasChangedAvailability:(BOOL)available {

    NSDictionary *data = @{
            @"available" : @(available)
    };
    [self fireEvent:EVENT_REWARDED_VIDEO_AVAILABILITY_CHANGED withData:data];
}

- (void)rewardedVideoDidOpen {

    [self fireEvent:EVENT_REWARDED_VIDEO_OPENED];
}

- (void)rewardedVideoDidStart {
    [self fireEvent:EVENT_REWARDED_VIDEO_STARTED];
}

- (void)rewardedVideoDidEnd {
    [self fireEvent:EVENT_REWARDED_VIDEO_ENDED];
}

- (void)rewardedVideoDidClose {
    [self fireEvent:EVENT_REWARDED_VIDEO_CLOSED];
}

- (void)didReceiveRewardForPlacement:(ISPlacementInfo *)placementInfo {

    NSDictionary *data = @{
            @"placement": @{
                    @"name": placementInfo.placementName,
                    @"reward": placementInfo.rewardName,
                    @"amount": placementInfo.rewardAmount
            }
    };
    [self fireEvent:EVENT_REWARDED_VIDEO_REWARDED withData:data];
}

- (void)rewardedVideoDidFailToShowWithError:(NSError *)error {

    NSDictionary *data = @{
            @"error": @{
                    @"user" : @(error.code),
                    @"message" : error.description
            }
    };

    [self fireEvent:EVENT_REWARDED_VIDEO_FAILED withData: data];
}

- (void)interstitialDidOpen {
}

- (void)interstitialDidShow {

    [self fireEvent:EVENT_INTERSTITIAL_SHOWN];
}

- (void)interstitialDidFailToShowWithError:(NSError *)error {
    NSDictionary *data = @{
                           @"error": @{
                                   @"user" : @(error.code),
                                   @"message" : error.description
                                   }
                           };
    
    [self fireEvent:EVENT_INTERSTITIAL_SHOW_FAILED withData: data];
}

- (void)interstitialDidFailToLoadWithError:(NSError *)error {

    NSDictionary *data = @{
            @"error": @{
                    @"user" : @(error.code),
                    @"message" : error.description
            }
    };

    [self fireEvent:EVENT_INTERSTITIAL_LOAD_FAILED withData: data];
}


- (void)interstitialDidLoad {

    [self fireEvent:EVENT_INTERSTITIAL_LOADED];
}

- (void)didClickInterstitial {
    [self fireEvent:EVENT_INTERSTITIAL_CLICKED];
}


- (void)interstitialDidClose {

    [self fireEvent:EVENT_INTERSTITIAL_CLOSED];
}


- (void)offerwallHasChangedAvailability:(BOOL)available {
    NSDictionary *data = @{
        @"available" : @(available)
    };
   [self fireEvent:EVENT_OFFERWALL_READY withData:data ];

}

- (void)offerwallDidShow {

    [self fireEvent:EVENT_OFFERWALL_OPENED];
}



- (void)offerwallDidFailToShowWithError:(NSError *)error {

    NSDictionary *data = @{
            @"error": @{
                    @"user" : @(error.code),
                    @"message" : error.description
            }
    };

    [self fireEvent:EVENT_OFFERWALL_SHOW_FAILED withData: data];
}

- (void)offerwallDidClose {

    [self fireEvent:EVENT_OFFERWALL_CLOSED];
}

- (BOOL)didReceiveOfferwallCredits:(NSDictionary *)creditInfo {

    NSDictionary *data = @{
            @"credit": @{
                    @"amount": creditInfo[@"credits"],
                    @"total": creditInfo[@"totalCredits"],
                    @"estimate": creditInfo[@"totalCreditsFlag"]
            }
    };
    [self fireEvent:EVENT_OFFERWALL_CREDITED withData:data];
    return YES;
}

- (void)didFailToReceiveOfferwallCreditsWithError:(NSError *)error {

    NSDictionary *data = @{
            @"error": @{
                    @"user" : @(error.code),
                    @"message" : error.description
            }
    };

    [self fireEvent:EVENT_OFFERWALL_CREDIT_FAILED withData: data];
}
@end
