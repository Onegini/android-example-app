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
import android.widget.EditText;
import android.widget.TextView;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.view.action.twowayotpidentityprovider.TwoWayOtpRegistrationAction;

public class TwoWayOtpRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

  public static final String OTP_CHALLENGE_EXTRA = "otp_challenge_extra";

  EditText responseCodeEditText;
  TextView challengeCodeTextView;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_two_way_otp_registration);
    initUI();
    initChallengeCode();
  }

  private void initUI() {
    responseCodeEditText = findViewById(R.id.two_way_otp_response_code);
    challengeCodeTextView = findViewById(R.id.two_way_otp_challenge_code);
  }

  public void onOkButtonClicked() {
    if (TwoWayOtpRegistrationAction.CALLBACK != null) {
      final String responseCode = responseCodeEditText.getText().toString();
      TwoWayOtpRegistrationAction.CALLBACK.returnSuccess(responseCode);
    }
    finish();
  }

  public void onCancelButtonClicked() {
    if (TwoWayOtpRegistrationAction.CALLBACK != null) {
      TwoWayOtpRegistrationAction.CALLBACK.returnError(new Exception("Registration canceled"));
    }
    finish();
  }

  private void initChallengeCode() {
    final String challengeCode = getIntent().getStringExtra(OTP_CHALLENGE_EXTRA);
    challengeCodeTextView.setText(challengeCode);
  }

  @Override
  public void onClick(final View view) {
    if (R.id.two_way_otp_ok_button == view.getId()) {
      onOkButtonClicked();
    } else if (R.id.two_way_otp_cancel_button == view.getId()) {
      onCancelButtonClicked();
    }
  }
}
