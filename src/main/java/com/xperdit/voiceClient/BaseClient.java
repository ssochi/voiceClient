package com.xperdit.voiceClient;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.xperdit.dto.Enums.MsgType;
import com.xperdit.dto.models.FileMessage;
import com.xperdit.dto.models.Message;
import com.xperdit.dto.models.TextMessage;
import com.xperdit.dto.utils.ModelUtils;
import com.xperdit.voiceClient.interfaces.VoiceClient;
import com.xperdit.voiceClient.interfaces.VoiceClientBuilder;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;


import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.NoSuchFileException;
import java.util.UUID;

/**
 * Copyright reserved by Beijing Muke Technology Co., Ltd. 8/20 0020.
 */
public class BaseClient implements VoiceClient{
    private  int PORT;
    private  String address;
    private  BaseClientHandler callback;
    private  long CONNECT_TIMEOUT = 30 * 1000L;
    private IoSession session;
    private OSSClient ossClient;
    private String aliyun_oss_endpoint ;
    private String aliyun_accessKeyId ;
    private String aliyun_accessKeySecret ;
    private String aliyun_oss_bucket;
    private String aliyun_oss_prefix;


    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public IoHandlerAdapter getCallback() {
        return callback;
    }

    public void setCallback(BaseClientHandler callback) {
        this.callback = callback;
    }

    public long getCONNECT_TIMEOUT() {
        return CONNECT_TIMEOUT;
    }

    public void setCONNECT_TIMEOUT(long CONNECT_TIMEOUT) {
        this.CONNECT_TIMEOUT = CONNECT_TIMEOUT;
    }

    public String getAliyun_oss_endpoint() {
        return aliyun_oss_endpoint;
    }

    public void setAliyun_oss_endpoint(String aliyun_oss_endpoint) {
        this.aliyun_oss_endpoint = aliyun_oss_endpoint;
    }

    public String getAliyun_accessKeyId() {
        return aliyun_accessKeyId;
    }

    public void setAliyun_accessKeyId(String aliyun_accessKeyId) {
        this.aliyun_accessKeyId = aliyun_accessKeyId;
    }

    public String getAliyun_accessKeySecret() {
        return aliyun_accessKeySecret;
    }

    public void setAliyun_accessKeySecret(String aliyun_accessKeySecret) {
        this.aliyun_accessKeySecret = aliyun_accessKeySecret;
    }

    public String getAliyun_oss_bucket() {
        return aliyun_oss_bucket;
    }

    public void setAliyun_oss_bucket(String aliyun_oss_bucket) {
        this.aliyun_oss_bucket = aliyun_oss_bucket;
    }

    public String getAliyun_oss_prefix() {
        return aliyun_oss_prefix;
    }

    public void setAliyun_oss_prefix(String aliyun_oss_prefix) {
        this.aliyun_oss_prefix = aliyun_oss_prefix;
    }

    public void init() {

        ossClient = new OSSClient(aliyun_oss_endpoint, aliyun_accessKeyId, aliyun_accessKeySecret);
        callback.setClient(this);
        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
        connector.getFilterChain().addLast( "codec",
                new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));

        connector.setHandler(callback);
        ConnectFuture future = connector.connect(new InetSocketAddress(
                address, PORT));
        future.awaitUninterruptibly();
        session = future.getSession();
    }


    public boolean sendMsg(Object obj){
        session.write(obj);
        return true;
    }
    public boolean sendTestMessage(Message message,String content){
        TextMessage textMessage = message.map(TextMessage.class);
        textMessage.setMsgType(MsgType.TEXT);
        textMessage.setContent(content);
        session.write(textMessage.toJson());
        return true;
    }
    /*
        you need to set message type before invoke this function .
     */
    public boolean sendTestMessage(TextMessage message){
        session.write(message.toJson());
        return true;
    }
    /*
        you need to set message type before invoke this function .
     */
    public boolean sendFileMessage(Message message,String FilePath){
        try {
            FileMessage fileMessage = message.map(FileMessage.class);
            String content = createFile(FilePath);
            fileMessage.setDownloadUrl(content);
            session.write(fileMessage.toJson());
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String createFile(String path) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(new File(path));
        String extension = fileExtension(path);
        String key = aliyun_oss_prefix+ModelUtils.createId()+extension;
        ossClient.putObject(aliyun_oss_bucket, key, inputStream);
        if (!key.startsWith("/"))
            key = "/"+key;
        return "http://"+aliyun_oss_bucket+"."+aliyun_oss_endpoint+key;
    }
    public boolean downloadFile(String url,String location){
        // 下载object到文件
        String key = keyFromUrl(url);
        if (key==null){
            return false;
        }
        ossClient.getObject(new GetObjectRequest(aliyun_oss_bucket, key), new File(location));
        return true;
    }


    private String keyFromUrl(String url)  {
        String key = null;
        if (url.contains(aliyun_oss_endpoint)){
            key =  url.substring(url.indexOf(aliyun_oss_endpoint)+aliyun_oss_endpoint.length(),url.length());
            if (key.startsWith("/")){
                key = key.substring(1,key.length());
            }
        }

        return key;
    }
    private String fileExtension(String path) {
        int index = path.lastIndexOf(".");
        if (index >= 0){
            return path.substring(index,path.length());
        }
        return "";

    }

    public boolean shutdown() {
        return false;
    }

    public boolean run() {
        return false;
    }

}
class BaseClientBuilder {
    BaseClient client;

    public BaseClient getClient() {
        return client;
    }

    public void setClient(BaseClient client) {
        this.client = client;
    }

    public BaseClientBuilder setPort(int port) {
        client.setPORT(port);
        return this;
    }

    public BaseClientBuilder setAddress(String address) {
        client.setAddress(address);
        return this;
    }

    public BaseClientBuilder setCallback(BaseClientHandler callback) {
        client.setCallback(callback);
        return this;
    }

    public BaseClientBuilder setCONNECT_TIMEOUT(long CONNECT_TIMEOUT) {
        client.setCONNECT_TIMEOUT(CONNECT_TIMEOUT);
        return this;
    }

    public BaseClientBuilder setAliyunOssEndPoint(String aliyun_oss_endpoint){
        client.setAliyun_oss_endpoint(aliyun_oss_endpoint);
        return this;
    }

    public BaseClientBuilder setAliyun_accessKeyId(String aliyun_accesskeyId){
        client.setAliyun_accessKeyId(aliyun_accesskeyId);
        return this;
    }

    public BaseClientBuilder setAliyun_accessKeySecret(String aliyun_accessKeySecret) {
        client.setAliyun_accessKeySecret(aliyun_accessKeySecret);
        return this;
    }



    public BaseClientBuilder setAliyun_oss_bucket(String aliyun_oss_bucket) {
        client.setAliyun_oss_bucket(aliyun_oss_bucket);
        return this;
    }



    public BaseClientBuilder setAliyun_oss_prefix(String aliyun_oss_prefix) {
        client.setAliyun_oss_prefix(aliyun_oss_prefix);
        return this;
    }

    public BaseClient build() {
        client.init();
        return client;
    }

    public static BaseClientBuilder getBuilder() {
        BaseClientBuilder builder = new BaseClientBuilder();
        builder.setClient(new BaseClient());
        return builder;
    }
}
