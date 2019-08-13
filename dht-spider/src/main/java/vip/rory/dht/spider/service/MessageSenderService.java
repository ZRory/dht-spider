package vip.rory.dht.spider.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vip.rory.bencode.Bencode;
import vip.rory.dht.common.util.GenerateUtils;
import vip.rory.dht.spider.config.ThreadPool;
import vip.rory.dht.spider.entity.Node;
import vip.rory.dht.spider.entity.SendTable;
import vip.rory.dht.spider.entity.message.RequestMessage;
import vip.rory.dht.spider.entity.message.body.FindNodeRequest;
import vip.rory.dht.spider.entity.message.body.PingRequest;

/**
 * @author zhanghangtian
 * @date 2019年7月8日 下午5:15:37
 */
@Service
public class MessageSenderService {

    @Autowired
    private DatagramSocket datagramSocket;
    @Autowired
    private Node           localNode;
    @Autowired
    private Bencode        bencode;
    @Autowired
    private SendTable      sendTable;

    /**
     * 对指定节点发送ping请求
     * 
     * @param node
     */
    public void ping(Node node) {
        if (!node.check()) {
            //node检查不通过
            return;
        }
        //构建ping参数
        PingRequest pingRequest = new PingRequest();
        pingRequest.setId(localNode.getNodeId());
        RequestMessage<PingRequest> requestMessage = new RequestMessage<PingRequest>(pingRequest);
        sendMessage(node, requestMessage);
    }

    /**
     * 对指定节点发送find_node请求
     * 
     * @param node
     */
    public void findNode(Node node) {
        if (!node.check()) {
            //node检查不通过
            return;
        }
        //构建find_node消息体
        FindNodeRequest findNodeRequest = new FindNodeRequest();
        findNodeRequest.setId(localNode.getNodeId());
        findNodeRequest.setTarget(GenerateUtils.generateNodeId());
        RequestMessage<FindNodeRequest> findNodeMessage = new RequestMessage<FindNodeRequest>(findNodeRequest);
        ThreadPool.findNodeSend.execute(new Runnable() {
            @Override
            public void run() {
                //Bencode要发送的数据
                byte[] data = bencode.encode(findNodeMessage);
                //System.out.println(new String(data, Charset.forName("ISO-8859-1")));
                try {
                    Thread.sleep(20);
                    //将消息放入发送列表中
                    sendTable.put(findNodeMessage.getT(), findNodeMessage.getQ());
                    datagramSocket.send(
                            new DatagramPacket(data, data.length, InetAddress.getByName(node.getIp()), node.getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendMessage(Node node, RequestMessage<?> message) {
        ThreadPool.messageSend.execute(new Runnable() {
            @Override
            public void run() {
                //Bencode要发送的数据
                byte[] data = bencode.encode(message);
                //System.out.println(new String(data, Charset.forName("ISO-8859-1")));
                try {
                    //将消息放入发送列表中
                    sendTable.put(message.getT(), message.getQ());
                    datagramSocket.send(
                            new DatagramPacket(data, data.length, InetAddress.getByName(node.getIp()), node.getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
