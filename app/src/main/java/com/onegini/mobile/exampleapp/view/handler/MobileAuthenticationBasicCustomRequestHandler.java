/*
 * Copyright (c) 2016-2017 Onegini B.V.
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

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.onegini.mobile.exampleapp.Constants.COMMAND_FINISH;
import static com.onegini.mobile.exampleapp.Constants.COMMAND_START;
import static com.onegini.mobile.exampleapp.Constants.EXTRA_COMMAND;
import static com.onegini.mobile.exampleapp.view.activity.AuthenticationActivity.EXTRA_MESSAGE;
import static com.onegini.mobile.exampleapp.view.activity.AuthenticationActivity.EXTRA_USER_PROFILE_ID;

import android.content.Context;
import android.content.Intent;
import com.onegini.mobile.exampleapp.network.fcm.NotificationHelper;
import com.onegini.mobile.exampleapp.view.activity.MobileAuthenticationCustomActivity;
import com.onegini.mobile.sdk.android.handlers.request.OneginiMobileAuthWithPushCustomRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomCallback;
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest;

public class MobileAuthenticationBasicCustomRequestHandler implements OneginiMobileAuthWithPushCustomRequestHandler {

  public static OneginiCustomCallback CALLBACK;

  private String userProfileId;
  private String message;
  private final Context context;
  private final NotificationHelper notificationHelper;

  public MobileAuthenticationBasicCustomRequestHandler(final Context context) {
    this.context = context;
    this.notificationHelper = new NotificationHelper(context);
  }

  @Override
  public void startAuthentication(final OneginiMobileAuthenticationRequest oneginiMobileAuthenticationRequest,
                                  final OneginiCustomCallback oneginiCustomCallback) {
    CALLBACK = oneginiCustomCallback;
    userProfileId = oneginiMobileAuthenticationRequest.getUserProfile().getProfileId();
    message = oneginiMobileAuthenticationRequest.getMessage();

    final Intent intent = prepareActivityIntent(COMMAND_START);
    notificationHelper.handleIntent(intent, message);
  }

  @Override
  public void finishAuthentication() {
    notifyActivity(COMMAND_FINISH);
  }

  private void notifyActivity(final String command) {
    final Intent intent = prepareActivityIntent(command);
    context.startActivity(intent);
  }

  private Intent prepareActivityIntent(final String command) {
    final Intent intent = new Intent(context, MobileAuthenticationCustomActivity.class);
    intent.putExtra(EXTRA_COMMAND, command);
    intent.putExtra(EXTRA_USER_PROFILE_ID, userProfileId);
    intent.putExtra(EXTRA_MESSAGE, message);
    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
    return intent;
  }
}
