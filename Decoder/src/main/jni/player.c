#include <jni.h>
#include "baseclass/log.h"
#define LOG_TAG "JNI_PLAYER_C"
#include "FileOperator.h"

JNIEXPORT jboolean
Java_com_merlin_player_Player_play(JNIEnv *env,jobject type,jstring path,jfloat seek){
    const  char* filePath=(*env)->GetStringUTFChars(env,path,0);
    if(filePath == NULL){
        LOGW("Can't play media,Media path is Empty.");
        return JNI_FALSE;
    }
    int fd = fileOpen(filePath,_FMODE_READ);

    (*env)->ReleaseStringChars(env,path,filePath);
    return 1;
}