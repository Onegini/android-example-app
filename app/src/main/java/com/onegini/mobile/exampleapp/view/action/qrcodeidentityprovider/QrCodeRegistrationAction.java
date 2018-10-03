package com.onegini.mobile.exampleapp.view.action.qrcodeidentityprovider;

import android.content.Context;
import android.content.Intent;
import com.onegini.mobile.exampleapp.view.activity.QrCodeRegistrationActivity;
import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback;
import com.onegini.mobile.sdk.android.model.entity.CustomInfo;

public class QrCodeRegistrationAction implements OneginiCustomRegistrationAction {

  public static OneginiCustomRegistrationCallback CALLBACK;
  private final Context context;

  public QrCodeRegistrationAction(final Context context) {
    this.context = context;
  }

  @Override
  public void finishRegistration(final OneginiCustomRegistrationCallback callback, final CustomInfo customInfo) {
    CALLBACK = callback;

    final Intent intent = new Intent(context, QrCodeRegistrationActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }
}
