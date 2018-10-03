/*
 * Copyright (c) 2016-2018 Onegini B.V.
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

package com.onegini.mobile.exampleapp.network.fcm;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.onegini.mobile.exampleapp.util.DeregistrationUtil;
import com.onegini.mobile.sdk.android.handlers.OneginiRefreshMobileAuthPushTokenHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiRefreshMobileAuthPushTokenError;

public class FCMInstanceIdService extends FirebaseInstanceIdService {

  private static final String TAG = FCMInstanceIdService.class.getSimpleName();

  /**
   * Called if InstanceID token is created or updated. This may occur if the security of the previous token had been compromised.
   * This call is initiated by the InstanceID provider.
   */
  @Override
  public void onTokenRefresh() {
    final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    final FCMRegistrationService fcmRegistrationService = new FCMRegistrationService(this);
    if (fcmRegistrationService.shouldUpdateRefreshToken(refreshedToken)) {
      // the token was updated, notify the SDK
      fcmRegistrationService.updateRefreshToken(refreshedToken, new TokenUpdateHandler(refreshedToken));
    } else {
      // the token is created for the first time
      fcmRegistrationService.storeNewRefreshToken(refreshedToken);
    }
  }

  private class TokenUpdateHandler implements OneginiRefreshMobileAuthPushTokenHandler {

    private final String token;

    public TokenUpdateHandler(final String token) {
      this.token = token;
    }

    @Override
    public void onSuccess() {
      Log.d(TAG, "The token has been updated: " + token);
    }

    @Override
    public void onError(final OneginiRefreshMobileAuthPushTokenError error) {
      Log.e(TAG, "The push token update has failed: " + error.getMessage());
      @OneginiRefreshMobileAuthPushTokenError.RefreshMobileAuthPushTokenErrorType final int errorType = error.getErrorType();
      if (errorType == OneginiRefreshMobileAuthPushTokenError.DEVICE_DEREGISTERED) {
        new DeregistrationUtil(FCMInstanceIdService.this).onDeviceDeregistered();
      }
    }
  }
}
