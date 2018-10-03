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
package com.onegini.mobile.exampleapp.view.action.twowayotpidentityprovider;

import static com.onegini.mobile.exampleapp.view.activity.TwoWayOtpRegistrationActivity.OTP_CHALLENGE_EXTRA;

import android.content.Context;
import android.content.Intent;
import com.onegini.mobile.exampleapp.view.activity.TwoWayOtpRegistrationActivity;
import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomTwoStepRegistrationAction;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback;
import com.onegini.mobile.sdk.android.model.entity.CustomInfo;

public class TwoWayOtpRegistrationAction implements OneginiCustomTwoStepRegistrationAction {

  public static OneginiCustomRegistrationCallback CALLBACK;

  private final Context context;

  public TwoWayOtpRegistrationAction(final Context context) {
    this.context = context;
  }

  @Override
  public void initRegistration(final OneginiCustomRegistrationCallback callback, final CustomInfo customInfo) {
    callback.returnSuccess(null);
  }

  @Override
  public void finishRegistration(final OneginiCustomRegistrationCallback callback, final CustomInfo customInfo) {
    CALLBACK = callback;

    final Intent intent = new Intent(context, TwoWayOtpRegistrationActivity.class);
    intent.putExtra(OTP_CHALLENGE_EXTRA, customInfo.getData());
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }
}
