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

import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;


public class PinKeyboard {

  private final PinInputFields pinInputFields;

  private ImageButton deleteButton;

  public PinKeyboard(final PinInputFields pinInputFields) {
    this.pinInputFields = pinInputFields;
  }

  public void initLayout(final TableLayout keyboardLayout, final Resources resources, final String packageName) {
    int resourceId;
    for (int digit = 0; digit < 10; digit++) {
      resourceId = resources.getIdentifier("pin_key_" + digit, "id", packageName);
      initPinDigitButton(keyboardLayout, resourceId, digit);
    }
    initPinDeleteButton(keyboardLayout, resources.getIdentifier("pin_key_del", "id", packageName));
  }

  public void reset() {
    deleteButton.setVisibility(View.INVISIBLE);
  }

  private void initPinDigitButton(final TableLayout keyboardLayout, final int buttonId, final int buttonValue) {
    final Button pinButton = (Button) keyboardLayout.findViewById(buttonId);
    pinButton.setOnClickListener(v -> onDigitKeyClicked(buttonValue));
  }

  private void initPinDeleteButton(final TableLayout keyboardLayout, final int buttonId) {
    deleteButton = (ImageButton) keyboardLayout.findViewById(buttonId);
    deleteButton.setOnClickListener(v -> onDeleteKeyClicked());
  }

  private void onDigitKeyClicked(final int digit) {
    deleteButton.setVisibility(View.VISIBLE);
    pinInputFields.onPinDigitPressed(digit);
  }

  private void onDeleteKeyClicked() {
    pinInputFields.onPinDigitRemoved();
    if (pinInputFields.inPinEmpty()) {
      deleteButton.setVisibility(View.INVISIBLE);
    }
  }
}