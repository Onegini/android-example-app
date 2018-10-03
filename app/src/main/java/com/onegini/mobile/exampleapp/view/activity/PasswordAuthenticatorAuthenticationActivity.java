package com.onegini.mobile.exampleapp.view.activity;

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

import com.onegini.mobile.exampleapp.view.action.passwordauth.PasswordCustomAuthAuthenticationAction;

public class PasswordAuthenticatorAuthenticationActivity extends PasswordAuthenticatorActivity {

  @Override
  protected void onSuccess(final String password) {
    if (PasswordCustomAuthAuthenticationAction.CALLBACK != null) {
      PasswordCustomAuthAuthenticationAction.CALLBACK.returnSuccess(password);
    }
  }

  @Override
  protected void onCanceled() {
    if (PasswordCustomAuthAuthenticationAction.CALLBACK != null) {
      PasswordCustomAuthAuthenticationAction.CALLBACK.returnError(new Exception("Authentication canceled"));
    }
  }
}
