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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.onegini.mobile.exampleapp.R;
import com.onegini.mobile.exampleapp.model.User;
import com.onegini.mobile.exampleapp.network.fcm.MobileAuthenticationService;
import com.onegini.mobile.exampleapp.storage.UserStorage;
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthWithPushRequest;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class PendingPushMessagesAdapter extends RecyclerView.Adapter<PendingPushMessagesAdapter.ViewHolder> {

  private final Context context;
  private final List<OneginiMobileAuthWithPushRequest> list;
  private final UserStorage userStorage;
  private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);

  public PendingPushMessagesAdapter(final Context context) {
    this.context = context;
    list = new ArrayList<>();
    userStorage = new UserStorage(context);
  }

  public void update(final Set<OneginiMobileAuthWithPushRequest> set) {
    list.clear();
    list.addAll(set);
    notifyDataSetChanged();
  }

  @Override
  public PendingPushMessagesAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
    final Context context = parent.getContext();
    final LayoutInflater inflater = LayoutInflater.from(context);

    final View deviceItemView = inflater.inflate(R.layout.row_item_pending_message, parent, false);

    return new PendingPushMessagesAdapter.ViewHolder(deviceItemView);
  }

  @Override
  public void onBindViewHolder(final PendingPushMessagesAdapter.ViewHolder viewHolder, final int position) {
    final OneginiMobileAuthWithPushRequest oneginiMobileAuthWithPushRequest = list.get(position);
    final User user = userStorage.loadUser(new UserProfile(oneginiMobileAuthWithPushRequest.getUserProfileId()));
    viewHolder.profileTextView.setText(user.getName());
    viewHolder.messageTextView.setText(oneginiMobileAuthWithPushRequest.getMessage());

    final Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(oneginiMobileAuthWithPushRequest.getTimestamp());
    viewHolder.dateTextView.setText(sdf.format(calendar.getTime()));

    calendar.add(Calendar.SECOND, oneginiMobileAuthWithPushRequest.getTimeToLiveSeconds());
    viewHolder.expiresTextView.setText(context.getString(R.string.notification_expires_at, sdf.format(calendar.getTime())));

    viewHolder.onClickListener = v -> context.startService(getServiceIntent(oneginiMobileAuthWithPushRequest));
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  private Intent getServiceIntent(final OneginiMobileAuthWithPushRequest mobileAuthWithPushRequest) {
    final Intent intent = new Intent(context, MobileAuthenticationService.class);
    intent.putExtra(MobileAuthenticationService.EXTRA_TRANSACTION_ID, mobileAuthWithPushRequest.getTransactionId());
    intent.putExtra(MobileAuthenticationService.EXTRA_MESSAGE, mobileAuthWithPushRequest.getMessage());
    intent.putExtra(MobileAuthenticationService.EXTRA_PROFILE_ID, mobileAuthWithPushRequest.getUserProfileId());
    return intent;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    final TextView messageTextView;
    final TextView dateTextView;
    final TextView profileTextView;
    final TextView expiresTextView;

    OnClickListener onClickListener;

    ViewHolder(final View itemView) {
      super(itemView);

      messageTextView = itemView.findViewById(R.id.pending_message);
      dateTextView = itemView.findViewById(R.id.pending_message_timestamp);
      profileTextView = itemView.findViewById(R.id.pending_message_profile);
      expiresTextView = itemView.findViewById(R.id.pending_message_expires);
      itemView.setOnClickListener(v -> {
        if (onClickListener != null) {
          onClickListener.onClick(v);
        }
      });
    }
  }
}
