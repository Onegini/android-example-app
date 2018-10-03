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

import android.widget.ImageView;
import com.onegini.mobile.exampleapp.R;

public class PinInputFields {

  private final int EMPTY_FIELD_RESOURCE_ID = R.drawable.circle_empty;
  private final int FOCUSED_FIELD_RESOURCE_ID = R.drawable.circle_focused;
  private final int FILLED_FIELD_RESOURCE_ID = R.drawable.circle_filled;

  private final ImageView[] pinInputs;
  private final int pinLength;
  private final PinProvidedListener pinProvidedListener;
  private int cursorIndex;
  private char[] pin;

  public interface PinProvidedListener {
    void onPinProvided(char[] pin);
  }

  public PinInputFields(final PinProvidedListener listener, final ImageView[] pinInputs) {
    pinProvidedListener = listener;
    this.pinInputs = pinInputs;
    this.pinLength = pinInputs.length;

    pin = new char[pinLength];
    reset();
  }

  public void onPinDigitRemoved() {
    if (cursorIndex > 0) {
      pinInputs[cursorIndex].setImageResource(EMPTY_FIELD_RESOURCE_ID);
      pin[--cursorIndex] = '\0';
    }

    pinInputs[cursorIndex].setImageResource(FOCUSED_FIELD_RESOURCE_ID);
  }

  public void onPinDigitPressed(final int digit) {
    if (cursorIndex >= pinLength) {
      return;
    }

    pin[cursorIndex] = Character.forDigit(digit, 10);
    pinInputs[cursorIndex].setImageResource(FILLED_FIELD_RESOURCE_ID);

    cursorIndex++;
    if (cursorIndex >= pinLength) {
      pinProvidedListener.onPinProvided(pin.clone());
      clearPin();
    } else {
      pinInputs[cursorIndex].setImageResource(FOCUSED_FIELD_RESOURCE_ID);
    }
  }

  public void reset() {
    for (int i = 1; i < pinLength; i++) {
      pinInputs[i].setImageResource(EMPTY_FIELD_RESOURCE_ID);
    }
    pinInputs[0].setImageResource(FOCUSED_FIELD_RESOURCE_ID);

    clearPin();
    cursorIndex = 0;
  }

  public boolean inPinEmpty() {
    return cursorIndex == 0;
  }

  private void clearPin() {
    for (int index = 0; index < pinLength; index++) {
      pin[index] = '\0';
    }
    pin = new char[pinLength];
  }
}
