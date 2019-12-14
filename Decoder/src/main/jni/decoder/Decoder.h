#ifndef _DECODER_H
#define _DECODER_H

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "mad/mad.h"
#define INPUT_BUFFER_SIZE	8192*5 /*(8192/4) */

typedef struct{
    int size;
    int64_t fileStartPos;
    int fd;
    struct mad_stream stream;
    struct mad_frame frame;
    struct mad_synth synth;
    mad_timer_t timer;
    int leftSamples;
    int offset;
    unsigned char inputBuffer[INPUT_BUFFER_SIZE];
} MP3FileHandle;

static MP3FileHandle* Handle;
unsigned int g_Samplerate;

#endif