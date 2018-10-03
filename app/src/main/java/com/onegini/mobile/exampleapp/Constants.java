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

package com.onegini.mobile.exampleapp;

public interface Constants {
  String[] DEFAULT_SCOPES = { "read" };
  String NEW_LINE = "\n";
  String FCM_SENDER_ID = "586427927998";
  String EXTRA_COMMAND = "command";
  String COMMAND_START = "start";
  String COMMAND_FINISH = "finish";
  String COMMAND_SHOW_SCANNING = "show";
  String COMMAND_RECEIVED_FINGERPRINT = "received";
  String COMMAND_ASK_TO_ACCEPT_OR_DENY = "ask";
}
