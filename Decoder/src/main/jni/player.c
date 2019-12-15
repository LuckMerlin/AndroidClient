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

static enum mad_flow output(void *data,struct mad_header const *header, struct mad_pcm *pcm){
    return MAD_FLOW_CONTINUE;
}

static enum mad_flow input(void *data,struct mad_stream *stream){
    return MAD_FLOW_CONTINUE;
}

static enum mad_flow error(void *data,struct mad_stream *stream,struct mad_frame *frame){
    return MAD_FLOW_CONTINUE;
}

JNIEXPORT jboolean
Java_com_merlin_player_Player_play(JNIEnv *env,jobject type,jstring path,jfloat seek){
    int start=0;
    const  char* filePath=(*env)->GetStringUTFChars(env,path,0);
    if(filePath == NULL){
        LOGW("Can't play media,Media path is Empty.");
        return JNI_FALSE;
    }
    int fd = fileOpen(filePath,_FMODE_READ);
    if(fd == -1|| fd == -2){
        LOGW(" Can't play media,File open failed.%d %s",fd,filePath);
        (*env)->ReleaseStringChars(env,path,filePath);
        return JNI_FALSE;
    }

    struct buffer buffer;
    struct mad_decoder decoder;
    /* configure input, output, and error functions */

    mad_decoder_init(&decoder, &buffer,
                     input, 0 /* header */, 0 /* filter */, output,
                     error, 0 /* message */);
    /* start decoding */
//    result = mad_decoder_run(&decoder, MAD_DECODER_MODE_SYNC);

//    LOGD("%s: 采样率: %d, 文件大小: %ld",__FUNCTION__,g_Samplerate,st.st_size);
    LOGW("%d  chengdu.",fd);
    /* release the decoder */

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