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

import java.util.Set;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.onegini.mobile.exampleapp.OneginiSDK;
import com.onegini.mobile.exampleapp.util.DeregistrationUtil;
import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.android.exception.OneginiInitializationException;
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler;
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthenticationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError;
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthenticationError;
import com.onegini.mobile.sdk.android.model.entity.CustomInfo;
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthWithPushRequest;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class MobileAuthenticationService extends IntentService {

  private static final String TAG = MobileAuthenticationService.class.getSimpleName();

  public static final String EXTRA_TRANSACTION_ID = "transaction_id";
  public static final String EXTRA_PROFILE_ID = "profile_id";
  public static final String EXTRA_MESSAGE = "message";

  public MobileAuthenticationService() {
    super(TAG);
  }

  @Override
  protected void onHandleIntent(@Nullable final Intent intent) {
    final OneginiMobileAuthWithPushRequest request = parseRequest(intent);
    if (request != null) {
      Log.d(TAG, "Mobile authentication request " + request.getTransactionId() + " received");
      try {
        handleMobileAuthenticationRequest(request);
      } catch (final OneginiInitializationException exception) {
        // Onegini SDK hasn't been started yet so we have to do it
        // before handling the mobile authentication request
        setupOneginiSDK(request);
      }
    }
  }

  @Nullable
  private OneginiMobileAuthWithPushRequest parseRequest(final Intent intent) {
    if (intent != null) {
      final Bundle extras = intent.getExtras();
      if (extras != null) {
        final String transactionId = extras.getString(EXTRA_TRANSACTION_ID);
        final String profileId = extras.getString(EXTRA_PROFILE_ID);
        final String message = extras.getString(EXTRA_MESSAGE);
        return new OneginiMobileAuthWithPushRequest(transactionId, message, profileId);
      }
    }
    return null;
  }

  private void setupOneginiSDK(final OneginiMobileAuthWithPushRequest request) {
    final OneginiClient oneginiClient = OneginiSDK.getOneginiClient(this);
    oneginiClient.start(new OneginiInitializationHandler() {
      @Override
      public void onSuccess(final Set<UserProfile> removedUserProfiles) {
        if (removedUserProfiles.isEmpty()) {
          handleMobileAuthenticationRequest(request);
        } else {
          removeUserProfiles(removedUserProfiles, request);
        }
      }

      @Override
      public void onError(final OneginiInitializationError error) {
        @OneginiInitializationError.InitializationErrorType final int errorType = error.getErrorType();
        if (errorType == OneginiInitializationError.DEVICE_DEREGISTERED) {
          new DeregistrationUtil(getApplicationContext()).onDeviceDeregistered();
        }
      }
    });
  }

  private void handleMobileAuthenticationRequest(final OneginiMobileAuthWithPushRequest request) {
    OneginiSDK.getOneginiClient(this).getUserClient().handleMobileAuthWithPushRequest(request, new OneginiMobileAuthenticationHandler() {
      @Override
      public void onSuccess(final CustomInfo customInfo) {
        Log.i(TAG, "The mobile authentication request " + request.getTransactionId() + " has finished with success");
      }

      @Override
      public void onError(final OneginiMobileAuthenticationError oneginiMobileAuthenticationError) {
        Log.e(TAG, "The mobile authentication request  " + request.getTransactionId() +
            " has finished with error: " + oneginiMobileAuthenticationError.getMessage());
        @OneginiMobileAuthenticationError.MobileAuthenticationErrorType final int errorType = oneginiMobileAuthenticationError.getErrorType();
        if (errorType == OneginiMobileAuthenticationError.DEVICE_DEREGISTERED) {
          new DeregistrationUtil(getApplicationContext()).onDeviceDeregistered();
        }
      }
    });
  }


  private void removeUserProfiles(final Set<UserProfile> removedUserProfiles, final OneginiMobileAuthWithPushRequest request) {
    final DeregistrationUtil deregistrationUtil = new DeregistrationUtil(this);
    for (UserProfile removedUserProfile : removedUserProfiles) {
      deregistrationUtil.onUserDeregistered(removedUserProfile);
    }
    handleMobileAuthenticationRequest(request);
  }
}
