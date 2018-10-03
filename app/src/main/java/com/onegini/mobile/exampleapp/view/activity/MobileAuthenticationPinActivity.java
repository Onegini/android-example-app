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

import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.OnClick;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.view.handler.MobileAuthenticationPinRequestHandler;

public class MobileAuthenticationPinActivity extends PinActivity {

  @BindView(R.id.auth_deny_button)
  Button denyButton;

  @Override
  protected void initialize() {
    super.initialize();
    initDenyButton();
  }

  @Override
  protected void setCancelButtonVisibility() {
    cancelButton.setVisibility(View.GONE);
  }

  @Override
  protected void updateErrorText() {
    if (failedAttemptsCount > 0) {
      final int remainingFailedAttempts = maxFailedAttempts - failedAttemptsCount;

      errorTextView.setText(getString(R.string.pin_error_invalid_pin, remainingFailedAttempts));
      errorTextView.setVisibility(View.VISIBLE);
    } else {
      errorTextView.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  protected void initPinListener() {
    pinProvidedListener = pin -> {
      errorTextView.setVisibility(View.INVISIBLE);
      MobileAuthenticationPinRequestHandler.CALLBACK.acceptAuthenticationRequest(pin);
    };
  }

  private void initDenyButton() {
    denyButton.setVisibility(View.VISIBLE);
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.auth_deny_button)
  public void onDenyClicked() {
    if (MobileAuthenticationPinRequestHandler.CALLBACK != null) {
      MobileAuthenticationPinRequestHandler.CALLBACK.denyAuthenticationRequest();
    }
  }

  @Override
  protected void cancelRequest() {
    //we don't want to cancel it. We already have accept and deny buttons - third option isn't needed
  }
}