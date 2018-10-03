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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.onegini.mobile.exampleapp.OneginiSDK;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.adapter.PendingPushMessagesAdapter;
import com.onegini.mobile.exampleapp.view.helper.ErrorMessageParser;
import com.onegini.mobile.sdk.android.handlers.OneginiPendingMobileAuthWithPushRequestsHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiPendingMobileAuthWithPushRequestError;
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthWithPushRequest;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class PendingPushMessagesActivity extends AppCompatActivity {

  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.pending_notifications_error)
  TextView errorTextView;
  @BindView(R.id.bottom_navigation)
  BottomNavigationView bottomNavigationView;
  @BindView(R.id.notifications_refresh_layout)
  SwipeRefreshLayout swipeRefreshLayout;

  private PendingPushMessagesAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pending_notifications);
    ButterKnife.bind(this);
    setupUi();
  }

  @Override
  protected void onResume() {
    super.onResume();
    bottomNavigationView.getMenu()
        .findItem(R.id.action_notifications)
        .setChecked(true);
    fetchPendingTransactions();
  }

  @Override
  protected void onPause() {
    super.onPause();
    overridePendingTransition(0, 0);
  }

  private void setupUi() {
    setupActionBar();
    setupListView();
    setupNavigationBar();
  }

  private void setupListView() {
    adapter = new PendingPushMessagesAdapter(this);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    swipeRefreshLayout.setOnRefreshListener(this::fetchPendingTransactions);
  }

  private void setupNavigationBar() {
    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
      final int itemId = item.getItemId();
      if (itemId == R.id.action_info) {
        onInfoClicked();
      } else if (itemId == R.id.action_notifications) {
        onNotificationsClicked();
      } else {
        onHomeClicked();
      }
      return true;
    });
  }

  private void fetchPendingTransactions() {
    swipeRefreshLayout.setRefreshing(true);
    recyclerView.setVisibility(View.GONE);
    errorTextView.setVisibility(View.GONE);
    OneginiSDK.getOneginiClient(this).getUserClient().getPendingMobileAuthWithPushRequests(new OneginiPendingMobileAuthWithPushRequestsHandler() {
      @Override
      public void onSuccess(final Set<OneginiMobileAuthWithPushRequest> set) {
        swipeRefreshLayout.setRefreshing(false);
        displayFetchedMessages(set);
      }

      @Override
      public void onError(final OneginiPendingMobileAuthWithPushRequestError oneginiPendingMobileAuthWithPushRequestError) {
        swipeRefreshLayout.setRefreshing(false);
        showError(ErrorMessageParser.parseErrorMessage(oneginiPendingMobileAuthWithPushRequestError));
      }
    });
  }

  private void displayFetchedMessages(final Set<OneginiMobileAuthWithPushRequest> set) {
    updateNotificationsButton(set.size());
    if (set.isEmpty()) {
      showError(getString(R.string.no_notifications));
    } else {
      recyclerView.setVisibility(View.VISIBLE);
      errorTextView.setVisibility(View.GONE);
      adapter.update(set);
    }
  }

  private void setupActionBar() {
    setSupportActionBar(toolbar);

    final ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setLogo(R.mipmap.ic_launcher);
      actionBar.setDisplayUseLogoEnabled(true);
      actionBar.setDisplayShowTitleEnabled(false);
    }
  }

  private void onHomeClicked() {
    final UserProfile authenticatedUserProfile = OneginiSDK.getOneginiClient(this).getUserClient().getAuthenticatedUserProfile();
    if (authenticatedUserProfile == null) {
      startActivity(new Intent(this, LoginActivity.class));
    } else {
      startActivity(new Intent(this, DashboardActivity.class));
    }
    finish();
  }

  private void onNotificationsClicked() {
    fetchPendingTransactions();
  }

  private void onInfoClicked() {
    startActivity(new Intent(this, InfoActivity.class));
    finish();
  }

  private void showError(final String text) {
    errorTextView.setText(text);
    errorTextView.setVisibility(View.VISIBLE);
    recyclerView.setVisibility(View.GONE);
  }

  private void updateNotificationsButton(final int notificationsCount) {
    final MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.action_notifications);
    if (notificationsCount == 0) {
      menuItem.setIcon(R.drawable.ic_notifications_white_24dp);
      menuItem.setTitle(getString(R.string.no_notifications));
    } else {
      menuItem.setIcon(R.drawable.ic_notifications_active_white_24dp);
      menuItem.setTitle(getString(R.string.multiple_notifications, notificationsCount));
    }
  }
}
