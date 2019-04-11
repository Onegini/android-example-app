package com.onegini.mobile.exampleapp.view.activity;

import android.os.Bundle;
import android.view.View;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.view.handler.MobileAuthOtpRequestHandler;

public class MobileAuthenticationOTPActivity extends AuthenticationActivity implements View.OnClickListener {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_push_simple);
    initialize();
  }

  public void onAcceptClicked() {
    if (MobileAuthOtpRequestHandler.CALLBACK != null) {
      MobileAuthOtpRequestHandler.CALLBACK.acceptAuthenticationRequest();
    }
  }

  public void onDenyClicked() {
    if (MobileAuthOtpRequestHandler.CALLBACK != null) {
      MobileAuthOtpRequestHandler.CALLBACK.denyAuthenticationRequest();
    }
  }

  @Override
  protected void initialize() {
    parseIntent();
    updateTexts();
  }

  @Override
  public void onClick(final View view) {
    if (R.id.auth_accept_button == view.getId()) {
      onAcceptClicked();
    } else if (R.id.auth_deny_button == view.getId()) {
      onDenyClicked();
    }
  }
}
