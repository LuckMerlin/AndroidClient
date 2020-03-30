package com.merlin.oksocket;

import com.merlin.client.__Client;
import com.merlin.server.Frame;

public interface OnFrameReceive {
    void onFrameReceived(Frame frame, __Client client);
}
