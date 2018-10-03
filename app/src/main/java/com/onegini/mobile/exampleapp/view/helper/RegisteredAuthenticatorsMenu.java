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

import static android.view.Menu.NONE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator;


public class RegisteredAuthenticatorsMenu {

  private final List<OneginiAuthenticator> authenticators;
  private final PopupMenu popupMenu;

  public RegisteredAuthenticatorsMenu(final PopupMenu popupMenu, final Set<OneginiAuthenticator> authenticators) {
    this.authenticators = new ArrayList<>(authenticators);
    this.popupMenu = popupMenu;
    inflateMenu();
  }

  private void inflateMenu() {
    final Menu menu = popupMenu.getMenu();
    for (int i = 0; i < authenticators.size(); i++) {
      final OneginiAuthenticator authenticator = authenticators.get(i);
      menu.add(NONE, i, NONE, String.format("%s (id: %s)", authenticator.getName(), authenticator.getId()));
    }
  }

  public RegisteredAuthenticatorsMenu setOnClickListener(@NonNull final OnMenuItemClickListener listener) {
    this.popupMenu.setOnMenuItemClickListener(item -> {
      listener.onMenuItemClick(authenticators.get(item.getItemId()));
      return true;
    });
    return this;
  }

  public void show() {
    popupMenu.show();
  }

  public interface OnMenuItemClickListener {
    void onMenuItemClick(OneginiAuthenticator item);
  }
}
