package com.onegini.mobile.exampleapp.model;

import android.content.Context;
import com.onegini.mobile.exampleapp.view.action.qrcodeidentityprovider.QrCodeRegistrationAction;
import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction;
import com.onegini.mobile.sdk.android.model.OneginiCustomIdentityProvider;

public class QrCodeIdentityProvider implements OneginiCustomIdentityProvider {

  private final QrCodeRegistrationAction qrCodeRegistrationAction;

  public QrCodeIdentityProvider(final Context context) {
    qrCodeRegistrationAction = new QrCodeRegistrationAction(context);
  }

  @Override
  public OneginiCustomRegistrationAction getRegistrationAction() {
    return qrCodeRegistrationAction;
  }

  @Override
  public String getId() {
    return "qr-code-api";
  }
}
