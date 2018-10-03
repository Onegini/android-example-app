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

import butterknife.OnClick;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.view.handler.MobileAuthenticationBasicCustomRequestHandler;

public class MobileAuthenticationCustomActivity extends CustomAuthActivity {

  @SuppressWarnings("unused")
  @OnClick(R.id.auth_accept_button)
  public void onAcceptClicked() {
    if (MobileAuthenticationBasicCustomRequestHandler.CALLBACK != null) {
      MobileAuthenticationBasicCustomRequestHandler.CALLBACK.acceptAuthenticationRequest();
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.auth_deny_button)
  public void onDenyClicked() {
    if (MobileAuthenticationBasicCustomRequestHandler.CALLBACK != null) {
      MobileAuthenticationBasicCustomRequestHandler.CALLBACK.denyAuthenticationRequest();
    }
  }


  @SuppressWarnings("unused")
  @OnClick(R.id.fallback_to_pin_button)
  public void onFallbackClicked() {
    if (MobileAuthenticationBasicCustomRequestHandler.CALLBACK != null) {
      MobileAuthenticationBasicCustomRequestHandler.CALLBACK.fallbackToPin();
    }
  }
}
