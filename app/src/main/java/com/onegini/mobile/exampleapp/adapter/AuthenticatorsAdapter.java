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

package com.onegini.mobile.exampleapp.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.model.AuthenticatorListItem;
import com.onegini.mobile.exampleapp.view.activity.SettingsAuthenticatorsActivity;
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator;

public class AuthenticatorsAdapter extends
    RecyclerView.Adapter<AuthenticatorsAdapter.ViewHolder> {

  private final AuthenticatorListItem[] authenticators;
  private final SettingsAuthenticatorsActivity.AuthenticatorClickListener authenticatorClickListener;

  public AuthenticatorsAdapter(final AuthenticatorListItem[] authenticators,
                               final SettingsAuthenticatorsActivity.AuthenticatorClickListener authenticatorClickListener) {
    this.authenticators = authenticators;
    this.authenticatorClickListener = authenticatorClickListener;
  }

  @Override
  public AuthenticatorsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View authenticatorItemView = inflater.inflate(R.layout.row_item_authenticator, parent, false);

    return new ViewHolder(authenticatorItemView);
  }

  @Override
  public void onBindViewHolder(final AuthenticatorsAdapter.ViewHolder holder, final int position) {
    final AuthenticatorListItem authenticatorListItem = authenticators[position];
    final OneginiAuthenticator authenticator = authenticatorListItem.getAuthenticator();

    holder.itemView.setClickable(true);
    holder.itemView.setOnClickListener(v -> authenticatorClickListener.onAuthenticatorItemClick(position));

    if (position % 2 == 0) {
      holder.itemView.setBackgroundResource(R.color.switcher_dark);
    }

    holder.authenticatorSwitch.setText(authenticator.getName());

    if (authenticator.getType() == OneginiAuthenticator.PIN) {
      holder.authenticatorSwitch.setChecked(true);
      holder.authenticatorSwitch.setEnabled(false);
      return;
    }

    holder.authenticatorSwitch.setChecked(authenticator.isRegistered());

    if (authenticatorListItem.isProcessed()) {
      holder.progressIndicator.setVisibility(VISIBLE);
      holder.authenticatorSwitch.setEnabled(false);
    } else {
      holder.progressIndicator.setVisibility(GONE);
      holder.authenticatorSwitch.setEnabled(true);
    }
  }

  @Override
  public int getItemCount() {
    return authenticators.length;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    final Switch authenticatorSwitch;
    final ProgressBar progressIndicator;

    ViewHolder(final View contactView) {
      super(contactView);
      authenticatorSwitch = (Switch) itemView.findViewById(R.id.authenticator_switch);
      progressIndicator = (ProgressBar) itemView.findViewById(R.id.progress_bar_authenticators);
    }
  }
}


