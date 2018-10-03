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

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.onegini.mobile.exampleapp.view.helper.ErrorMessageParser.parseErrorMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.onegini.mobile.exampleapp.OneginiSDK;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.adapter.AuthenticatorsAdapter;
import com.onegini.mobile.exampleapp.model.AuthenticatorListItem;
import com.onegini.mobile.exampleapp.util.DeregistrationUtil;
import com.onegini.mobile.exampleapp.view.helper.OneginiAuthenticatorComperator;
import com.onegini.mobile.sdk.android.client.UserClient;
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorDeregistrationHandler;
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorRegistrationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorDeregistrationError;
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorRegistrationError;
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator;
import com.onegini.mobile.sdk.android.model.entity.CustomInfo;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class SettingsAuthenticatorsActivity extends AppCompatActivity {

  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.settings_authenticator_selector_text)
  TextView loginMethodTextView;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.authenticators_list)
  RecyclerView authenticatorsRecyclerView;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.result)
  TextView resultTextView;

  private AuthenticatorListItem[] authenticators;
  private AuthenticatorsAdapter authenticatorsAdapter;
  private UserClient userClient;
  private UserProfile authenticatedUserProfile;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings_authenticators);
    ButterKnife.bind(this);
    userClient = OneginiSDK.getOneginiClient(this).getUserClient();
    authenticatedUserProfile = userClient.getAuthenticatedUserProfile();
  }

  @Override
  protected void onResume() {
    super.onResume();
    setupView();
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.settings_authenticator_selector)
  public void onChangePreferredAuthenticatorClick() {
    final Set<OneginiAuthenticator> registeredAuthenticators = userClient.getRegisteredAuthenticators(authenticatedUserProfile);

    final List<String> authenticatorNames = new ArrayList<>(registeredAuthenticators.size());
    for (final OneginiAuthenticator authenticator : registeredAuthenticators) {
      authenticatorNames.add(authenticator.getName());
    }
    final String[] authenticatorsToSelect = new String[authenticatorNames.size()];

    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.pick_authenticator);
    builder.setItems(authenticatorNames.toArray(authenticatorsToSelect), (dialog, which) -> updatePreferredAuthenticator(which, registeredAuthenticators));
    builder.show();
  }

  private void updatePreferredAuthenticator(final int which, final Set<OneginiAuthenticator> registeredAuthenticators) {
    int index = 0;
    for (final OneginiAuthenticator authenticator : registeredAuthenticators) {
      if (which == index) {
        setPreferredAuthenticator(authenticator);
        setPreferredAuthenticatorLabel(authenticator);
        break;
      }
      index++;
    }
  }

  private void setPreferredAuthenticator(final OneginiAuthenticator authenticator) {
    userClient.setPreferredAuthenticator(authenticator);
  }

  private void setupView() {
    setupActionBar();
    prepareAuthenticatorsList();
  }

  private void setupActionBar() {
    setSupportActionBar(toolbar);

    final ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setLogo(R.mipmap.ic_launcher);
      actionBar.setDisplayUseLogoEnabled(true);
      actionBar.setDisplayShowTitleEnabled(false);
    }
  }

  private void prepareAuthenticatorsList() {
    final Set<OneginiAuthenticator> allAuthenticators = userClient.getAllAuthenticators(authenticatedUserProfile);
    final OneginiAuthenticator[] oneginiAuthenticators = sortLists(allAuthenticators);
    authenticators = wrapAuthenticatorsToListItems(oneginiAuthenticators);
    authenticatorsAdapter = new AuthenticatorsAdapter(authenticators, new AuthenticatorClickListener());
    authenticatorsRecyclerView.setAdapter(authenticatorsAdapter);
    authenticatorsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
  }

  private OneginiAuthenticator[] sortLists(final Set<OneginiAuthenticator> allAuthenticators) {
    final List<OneginiAuthenticator> authenticatorList = new ArrayList<>(allAuthenticators);
    Collections.sort(authenticatorList, new OneginiAuthenticatorComperator());
    return authenticatorList.toArray(new OneginiAuthenticator[authenticatorList.size()]);
  }

  private AuthenticatorListItem[] wrapAuthenticatorsToListItems(final OneginiAuthenticator[] oneginiAuthenticators) {
    final AuthenticatorListItem[] authenticators = new AuthenticatorListItem[oneginiAuthenticators.length];
    for (int i = 0; i < oneginiAuthenticators.length; i++) {
      final OneginiAuthenticator currentAuthenticator = oneginiAuthenticators[i];
      if (currentAuthenticator.isPreferred()) {
        setPreferredAuthenticatorLabel(currentAuthenticator);
      }
      authenticators[i] = new AuthenticatorListItem(currentAuthenticator);
    }
    return authenticators;
  }

  private void setPreferredAuthenticatorLabel(final OneginiAuthenticator authenticator) {
    loginMethodTextView.setText(authenticator.getName());
  }

  private void registerAuthenticator(final OneginiAuthenticator authenticator, final int position) {
    userClient.registerAuthenticator(authenticator, new OneginiAuthenticatorRegistrationHandler() {
      @Override
      public void onSuccess(final CustomInfo customInfo) {
        authenticators[position].setIsProcessed(false);
        prepareAuthenticatorsList();
        clearErrorMessage();
        Toast.makeText(SettingsAuthenticatorsActivity.this, "Authenticator registered", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onError(final OneginiAuthenticatorRegistrationError error) {
        @OneginiAuthenticatorRegistrationError.AuthenticatorRegistrationErrorType int errorType = error.getErrorType();
        final String errorMessage = parseErrorMessage(error);
        if (errorType == OneginiAuthenticatorRegistrationError.USER_DEREGISTERED) {
          new DeregistrationUtil(SettingsAuthenticatorsActivity.this).onUserDeregistered(authenticatedUserProfile);
          startLoginActivity(errorMessage);
        } else if (errorType == OneginiAuthenticatorRegistrationError.DEVICE_DEREGISTERED) {
          new DeregistrationUtil(SettingsAuthenticatorsActivity.this).onDeviceDeregistered();
          startLoginActivity(errorMessage);
        }

        onErrorOccurred(position, errorMessage);
      }
    });
  }

  private void deregisterAuthenticator(final OneginiAuthenticator authenticator, final int position) {
    userClient.deregisterAuthenticator(authenticator, new OneginiAuthenticatorDeregistrationHandler() {
      @Override
      public void onSuccess() {
        authenticators[position].setIsProcessed(false);
        if (authenticators[position].getAuthenticator().getName().equals(loginMethodTextView.getText().toString())) {
          setPinAsPreferredAuthenticator();
        }
        prepareAuthenticatorsList();
        clearErrorMessage();
        Toast.makeText(SettingsAuthenticatorsActivity.this, "Authenticator deregistered", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onError(final OneginiAuthenticatorDeregistrationError error) {
        @OneginiAuthenticatorDeregistrationError.AuthenticatorDeregistrationErrorType int errorType = error.getErrorType();
        final String errorMessage = parseErrorMessage(error);
        onErrorOccurred(position, errorMessage);
        if (errorType == OneginiAuthenticatorDeregistrationError.USER_NOT_AUTHENTICATED) {
          startLoginActivity(errorMessage);
        } else if (errorType == OneginiAuthenticatorDeregistrationError.USER_DEREGISTERED) {
          new DeregistrationUtil(SettingsAuthenticatorsActivity.this).onUserDeregistered(authenticatedUserProfile);
          startLoginActivity(errorMessage);
        } else if (errorType == OneginiAuthenticatorDeregistrationError.DEVICE_DEREGISTERED) {
          new DeregistrationUtil(SettingsAuthenticatorsActivity.this).onDeviceDeregistered();
          startLoginActivity(errorMessage);
        }
      }
    });
  }

  private void onErrorOccurred(int position, String errorDescription) {
    authenticators[position].setIsProcessed(false);
    resultTextView.setText(errorDescription);
  }

  private void clearErrorMessage() {
    resultTextView.setText("");
  }

  private void setPinAsPreferredAuthenticator() {
    final Set<OneginiAuthenticator> allAuthenticators = userClient.getAllAuthenticators(authenticatedUserProfile);
    for (final OneginiAuthenticator auth : allAuthenticators) {
      if (auth.getType() == OneginiAuthenticator.PIN) {
        setPreferredAuthenticator(auth);
      }
    }
  }

  public class AuthenticatorClickListener {

    public void onAuthenticatorItemClick(final int position) {
      final AuthenticatorListItem clickedAuthenticatorItem = authenticators[position];
      final OneginiAuthenticator clickedAuthenticator = clickedAuthenticatorItem.getAuthenticator();

      if (clickedAuthenticatorItem.isProcessed() || clickedAuthenticator.getType() == OneginiAuthenticator.PIN) {
        return;
      }
      clickedAuthenticatorItem.setIsProcessed(true);
      authenticatorsAdapter.notifyDataSetChanged();

      if (clickedAuthenticator.isRegistered()) {
        deregisterAuthenticator(clickedAuthenticatorItem.getAuthenticator(), position);
      } else {
        registerAuthenticator(clickedAuthenticatorItem.getAuthenticator(), position);
      }
    }
  }

  private void startLoginActivity(final String errorMessage) {
    final Intent intent = new Intent(this, LoginActivity.class);
    intent.putExtra(LoginActivity.ERROR_MESSAGE_EXTRA, errorMessage);
    intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
  }
}
