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
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.onegini.mobile.exampleapp.R;

public abstract class PasswordAuthenticatorActivity extends AppCompatActivity {

  @SuppressWarnings({ "unused" })
  @BindView(R.id.custom_auth_password)
  EditText passwordEditText;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_password_authenticator);
    ButterKnife.bind(this);
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.custom_auth_password_send)
  public void onPositiveButtonClicked() {
    onSuccess(passwordEditText.getText().toString());
    finish();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.custom_auth_password_cancel)
  public void onNegativeButtonClicked() {
    onCanceled();
    finish();
  }

  @Override
  public void onBackPressed() {
    onNegativeButtonClicked();
  }

  protected abstract void onSuccess(final String password);

  protected abstract void onCanceled();
}
