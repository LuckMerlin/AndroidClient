
#ifndef _FILE_OPERATOR_H
#define _FILE_OPERATOR_H

#include <unistd.h>  
#include <sys/stat.h>  
#include <sys/time.h>  
#include <sys/types.h>  
#include <stdlib.h>  
#include <fcntl.h>

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

