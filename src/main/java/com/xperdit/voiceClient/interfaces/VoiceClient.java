package com.xperdit.voiceClient.interfaces;

import org.apache.mina.core.service.IoHandlerAdapter;

import java.io.IOException;

/**
 * Copyright reserved by Beijing Muke Technology Co., Ltd. 8/20 0020.
 */
public interface VoiceClient {
    public boolean sendMsg(Object obj) throws IOException;
    public boolean shutdown();
    public boolean run();
}
