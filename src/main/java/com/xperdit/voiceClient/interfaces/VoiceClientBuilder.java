package com.xperdit.voiceClient.interfaces;

import org.apache.mina.core.service.IoHandlerAdapter;

/**
 * Copyright reserved by Beijing Xperdit Technology Co., Ltd. 8/27 0027.
 */
public interface VoiceClientBuilder {
    public VoiceClientBuilder setPort(int port);
    public VoiceClientBuilder setAddress(String address);
    public VoiceClientBuilder setCallback(IoHandlerAdapter callback);
    public VoiceClientBuilder setCONNECT_TIMEOUT(long CONNECT_TIMEOUT);
    public VoiceClient build();
}
