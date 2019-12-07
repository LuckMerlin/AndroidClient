package com.merlin.oksocket;

import com.merlin.client.Client;
import com.merlin.server.Frame;

public interface OnFrameReceive {
    void onFrameReceived(Frame frame, Client client);
}
