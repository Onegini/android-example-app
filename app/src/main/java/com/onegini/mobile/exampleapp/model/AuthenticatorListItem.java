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

import com.onegini.mobile.sdk.android.model.OneginiAuthenticator;

public class AuthenticatorListItem {

  private final OneginiAuthenticator authenticator;

  private boolean isProcessed;

  public AuthenticatorListItem(final OneginiAuthenticator authenticator) {
    this.authenticator = authenticator;
  }

  public boolean isProcessed() {
    return isProcessed;
  }

  public void setIsProcessed(boolean isProcessed) {
    this.isProcessed = isProcessed;
  }

  public OneginiAuthenticator getAuthenticator() {
    return authenticator;
  }
}
