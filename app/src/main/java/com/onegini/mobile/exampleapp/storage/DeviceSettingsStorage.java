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

package com.onegini.mobile.exampleapp.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class DeviceSettingsStorage {

  private static final String PREFS_NAME = "client_settings_storage";
  private static final String KEY_RETROFIT = "retrofit";

  private final SharedPreferences sharedPreferences;

  public DeviceSettingsStorage(final Context context) {
    sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
  }

  public boolean shouldUseRetrofit2() {
    return sharedPreferences.getBoolean(KEY_RETROFIT, false);
  }

  public void setShouldUseRetrofit2(final boolean use) {
    sharedPreferences.edit().putBoolean(KEY_RETROFIT, use).commit();
  }
}
