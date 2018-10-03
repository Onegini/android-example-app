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

package com.onegini.mobile.exampleapp.view.handler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.onegini.mobile.sdk.android.handlers.request.OneginiBrowserRegistrationRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback;

public class RegistrationRequestHandler implements OneginiBrowserRegistrationRequestHandler {

  private static OneginiBrowserRegistrationCallback CALLBACK;

  /**
   * Finish registration action with result from web browser
   */
  public static void handleRegistrationCallback(final Uri uri) {
    if (CALLBACK != null) {
      CALLBACK.handleRegistrationCallback(uri);
      CALLBACK = null;
    }
  }

  /**
   * Cancel registration action in case of web browser error
   */
  public static void onRegistrationCanceled() {
    if (CALLBACK != null) {
      CALLBACK.denyRegistration();
      CALLBACK = null;
    }
  }

  private final Context context;

  public RegistrationRequestHandler(final Context context) {
    this.context = context;
  }

  @Override
  public void startRegistration(final Uri uri, final OneginiBrowserRegistrationCallback oneginiBrowserRegistrationCallback) {
    CALLBACK = oneginiBrowserRegistrationCallback;

    // We're going to launch external browser to allow user to log in. You could also use embedded WebView instead.
    final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

    context.startActivity(intent);
  }
}
