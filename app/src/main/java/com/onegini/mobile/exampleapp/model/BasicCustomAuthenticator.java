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

package com.onegini.mobile.exampleapp.model;

import android.content.Context;
import com.onegini.mobile.exampleapp.view.action.basicauth.BasicCustomAuthAuthenticationAction;
import com.onegini.mobile.exampleapp.view.action.basicauth.BasicCustomAuthDeregistrationAction;
import com.onegini.mobile.exampleapp.view.action.basicauth.BasicCustomAuthRegistrationAction;
import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomAuthAuthenticationAction;
import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomAuthDeregistrationAction;
import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomAuthRegistrationAction;
import com.onegini.mobile.sdk.android.model.OneginiCustomAuthenticator;

public class BasicCustomAuthenticator implements OneginiCustomAuthenticator {

  public static final String AUTH_DATA = "fakeAuthenticationData";

  private final OneginiCustomAuthRegistrationAction registrationAction;
  private final OneginiCustomAuthDeregistrationAction deregistrationAction;
  private final OneginiCustomAuthAuthenticationAction authAuthenticationAction;

  public BasicCustomAuthenticator(final Context context) {
    registrationAction = new BasicCustomAuthRegistrationAction(context);
    deregistrationAction = new BasicCustomAuthDeregistrationAction(context);
    authAuthenticationAction = new BasicCustomAuthAuthenticationAction(context);
  }

  @Override
  public OneginiCustomAuthRegistrationAction getRegistrationAction() {
    return registrationAction;
  }

  @Override
  public OneginiCustomAuthDeregistrationAction getDeregistrationAction() {
    return deregistrationAction;
  }

  @Override
  public OneginiCustomAuthAuthenticationAction getAuthenticationAction() {
    return authAuthenticationAction;
  }

  @Override
  public String getId() {
    return "EXPERIMENTAL_CA_ID";
  }
}
