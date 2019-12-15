#include <jni.h>
#include "baseclass/log.h"
#define LOG_TAG "LM"
#include "FileOperator.h"
#include "mad/mad.h"

/*
 * This is a private message structure. A generic pointer to this structure
 * is passed to each of the callback functions. Put here any data you need
 * to access from within the callbacks.
 */
struct buffer {
    unsigned char const *start;
    unsigned long length;
};


static JavaVM  *VM=NULL;

jint JNI_OnLoad(JavaVM* vm,void* resolved){
    VM=vm;
    LOGD("JNI 被加载了");
    return JNI_VERSION_1_6;
}

/*
 * The following utility routine performs simple rounding, clipping, and
 * scaling of MAD's high-resolution samples down to 16 bits. It does not
 * perform any dithering or noise shaping, which would be recommended to
 * obtain any exceptional audio quality. It is therefore not recommended to
 * use this routine if high-quality output is desired.
 */

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

 enum mad_flow output(void *data,struct mad_header const *header, struct mad_pcm *pcm){
//    LOGD("输出 le ");
    unsigned int  nsamples;
    mad_fixed_t const *left_ch, *right_ch;
    /* pcm->samplerate contains the sampling frequency */
    unsigned int nchannels = pcm->channels;
    int length=nsamples  = pcm->length;
    left_ch   = pcm->samples[0];
    right_ch  = pcm->samples[1];
//    int speed = pcm->samplerate * 2;    /*播放速度是采样率的两倍 */
    unsigned char* output = malloc(nsamples*nchannels*2);;
    int index=0;
    while (nsamples--) {
        /* output sample(s) in 16-bit signed little-endian PCM */
        signed int sample = scale(*left_ch++);
        *(output+2*nchannels*index+0)=(sample >> 0) & 0xff;
        *(output+2*nchannels*index+1)=(sample >> 8) & 0xff;
        if (nchannels == 2) {
            sample = scale(*right_ch++);
            *(output+2*nchannels*index+2)=(sample >> 0) & 0xff;
            *(output+2*nchannels*index+3)=(sample >> 8) & 0xff;
        }
        index++;
    }
    length *= nchannels * 2;         //数据长度为pcm音频的4倍
    JNIEnv *jniEnv;
    int res = (*VM)->GetEnv(VM,(void **) &jniEnv, JNI_VERSION_1_6);
    if(res==JNI_OK){
        jclass callbackClass = (*jniEnv)->FindClass(jniEnv,"com/merlin/player/Player");
        jmethodID callbackMethod = (*jniEnv)->GetStaticMethodID(jniEnv,callbackClass,"onDecodeFinish","([BII)V");
        jbyteArray data = (*jniEnv)->NewByteArray(jniEnv, length);
        (*jniEnv)->SetByteArrayRegion(jniEnv, data, 0, length, output);
        (*jniEnv)->CallStaticVoidMethod(jniEnv,callbackClass,callbackMethod,data,1,1);
        (*jniEnv)->DeleteLocalRef(jniEnv, data);
        (*jniEnv)->DeleteLocalRef(jniEnv,callbackClass);
        (*jniEnv)->DeleteLocalRef(jniEnv,callbackMethod);
    }
    return MAD_FLOW_CONTINUE;
}

static enum mad_flow input(void *data,struct mad_stream *stream){
//    LOGD("input ");
    struct buffer *buffer = data;
    if (!buffer->length) {
        return MAD_FLOW_STOP;
    }
    mad_stream_buffer(stream, buffer->start, buffer->length);
    buffer->length = 0;
    return MAD_FLOW_CONTINUE;
}

static enum mad_flow error(void *data,struct mad_stream *stream,struct mad_frame *frame){
    LOGD("ERROR ");
    return MAD_FLOW_CONTINUE;
}



JNIEXPORT jboolean
Java_com_merlin_player_Player_play(JNIEnv *env,jobject type,jstring path,jfloat seek){
//    int start=0;
//    const  char* filePath=(*env)->GetStringUTFChars(env,path,0);
//    if(filePath == NULL){
//        LOGW("Can't play media,Media path is Empty.");
//        return JNI_FALSE;
//    }
//    int fd = fileOpen(filePath,_FMODE_READ);
//    if(fd == -1|| fd == -2){
//        LOGW(" Can't play media,File open failed.%d %s",fd,filePath);
//        (*env)->ReleaseStringChars(env,path,filePath);
//        return JNI_FALSE;
//    }
//    struct buffer buffer;
//    struct mad_decoder decoder;
//    /* configure input, output, and error functions */
//    mad_decoder_init(&decoder, &buffer,
//                     input, 0 /* header */, 0 /* filter */, output,
//                     error, 0 /* message */);
//    /* start decoding */
//    int result = mad_decoder_run(&decoder, MAD_DECODER_MODE_SYNC);
//    LOGD("DDDDDD %d", result);
////    LOGD("%s: 采样率: %d, 文件大小: %ld",__FUNCTION__,g_Samplerate,st.st_size);
//    LOGW("%d  chengdu.",fd);
//    /* release the decoder */
//
//    mad_decoder_finish(&decoder);
    return JNI_TRUE;
}

//static inline int readNextFrame(MP3FileHandle* mp3){
//    do{
//        if(mp3->stream.buffer == 0 || mp3->stream.error == MAD_ERROR_BUFLEN){
//            int inputBufferSize = 0;
//            if(mp3->stream.next_frame != 0){
//                int leftOver = mp3->stream.bufend - mp3->stream.next_frame;
//                int i;
//                for(i= 0;i<leftOver;i++){
//                    mp3->inputBuffer[i] = mp3->stream.next_frame[i];
//                }
//                int readBytes = fileRead(mp3->fd, mp3->inputBuffer+leftOver,INPUT_BUFFER_SIZE-leftOver);
//                if(readBytes == 0)
//                    return 0;
//                inputBufferSize = leftOver + readBytes;
//                mp3 ->fileStartPos += readBytes;
//            }else{
//                int readBytes = fileRead(mp3->fd,mp3->inputBuffer,INPUT_BUFFER_SIZE);
//                if(readBytes == 0)
//                    return 0;
//                inputBufferSize = readBytes;
//                mp3 ->fileStartPos += readBytes;
//            }
//            mad_stream_buffer(&mp3->stream,mp3->inputBuffer,inputBufferSize);
//            mp3->stream.error = MAD_ERROR_NONE;
//        }
//        if(mad_frame_decode(&mp3->frame,&mp3->stream)){
//            if( mp3->stream.error == MAD_ERROR_BUFLEN ||(MAD_RECOVERABLE(mp3->stream.error)))
//                continue;
//            else
//                return 0;
//        }else
//            break;
//    }while(1);
//    mad_timer_add(&mp3->timer, mp3->frame.header.duration);
//    mad_synth_frame(&mp3->synth,&mp3->frame);
//    mp3->leftSamples = mp3->synth.pcm.length;
//    mp3->offset = 0;
//    return -1;
//}
JNIEXPORT jboolean JNICALL
Java_com_merlin_player_Player_playBytes(JNIEnv *env, jobject thiz, jbyteArray data, jint offset,
                                        jint length, jboolean reset) {
    int byteLength = data ==NULL?-1:(*env)->GetArrayLength(env,data);
    if(byteLength <= 0){
        LOGW("Can'T play bytes which is NULL.%d",byteLength);
        return JNI_FALSE;
    }
    LOGW("长度 %d",byteLength);
    jbyte* bytesStart =(*env)->GetByteArrayElements(env,data, 0);
    struct buffer buffer;
    buffer.start =bytesStart;
    buffer.length=byteLength;
    struct mad_decoder decoder;
    /* configure input, output, and error functions */
    mad_decoder_init(&decoder, &buffer,
                     input, 0 /* header */, 0 /* filter */, output,
                     error, 0 /* message */);
    /* start decoding */
    int result = mad_decoder_run(&decoder, MAD_DECODER_MODE_SYNC);
    LOGW("WWWWWWWW %d %d %d", result,bytesStart,byteLength);
    (*env)->ReleaseByteArrayElements(env,data,bytesStart,0);
    /* release the decoder */
    mad_decoder_finish(&decoder);
    return JNI_TRUE;
}
