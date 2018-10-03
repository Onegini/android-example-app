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

import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.model.BasicCustomAuthenticator;
import com.onegini.mobile.exampleapp.view.action.basicauth.BasicCustomAuthAuthenticationAction;

public class BasicAuthenticatorAuthenticationActivity extends BasicAuthenticatorActivity {

  @Override
  protected void setTitle() {
    titleText.setText(R.string.custom_auth_authentication_title);
  }

  @Override
  protected void onSuccess() {
    if (BasicCustomAuthAuthenticationAction.CALLBACK != null) {
      BasicCustomAuthAuthenticationAction.CALLBACK.returnSuccess(BasicCustomAuthenticator.AUTH_DATA);
    }
  }

  @Override
  protected void onFailure() {
    if (BasicCustomAuthAuthenticationAction.CALLBACK != null) {
      BasicCustomAuthAuthenticationAction.CALLBACK.returnError(new Exception("Authentication failed"));
    }
  }

  @Override
  protected void onError() {
    if (BasicCustomAuthAuthenticationAction.CALLBACK != null) {
      BasicCustomAuthAuthenticationAction.CALLBACK.returnError(new Exception("Fake exception"));
    }
  }
}
