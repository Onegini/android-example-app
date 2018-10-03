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

import android.content.Intent;
import android.support.annotation.Nullable;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.onegini.mobile.exampleapp.view.helper.AppLifecycleListener;
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthWithPushRequest;

public class FCMListenerService extends FirebaseMessagingService {

  private final Gson gson = new Gson();

  @Override
  public void onMessageReceived(final RemoteMessage message) {
    final OneginiMobileAuthWithPushRequest mobileAuthWithPushRequest = parseOneginiMobileAuthRequest(message);
    if (mobileAuthWithPushRequest != null) {
      final Intent serviceIntent = getServiceIntent(mobileAuthWithPushRequest);
      if (AppLifecycleListener.isAppInForeground()) {
        startService(serviceIntent);
      } else {
        NotificationHelper.getInstance(this).showNotification(serviceIntent, mobileAuthWithPushRequest.getMessage());
      }
    }
  }

  @Nullable
  private OneginiMobileAuthWithPushRequest parseOneginiMobileAuthRequest(final RemoteMessage message) {
    if (message == null || message.getData() == null) {
      return null;
    }

    final String json = message.getData().get("content");
    try {
      return gson.fromJson(json, OneginiMobileAuthWithPushRequest.class);
    } catch (final JsonSyntaxException e) {
      return null;
    }
  }

  private Intent getServiceIntent(final OneginiMobileAuthWithPushRequest mobileAuthWithPushRequest) {
    final Intent intent = new Intent(this, MobileAuthenticationService.class);
    intent.putExtra(MobileAuthenticationService.EXTRA_TRANSACTION_ID, mobileAuthWithPushRequest.getTransactionId());
    intent.putExtra(MobileAuthenticationService.EXTRA_MESSAGE, mobileAuthWithPushRequest.getMessage());
    intent.putExtra(MobileAuthenticationService.EXTRA_PROFILE_ID, mobileAuthWithPushRequest.getUserProfileId());
    return intent;
  }
}
