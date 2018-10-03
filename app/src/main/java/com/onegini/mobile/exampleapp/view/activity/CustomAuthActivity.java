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
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.view.handler.BasicCustomAuthenticationRequestHandler;

public class CustomAuthActivity extends AuthenticationActivity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_custom);
    ButterKnife.bind(this);
    initialize();
  }

  @Override
  protected void initialize() {
    parseIntent();
    updateTexts();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.auth_accept_button)
  public void onAcceptClicked() {
    if (BasicCustomAuthenticationRequestHandler.CALLBACK != null) {
      BasicCustomAuthenticationRequestHandler.CALLBACK.acceptAuthenticationRequest();
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.auth_deny_button)
  public void onDenyClicked() {
    if (BasicCustomAuthenticationRequestHandler.CALLBACK != null) {
      BasicCustomAuthenticationRequestHandler.CALLBACK.denyAuthenticationRequest();
    }
  }


  @SuppressWarnings("unused")
  @OnClick(R.id.fallback_to_pin_button)
  public void onFallbackClicked() {
    if (BasicCustomAuthenticationRequestHandler.CALLBACK != null) {
      BasicCustomAuthenticationRequestHandler.CALLBACK.fallbackToPin();
    }
  }
}
