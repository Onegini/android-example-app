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

package com.onegini.mobile.exampleapp.util;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

public class AnimationUtils {

  private AnimationUtils() {
  }

  public static Animation getBlinkAnimation() {
    final Animation animation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f);
    animation.setDuration(350);
    animation.setInterpolator(new LinearInterpolator());
    animation.setRepeatCount(1);
    animation.setRepeatMode(Animation.REVERSE);

    return animation;
  }
}
