
#include "FileOperator.h"

int fileOpen(const char *filename, int access){
	int fd=0;
	if(access == _CREATE){
		fd = open(filename,O_RDWR|O_CREAT|O_EXCL,0666);
	}else if(access == _WRONLY){
		fd = open(filename,O_WRONLY|O_TRUNC);
	}else if(access == _RDONLY){
		fd = open(filename,O_RDONLY,0666);
	}else if(access == _RDWR){
		fd = open(filename,O_RDWR);
	}
	return fd;
}

int fileRead(int fd, unsigned char *buf, int size){
	return read(fd, buf, size);  
}  

int fileWrite(int fd, unsigned char *buf, int size){
	return write(fd, buf, size);  
}

int64_t fileSeek(int fd, int64_t pos, int whence){
    return lseek(fd, pos, whence);    
}

int fileClose(int fd){
	return close(fd);  
}

