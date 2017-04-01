/*
 Widespace SDK for Cordova
 www.widespace.com
 */

#include <sys/types.h>
#include <sys/sysctl.h>

#import <Cordova/CDV.h>
#import "CDVWidespace.h"
#import <WSLibrary/WSAdSpace.h>

@interface CDVWidespace () <WSAdSpaceDelegate>

@property (nonatomic, strong) WSAdSpace *interstitialAdView;
@property (nonatomic, strong) WSAdSpace *bannerAdView;
@property (nonatomic, strong) NSString *interstitialSid;
@property (nonatomic, strong) NSString *bannerSid;
@property (nonatomic, assign) NSInteger displayBannerTop;

@end


@implementation CDVWidespace


#pragma mark - Object lifecycle

- (void)dealloc
{
    _interstitialAdView.delegate = nil;
    [_interstitialAdView removeFromSuperview];
    
    _bannerAdView.delegate = nil;
    [_bannerAdView removeFromSuperview];
}


#pragma mark - Cordova Actions

- (void)setWidespaceSettings:(CDVInvokedUrlCommand *)command
{
    if([command.arguments count])
    {
        NSDictionary *options = [command.arguments firstObject];
        
        self.interstitialSid = options[@"theInterstitialSid"];
        self.bannerSid = options[@"theBannerSid"];
        self.displayBannerTop = [options[@"displayBannerTop"] integerValue];
    }
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)showInterstitialAd:(CDVInvokedUrlCommand*)command
{
    if (_interstitialAdView)
    {
        _interstitialAdView.delegate = nil;
        [_interstitialAdView removeFromSuperview];
        self.interstitialAdView = nil;
    }
    
    self.interstitialAdView = [[WSAdSpace alloc] initWithFrame:CGRectNull sid:_interstitialSid autoStart:NO autoUpdate:NO delegate:self];
    [self.webView.superview addSubview:_interstitialAdView];
    [_interstitialAdView runAd];
}

- (void)showBannerAd:(CDVInvokedUrlCommand*)command
{
    if (_bannerAdView)
    {
        _bannerAdView.delegate = nil;
        [_bannerAdView removeFromSuperview];
        self.bannerAdView = nil;
    }
    
    self.bannerAdView = [[WSAdSpace alloc] initWithFrame:CGRectMake(0, 20, self.webView.frame.size.width, 0) sid:_bannerSid autoStart:NO autoUpdate:NO delegate:self];
    _bannerAdView.shadowEnabled = NO;
    
    [self.webView.superview addSubview:_bannerAdView];
    [_bannerAdView runAd];
}

- (void)closeAd:(CDVInvokedUrlCommand*)command
{
    [_bannerAdView closeAd];
    [_interstitialAdView closeAd];
}


#pragma mark - Internal Actions

- (void)evalJS:(NSString *)scriptString
{
    [self.commandDelegate evalJs:scriptString];
}


#pragma mark - WSAdSpaceDelegate

- (void)willCloseAd:(WSAdSpace *)adSpace adType:(NSString *)adType
{
    [self evalJS:@"cordova.fireDocumentEvent('willCloseAd');"];
}

- (void)didCloseAd:(WSAdSpace *)adSpace adType:(NSString *)adType
{
    [self evalJS:@"cordova.fireDocumentEvent('didCloseAd');"];
}

- (void)willLoadAd:(WSAdSpace *)adSpace
{
    [self evalJS:@"cordova.fireDocumentEvent('willLoadAd');"];
}

- (void)didLoadAd:(WSAdSpace *)adSpace adType:(NSString *)adType
{
    [self evalJS:@"cordova.fireDocumentEvent('didLoadAd');"];
}

- (void)willStartMedia:(WSAdSpace *)adSpace mediaType:(NSString *)mediaType
{
    [self evalJS:@"cordova.fireDocumentEvent('willStartMedia');"];
}

- (void)didStopMedia:(WSAdSpace *)adSpace mediaType:(NSString *)mediaType
{
    [self evalJS:@"cordova.fireDocumentEvent('didStopMedia');"];
}

- (void)didCompleteMedia:(WSAdSpace *)adSpace mediaType:(NSString *)mediaType
{
    [self evalJS:@"cordova.fireDocumentEvent('didCompleteMedia');"];
}

- (void)didReceiveNoAd:(WSAdSpace *)adSpace
{
    [self evalJS:@"cordova.fireDocumentEvent('didReceiveNoAd');"];
}

- (void)didFailWithError:(WSAdSpace *)adSpace type:(NSString *)type message:(NSString *)message error:(NSError *)error
{
    NSLog(@"didFailWithError: %@ Type: %@", message, type);
}

- (void)didExpandAd:(WSAdSpace *)adSpace expandDirection:(NSString *)expandDirection finalWidth:(CGFloat)finalWidth finalHeight:(CGFloat)finalHeight
{
    NSLog(@"didExpandAdToDirection: %@, FinalWidth: %f, FinalHeight: %f",expandDirection,finalWidth,finalHeight);
}

- (void)didCollapseAd:(WSAdSpace *)adSpace collapsedDirection:(NSString *)collapsedDirection finalWidth:(CGFloat)finalWidth finalHeight:(CGFloat)finalHeight
{
    NSLog(@"didCollapseAdFromDirection: %@, FinalWidth: %f, FinalHeight: %f",collapsedDirection,finalWidth,finalHeight);
}

- (void)didPrefetchAd:(WSAdSpace *)adSpace mediaStatus:(NSString *)mediaStatus
{
    [self evalJS:@"cordova.fireDocumentEvent('didPrefetchAd');"];
}

- (void)willAnimateOut:(WSAdSpace *)adSpace
{
    [self evalJS:@"cordova.fireDocumentEvent('willAnimateOut');"];
}

- (void)didAnimateOut:(WSAdSpace *)adSpace
{
    [self evalJS:@"cordova.fireDocumentEvent('didAnimateOut');"];
}

- (void)willAnimateIn:(WSAdSpace *)adSpace
{
    if (_displayBannerTop == 0)
    {
        _bannerAdView.frame = CGRectMake(0, self.webView.superview.frame.size.height/1 - _bannerAdView.frame.size.height/1, _bannerAdView.frame.size.width, _bannerAdView.frame.size.height);
    }
    
    [self evalJS:@"cordova.fireDocumentEvent('willAnimateIn');"];
}

- (void)didAnimateIn:(WSAdSpace *)adSpace
{
    [self evalJS:@"cordova.fireDocumentEvent('didAnimateIn');"];
}

@end
