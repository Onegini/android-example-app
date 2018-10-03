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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import com.onegini.mobile.exampleapp.model.User;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class UserStorage {
  private static final String PREFS_NAME = "user_storage";
  private static final String EMPTY = "";

  private final SharedPreferences sharedPreferences;

  public UserStorage(final Context context) {
    sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
  }

  public void saveUser(final User user) {
    final SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(user.getUserProfile().getProfileId(), user.getName());
    editor.apply();
  }

  public User loadUser(final UserProfile userProfile) {
    return new User(userProfile, sharedPreferences.getString(userProfile.getProfileId(), EMPTY));
  }

  public void removeUser(final UserProfile userProfile) {
    final SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.remove(userProfile.getProfileId());
    editor.apply();
  }

  public List<User> loadUsers(final Set<UserProfile> userProfiles) {
    final List<User> listOfUsers = new ArrayList<>(userProfiles.size());

    for (final UserProfile userProfile : userProfiles) {
      listOfUsers.add(loadUser(userProfile));
    }

    return listOfUsers;
  }

  public void clearStorage() {
    sharedPreferences.edit().clear().apply();
  }
}
