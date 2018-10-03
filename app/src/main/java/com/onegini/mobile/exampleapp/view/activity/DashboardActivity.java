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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.onegini.mobile.exampleapp.OneginiSDK;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.model.User;
import com.onegini.mobile.exampleapp.storage.UserStorage;
import com.onegini.mobile.exampleapp.util.DeregistrationUtil;
import com.onegini.mobile.exampleapp.view.helper.AskForOtpDialog;
import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.android.handlers.OneginiDeregisterUserProfileHandler;
import com.onegini.mobile.sdk.android.handlers.OneginiLogoutHandler;
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithOtpHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeregistrationError;
import com.onegini.mobile.sdk.android.handlers.error.OneginiLogoutError;
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthWithOtpError;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class DashboardActivity extends AppCompatActivity {

  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.dashboard_welcome_text)
  TextView dashboardWelcomeText;

  private UserStorage userStorage;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboard);
    ButterKnife.bind(this);

    userStorage = new UserStorage(this);
    setupUserInterface();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.button_auth_with_otp)
  public void mobileAuthWithOtp() {
    final OneginiMobileAuthWithOtpHandler oneginiMobileAuthWithOtpHandler = new OneginiMobileAuthWithOtpHandler() {
      @Override
      public void onSuccess() {
        showToast("Mobile auth with OTP succeeded");
      }

      @Override
      public void onError(final OneginiMobileAuthWithOtpError oneginiMobileAuthWithOtpError) {
        showToast("Mobile auth with OTP error:" + oneginiMobileAuthWithOtpError.getMessage());
      }
    };

    AskForOtpDialog askForOtpDialog = new AskForOtpDialog(this, oneginiMobileAuthWithOtpHandler);
    askForOtpDialog.show();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.button_logout)
  public void logout() {
    final OneginiClient oneginiClient = OneginiSDK.getOneginiClient(this);
    final UserProfile userProfile = oneginiClient.getUserClient().getAuthenticatedUserProfile();
    OneginiSDK.getOneginiClient(this).getUserClient().logout(
        new OneginiLogoutHandler() {
          @Override
          public void onSuccess() {
            // Go to login screen
            startLoginActivity();
          }

          @Override
          public void onError(final OneginiLogoutError oneginiLogoutError) {
            handleLogoutError(oneginiLogoutError, userProfile);
          }
        }
    );
  }

  private void handleLogoutError(final OneginiLogoutError oneginiLogoutError, final UserProfile userProfile) {
    @OneginiLogoutError.LogoutErrorType final int errorType = oneginiLogoutError.getErrorType();

    if (errorType == OneginiLogoutError.DEVICE_DEREGISTERED) {
      new DeregistrationUtil(this).onDeviceDeregistered();
    } else if (errorType == OneginiLogoutError.USER_DEREGISTERED) {
      new DeregistrationUtil(this).onUserDeregistered(userProfile);
    }

    // other errors don't really require our reaction, but you might consider displaying some message to the user
    showToast("Logout error: " + oneginiLogoutError.getMessage());

    startLoginActivity();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.button_deregister_user)
  public void deregisterUser() {
    final OneginiClient oneginiClient = OneginiSDK.getOneginiClient(this);
    final UserProfile userProfile = oneginiClient.getUserClient().getAuthenticatedUserProfile();
    if (userProfile == null) {
      showToast("userProfile == null");
      return;
    }

    new DeregistrationUtil(this).onUserDeregistered(userProfile);
    oneginiClient.getUserClient().deregisterUser(userProfile, new OneginiDeregisterUserProfileHandler() {
          @Override
          public void onSuccess() {
            onUserDeregistered();
          }

          @Override
          public void onError(final OneginiDeregistrationError oneginiDeregistrationError) {
            onUserDeregistrationError(oneginiDeregistrationError);
          }
        }
    );
  }

  private void onUserDeregistered() {
    showToast("deregisterUserSuccess");

    startLoginActivity();
  }

  private void onUserDeregistrationError(final OneginiDeregistrationError oneginiDeregistrationError) {
    @OneginiDeregistrationError.DeregistrationErrorType final int errorType = oneginiDeregistrationError.getErrorType();
    if (errorType == OneginiDeregistrationError.DEVICE_DEREGISTERED) {
      // Deregistration failed due to missing device credentials. Register app once again.
      new DeregistrationUtil(this).onDeviceDeregistered();
    }

    // other errors don't really require our reaction, but you might consider displaying some message to the user
    showToast("Deregistration error: " + oneginiDeregistrationError.getMessage());

    startLoginActivity();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.button_your_devices)
  public void startDevicesActivity() {
    startActivity(new Intent(this, DevicesListActivity.class));
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.button_settings)
  public void startSettingsActivity() {
    startActivity(new Intent(this, SettingsActivity.class));
  }


  private void showToast(final String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  private void setupUserInterface() {
    setupActionBar();
    setupWelcomeText();
  }

  private void setupWelcomeText() {
    final UserProfile userProfile = OneginiSDK.getOneginiClient(this).getUserClient().getAuthenticatedUserProfile();
    final User user = userStorage.loadUser(userProfile);
    dashboardWelcomeText.setText(getString(R.string.welcome_user_text, user.getName()));
  }

  private void setupActionBar() {
    setSupportActionBar(toolbar);

    final ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setLogo(R.mipmap.ic_launcher);
      actionBar.setDisplayUseLogoEnabled(true);
      actionBar.setDisplayShowTitleEnabled(false);
    }
  }

  private void startLoginActivity() {
    final Intent intent = new Intent(this, LoginActivity.class);
    startActivity(intent);
    finish();
  }
}
