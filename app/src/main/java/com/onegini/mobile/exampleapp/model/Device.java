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

import com.google.gson.annotations.SerializedName;
import com.onegini.mobile.exampleapp.Constants;

@SuppressWarnings("unused")
public class Device {

  @SerializedName("id")
  private String id;
  @SerializedName("name")
  private String name;
  @SerializedName("application")
  private String application;
  @SerializedName("platform")
  private String platform;
  @SerializedName("mobile_authentication_enabled")
  private boolean mobileAuthenticationEnabled;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getApplication() {
    return application;
  }

  public String getPlatform() {
    return platform;
  }

  public boolean isMobileAuthenticationEnabled() {
    return mobileAuthenticationEnabled;
  }

  public String getDeviceFullInfo() {
    return getName() + Constants.NEW_LINE + getApplication() + Constants.NEW_LINE + getPlatform() + Constants.NEW_LINE
        + "Mobile authentication enabled: " + isMobileAuthenticationEnabled();
  }
}