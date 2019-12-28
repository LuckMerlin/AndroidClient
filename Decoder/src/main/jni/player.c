#include <jni.h>
#include "baseclass/log.h"
#define LOG_TAG "LM"
#include "FileOperator.h"
#include "mad/mad.h"
#include "string.h"
#define INPUT_BUFFER_SIZE	8192*5 /*(8192/4) */

struct FileHandle {
    int file;
    unsigned char const *start;
    int64_t length;
    struct mad_stream stream;
    struct mad_frame frame;
    struct mad_synth synth;
    mad_timer_t timer;
    unsigned char inputBuffer[INPUT_BUFFER_SIZE];
};

static JavaVM  *VM=NULL;

//unsigned long currPlayingPos,cachedLength;

jint JNI_OnLoad(JavaVM* vm,void* resolved){
    VM=vm;
    return JNI_VERSION_1_6;
}

//char * openTempFile(int access){
//    char * tempPath="/sdcard/temp.lm";
//    return fileOpen(tempPath,access);
//}
/*
 * The following utility routine performs simple rounding, clipping, and
 * scaling of MAD's high-resolution samples down to 16 bits. It does not
 * perform any dithering or noise shaping, which would be recommended to
 * obtain any exceptional audio quality. It is therefore not recommended to
 * use this routine if high-quality output is desired.
 */
//
//static inline signed int scale(mad_fixed_t sample){
//    /* round */
//    sample += (1L << (MAD_F_FRACBITS - 16));
//    /* clip */
//    if (sample >= MAD_F_ONE)
//        sample = MAD_F_ONE - 1;
//    else if (sample < -MAD_F_ONE)
//        sample = -MAD_F_ONE;
//    /* quantize */
//    return sample >> (MAD_F_FRACBITS + 1 - 16);
//}
//
// enum mad_flow output(void *data,struct mad_header const *header, struct mad_pcm *pcm){
//    LOGD("输出 le ");
//    mad_fixed_t const *left_ch, *right_ch;
//    /* pcm->samplerate contains the sampling frequency */
//    unsigned int layer=header->layer;
//    unsigned int mode=header->mode;
//    unsigned long bitrate=header->bitrate;
//
//    unsigned int sampleRate=pcm->samplerate;
//    unsigned int nChannels = pcm->channels;
//    //
//    unsigned int length= pcm->length;
//    unsigned int nSamples = length;
//    left_ch   = pcm->samples[0];
//    right_ch  = pcm->samples[1];
////    int speed = pcm->sampleRate * 2;    /*播放速度是采样率的两倍 */
//    LOGD("bitrate %ld Mode %d Layer %d 通道 %d 采样率 %d",bitrate,mode,layer,nChannels,sampleRate);
//    unsigned char* output = malloc(nSamples*nChannels*2);;
//    int index=0;
//    while (nSamples--) {
//        /* output sample(s) in 16-bit signed little-endian PCM */
//        signed int sample = scale(*left_ch++);
//        *(output+2*nChannels*index+0)=(sample >> 0) & 0xff;
//        *(output+2*nChannels*index+1)=(sample >> 8) & 0xff;
//        if (nChannels == 2) {
//            sample = scale(*right_ch++);
//            *(output+2*nChannels*index+2)=(sample >> 0) & 0xff;
//            *(output+2*nChannels*index+3)=(sample >> 8) & 0xff;
//        }
//        index++;
//    }
//    length *= nChannels * 2;         //数据长度为pcm音频的4倍
//    JNIEnv *jniEnv;
//    int res = (*VM)->GetEnv(VM,(void **) &jniEnv, JNI_VERSION_1_6);
//    if(res==JNI_OK){
//        jclass callbackClass = (*jniEnv)->FindClass(jniEnv,"com/merlin/player/Player");
//        jmethodID callbackMethod = (*jniEnv)->GetStaticMethodID(jniEnv,callbackClass,"onDecodeFinish","([BII)V");
//        jbyteArray data = (*jniEnv)->NewByteArray(jniEnv, length);
//        (*jniEnv)->SetByteArrayRegion(jniEnv, data, 0, length, output);
////        (*jniEnv)->CallStaticVoidMethod(jniEnv,callbackClass,callbackMethod,data,nChannels,sampleRate);
//        (*jniEnv)->DeleteLocalRef(jniEnv, data);
//        (*jniEnv)->DeleteLocalRef(jniEnv,callbackClass);
//        (*jniEnv)->DeleteLocalRef(jniEnv,callbackMethod);
//    }
//    return MAD_FLOW_CONTINUE;
//}
//
//
//static enum mad_flow input(void *data,struct mad_stream *stream){
//     LOGW("输入 ");
////    struct buffer *buffer = data;
////    if (!buffer->length) {
////        LOGD("播放结束 %d", buffer->length);
////        return MAD_FLOW_STOP;
////    }
////    mad_stream_buffer(stream, buffer->file, 1024);
////    buffer->start+=1024;
//    return MAD_FLOW_CONTINUE;
////      int fp=openTempFile(_RDONLY);
////      if(fp){
////          int next=currPlayingPos+1024;
////          int end=next<=cachedLength?next:cachedLength;
////          if(end!=currPlayingPos){
////              struct buffer *buffer = data;
////
////             mad_stream_buffer(stream, buffer->start, buffer->length);
////          }
////          LOGW("deeeeeeee");
////          int size=fileTell(fp);
////          LOGW("WWWWW 当前大小 %d",size);
////          return MAD_FLOW_CONTINUE;
////      }
////    struct buffer *buffer = data;
////    if (!buffer->length) {
////        LOGD("Input &&&&&&&& %d",buffer->length);
////        return MAD_FLOW;
////    }
////    LOGD("Input aaaaa %d %d",buffer->start,buffer->length);
////    mad_stream_buffer(stream, buffer->start, buffer->length);
////    buffer->length = 0;
//     // LOGW("失败了 %d",fp);
//    return MAD_FLOW_BREAK;
//}
//
//static enum mad_flow error(void *data,struct mad_stream *stream,struct mad_frame *frame){
//    LOGD("ERROR ");
//    return MAD_FLOW_CONTINUE;
//}


//int resetPlayer(){
//    int result=openTempFile(_WRONLY);
//    if(result){
//        LOGD("Succeed reset player. %d",result);
//        currPlayingPos=0;
//        cachedLength=0;
//        fileClose(result);
//        return -1;
//    }
//    LOGW("Failed to reset player. %d",result);
//    return 0;
//}



static inline int readNextFrame(struct FileHandle* handle){
    do{
        if(handle->stream.buffer == 0 || handle->stream.error == MAD_ERROR_BUFLEN){
            LOGD("DDDDDDDDDDd jinlai ");
            int inputBufferSize = 0;
            if(handle->stream.next_frame != 0){
                int leftOver = handle->stream.bufend - handle->stream.next_frame;
                int i;
                for(i= 0;i<leftOver;i++){
                    handle->inputBuffer[i] = handle->stream.next_frame[i];
                }
                int readBytes = fileRead(handle->file, handle->inputBuffer+leftOver,INPUT_BUFFER_SIZE-leftOver);
                if(readBytes == 0){
                    LOGD("File read finish.");
                    return 0;
                }
                inputBufferSize = leftOver + readBytes;
                handle->start += readBytes;
            }else{
                int readBytes = fileRead(handle->file,handle->inputBuffer,INPUT_BUFFER_SIZE);
                if(readBytes == 0){
                    LOGD("File read finish.");
                    return 0;
                }
                inputBufferSize = readBytes;
                handle->start += readBytes;
            }
            mad_stream_buffer(&handle->stream,&handle->inputBuffer,inputBufferSize);
            handle->stream.error = MAD_ERROR_NONE;
            LOGD("####### %d",inputBufferSize);
        }
        LOGD("QQQQQQ %d",handle->frame.header.samplerate);
        int decodeResult=mad_frame_decode(&handle->frame,&handle->stream);
        if(decodeResult){
            LOGD("Fail decode %d",decodeResult);
            if(handle->stream.error == MAD_ERROR_BUFLEN ||(MAD_RECOVERABLE(handle->stream.error))){
                continue;
            }
        }else{
            LOGD("Succeed decode frame.");
            break;
        }
    }while (1);
    mad_timer_add(&handle->timer, handle->frame.header.duration);
    mad_synth_frame(&handle->synth,&handle->frame);
//    handle.leftSamples = mp3->synth.pcm.length;
//    mp3->offset = 0;
    return 0;
}


JNIEXPORT jboolean JNICALL
Java_com_merlin_player_Player_playBytes(JNIEnv *env, jobject thiz, jbyteArray data, jint offset,
                                        jint length, jboolean reset) {
//    int byteLength = data ==NULL?-1:(*env)->GetArrayLength(env,data);
//    if(byteLength <= 0){
//        LOGW("Can'T play bytes which is NULL.%d",byteLength);
//        return JNI_FALSE;
//    }
//    LOGW("长度 %d",byteLength);
//    jbyte* bytesStart =(*env)->GetByteArrayElements(env,data, 0);
//    struct buffer buffer;
//    buffer.start=buffer.length =0;
//    struct mad_decoder decoder;
//    /* configure input, output, and error functions */
//    mad_decoder_init(&decoder, &buffer,
//                     input, 0 /* header */, 0 /* filter */, output,
//                     error, 0 /* message */);
//    /* start decoding */
//    int result = mad_decoder_run(&decoder, MAD_DECODER_MODE_SYNC);
//    LOGW("WWWWWWWW %d %d %d", result,bytesStart,byteLength);
//    (*env)->ReleaseByteArrayElements(env,data,bytesStart,0);
//    /* release the decoder */
//    mad_decoder_finish(&decoder);
    return JNI_TRUE;
}


JNIEXPORT jboolean
Java_com_merlin_player_Player_play(JNIEnv *env,jobject type,jstring path,jfloat seek){
    const  char* filePath=(*env)->GetStringUTFChars(env,path,0);
    if(filePath == NULL){
        LOGW("Can't play media,Media path is Empty.");
        return JNI_FALSE;
    }
    int fd = fileOpen(filePath,_RDONLY);
    struct stat fileStat;
    int statResult=fstat(fd, &fileStat);
    if(fd == -1|| fd == -2||statResult<0){
        LOGW(" Can't play media,File open failed.%d %d %s",fd,statResult,filePath);
        (*env)->ReleaseStringChars(env,path,filePath);
        return JNI_FALSE;
    }
    int seekPosition=0;
    int length= fileStat.st_size;
    LOGW("Playing media %d %s",length, filePath);
    seekPosition=seekPosition>=0&&seekPosition<=length?seekPosition:0;
    int64_t remainLength=fileSeek (fd,seekPosition, SEEK_SET);
//    struct FileHandle handle;
    size_t handleSize=sizeof(struct FileHandle);
    struct FileHandle* handle = (struct FileHandle*)malloc(handleSize);
    memset(handle,0,handleSize);
    handle->file=fd;
    handle->length=length;
    handle->start=seekPosition;
    mad_stream_init(&(handle->stream));
    mad_frame_init(&handle->frame);
    mad_synth_init(&handle->synth);
    mad_timer_reset(&handle->timer);
    readNextFrame(handle);
    unsigned int samplerate = handle->frame.header.samplerate;
    enum mad_layer layer = handle->frame.header.layer;
    LOGD("@@@@@@@@@@@@@ %d %d",samplerate,layer);


//    FileHandler* handle = malloc(sizeof(FileHandler));
//    FileHandler* mp3Handle = (FileHandler*)malloc(sizeof(FileHandler));
//    memset(mp3Handle,0,sizeof(MP3FileHandle));
//    mad_stream_init(&mp3Handle->stream);
//    memset(mp3Handle,0,sizeof(MP3FileHandle));
//    buffer.start=seekPosition;
//    int64_t length=fileSeek (fd, 0, SEEK_END);
//    int length=fstat(fd, &st);
//    LOGW("Media file %d %d", length, seekPosition);
//    struct mad_decoder decoder;
    /* configure input, output, and error functions */
//    mad_decoder_init(&decoder, &handle,
//                     input, 0 /* header */, 0 /* filter */, output,
//                     error, 0 /* message */);
    /* start decoding */
//    int result = mad_decoder_run(&decoder, MAD_DECODER_MODE_ASYNC);
//    readNextFrame(handle);
////    LOGD("%s: 采样率: %d, 文件大小: %ld",__FUNCTION__,g_Samplerate,st.st_size);
//    LOGD("Finish decoder. %d",result);
//    mad_decoder_finish(&decoder);    /* release the decoder */
    (*env)->ReleaseStringChars(env,path,filePath);
    return JNI_TRUE;
}
