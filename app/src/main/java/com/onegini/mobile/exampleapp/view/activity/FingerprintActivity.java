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

import static com.onegini.mobile.exampleapp.Constants.COMMAND_RECEIVED_FINGERPRINT;
import static com.onegini.mobile.exampleapp.Constants.COMMAND_SHOW_SCANNING;
import static com.onegini.mobile.exampleapp.Constants.COMMAND_START;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.util.AnimationUtils;
import com.onegini.mobile.exampleapp.view.handler.FingerprintAuthenticationRequestHandler;

public class FingerprintActivity extends AuthenticationActivity {

  @BindView(R.id.action_text)
  TextView actionTextView;
  @BindView(R.id.content_fingerprint)
  LinearLayout layoutFingerprint;
  @BindView(R.id.content_accept_deny)
  LinearLayout layoutAcceptDeny;
  @BindView(R.id.fallback_to_pin_button)
  Button fallbackToPinButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fingerprint);
    ButterKnife.bind(this);

    initialize();
  }

  @Override
  protected void initialize() {
    parseIntent();
    updateTexts();
    setFingerprintAuthenticationPermissionVisibility(false);
    setCancelButtonVisibility();
    setupUi();
  }

  protected void setCancelButtonVisibility() {
    cancelButton.setVisibility(View.VISIBLE);
  }

  protected void setupUi() {
    if (COMMAND_START.equals(command)) {
      actionTextView.setText(R.string.scan_fingerprint);
      FingerprintAuthenticationRequestHandler.CALLBACK.acceptAuthenticationRequest();
    } else if (COMMAND_SHOW_SCANNING.equals(command)) {
      actionTextView.setText(R.string.verifying);
    } else if (COMMAND_RECEIVED_FINGERPRINT.equals(command)) {
      actionTextView.setText(R.string.try_again);
      actionTextView.setAnimation(AnimationUtils.getBlinkAnimation());
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.fallback_to_pin_button)
  public void onFallbackToPinButtonClick() {
    if (FingerprintAuthenticationRequestHandler.CALLBACK != null) {
      FingerprintAuthenticationRequestHandler.CALLBACK.fallbackToPin();
      finish();
    }
  }

  protected void setFingerprintAuthenticationPermissionVisibility(final boolean isVisible) {
    layoutAcceptDeny.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    layoutFingerprint.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    fallbackToPinButton.setVisibility(isVisible ? View.GONE : View.VISIBLE);
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.auth_cancel_button)
  public void onCancelClicked() {
    cancelRequest();
  }

  @Override
  protected void cancelRequest() {
    if(FingerprintAuthenticationRequestHandler.CALLBACK != null) {
      FingerprintAuthenticationRequestHandler.CALLBACK.denyAuthenticationRequest();
      finish();
    }
  }
}
