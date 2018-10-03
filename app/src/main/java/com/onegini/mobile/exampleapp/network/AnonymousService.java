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
import com.onegini.mobile.exampleapp.model.ApplicationDetails;
import com.onegini.mobile.exampleapp.network.client.AnonymousClient;
import com.onegini.mobile.exampleapp.network.client.AnonymousClient2;
import com.onegini.mobile.exampleapp.network.client.SecureResourceClient;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AnonymousService {

  private static AnonymousService INSTANCE;

  public static AnonymousService getInstance(final Context context) {
    if (INSTANCE == null) {
      INSTANCE = new AnonymousService(context);
    }
    return INSTANCE;
  }

  // the client using Retrofit 1.9
  private final AnonymousClient applicationDetailsRetrofitClient;
  // the client using Retrofit 2.X
  private final AnonymousClient2 applicationDetailsRetrofit2Client;

  private AnonymousService(final Context context) {
    applicationDetailsRetrofitClient = SecureResourceClient.prepareSecuredAnonymousRetrofitClient(AnonymousClient.class, context);
    applicationDetailsRetrofit2Client = SecureResourceClient.prepareSecuredAnonymousRetrofit2Client(AnonymousClient2.class, context);
  }

  public Observable<ApplicationDetails> getApplicationDetails(final boolean useRetrofit2) {
    return getObservable(useRetrofit2)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .unsubscribeOn(Schedulers.io());
  }

  private Observable<ApplicationDetails> getObservable(final boolean useRetrofit2) {
    if (useRetrofit2) {
      return applicationDetailsRetrofit2Client.getApplicationDetails();
    } else {
      return applicationDetailsRetrofitClient.getApplicationDetails();
    }
  }
}