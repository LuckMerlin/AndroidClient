package com.merlin.task;

public interface Status {
    int INVALID=997;
    int UNKNOWN=998;
    int FINISH_SERVICE_FAIL=999;
    int FINISH_START_FAIL=1000;
    int FINISH_EXCEPTION=1001;
    int FINISH_WRITE_EXCEPTION=1002;
    int FINISH_SUCCEED=1003;
    int FINISH_CANCEL=1004;
    int START=1005;
    int DOWNLOADING=1006;
    int PAUSE=1007;
    int RESTART=1008;
    int WAITING=1009;
}
