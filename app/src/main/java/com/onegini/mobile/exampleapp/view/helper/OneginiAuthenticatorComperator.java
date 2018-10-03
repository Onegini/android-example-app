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

package com.onegini.mobile.exampleapp.view.helper;

import java.util.Comparator;

import com.onegini.mobile.sdk.android.model.OneginiAuthenticator;

/**
 * Order provided authenticators in following order:
 * 1. PIN authenticator
 * 2. Android fingerprint authenticator
 * 3. Custom authenticators ordered by name
 */
public class OneginiAuthenticatorComperator implements Comparator<OneginiAuthenticator> {

  @Override
  public int compare(final OneginiAuthenticator o1, final OneginiAuthenticator o2) {
    final int resultByType = compareByType(o1.getType(), o2.getType());
    if (resultByType == 0) {
      return compareByName(o1, o2);
    } else {
      return resultByType;
    }
  }

  private int compareByType(final int o1, final int o2) {
    if (o1 == o2) {
      return 0;
    }

    // o1 == PIN && o2 == FINGERPRINT || CUSTOM
    if (o1 == OneginiAuthenticator.PIN) {
      return -1;
    }

    // o2 == PIN && o1 == FINGERPRINT || CUSTOM
    if (o2 == OneginiAuthenticator.PIN) {
      return 1;
    }

    // o1 == FINGERPRINT && o2 == CUSTOM
    if (o1 == OneginiAuthenticator.FINGERPRINT) {
      return -1;
    }

    // o2 == FINGERPRINT && o1 == CUSTOM
    return 1;
  }

  private int compareByName(final OneginiAuthenticator o1, final OneginiAuthenticator o2) {
    return o1.getName().compareTo(o2.getName());
  }
}
