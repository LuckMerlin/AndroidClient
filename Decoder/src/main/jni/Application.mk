APP_STL := gnustl_shared

APP_CPPFLAGS += -frtti
APP_CPPFLAGS += -fexceptions
APP_CPPFLAGS += -std=c++11

####APP_ABI := armeabi armeabi-v7a x86 x86_64 arm64-v8a

APP_ABI := all
#armeabi-v7a x86 x86_64 arm64-v8a

####NDK_TOOLCHAIN_VERSION := 4.8

APP_MODULES := mp3decoder

