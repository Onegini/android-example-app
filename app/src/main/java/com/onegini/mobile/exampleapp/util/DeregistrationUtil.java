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

package com.onegini.mobile.exampleapp.util;

import android.content.Context;
import android.util.Log;
import com.onegini.mobile.exampleapp.storage.UserStorage;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class DeregistrationUtil {

  private final Context context;

  public DeregistrationUtil(Context context) {
    this.context = context;
  }

  public void onUserDeregistered(UserProfile userProfile) {
    if (userProfile == null) {
      Log.e("DeregistrationUtil", "userProfile == null");
      return;
    }
    new UserStorage(context).removeUser(userProfile);
  }

  public void onDeviceDeregistered() {
    new UserStorage(context).clearStorage();
  }
}
