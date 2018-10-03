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

package com.onegini.mobile.exampleapp.view.handler;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.onegini.mobile.exampleapp.Constants.COMMAND_FINISH;
import static com.onegini.mobile.exampleapp.Constants.COMMAND_START;
import static com.onegini.mobile.exampleapp.Constants.EXTRA_COMMAND;
import static com.onegini.mobile.exampleapp.view.activity.AuthenticationActivity.EXTRA_ERROR_MESSAGE;
import static com.onegini.mobile.exampleapp.view.activity.AuthenticationActivity.EXTRA_MESSAGE;
import static com.onegini.mobile.exampleapp.view.helper.ErrorMessageParser.parseErrorMessage;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import com.onegini.mobile.exampleapp.OneginiSDK;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.util.DeregistrationUtil;
import com.onegini.mobile.exampleapp.view.activity.LoginActivity;
import com.onegini.mobile.exampleapp.view.activity.PinActivity;
import com.onegini.mobile.sdk.android.handlers.OneginiPinValidationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError;
import com.onegini.mobile.sdk.android.handlers.request.OneginiCreatePinRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class CreatePinRequestHandler implements OneginiCreatePinRequestHandler {

  public static PinWithConfirmationHandler CALLBACK;

  private final Context context;

  public CreatePinRequestHandler(final Context context) {
    this.context = context;
  }

  @Override
  public void startPinCreation(final UserProfile userProfile, final OneginiPinCallback oneginiPinCallback, final int pinLength) {
    PinActivity.setIsCreatePinFlow(true);
    notifyActivity(context.getString(R.string.pin_title_choose_pin), "");

    CALLBACK = new PinWithConfirmationHandler(oneginiPinCallback);
  }

  @Override
  public void onNextPinCreationAttempt(final OneginiPinValidationError oneginiPinValidationError) {
    handlePinValidationError(oneginiPinValidationError);
  }

  @Override
  public void finishPinCreation() {
    notifyActivity("", "", COMMAND_FINISH);
  }

  /**
   * Extended pin handler, used to create PIN verification step
   */
  public class PinWithConfirmationHandler {

    private final OneginiPinCallback originalHandler;

    private char[] pin;

    public PinWithConfirmationHandler(final OneginiPinCallback originalHandler) {
      this.originalHandler = originalHandler;
    }

    public void onPinProvided(final char[] pin) {
      if (isPinSet()) {
        secondPinProvided(pin);
      } else {
        firstPinProvided(pin);
      }
    }

    private void firstPinProvided(final char[] pin) {
      OneginiSDK.getOneginiClient(context).getUserClient().validatePinWithPolicy(pin, new OneginiPinValidationHandler() {
        @Override
        public void onSuccess() {
          PinWithConfirmationHandler.this.pin = pin;
          notifyActivity(context.getString(R.string.pin_title_verify_pin), "");
        }

        @Override
        public void onError(final OneginiPinValidationError oneginiPinValidationError) {
          handlePinValidationError(oneginiPinValidationError);
        }
      });
    }

    public void secondPinProvided(final char[] pin) {
      final boolean pinsEqual = Arrays.equals(this.pin, pin);
      nullifyPinArray();
      if (pinsEqual) {
        originalHandler.acceptAuthenticationRequest(pin);
      } else {
        notifyActivity(context.getString(R.string.pin_title_choose_pin), context.getString(R.string.pin_error_not_equal));
      }
    }

    public void pinCancelled(){
      nullifyPinArray();
      originalHandler.denyAuthenticationRequest();
    }

    private boolean isPinSet() {
      return pin != null;
    }

    private void nullifyPinArray() {
      if (isPinSet()) {
        final int arraySize = pin.length;
        for (int i = 0; i < arraySize; i++) {
          pin[i] = '\0';
        }
        pin = null;
      }
    }
  }

  private void handlePinValidationError(final OneginiPinValidationError oneginiPinValidationError) {
    @OneginiPinValidationError.PinValidationErrorType int errorType = oneginiPinValidationError.getErrorType();
    switch (errorType) {
      case OneginiPinValidationError.WRONG_PIN_LENGTH:
        notifyActivity(context.getString(R.string.pin_title_choose_pin), context.getString(R.string.pin_error_invalid_length));
        break;
      case OneginiPinValidationError.PIN_BLACKLISTED:
        notifyActivity(context.getString(R.string.pin_title_choose_pin), context.getString(R.string.pin_error_blacklisted));
        break;
      case OneginiPinValidationError.PIN_IS_A_SEQUENCE:
        notifyActivity(context.getString(R.string.pin_title_choose_pin), context.getString(R.string.pin_error_sequence));
        break;
      case OneginiPinValidationError.PIN_USES_SIMILAR_DIGITS:
        notifyActivity(context.getString(R.string.pin_title_choose_pin), context.getString(R.string.pin_error_similar));
        break;
      case OneginiPinValidationError.DEVICE_DEREGISTERED:
        new DeregistrationUtil(context).onDeviceDeregistered();
        startLoginActivity(parseErrorMessage(oneginiPinValidationError));
        break;
      case OneginiPinValidationError.GENERAL_ERROR:
      default:
        notifyActivity(context.getString(R.string.pin_title_choose_pin), oneginiPinValidationError.getMessage());
        break;
    }
  }

  private void notifyActivity(final String message, final String errorMessage) {
    notifyActivity(message, errorMessage, COMMAND_START);
  }

  private void notifyActivity(final String message, final String errorMessage, final String command) {
    final Intent intent = new Intent(context, PinActivity.class);
    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra(EXTRA_MESSAGE, message);
    intent.putExtra(EXTRA_ERROR_MESSAGE, errorMessage);
    intent.putExtra(EXTRA_COMMAND, command);
    context.startActivity(intent);
  }

  private void startLoginActivity(final String errorMessage) {
    final Intent intent = new Intent(context, LoginActivity.class);
    intent.putExtra(LoginActivity.ERROR_MESSAGE_EXTRA, errorMessage);
    intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
    context.startActivity(intent);
  }

}
