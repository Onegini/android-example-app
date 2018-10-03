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
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.view.handler.CreatePinRequestHandler;
import com.onegini.mobile.exampleapp.view.handler.PinAuthenticationRequestHandler;
import com.onegini.mobile.exampleapp.view.helper.PinInputFields;
import com.onegini.mobile.exampleapp.view.helper.PinKeyboard;

public class PinActivity extends AuthenticationActivity {

  public static final String EXTRA_FAILED_ATTEMPTS_COUNT = "failed_attempts";
  public static final String EXTRA_MAX_FAILED_ATTEMPTS = "max_failed_attempts";

  protected static final int MAX_DIGITS = 5;

  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.pin_error_message)
  TextView errorTextView;


  protected int failedAttemptsCount;
  protected int maxFailedAttempts;
  protected PinInputFields.PinProvidedListener pinProvidedListener;

  private static boolean isCreatePinFlow = false;

  public static void setIsCreatePinFlow(final boolean isCreatePinFlow) {
    PinActivity.isCreatePinFlow = isCreatePinFlow;
  }

  private final ImageView[] pinInputs = new ImageView[MAX_DIGITS];
  private PinKeyboard pinKeyboard;
  private PinInputFields pinInputFields;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pin);
    ButterKnife.bind(this);

    initialize();
  }

  @Override
  protected void onPause() {
    super.onPause();
    resetView();
  }

  @Override
  protected void initialize() {
    parseIntent();
    initPinInputs();
    initPinListener();
    initLayout();
    initKeyboard();
  }

  @Override
  protected void parseIntent() {
    super.parseIntent();
    final Intent intent = getIntent();
    failedAttemptsCount = intent.getIntExtra(EXTRA_FAILED_ATTEMPTS_COUNT, 0);
    maxFailedAttempts = intent.getIntExtra(EXTRA_MAX_FAILED_ATTEMPTS, 0);
  }

  @Override
  protected void updateTexts() {
    super.updateTexts();
    updateErrorText();
  }

  protected void updateErrorText() {
    final int remainingFailedAttempts = maxFailedAttempts - failedAttemptsCount;

    if (isCreatePinFlow && isNotBlank(errorMessage)) {
      errorTextView.setText(errorMessage);
      errorTextView.setVisibility(View.VISIBLE);
    } else if (!isCreatePinFlow && remainingFailedAttempts > 0) {
      errorTextView.setText(getString(R.string.pin_error_invalid_pin, remainingFailedAttempts));
      errorTextView.setVisibility(View.VISIBLE);
    } else {
      errorTextView.setVisibility(View.INVISIBLE);
    }
  }

  protected void initPinListener() {
    pinProvidedListener = pin -> {
      errorTextView.setVisibility(View.INVISIBLE);
      callHandler(pin);
    };
  }

  private void initLayout() {
    initPinInputs();
    updateTexts();
    setCancelButtonVisibility();
  }

  protected void setCancelButtonVisibility() {
    cancelButton.setVisibility(View.VISIBLE);
  }

  private void initPinInputs() {
    final Resources resources = getResources();
    final String packageName = getPackageName();
    for (int input = 0; input < MAX_DIGITS; input++) {
      final int inputId = resources.getIdentifier("pin_input_" + input, "id", packageName);
      pinInputs[input] = (ImageView) findViewById(inputId);
    }
  }

  private void initKeyboard() {
    pinInputFields = new PinInputFields(pinProvidedListener, pinInputs);
    pinKeyboard = new PinKeyboard(pinInputFields);

    final TableLayout keyboardLayout = (TableLayout) findViewById(R.id.pin_keyboard);
    pinKeyboard.initLayout(keyboardLayout, getResources(), getPackageName());
  }

  private void resetView() {
    pinKeyboard.reset();
    pinInputFields.reset();
    errorTextView.setVisibility(View.INVISIBLE);
  }

  private void callHandler(final char[] pin) {
    if (isCreatePinFlow) {
      CreatePinRequestHandler.CALLBACK.onPinProvided(pin);
    } else {
      PinAuthenticationRequestHandler.CALLBACK.acceptAuthenticationRequest(pin);
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.auth_cancel_button)
  public void onCancelClicked() {
    cancelRequest();
  }

  @Override
  protected void cancelRequest() {
    if (isCreatePinFlow) {
      CreatePinRequestHandler.CALLBACK.pinCancelled();
    } else {
      PinAuthenticationRequestHandler.CALLBACK.denyAuthenticationRequest();
    }
  }
}
