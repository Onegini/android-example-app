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

import static com.onegini.mobile.exampleapp.view.activity.RegistrationActivity.IDENTITY_PROVIDER_EXTRA;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import com.onegini.mobile.exampleapp.OneginiSDK;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.model.User;
import com.onegini.mobile.exampleapp.storage.UserStorage;
import com.onegini.mobile.exampleapp.util.DeregistrationUtil;
import com.onegini.mobile.exampleapp.view.helper.AlertDialogFragment;
import com.onegini.mobile.exampleapp.view.helper.AvailableIdentityProvidersMenu;
import com.onegini.mobile.exampleapp.view.helper.RegisteredAuthenticatorsMenu;
import com.onegini.mobile.sdk.android.client.UserClient;
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticationHandler;
import com.onegini.mobile.sdk.android.handlers.OneginiPendingMobileAuthWithPushRequestsHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticationError;
import com.onegini.mobile.sdk.android.handlers.error.OneginiPendingMobileAuthWithPushRequestError;
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator;
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider;
import com.onegini.mobile.sdk.android.model.entity.CustomInfo;
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthWithPushRequest;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class LoginActivity extends Activity {

  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.label)
  TextView label;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.users_spinner)
  Spinner usersSpinner;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.login_button)
  Button loginButton;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.register_button)
  Button registerButton;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.progress_bar_login)
  ProgressBar progressBar;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.layout_login_content)
  RelativeLayout layoutLoginContent;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.login_with_preferred_authenticator)
  SwitchCompat usePreferredAuthenticatorSwitchCompat;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.register_with_preferred_identity_provider)
  SwitchCompat usePreferredIdentityProviderSwitchCompat;
  @BindView(R.id.bottom_navigation)
  BottomNavigationView bottomNavigationView;

  public static final String ERROR_MESSAGE_EXTRA = "error_message";

  public static User selectedUser;

  private List<User> listOfUsers = new ArrayList<>();
  private boolean userIsLoggingIn = false;
  private String lastErrorMessage;
  private boolean isAppVisible = false;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    isAppVisible = true;
    getErrorMessage();
    setupUserInterface();
    bottomNavigationView.getMenu()
        .findItem(R.id.action_home)
        .setChecked(true);
    showError();
  }

  @Override
  protected void onPause() {
    super.onPause();
    isAppVisible = false;
    overridePendingTransition(0, 0);
  }

  private void getErrorMessage() {
    if (isNoErrorMessagePending() && getIntent().hasExtra(ERROR_MESSAGE_EXTRA)) {
      lastErrorMessage = getIntent().getStringExtra(ERROR_MESSAGE_EXTRA);
    }
  }

  @SuppressWarnings("unused")
  @OnCheckedChanged(R.id.login_with_preferred_authenticator)
  public void usePreferredAuthenticatorSwitchStateChanged() {
    if (usePreferredAuthenticatorSwitchCompat.isChecked()) {
      loginButton.setText(getString(R.string.btn_login_label));
    } else {
      loginButton.setText(getString(R.string.btn_login_with_label));
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.login_button)
  public void loginButtonClicked() {
    if (usePreferredAuthenticatorSwitchCompat.isChecked()) {
      authenticate(selectedUser.getUserProfile(), null);
    } else {
      showRegisteredAuthenticatorsPopup(selectedUser.getUserProfile());
    }
  }

  @SuppressWarnings("unused")
  @OnCheckedChanged(R.id.register_with_preferred_identity_provider)
  public void setUsePreferredIdentityProviderSwitchStateChanged() {
    if (usePreferredIdentityProviderSwitchCompat.isChecked()) {
      registerButton.setText(getString(R.string.btn_register_label));
    } else {
      registerButton.setText(getString(R.string.btn_register_with_label));
    }
  }

  private void showRegisteredAuthenticatorsPopup(@NonNull final UserProfile userProfile) {
    final Set<OneginiAuthenticator> registeredAuthenticators = OneginiSDK.getOneginiClient(this).getUserClient().getRegisteredAuthenticators(userProfile);
    final RegisteredAuthenticatorsMenu menu = new RegisteredAuthenticatorsMenu(new PopupMenu(this, loginButton), registeredAuthenticators);
    menu.setOnClickListener(authenticator -> authenticate(userProfile, authenticator)).show();
  }

  private void showAvailableIdentityProvidersPopup() {
    final Set<OneginiIdentityProvider> identityProviders = OneginiSDK.getOneginiClient(this).getUserClient().getIdentityProviders();
    final AvailableIdentityProvidersMenu menu = new AvailableIdentityProvidersMenu(new PopupMenu(this, registerButton), identityProviders);
    menu.setOnClickListener(identityProvider -> registerUser(identityProvider)).show();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.register_button)
  public void registerButtonClicked() {
    if(usePreferredIdentityProviderSwitchCompat.isChecked()) {
      registerUser(null);
    } else {
      showAvailableIdentityProvidersPopup();
    }
  }

  private void registerUser(final OneginiIdentityProvider identityProvider) {
    final Intent intent = new Intent(this, RegistrationActivity.class);
    if (identityProvider != null) {
      intent.putExtra(IDENTITY_PROVIDER_EXTRA, identityProvider);
    }
    startActivity(intent);
    finish();
  }

  @SuppressWarnings("unused")
  @OnItemSelected(R.id.users_spinner)
  void onItemSelected(int position) {
    selectedUser = listOfUsers.get(position);
  }

  private void setupUserInterface() {
    if (userIsLoggingIn) {
      registerButton.setVisibility(View.INVISIBLE);
    } else {
      setProgressbarVisibility(false);
      registerButton.setVisibility(View.VISIBLE);
    }

    if (isRegisteredAtLeastOneUser()) {
      prepareListOfProfiles();
      setupUsersSpinner();
      loginButton.setVisibility(View.VISIBLE);
      usePreferredAuthenticatorSwitchCompat.setVisibility(View.VISIBLE);
    } else {
      usersSpinner.setVisibility(View.INVISIBLE);
      loginButton.setVisibility(View.INVISIBLE);
      usePreferredAuthenticatorSwitchCompat.setVisibility(View.INVISIBLE);
    }
    setupNavigationDrawer();
  }

  private void setupUsersSpinner() {
    usersSpinner.setVisibility(View.VISIBLE);

    final ArrayAdapter<User> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listOfUsers);
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    usersSpinner.setAdapter(spinnerArrayAdapter);
  }

  private void authenticate(final UserProfile userProfile, @Nullable final OneginiAuthenticator authenticator) {
    setProgressbarVisibility(true);
    userIsLoggingIn = true;

    final UserClient userClient = OneginiSDK.getOneginiClient(this).getUserClient();
    final OneginiAuthenticationHandler handler = getAuthenticationHandler(userProfile);

    if (authenticator == null) {
      userClient.authenticateUser(userProfile, handler);
    } else {
      userClient.authenticateUser(userProfile, authenticator, handler);
    }
  }

  private OneginiAuthenticationHandler getAuthenticationHandler(final UserProfile userProfile) {
    return new OneginiAuthenticationHandler() {

      @Override
      public void onSuccess(final UserProfile userProfile, final CustomInfo customInfo) {
        startDashboardActivity();
      }

      @Override
      public void onError(final OneginiAuthenticationError oneginiAuthenticationError) {
        if (isNoErrorMessagePending()) {
          LoginActivity.this.onError(oneginiAuthenticationError, userProfile);
        }
      }
    };
  }

  private void onError(final OneginiAuthenticationError oneginiAuthenticationError, final UserProfile userProfile) {
    userIsLoggingIn = false;
    setProgressbarVisibility(true);
    handleAuthenticationErrors(oneginiAuthenticationError, userProfile);
  }

  private void handleAuthenticationErrors(final OneginiAuthenticationError error, final UserProfile userProfile) {
    @OneginiAuthenticationError.AuthenticationErrorType int errorType = error.getErrorType();
    final StringBuilder stringBuilder = new StringBuilder()
        .append(errorType)
        .append(": ");
    switch (errorType) {
      case OneginiAuthenticationError.ACTION_CANCELED:
        stringBuilder.append("Authentication was cancelled.");
        break;
      case OneginiAuthenticationError.NETWORK_CONNECTIVITY_PROBLEM:
        stringBuilder.append("No internet connection.");
        break;
      case OneginiAuthenticationError.SERVER_NOT_REACHABLE:
        stringBuilder.append("The server is not reachable.");
        break;
      case OneginiAuthenticationError.OUTDATED_APP:
        stringBuilder.append("Please update this application in order to use it.");
        break;
      case OneginiAuthenticationError.OUTDATED_OS:
        stringBuilder.append("Please update your Android version to use this application.");
        break;
      case OneginiAuthenticationError.USER_DEREGISTERED:
        stringBuilder.append("User deregistered.");
        new DeregistrationUtil(this).onUserDeregistered(userProfile);
        break;
      case OneginiAuthenticationError.DEVICE_DEREGISTERED:
        stringBuilder.append("Device deregistered.");
        new DeregistrationUtil(this).onDeviceDeregistered();
        break;
      case OneginiAuthenticationError.GENERAL_ERROR:
      default:
        // Just display the error for other, less relevant errors
        error.printStackTrace();
        stringBuilder.append(error.getMessage())
            .append(" ")
            .append("Check logcat for more details.");
        break;
    }
    lastErrorMessage = stringBuilder.toString();
    if (isAppVisible) {
      showError();
    }
  }

  private boolean isRegisteredAtLeastOneUser() {
    final Set<UserProfile> userProfiles = OneginiSDK.getOneginiClient(this).getUserClient().getUserProfiles();
    return userProfiles.size() > 0;
  }

  private void setProgressbarVisibility(final boolean isVisible) {
    progressBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);

    registerButton.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    layoutLoginContent.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    if (isRegisteredAtLeastOneUser()) {
      loginButton.setVisibility(isVisible ? View.GONE : View.VISIBLE);
      usePreferredAuthenticatorSwitchCompat.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }
  }

  private void prepareListOfProfiles() {
    final Set<UserProfile> userProfiles = OneginiSDK.getOneginiClient(this).getUserClient().getUserProfiles();
    final UserStorage userStorage = new UserStorage(this);
    listOfUsers = userStorage.loadUsers(userProfiles);
  }

  private void showError() {
    if (isErrorMessagePending()) {
      final DialogFragment dialog = AlertDialogFragment.newInstance(lastErrorMessage);
      lastErrorMessage = null;
      dialog.show(getFragmentManager(), "alert_dialog");
    }
  }

  private void startDashboardActivity() {
    startActivity(new Intent(this, DashboardActivity.class));
    finish();
  }

  private void setupNavigationDrawer() {
    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
      final int itemId = item.getItemId();
      if (itemId == R.id.action_notifications) {
        startActivity(new Intent(LoginActivity.this, PendingPushMessagesActivity.class));
      } else if (itemId == R.id.action_info) {
        startActivity(new Intent(LoginActivity.this, InfoActivity.class));
      }
      return true;
    });

    OneginiSDK.getOneginiClient(this).getUserClient().getPendingMobileAuthWithPushRequests(new OneginiPendingMobileAuthWithPushRequestsHandler() {
      @Override
      public void onSuccess(final Set<OneginiMobileAuthWithPushRequest> set) {
        final MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.action_notifications);
        if (set.isEmpty()) {
          menuItem.setIcon(R.drawable.ic_notifications_white_24dp);
          menuItem.setTitle(getString(R.string.no_notifications));
        } else {
          menuItem.setIcon(R.drawable.ic_notifications_active_white_24dp);
          menuItem.setTitle(getString(R.string.multiple_notifications, set.size()));
        }
      }

      @Override
      public void onError(final OneginiPendingMobileAuthWithPushRequestError error) {
        if (isNoErrorMessagePending()) {
          LoginActivity.this.handlePendingMobileRequestsErrors(error);
        }
      }
    });
  }

  private void handlePendingMobileRequestsErrors(final OneginiPendingMobileAuthWithPushRequestError error) {
    @OneginiPendingMobileAuthWithPushRequestError.OneginiFetchPendingMobileAuthMessagesErrorType int errorType = error.getErrorType();
    final StringBuilder stringBuilder = new StringBuilder()
        .append(errorType)
        .append(": ");
    switch (errorType) {
      case OneginiPendingMobileAuthWithPushRequestError.NETWORK_CONNECTIVITY_PROBLEM:
        stringBuilder.append("No internet connection.");
        break;
      case OneginiPendingMobileAuthWithPushRequestError.SERVER_NOT_REACHABLE:
        stringBuilder.append("The server is not reachable.");
        break;
      case OneginiPendingMobileAuthWithPushRequestError.DEVICE_DEREGISTERED:
        stringBuilder.append("Device deregistered");
        new DeregistrationUtil(this).onDeviceDeregistered();
        break;
      case OneginiPendingMobileAuthWithPushRequestError.GENERAL_ERROR:
      case OneginiPendingMobileAuthWithPushRequestError.CONFIGURATION_ERROR:
      default:
        // Just display the error for other, less relevant errors
        error.printStackTrace();
        stringBuilder.append(error.getMessage())
            .append(" ")
            .append("Check logcat for more details.");
        break;
    }
    lastErrorMessage = stringBuilder.toString();
    if (isAppVisible) {
      showError();
    }
  }

  private boolean isNoErrorMessagePending() {
    return lastErrorMessage == null || lastErrorMessage.isEmpty();
  }

  private boolean isErrorMessagePending() {
    return !isNoErrorMessagePending();
  }
}
