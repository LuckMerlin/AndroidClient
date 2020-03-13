package com.merlin.socket;

public interface OnConnectChange {
    int CONNECT_SUCCEED = 1110;
    void onSocketConnectChanged(boolean connected, int what, Socket socket);
}
