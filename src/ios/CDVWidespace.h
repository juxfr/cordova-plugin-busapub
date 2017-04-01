/*
 Widespace SDK for Cordova
 www.widespace.com
 */

#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>

@interface CDVWidespace : CDVPlugin

- (void)setWidespaceSettings:(CDVInvokedUrlCommand*)command;
- (void)showInterstitialAd:(CDVInvokedUrlCommand*)command;
- (void)showBannerAd:(CDVInvokedUrlCommand*)command;

@end
