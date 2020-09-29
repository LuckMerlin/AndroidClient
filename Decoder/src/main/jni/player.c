#include <jni.h>
#include "baseclass/log.h"
#include <pthread.h>
#define LOG_TAG "LM"
#include "FileOperator.h"
#include "mad/mad.h"
#include "string.h"
#include "pthread.h"

#define INPUT_BUFFER_SIZE	8192*3 /*(8192/5
 * ) */

#define  STATUS_NORMAL 0 //Keep not change for java
#define  STATUS_FATAL_ERROR -2 //Keep not change for java

#define  STATUS_END -2001
#define  STATUS_IDLE -2003

#define  STATUS_STOP -2005
#define  STATUS_PROGRESS -2006
#define  STATUS_CREATE -2021
#define  STATUS_DESTROY -2022

int MEDIA_TYPE_AUDIO =1;
int MEDIA_TYPE_AUDIO_STREAM=2;

static JavaVM  *VM=NULL;

jint JNI_OnLoad(JavaVM* vm,void* resolved){
    VM=vm;
    return JNI_VERSION_1_6;
}

struct BufferHandle{
    struct mad_stream stream;
    struct mad_frame frame;
    struct mad_synth synth;
    mad_timer_t timer;
    int playStatus;
    jbyteArray  buffer;
};

struct BufferHandle* handle;

void notifyStatusChange(int status, const char* note){
//    JNIEnv *env;
//    int res = (*VM)->GetEnv(VM,(void **) &env, JNI_VERSION_1_6);
//    if(res==JNI_OK){
//        jclass playerClass = (*env)->FindClass(env,"com/merlin/player/BK_Player");
//        jmethodID methodId=(*env)->GetStaticMethodID(env,playerClass,"onStatusChanged","(ILcom/merlin/player/MediaBuffer;Ljava/lang/String;)V");
//        jstring noteString= NULL==note?NULL:(*env)->NewStringUTF(env,"While play.");;
//        (*env)->CallStaticVoidMethod(env,playerClass,methodId,status,media,noteString);
//        (*env)->DeleteLocalRef(env, playerClass);
//        if (NULL!=noteString){
//            (*env)->DeleteLocalRef(env, noteString);
//        }
//    }
}

static inline signed int scale(mad_fixed_t sample){
    /* round */
    sample += (1L << (MAD_F_FRACBITS - 16));
    /* clip */
    if (sample >= MAD_F_ONE)
        sample = MAD_F_ONE - 1;
    else if (sample < -MAD_F_ONE)
        sample = -MAD_F_ONE;
    /* quantize */
    return sample >> (MAD_F_FRACBITS + 1 - 16);
}

static inline void onFrameDecode(jobject player,int mediaType,mad_timer_t timer,struct mad_header header,struct mad_pcm pcm){
    /* pcm->samplerate contains the sampling frequency */
    unsigned int layer=header.layer;
    unsigned int mode=header.mode;
    unsigned long bitrate=header.bitrate;
    unsigned short sampleRate=pcm.samplerate;
    unsigned short channels = pcm.channels;
    unsigned int length= pcm.length;
    unsigned int samples=length;
    mad_timer_add(&timer,header.duration);
//    LOGD("AAA %ld %ld", header.duration.seconds, header.duration.fraction);
//    mad_timer_fraction(timer,header.duration.fraction);
    mad_fixed_t const *left_ch, *right_ch;
    left_ch   = pcm.samples[0];
    left_ch   = pcm.samples[0];
    right_ch  = pcm.samples[1];
//    LOGD("bitrate %ld Mode %d Layer %d 通道 %d 采样率 %d",bitrate,mode,layer,channels,sampleRate);
    unsigned char* output = malloc(samples*channels*2);
    int index=0;
    while (samples--) {
        /* output sample(s) in 16-bit signed little-endian PCM */
        signed int sample = scale(*left_ch++);
        *(output+2*channels*index+0)=(sample >> 0) & 0xff;
        *(output+2*channels*index+1)=(sample >> 8) & 0xff;
        if (channels == 2) {
            sample = scale(*right_ch++);
            *(output+2*channels*index+2)=(sample >> 0) & 0xff;
            *(output+2*channels*index+3)=(sample >> 8) & 0xff;
        }
        index++;
    }
    int speed = sampleRate * 2;    /*播放速度是采样率的两倍 */
    length *= channels * 2;         //数据长度为pcm音频的4倍
    if (channels>0&&sampleRate>0){
        JNIEnv *jniEnv;
        int res = (*VM)->GetEnv(VM,(void **) &jniEnv, JNI_VERSION_1_6);
        if(res==JNI_OK){

            jclass callbackClass = (*jniEnv)->FindClass(jniEnv,"com/merlin/player/Player");
            jmethodID callbackMethod = (*jniEnv)->GetMethodID(jniEnv,callbackClass,"onMediaFrameDecodeFinish",
                                                              "(I[BBII)V");
            jbyteArray data = (*jniEnv)->NewByteArray(jniEnv, length);
            (*jniEnv)->SetByteArrayRegion(jniEnv, data, 0, length, output);
            (*jniEnv)->CallVoidMethod(jniEnv,player,callbackMethod,mediaType,data,channels,sampleRate,speed);
            (*jniEnv)->DeleteLocalRef(jniEnv, data);
            (*jniEnv)->DeleteLocalRef(jniEnv,callbackClass);
            notifyStatusChange(STATUS_PROGRESS,"Update progress.");
        }
    }
}

int readMediaBytes(jobject  loader,int offset,jbyteArray buffer){
    JNIEnv *env;
    int res = (*VM)->GetEnv(VM,(void **) &env, JNI_VERSION_1_6);
    if(res==JNI_OK){
        jclass bufferClass = (*env)->FindClass(env,"com/merlin/player/OnLoadMedia");
        jmethodID  methodId=(*env)->GetMethodID(env,bufferClass,"onLoadMedia", "([BI)I");
//        jmethodID  methodId=(*env)->GetMethodID(env,bufferClass,"nativeLoadBytes", "([BI)I");
        jint opened=(*env)->CallIntMethod(env,loader,methodId,buffer,offset);
        (*env)->DeleteLocalRef(env, bufferClass);
        return opened;
    }
    LOGE("Jni env open fail.%d",res);
    return STATUS_FATAL_ERROR;
}


JNIEXPORT jboolean JNICALL
Java_com_merlin_player_Player_create(JNIEnv *env, jobject player, jobject loader) {
    if (handle!=NULL){
        LOGW("Not need create media player which already started.");
        return JNI_FALSE;
    }





    int res = (*VM)->GetEnv(VM, (void **) &jniEnv, JNI_VERSION_1_6);
    if (res != JNI_OK) {
        LOGW("Can't create media player while create vm fail.");
        return JNI_FALSE;
    }
    LOGW("Create media player.");
    size_t handleSize = sizeof(struct BufferHandle);
    handle = (struct BufferHandle *) malloc(handleSize);
    handle->playStatus = STATUS_IDLE;
    notifyStatusChange(STATUS_CREATE, "Media player Create.");
    handle->buffer = (*jniEnv)->NewByteArray(jniEnv, INPUT_BUFFER_SIZE);
    mad_stream_init(&(handle->stream));
    mad_frame_init(&handle->frame);
    mad_timer_reset(&handle->timer);
    mad_synth_init(&handle->synth);
    while (JNI_TRUE){
        if (handle->playStatus==STATUS_STOP){
            LOGD("Stopped media player.");
            break;
        }
        int readState=STATUS_NORMAL;
        do{
            readState=STATUS_NORMAL;
            int readLength=0;
            if(handle->stream.buffer == 0 || handle->stream.error == MAD_ERROR_BUFLEN){
                if(handle->stream.next_frame != 0){
                    int leftOver = handle->stream.bufend - handle->stream.next_frame;
                    int i;
                    for(i= 0;i<leftOver;i++){
                        (*env)->SetByteArrayRegion(env,handle->buffer, i,1, &(handle->stream.next_frame[i]));
                    }
                    int readBytes =readMediaBytes(loader,leftOver,handle->buffer);
                    if(readBytes <= 0){
                        readState=readBytes;
                        break;
                    }
                    readLength = leftOver + readBytes;
                }else{
                    readLength  = readMediaBytes(loader,0,handle->buffer);
                    if(readLength <= 0){
                        readState=readLength;
                        break;
                    }
                }
                jbyte* bBuffer = (*env)->GetByteArrayElements(env,handle->buffer,0);
                unsigned char* buf=(unsigned char*)bBuffer;
                (*env)->DeleteLocalRef(env, bBuffer);
                mad_stream_buffer(&handle->stream,buf,readLength);
                handle->stream.error = MAD_ERROR_NONE;
            }
            int decodeResult=mad_frame_decode(&handle->frame,&handle->stream);
            if(decodeResult){
                if(handle->stream.error == MAD_ERROR_BUFLEN ||(MAD_RECOVERABLE(handle->stream.error))){
                    continue;
                }
            }else{
                break;
            }
        }while (JNI_TRUE);
        mad_synth_frame(&handle->synth, &handle->frame);
        onFrameDecode(player,MEDIA_TYPE_AUDIO, handle->timer, handle->frame.header, handle->synth.pcm);
        if (readState==STATUS_END){
            handle->playStatus=STATUS_IDLE;
            notifyStatusChange(STATUS_END,"Media play end");
        }
        if (readState==STATUS_DESTROY||readState==STATUS_FATAL_ERROR){
            handle->playStatus=STATUS_DESTROY;
            notifyStatusChange(STATUS_DESTROY,"Destroy media player.");
            break;
        }
    }
    mad_synth_finish(&handle->synth);
    mad_frame_finish(&handle->frame);
    mad_stream_finish(&handle->stream);
    handle->playStatus = STATUS_DESTROY;
    LOGW("Destroy media player.");
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_merlin_player_Player_destroy(JNIEnv *env, jobject thiz) {
    // TODO: implement stop()
}
