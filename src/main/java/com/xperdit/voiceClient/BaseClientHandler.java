package com.xperdit.voiceClient;

import com.aliyun.oss.OSSClient;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class BaseClientHandler extends IoHandlerAdapter {

    BaseClient client ;

    public BaseClient getClient() {
        return client;
    }

    public void setClient(BaseClient client) {
        this.client = client;
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        String str = message.toString();
        System.out.println(str);
    }
}