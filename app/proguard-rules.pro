# Copyright (c) 2016-2017 Onegini B.V.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Onegini Mobile Security Platform Android application proguard configuration

-printmapping build/obfuscation-mapping.map
-printseeds build/seeds.txt
-keepattributes Signature,InnerClasses,EnclosingMethod

-dontwarn retrofit.**
-dontwarn retrofit2.**
-dontwarn android.test.**
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn org.bouncycastle.**
-dontwarn org.spongycastle.**
-dontwarn rx.*

-keep class com.onegini.mobile.sdk.android.library.internal.* { *; }
-keep class * implements com.onegini.mobile.sdk.android.model.OneginiClientConfigModel { *; }

-keep interface retrofit.** { *; }
-keep class retrofit.** { *; }
-keep class retrofit2.** { *; }
-keep class com.squareup.okhttp.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.inject.* { *; }
-keep class org.apache.http.* { *; }
-keep class org.apache.james.mime4j.* { *; }
-keep class javax.inject.* { *; }
-keep class sun.misc.Unsafe { *; }
-keep class org.spongycastle.jcajce.provider.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}


# Okhttp 3
-dontwarn okhttp3.**

# ButterKnife 7
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Rx java
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.invoke.*

# Smasung FIDO
-dontwarn com.samsung.sds.**