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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.onegini.mobile.exampleapp.Constants;
import com.onegini.mobile.exampleapp.OneginiSDK;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.model.User;
import com.onegini.mobile.exampleapp.storage.UserStorage;
import com.onegini.mobile.exampleapp.util.DeregistrationUtil;
import com.onegini.mobile.exampleapp.view.handler.RegistrationRequestHandler;
import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.android.handlers.OneginiRegistrationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticationError;
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError;
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider;
import com.onegini.mobile.sdk.android.model.entity.CustomInfo;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class RegistrationActivity extends Activity {

  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.name_edit_text)
  EditText nameEditText;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.create_profile_button)
  Button createProfileButton;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.cancel_registration_button)
  Button cancelRegistrationButton;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.progress_bar_register)
  ProgressBar progressBar;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.layout_register_content)
  LinearLayout layoutRegisterContent;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.user_profile_debug)
  TextView userProfileDebugText;

  public static final String IDENTITY_PROVIDER_EXTRA = "identity_provider_id";

  private UserProfile registeredProfile;

  final OneginiRegistrationHandler registrationHandler = new OneginiRegistrationHandler() {

    @Override
    public void onSuccess(final UserProfile userProfile, final CustomInfo customInfo) {
      registeredProfile = userProfile;
      userProfileDebugText.setText(userProfile.getProfileId());
      askForProfileName();
    }

    @Override
    public void onError(final OneginiRegistrationError oneginiRegistrationError) {
      handleRegistrationErrors(oneginiRegistrationError);
    }
  };

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_registration);
    ButterKnife.bind(this);

    setupUserInterface();
    final OneginiIdentityProvider oneginiIdentityProvider = getIntent().getParcelableExtra(IDENTITY_PROVIDER_EXTRA);
    registerUser(oneginiIdentityProvider);
  }

  private void setupUserInterface() {
    createProfileButton.setEnabled(false);
    layoutRegisterContent.setVisibility(View.GONE);
    cancelRegistrationButton.setVisibility(View.VISIBLE);
    progressBar.setVisibility(View.VISIBLE);
    userProfileDebugText.setText("");
    nameEditText.addTextChangedListener(new ProfileNameChangeListener());
  }

  @Override
  public void onNewIntent(final Intent intent) {
    super.onNewIntent(intent);
    handleRedirection(intent.getData());
  }

  private void handleRedirection(final Uri uri) {
    if (uri == null) {
      return;
    }

    final OneginiClient client = OneginiSDK.getOneginiClient(getApplicationContext());
    final String redirectUri = client.getConfigModel().getRedirectUri();
    if (redirectUri.startsWith(uri.getScheme())) {
      RegistrationRequestHandler.handleRegistrationCallback(uri);
    }
  }

  private void registerUser(final OneginiIdentityProvider identityProvider) {
    final OneginiClient oneginiClient = OneginiSDK.getOneginiClient(this);
    oneginiClient.getUserClient().registerUser(identityProvider, Constants.DEFAULT_SCOPES, registrationHandler);
  }

  private void handleRegistrationErrors(final OneginiRegistrationError oneginiRegistrationError) {
    @OneginiRegistrationError.RegistrationErrorType final int errorType = oneginiRegistrationError.getErrorType();
    switch (errorType) {
      case OneginiRegistrationError.DEVICE_DEREGISTERED:
        showToast("The device was deregistered, please try registering again");

        new DeregistrationUtil(this).onDeviceDeregistered();
        break;
      case OneginiRegistrationError.ACTION_CANCELED:
        showToast("Registration was cancelled");
        break;
      case OneginiAuthenticationError.NETWORK_CONNECTIVITY_PROBLEM:
        showToast("No internet connection.");
        break;
      case OneginiAuthenticationError.SERVER_NOT_REACHABLE:
        showToast("The server is not reachable.");
        break;
      case OneginiRegistrationError.OUTDATED_APP:
        showToast("Please update this application in order to use it.");
        break;
      case OneginiRegistrationError.OUTDATED_OS:
        showToast("Please update your Android version to use this application.");
        break;
      case OneginiRegistrationError.INVALID_IDENTITY_PROVIDER:
        showToast("The Identity provider you were trying to use is invalid.");
        break;
      case OneginiRegistrationError.CUSTOM_REGISTRATION_EXPIRED:
        showToast("Custom registration request has expired. Please retry.");
        break;
      case OneginiRegistrationError.CUSTOM_REGISTRATION_FAILURE:
        showToast("Custom registration request has failed, see logcat for more details.");
        break;
      case OneginiRegistrationError.GENERAL_ERROR:
      default:
        // General error handling for other, less relevant errors
        handleGeneralError(oneginiRegistrationError);
        break;
    }
    // start login screen again
    startActivity(new Intent(this, LoginActivity.class));
    finish();
  }

  private void handleGeneralError(final OneginiRegistrationError error) {
    final StringBuilder stringBuilder = new StringBuilder("Error: ");
    stringBuilder.append(error.getMessage());
    stringBuilder.append(" Check logcat for more details.");

    error.printStackTrace();

    showToast(stringBuilder.toString());
  }

  private void askForProfileName() {
    progressBar.setVisibility(View.GONE);
    cancelRegistrationButton.setVisibility(View.GONE);
    layoutRegisterContent.setVisibility(View.VISIBLE);
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.create_profile_button)
  public void onCreateProfileClick() {
    storeUserProfile();
    startDashboardActivity();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.cancel_registration_button)
  public void onCancelRegistrationButton() {
    RegistrationRequestHandler.onRegistrationCanceled();
  }

  private void showToast(final String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }

  private void storeUserProfile() {
    final String name = nameEditText.getText().toString().trim();
    final User user = new User(registeredProfile, name);
    final UserStorage userStorage = new UserStorage(this);
    userStorage.saveUser(user);
  }

  private void startDashboardActivity() {
    final Intent intent = new Intent(this, DashboardActivity.class);
    startActivity(intent);
    finish();
  }

  private class ProfileNameChangeListener implements TextWatcher {

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
      //nothing to do here
    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
      //nothing to do here
    }

    @Override
    public void afterTextChanged(final Editable s) {
      final String name = s.toString().trim();
      if (name.isEmpty()) {
        createProfileButton.setEnabled(false);
      } else {
        createProfileButton.setEnabled(true);
      }
    }
  }
}
