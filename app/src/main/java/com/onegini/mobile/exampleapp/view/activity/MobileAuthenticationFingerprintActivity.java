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

import static com.onegini.mobile.exampleapp.Constants.COMMAND_ASK_TO_ACCEPT_OR_DENY;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.view.handler.MobileAuthenticationFingerprintRequestHandler;

public class MobileAuthenticationFingerprintActivity extends FingerprintActivity implements View.OnClickListener {

  Button fallbackToPinButton;
  Button authAcceptButton;
  Button authDenyButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    fallbackToPinButton = findViewById(R.id.fallback_to_pin_button);
    authAcceptButton = findViewById(R.id.auth_accept_button);
    authDenyButton = findViewById(R.id.auth_deny_button);

    fallbackToPinButton.setOnClickListener(this);
    authAcceptButton.setOnClickListener(this);
    authDenyButton.setOnClickListener(this);
  }

  @Override
  protected void setupUi() {
    if (COMMAND_ASK_TO_ACCEPT_OR_DENY.equals(command)) {
      setFingerprintAuthenticationPermissionVisibility(true);
    } else {
      super.setupUi();
    }
  }

  @Override
  protected void setCancelButtonVisibility() {
    cancelButton.setVisibility(View.GONE);
  }

  public void onFallbackToPinButtonClick() {
    if (MobileAuthenticationFingerprintRequestHandler.CALLBACK != null) {
      MobileAuthenticationFingerprintRequestHandler.CALLBACK.fallbackToPin();
    }
  }

  public void onAuthenticationAcceptButtonClick() {
    if (MobileAuthenticationFingerprintRequestHandler.CALLBACK != null) {
      MobileAuthenticationFingerprintRequestHandler.CALLBACK.acceptAuthenticationRequest();
      actionTextView.setText(R.string.scan_fingerprint);
      setFingerprintAuthenticationPermissionVisibility(false);
    }
  }

  public void onDenyAuthenticationButtonClick() {
    if (MobileAuthenticationFingerprintRequestHandler.CALLBACK != null) {
      MobileAuthenticationFingerprintRequestHandler.CALLBACK.denyAuthenticationRequest();
      finish();
    }
  }

  @Override
  protected void cancelRequest() {
    //we don't want to cancel it. We already have accept and deny buttons - third option isn't needed
  }

  @Override
  public void onClick(final View view) {
    if (R.id.fallback_to_pin_button == view.getId()) {
      onFallbackToPinButtonClick();
    } else if (R.id.auth_accept_button == view.getId()) {
      onAuthenticationAcceptButtonClick();
    } else if (R.id.auth_deny_button == view.getId()) {
      onDenyAuthenticationButtonClick();
    }
  }
}
