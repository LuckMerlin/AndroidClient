#include <jni.h>
#include "baseclass/log.h"
#define LOG_TAG "LM_PLAYER_C"
#include "decoder/FileOperator.h"

JNIEXPORT jboolean
Java_com_merlin_player_Player_play(JNIEnv *env,jobject type,jstring path,jfloat seek){
    const  char* filePath=(*env)->GetStringUTFChars(env,path,0);
    if(filePath == NULL){
        LOGW("Can't play media,Media path is Empty.");
        return JNI_FALSE;
    }
    int fd = fileOpen(filePath,_FMODE_READ);
    if(fd == -1){
        LOGW(" Can't play media,File open failed.%d %s",fd,filePath);
        (*env)->ReleaseStringChars(env,path,filePath);
        return JNI_FALSE;
    }
    LOGW("%d  chengdu.",fd);
    (*env)->ReleaseStringChars(env,path,filePath);
    return 1;
}