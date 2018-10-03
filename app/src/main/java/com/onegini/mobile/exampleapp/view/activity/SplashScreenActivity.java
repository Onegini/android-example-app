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

package com.onegini.mobile.exampleapp.view.activity;

import java.util.Set;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import com.onegini.mobile.exampleapp.OneginiSDK;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.network.fcm.NotificationHelper;
import com.onegini.mobile.exampleapp.storage.UserStorage;
import com.onegini.mobile.exampleapp.util.DeregistrationUtil;
import com.onegini.mobile.exampleapp.view.helper.AlertDialogFragment;
import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiError;
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class SplashScreenActivity extends Activity {

  private UserStorage userStorage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.userStorage = new UserStorage(this);

    setContentView(R.layout.activity_splashscreen);
    setupOneginiSDK();
    cancelAllNotifications();
  }

  private void setupOneginiSDK() {
    final OneginiClient oneginiClient = OneginiSDK.getOneginiClient(this);
    oneginiClient.start(new OneginiInitializationHandler() {
      @Override
      public void onSuccess(final Set<UserProfile> removedUserProfiles) {
        boolean removedProfiles = !removedUserProfiles.isEmpty();
        if (removedProfiles) {
          removeUserProfiles(removedUserProfiles);
        }
        startLoginActivity();
      }

      @Override
      public void onError(final OneginiInitializationError error) {
        handleInitializationErrors(error);
      }
    });
  }

  private void cancelAllNotifications() {
    NotificationHelper.getInstance(this).cancelAllNotifications();
  }

  private void handleInitializationErrors(final OneginiInitializationError error) {
    @OneginiInitializationError.InitializationErrorType int errorType = error.getErrorType();
    switch (errorType) {
      case OneginiInitializationError.NETWORK_CONNECTIVITY_PROBLEM:
        showError("No internet connection.");
        break;
      case OneginiInitializationError.SERVER_NOT_REACHABLE:
        showError("The server is not reachable.");
        break;
      case OneginiInitializationError.OUTDATED_APP:
        showError("Please update this application in order to use it.");
        break;
      case OneginiInitializationError.OUTDATED_OS:
        showError("Please update your Android version to use this application.");
        break;
      case OneginiInitializationError.DEVICE_DEREGISTERED:
        // in this case clear the local storage from the device and all user related data
        onDeviceDeregistered();
        break;
      case OneginiInitializationError.DEVICE_REGISTRATION_ERROR:
      case OneginiInitializationError.GENERAL_ERROR:
      default:
        // Just display the error for other, less relevant errors
        displayError(error);
        break;
    }
  }

  private void onDeviceDeregistered() {
    new DeregistrationUtil(this).onDeviceDeregistered();
    showError(OneginiInitializationError.DEVICE_DEREGISTERED + ": Device deregistered");
  }

  private void displayError(final OneginiError error) {
    final StringBuilder stringBuilder = new StringBuilder(error.getMessage());
    stringBuilder.append(" Check logcat for more details.");

    error.printStackTrace();

    showError(stringBuilder.toString());
  }

  private void removeUserProfiles(final Set<UserProfile> removedUserProfiles) {
    for (final UserProfile userProfile : removedUserProfiles) {
      userStorage.removeUser(userProfile);
    }
  }

  private void startLoginActivity() {
    startActivity(new Intent(this, LoginActivity.class));
    finish();
  }

  private void showError(final String message) {
    final DialogFragment dialog = AlertDialogFragment.newInstance(message);
    dialog.show(getFragmentManager(), "alert_dialog");
  }
}
