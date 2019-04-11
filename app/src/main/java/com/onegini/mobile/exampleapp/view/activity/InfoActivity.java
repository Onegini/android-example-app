package com.onegini.mobile.exampleapp.view.activity;

import static com.onegini.mobile.exampleapp.view.helper.ErrorMessageParser.parseErrorMessage;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.onegini.mobile.exampleapp.OneginiSDK;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.model.ApplicationDetails;
import com.onegini.mobile.exampleapp.model.ImplicitUserDetails;
import com.onegini.mobile.exampleapp.model.User;
import com.onegini.mobile.exampleapp.network.AnonymousService;
import com.onegini.mobile.exampleapp.network.ImplicitUserService;
import com.onegini.mobile.exampleapp.util.DeregistrationUtil;
import com.onegini.mobile.exampleapp.view.helper.AlertDialogFragment;
import com.onegini.mobile.sdk.android.handlers.OneginiDeviceAuthenticationHandler;
import com.onegini.mobile.sdk.android.handlers.OneginiImplicitAuthenticationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeviceAuthenticationError;
import com.onegini.mobile.sdk.android.handlers.error.OneginiError;
import com.onegini.mobile.sdk.android.handlers.error.OneginiImplicitTokenRequestError;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import io.reactivex.disposables.CompositeDisposable;

public class InfoActivity extends AppCompatActivity {

  Toolbar toolbar;
  TextView userProfileId;
  TextView userProfileName;
  TextView implicitDetails;
  RelativeLayout deviceDetails;
  TextView applicationId;
  TextView applicationVersion;
  TextView platform;
  TextView deviceDetailsFetchFailed;
  BottomNavigationView bottomNavigationView;


  private CompositeDisposable disposables = new CompositeDisposable();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_info);
    initUI();
    setupUi();
  }

  private void initUI() {
    toolbar = findViewById(R.id.toolbar);
    userProfileId = findViewById(R.id.user_profile_id);
    userProfileName = findViewById(R.id.user_profile_name);
    implicitDetails = findViewById(R.id.implicit_details);
    deviceDetails = findViewById(R.id.device_details);
    applicationId = findViewById(R.id.application_id);
    applicationVersion = findViewById(R.id.application_version);
    platform = findViewById(R.id.platform);
    deviceDetailsFetchFailed = findViewById(R.id.device_details_fetch_failed);
    bottomNavigationView = findViewById(R.id.bottom_navigation);

  }

  @Override
  protected void onPause() {
    super.onPause();
    overridePendingTransition(0, 0);
  }

  @Override
  protected void onResume() {
    super.onResume();
    bottomNavigationView.getMenu()
        .findItem(R.id.action_info)
        .setChecked(true);
    authenticateDevice();
  }

  @Override
  public void onDestroy() {
    disposables.clear();
    super.onDestroy();
  }

  private void setupUi() {
    setupActionBar();
    setupNavigationBar();
    setupUserDetails();
  }

  private void setupUserDetails() {
    final User user = LoginActivity.selectedUser;
    if (user == null) {
      return;
    }

    userProfileId.setText(user.getUserProfile().getProfileId());
    userProfileName.setText(user.getName());
    getImplicitUserDetails(user.getUserProfile());
  }

  private void showError(final String errorMessage) {
    final DialogFragment dialog = AlertDialogFragment.newInstance(errorMessage);
    dialog.show(getFragmentManager(), "alert_dialog");
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

  private void setupNavigationBar() {
    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
      final int itemId = item.getItemId();
      if (itemId == R.id.action_home) {
        onHomeClicked();
      } else if (itemId == R.id.action_notifications) {
        onNotificationsClicked();
      }
      return true;
    });
  }

  private void onNotificationsClicked() {
    startActivity(new Intent(this, PendingPushMessagesActivity.class));
    finish();
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

  private void authenticateDevice() {
    OneginiSDK.getOneginiClient(this).getDeviceClient().authenticateDevice(new String[]{ "application-details" }, new OneginiDeviceAuthenticationHandler() {
          @Override
          public void onSuccess() {
            callAnonymousResourceCallToFetchApplicationDetails();
          }

          @Override
          public void onError(final OneginiDeviceAuthenticationError error) {
            onApplicationDetailsFetchFailed();
            final @OneginiDeviceAuthenticationError.DeviceAuthenticationErrorType int errorType = error.getErrorType();

            if (errorType == OneginiDeviceAuthenticationError.DEVICE_DEREGISTERED) {
              new DeregistrationUtil(InfoActivity.this).onDeviceDeregistered();
            }
            InfoActivity.this.onError(error);
          }
        }
    );
  }

  private void callAnonymousResourceCallToFetchApplicationDetails() {
    disposables.add(
        AnonymousService.getInstance(this)
            .getApplicationDetails()
            .subscribe(this::onApplicationDetailsFetched, throwable -> onApplicationDetailsFetchFailed())
    );
  }

  private void onApplicationDetailsFetched(final ApplicationDetails details) {
    deviceDetails.setVisibility(View.VISIBLE);
    deviceDetailsFetchFailed.setVisibility(View.GONE);
    applicationId.setText(details.getApplicationIdentifier());
    applicationVersion.setText(details.getApplicationVersion());
    platform.setText(details.getApplicationPlatform());
  }

  private void onApplicationDetailsFetchFailed() {
    deviceDetails.setVisibility(View.GONE);
    deviceDetailsFetchFailed.setVisibility(View.VISIBLE);
  }

  private void onError(final OneginiError error) {
    error.printStackTrace();
    showError(parseErrorMessage(error) + " Check logcat for more details.");
  }

  private void getImplicitUserDetails(final UserProfile userProfile) {
    OneginiSDK.getOneginiClient(this).getUserClient()
        .authenticateUserImplicitly(userProfile, new String[]{ "read" }, new OneginiImplicitAuthenticationHandler() {
          @Override
          public void onSuccess(final UserProfile profile) {
            callImplicitResourceCallToFetchImplicitUserDetails();
          }

          @Override
          public void onError(final OneginiImplicitTokenRequestError error) {
            onImplicitDetailsFetchFailed(error);
            @OneginiImplicitTokenRequestError.ImplicitTokenRequestErrorType int errorType = error.getErrorType();
            if (errorType == OneginiImplicitTokenRequestError.DEVICE_DEREGISTERED) {
              new DeregistrationUtil(InfoActivity.this).onDeviceDeregistered();
            } else if (errorType == OneginiImplicitTokenRequestError.USER_DEREGISTERED) {
              new DeregistrationUtil(InfoActivity.this).onUserDeregistered(userProfile);
            }
          }
        });
  }

  private void callImplicitResourceCallToFetchImplicitUserDetails() {
    disposables.add(
        ImplicitUserService.getInstance(this)
            .getImplicitUserDetails()
            .subscribe(this::onImplicitUserDetailsFetched, this::onImplicitDetailsFetchFailed)
    );
  }

  private void onImplicitUserDetailsFetched(final ImplicitUserDetails implicitUserDetails) {
    implicitDetails.setText(implicitUserDetails.toString());
  }

  private void onImplicitDetailsFetchFailed(final Throwable throwable) {
    implicitDetails.setText(R.string.implicit_user_details_fetch_failed_label);
    throwable.printStackTrace();
  }
}
