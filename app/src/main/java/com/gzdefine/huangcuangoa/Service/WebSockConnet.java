package com.gzdefine.huangcuangoa.Service;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.simple.eventbus.EventBus;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author max
 *
 * This example demonstrates how to create a websocket connection to a server. Only the most important callbacks are overloaded. */
public class WebSockConnet extends WebSocketClient {

    public WebSockConnet(URI serverUri , Draft draft ) {
        super( serverUri, draft );
    }

    public WebSockConnet(URI serverURI ) {
        super( serverURI );
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        System.out.println( "打开连接" );
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }


    @Override
    public void onMessage( String message ) {
        EventBus.getDefault().post(message);
       Log.d("maxMessage", "收到信息: " + message );
    }


    @Override
    public void onFragment( Framedata fragment ) {
        System.out.println( "received fragment: " + new String( fragment.getPayloadData().array() ) );
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println( "连接关闭 " + ( remote ? "服务器" : "我们" ) );
    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

    public static void main( String[] args ) throws URISyntaxException {
//        WebSockConnet c = new WebSockConnet( new URI("ws://192.168.1.209:8080/webpush/oaSocket?userId=1" ),new Draft_17());
//        try {
//            c.connectBlocking();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        c.send("程序员好帅");
    }
}