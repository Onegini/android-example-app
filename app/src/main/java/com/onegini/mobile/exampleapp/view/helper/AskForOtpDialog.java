package com.onegini.mobile.exampleapp.view.helper;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.onegini.mobile.exampleapp.OneginiSDK;
import com.onegini.mobile.sdk.android.client.UserClient;
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithOtpHandler;

public class AskForOtpDialog {

  private final Activity activity;
  private final OneginiMobileAuthWithOtpHandler handler;

  public AskForOtpDialog(final Activity activity, final OneginiMobileAuthWithOtpHandler handler) {
    this.activity = activity;
    this.handler = handler;
  }

  public void show() {
    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
    alertDialog.setTitle("Mobile auth with OTP");
    alertDialog.setMessage("Enter One Time Password encoded in Base64.");

    final EditText input = new EditText(activity);
    final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT);
    input.setLayoutParams(lp);
    alertDialog.setView(input);

    alertDialog.setPositiveButton("Confirm", (dialog, which) -> {
      final String otpCode = input.getText().toString();
      final UserClient userClient = OneginiSDK.getOneginiClient(activity.getApplicationContext()).getUserClient();

      userClient.handleMobileAuthWithOtp(otpCode, handler);
    });

    alertDialog.setNegativeButton("Dismiss", (dialog, which) -> dialog.cancel());
    alertDialog.show();
  }
}
