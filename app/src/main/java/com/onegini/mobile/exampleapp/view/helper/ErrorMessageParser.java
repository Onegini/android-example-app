package com.onegini.mobile.exampleapp.view.helper;

import java.util.Locale;

import com.onegini.mobile.sdk.android.handlers.error.OneginiError;

public class ErrorMessageParser {

  private ErrorMessageParser(){}

  public static String parseErrorMessage(final OneginiError error){
    return String.format(Locale.getDefault(), "%d: %s", error.getErrorType(), error.getMessage());
  }
}
