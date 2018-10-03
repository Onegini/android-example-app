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

@SuppressWarnings("unused")
public class ApplicationDetails {

  @SerializedName("application_identifier")
  private String applicationIdentifier;
  @SerializedName("application_platform")
  private String applicationPlatform;
  @SerializedName("application_version")
  private String applicationVersion;

  public String getApplicationIdentifier() {
    return applicationIdentifier;
  }

  public String getApplicationPlatform() {
    return applicationPlatform;
  }

  public String getApplicationVersion() {
    return applicationVersion;
  }
}
