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

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.onegini.mobile.exampleapp.OneginiSDK;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.storage.FCMStorage;
import com.onegini.mobile.sdk.android.client.UserClient;
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithPushEnrollmentHandler;
import com.onegini.mobile.sdk.android.handlers.OneginiRefreshMobileAuthPushTokenHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiRefreshMobileAuthPushTokenError;

public class FCMRegistrationService {

  private static final String TAG = "FCMRegistrationService";

  private final Context context;
  private final FCMStorage storage;

  public FCMRegistrationService(final Context context) {
    this.context = context;
    storage = new FCMStorage(context);
  }

  public void enrollForPush(final OneginiMobileAuthWithPushEnrollmentHandler enrollmentHandler) {
    FirebaseApp.initializeApp(context);
    final String fcmRefreshToken = FirebaseInstanceId.getInstance().getToken();
    if (fcmRefreshToken == null) {
      Toast.makeText(context, context.getString(R.string.push_token_is_null_error_message), Toast.LENGTH_LONG).show();
    } else {
      final UserClient userClient = OneginiSDK.getOneginiClient(context).getUserClient();
      userClient.enrollUserForMobileAuthWithPush(fcmRefreshToken, enrollmentHandler);
    }
  }

  public boolean shouldUpdateRefreshToken(final String refreshToken) {
    final String previousRefreshToken = getStoredRegistrationId();
    if (previousRefreshToken.isEmpty()) {
      return false;
    }
    return !previousRefreshToken.equals(refreshToken);
  }

  public void storeNewRefreshToken(final String newRefreshToken) {
    storage.setRegistrationId(newRefreshToken);
    storage.save();
  }

  public void updateRefreshToken(final String newRefreshToken, final OneginiRefreshMobileAuthPushTokenHandler originalHandler) {
    final OneginiRefreshMobileAuthPushTokenHandler handler = new OneginiRefreshMobileAuthPushTokenHandler() {
      @Override
      public void onSuccess() {
        storeNewRefreshToken(newRefreshToken);
        originalHandler.onSuccess();
      }

      @Override
      public void onError(final OneginiRefreshMobileAuthPushTokenError error) {
        storeNewRefreshToken("");
        originalHandler.onError(error);
      }
    };

    OneginiSDK.getOneginiClient(context).getDeviceClient().refreshMobileAuthPushToken(newRefreshToken, handler);
  }

  /**
   * Gets the stored registration ID for application on FCM service. If result is empty, the app needs to register.
   *
   * @return registration ID, or empty string if there is no existing registration ID.
   */
  private String getStoredRegistrationId() {
    final String registrationId = storage.getRegistrationId();
    if (registrationId == null || registrationId.isEmpty()) {
      Log.i(TAG, "Registration not found.");
      return "";
    }
    return registrationId;
  }
}
