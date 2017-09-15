package com.xperdit.voiceClient;


import com.xperdit.dto.Enums.MsgType;
import com.xperdit.dto.models.Message;
import com.xperdit.dto.models.UserInfo;
import com.xperdit.dto.utils.ModelFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Copyright reserved by Beijing Muke Technology Co., Ltd. 8/20 0020.
 */
public class test {
    public static void main(String[] args) throws IOException {
        BaseClient client = BaseClientBuilder.getBuilder()
                .setAddress("104.238.151.166")
                .setPort(10000)
                .setCallback(new BaseClientHandler())
                .setAliyunOssEndPoint("oss-cn-shanghai.aliyuncs.com")
                .setAliyun_accessKeyId("LTAIregA6tNVCpbl")
                .setAliyun_accessKeySecret("I9aSDnrkhGyY1JzdZMFj0UB4lnphCB")
                .setAliyun_oss_bucket("test-what")
                .setAliyun_oss_prefix("voice-test/")
                .build();

        Message message = ModelFactory.create(Message.class);
        message.setCreate_at(new Date());

//        TextMessage message = ModelFactory.create(TextMessage.class);
//        message.setContent("hello");
//        message.setCreate_at(new Date());

        UserInfo info = ModelFactory.create(UserInfo.class);
        info.setUid("123");
        info.setToken("123");
        info.setScreenName("tt");
        message.setPublisher(info);
        info = ModelFactory.create(UserInfo.class);
        info.setUid("456");
        info.setToken("123");
        info.setScreenName("gai");
        List<UserInfo> userInfos = new ArrayList<>();
        userInfos.add(info);

        message.setReceivers(userInfos);

        client.sendTestMessage(message,"hello");
        message.setMsgType(MsgType.PICTURE);
        client.sendFileMessage(message,"tmp.png");

//        message.setReceivers(userInfos);
//        message.setMsgType(MsgType.TEXT);
//        client.sendMsg(message.toJson());
    }
}
