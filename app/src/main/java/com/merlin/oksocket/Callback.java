package com.merlin.oksocket;

public interface Callback {
    int REQUEST_SUCCEED=10000;
    int REQUEST_FAILED_SEND_FAIL=10001;
    int REQUEST_FAILED_ARG_INVALID=10002;
    int REQUEST_FAILED_TIMEOUT=10003;
    int REQUEST_CANCELED=10004;
}
