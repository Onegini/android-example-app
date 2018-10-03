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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {

  private static final String EXTRA_TITLE = "title";
  private static final String EXTRA_MESSAGE = "message";

  public static AlertDialogFragment newInstance(final String errorMessage) {
    final AlertDialogFragment fragment = new AlertDialogFragment();
    final Bundle args = new Bundle();
    args.putString(EXTRA_MESSAGE, errorMessage);
    fragment.setArguments(args);

    return fragment;
  }

  public static AlertDialogFragment newInstance(final String errorMessage, final String title) {
    final AlertDialogFragment fragment = new AlertDialogFragment();

    final Bundle args = new Bundle();
    args.putString(EXTRA_TITLE, title);
    args.putString(EXTRA_MESSAGE, errorMessage);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public Dialog onCreateDialog(final Bundle savedInstanceState) {
    final Bundle arguments = getArguments();
    final String title = arguments.getString(EXTRA_TITLE, "Error");
    final String message = arguments.getString(EXTRA_MESSAGE);


    return new AlertDialog.Builder(getActivity())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK", (dialog, which) -> { })
        .create();
  }
}
