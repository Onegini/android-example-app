/*
 * Copyright (c) 2016-2017 Onegini B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onegini.mobile.exampleapp;

import java.util.concurrent.TimeUnit;

import android.content.Context;
import com.onegini.mobile.exampleapp.model.SimpleCustomAuthenticator;
import com.onegini.mobile.exampleapp.view.handler.CreatePinRequestHandler;
import com.onegini.mobile.exampleapp.view.handler.FidoAuthenticationRequestHandler;
import com.onegini.mobile.exampleapp.view.handler.FingerprintAuthenticationRequestHandler;
import com.onegini.mobile.exampleapp.view.handler.MobileAuthOtpRequestHandler;
import com.onegini.mobile.exampleapp.view.handler.MobileAuthenticationFidoRequestHandler;
import com.onegini.mobile.exampleapp.view.handler.MobileAuthenticationFingerprintRequestHandler;
import com.onegini.mobile.exampleapp.view.handler.MobileAuthenticationPinRequestHandler;
import com.onegini.mobile.exampleapp.view.handler.MobileAuthenticationRequestHandler;
import com.onegini.mobile.exampleapp.view.handler.MobileAuthenticationSimpleCustomRequestHandler;
import com.onegini.mobile.exampleapp.view.handler.PinAuthenticationRequestHandler;
import com.onegini.mobile.exampleapp.view.handler.RegistrationRequestHandler;
import com.onegini.mobile.exampleapp.view.handler.SimpleCustomAuthenticationRequestHandler;
import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.android.client.OneginiClientBuilder;

public class OneginiSDK {

  public static OneginiClient getOneginiClient(final Context context) {
    OneginiClient oneginiClient = OneginiClient.getInstance();
    if (oneginiClient == null) {
      oneginiClient = buildSDK(context);
    }
    return oneginiClient;
  }

  private static OneginiClient buildSDK(final Context context) {
    final Context applicationContext = context.getApplicationContext();
    final RegistrationRequestHandler registrationRequestHandler = new RegistrationRequestHandler(applicationContext);
    final CreatePinRequestHandler createPinRequestHandler = new CreatePinRequestHandler(applicationContext);
    final PinAuthenticationRequestHandler pinAuthenticationRequestHandler = new PinAuthenticationRequestHandler(applicationContext);
    final SimpleCustomAuthenticator simpleCustomAuthenticator = new SimpleCustomAuthenticator(applicationContext);

    // will throw OneginiConfigNotFoundException if OneginiConfigModel class can't be found
    return new OneginiClientBuilder(applicationContext, registrationRequestHandler, createPinRequestHandler, pinAuthenticationRequestHandler)
        // handlers for optional functionalities
        .setFingerprintAuthenticatioRequestHandler(new FingerprintAuthenticationRequestHandler(applicationContext))
        .setFidoAuthenticationRequestHandler(new FidoAuthenticationRequestHandler(applicationContext))
        .setCustomAuthenticationRequestHandler(new SimpleCustomAuthenticationRequestHandler())
        .setMobileAuthWithPushRequestHandler(new MobileAuthenticationRequestHandler(applicationContext))
        .setMobileAuthWithPushPinRequestHandler(new MobileAuthenticationPinRequestHandler(applicationContext))
        .setMobileAuthWithPushFingerprintRequestHandler(new MobileAuthenticationFingerprintRequestHandler(applicationContext))
        .setMobileAuthWithPushFidoRequestHandler(new MobileAuthenticationFidoRequestHandler(applicationContext))
        .setMobileAuthWithPushCustomRequestHandler(new MobileAuthenticationSimpleCustomRequestHandler())
        .setMobileAuthWithOtpRequestHandler(new MobileAuthOtpRequestHandler())
        .addCustomAuthenticator(simpleCustomAuthenticator)
        // Set http connect / read timeout
        .setHttpConnectTimeout((int) TimeUnit.SECONDS.toMillis(5))
        .setHttpReadTimeout((int) TimeUnit.SECONDS.toMillis(20))
        .build();
  }
}
