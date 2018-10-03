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
import com.onegini.mobile.exampleapp.model.ImplicitUserDetails;
import com.onegini.mobile.exampleapp.network.client.ImplicitUserClient;
import com.onegini.mobile.exampleapp.network.client.ImplicitUserClient2;
import com.onegini.mobile.exampleapp.network.client.SecureResourceClient;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ImplicitUserService {

  private static ImplicitUserService INSTANCE;

  public static ImplicitUserService getInstance(final Context context) {
    if (INSTANCE == null) {
      INSTANCE = new ImplicitUserService(context);
    }
    return INSTANCE;
  }

  // the client using Retrofit 1.9
  private final ImplicitUserClient applicationDetailsRetrofitClient;
  // the client using Retrofit 2.X
  private final ImplicitUserClient2 applicationDetailsRetrofit2Client;

  private ImplicitUserService(final Context context) {
    applicationDetailsRetrofitClient = SecureResourceClient.prepareSecuredImplicitUserRetrofitClient(ImplicitUserClient.class, context);
    applicationDetailsRetrofit2Client = SecureResourceClient.prepareSecuredImplicitUserRetrofit2Client(ImplicitUserClient2.class, context);
  }

  public Observable<ImplicitUserDetails> getImplicitUserDetails(final boolean useRetrofit2) {
    return getObservable(useRetrofit2)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  private Observable<ImplicitUserDetails> getObservable(final boolean useRetrofit2) {
    if (useRetrofit2) {
      return applicationDetailsRetrofit2Client.getImplicitUserDetails();
    } else {
      return applicationDetailsRetrofitClient.getImplicitUserDetails();
    }
  }
}
