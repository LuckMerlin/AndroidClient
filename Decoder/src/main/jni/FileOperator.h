
#ifndef _FILE_OPERATOR_H
#define _FILE_OPERATOR_H

#include "../../../../../../../../Users/luckmerlin/Library/Android/sdk/ndk/20.1.5948944/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/include/unistd.h"
#include "../../../../../../../../Users/luckmerlin/Library/Android/sdk/ndk/20.1.5948944/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/include/sys/stat.h"
#include "../../../../../../../../Users/luckmerlin/Library/Android/sdk/ndk/20.1.5948944/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/include/sys/time.h"
#include "../../../../../../../../Users/luckmerlin/Library/Android/sdk/ndk/20.1.5948944/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/include/sys/types.h"
#include "../../../../../../../../Users/luckmerlin/Library/Android/sdk/ndk/20.1.5948944/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/local/include/stdlib.h"
#include "../../../../../../../../Users/luckmerlin/Library/Android/sdk/ndk/20.1.5948944/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/include/fcntl.h"

#define _CREATE 0
#define _RDONLY 1
#define _WRONLY 2
#define _RDWR   3

#define _FMODE_READ     _RDONLY  
#define _FMODE_WRITE    _WRONLY  
#define _FMODE_CREATE   _CREATE  
#define _FMODE_RDWR     _RDWR 

int fileOpen(const char* filename,int access);
int fileRead(int fd,unsigned char* buf,int size);
int fileWrite(int fd,unsigned char* buf,int size);
int64_t fileSeek(int fd,int64_t pos,int whence);
int fileClose(int fd);

#endif

