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
import android.widget.EditText;
import com.onegini.mobile.exampleapp.R;

public abstract class PasswordAuthenticatorActivity extends AppCompatActivity implements View.OnClickListener {

  EditText passwordEditText;
  Button customAuthPasswordSend;
  Button customAuthPasswordCancel;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_password_authenticator);
    initUI();
  }

  private void initUI() {
   passwordEditText = findViewById(R.id.custom_auth_password);
   customAuthPasswordSend = findViewById(R.id.custom_auth_password_send);
   customAuthPasswordCancel = findViewById(R.id.custom_auth_password_cancel);

   customAuthPasswordSend.setOnClickListener(this);
   customAuthPasswordCancel.setOnClickListener(this);
  }

  public void onPositiveButtonClicked() {
    onSuccess(passwordEditText.getText().toString());
    finish();
  }

  public void onNegativeButtonClicked() {
    onCanceled();
    finish();
  }

  @Override
  public void onClick(final View view) {
    if (R.id.custom_auth_password_send == view.getId()) {
      onPositiveButtonClicked();
    } else if (R.id.custom_auth_password_cancel == view.getId()) {
      onNegativeButtonClicked();
    }
  }

  @Override
  public void onBackPressed() {
    onNegativeButtonClicked();
  }

  protected abstract void onSuccess(final String password);

  protected abstract void onCanceled();
}
