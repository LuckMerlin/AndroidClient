# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-optimizationpasses 5
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-keepparameternames
-keepattributes Exceptions
-keepattributes Signature
-keepattributes InnerClasses
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#########################################
-keep interface com.luckmerlin.core.proguard.PublishMethods { *; }
-keep interface com.luckmerlin.core.proguard.PublishFields { *; }
-keep interface com.luckmerlin.core.proguard.PublishProtectedMethod { *; }

-keep class * implements com.luckmerlin.core.proguard.PublishMethods {
       public <methods>;
 }

-keep class * implements com.luckmerlin.core.proguard.PublishFields {
      public <fields>;
 }

 -keep class * implements com.luckmerlin.core.proguard.PublishProtectedMethod {
      protected <methods>;
 }