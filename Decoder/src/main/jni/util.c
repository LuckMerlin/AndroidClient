#include "../../../../../../../../Users/luckmerlin/Library/Android/sdk/ndk/20.1.5948944/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/include/jni.h"

//
// Created by Luck Merlin on 2020-01-05.
//
//jstring charTojstring( JNIEnv* env, const char* pat ){
//    // 定义java String类 strClass
//    jclass strClass = (*env)->FindClass(env,"java/lang/String");
//    // 获取java String类方法String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
//    jmethodID ctorID = (*env)->GetMethodID(env,strClass, "<init>", "([BLjava/lang/String;)V");
//    // 建立byte数组
//    jbyteArray bytes = (env)->NewByteArray((jsize)strlen(pat));
//    // 将char* 转换为byte数组
//    (env)->SetByteArrayRegion(bytes, 0, (jsize)strlen(pat), (jbyte*)pat);
//    //设置String, 保存语言类型,用于byte数组转换至String时的参数
//    jstring encoding = (env)->NewStringUTF("utf-8");
//    //将byte数组转换为java String,并输出
//    return (jstring)(env)->NewObject(strClass, ctorID, bytes, encoding);
//}
