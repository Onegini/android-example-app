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

public class FCMStorage {

  private static final String DEPRECATED_GCM_FILENAME = "gcm_shared_preferences";
  private static final String FILENAME = "fcm_shared_preference";
  private static final String KEY_REGISTRATION_ID = "registration_id";

  private final SharedPreferences sharedPreferences;
  private final SharedPreferences.Editor editor;
  private final Context context;

  public FCMStorage(final Context context) {
    this.context = context;
    sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    editor = sharedPreferences.edit();
  }

  public void setRegistrationId(final String regId) {
    editor.putString(KEY_REGISTRATION_ID, regId);
  }

  public String getRegistrationId() {
    return sharedPreferences.getString(KEY_REGISTRATION_ID, "");
  }

  public void save() {
    editor.apply();
    //It this application version we migrate from GCM to FCM, so we should remove old GCM registration id and generate new FCM token instead
    removeDeprecatedGcmRegistrationId();
  }

  private void removeDeprecatedGcmRegistrationId() {
    final SharedPreferences gcmSharedPreferences = context.getSharedPreferences(DEPRECATED_GCM_FILENAME, Context.MODE_PRIVATE);
    if (gcmSharedPreferences.contains(KEY_REGISTRATION_ID)) {
      gcmSharedPreferences.edit().clear().apply();
    }
  }
}
