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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.onegini.mobile.exampleapp.R;

public abstract class BasicAuthenticatorActivity extends AppCompatActivity implements View.OnClickListener {

  TextView titleText;
  Button positiveButton;
  Button negativeButton;
  Button errorButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_basic_authenticator);
    initUI();
    setTitle();
  }

  private void initUI() {
    titleText = findViewById(R.id.title_text);
    positiveButton = findViewById(R.id.custom_auth_positive_button);
    negativeButton = findViewById(R.id.custom_auth_negative_button);
    errorButton = findViewById(R.id.custom_auth_error_button);
  }

  public void onPositiveButtonClicked() {
    onSuccess();
    finish();
  }

  public void onNegativeButtonClicked() {
    onFailure();
    finish();
  }

  public void onErrorButtonClicked() {
    onError();
    finish();
  }

  @Override
  public void onClick(final View view) {
    if (R.id.custom_auth_positive_button == view.getId()) {
      onPositiveButtonClicked();
    } else if (R.id.custom_auth_negative_button == view.getId()) {
      onNegativeButtonClicked();
    } else if (R.id.custom_auth_error_button == view.getId()) {
      onErrorButtonClicked();
    }
  }

  @Override
  public void onBackPressed() {
    onNegativeButtonClicked();
  }

  protected abstract void setTitle();

  protected abstract void onSuccess();

  protected abstract void onFailure();

  protected abstract void onError();
}
