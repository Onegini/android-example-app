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
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider;

public class AvailableIdentityProvidersMenu {

  private final List<OneginiIdentityProvider> identityProviders;
  private final PopupMenu popupMenu;

  public AvailableIdentityProvidersMenu(final PopupMenu popupMenu, final Set<OneginiIdentityProvider> identityProviders) {
    this.identityProviders = new ArrayList<>(identityProviders);
    this.popupMenu = popupMenu;
    inflateMenu();
  }

  private void inflateMenu() {
    final Menu menu = popupMenu.getMenu();
    for (int i = 0; i < identityProviders.size(); i++) {
      final OneginiIdentityProvider identityProvider = identityProviders.get(i);
      menu.add(NONE, i, NONE, String.format("%s (id: %s)", identityProvider.getName(), identityProvider.getId()));
    }
  }

  public AvailableIdentityProvidersMenu setOnClickListener(@NonNull final OnMenuItemClickListener listener) {
    this.popupMenu.setOnMenuItemClickListener(item -> {
      listener.onMenuItemClick(identityProviders.get(item.getItemId()));
      return true;
    });
    return this;
  }

  public void show() {
    popupMenu.show();
  }

  public interface OnMenuItemClickListener {
    void onMenuItemClick(OneginiIdentityProvider item);
  }
}
