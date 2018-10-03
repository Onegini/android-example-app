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

package com.onegini.mobile.exampleapp.network;

import android.content.Context;
import com.onegini.mobile.exampleapp.network.client.SecureResourceClient;
import com.onegini.mobile.exampleapp.network.client.UserClient;
import com.onegini.mobile.exampleapp.network.client.UserClient2;
import com.onegini.mobile.exampleapp.network.response.DevicesResponse;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserService {

  private static UserService INSTANCE;

  public static UserService getInstance(final Context context) {
    if (INSTANCE == null) {
      INSTANCE = new UserService(context);
    }
    return INSTANCE;
  }

  // the client using Retrofit 1.9
  private final UserClient userRetrofitClient;
  // the client using Retrofit 2.X
  private final UserClient2 userRetrofit2Client;

  private UserService(final Context context) {
    userRetrofitClient = SecureResourceClient.prepareSecuredUserRetrofitClient(UserClient.class, context);
    userRetrofit2Client = SecureResourceClient.prepareSecuredUserRetrofit2Client(UserClient2.class, context);
  }

  public Observable<DevicesResponse> getDevices(final boolean useRetrofit2) {
    return getObservable(useRetrofit2)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .unsubscribeOn(Schedulers.io());
  }

  private Observable<DevicesResponse> getObservable(final boolean useRetrofit2) {
    if (useRetrofit2) {
      return userRetrofit2Client.getDevices();
    } else {
      return userRetrofitClient.getDevices();
    }
  }
}
