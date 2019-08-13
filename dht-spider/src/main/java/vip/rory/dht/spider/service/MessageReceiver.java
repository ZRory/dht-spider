package vip.rory.dht.spider.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vip.rory.bencode.Bencode;
import vip.rory.bencode.Type;
import vip.rory.dht.common.util.ByteUtils;
import vip.rory.dht.spider.config.ThreadPool;
import vip.rory.dht.spider.entity.SendTable;
import vip.rory.dht.spider.entity.message.body.FindNodeResponse;
import vip.rory.dht.spider.entity.message.body.GetPeersResponse;
import vip.rory.dht.spider.entity.message.body.PingResponse;
import vip.rory.dht.spider.enumerate.DhtMessageMethodEnum;
import vip.rory.dht.spider.enumerate.DhtMessageTypeEnum;
import vip.rory.dht.spider.enumerate.DhtParamEnum;

/**
 * @author zhanghangtian
 * @date 2019年7月1日 下午3:09:35
 */
@Slf4j
@Service
public class MessageReceiver {

    @Autowired
    private DatagramSocket         datagramSocket;
    @Autowired
    private Bencode                bencode;
    @Autowired
    private MessageResponseService messageResponseService;
    @Autowired
    private MessageReceiveService  messageReceiveService;
    @Autowired
    private SendTable              sendTable;

    public void listen() {
        ThreadPool.messageReceive.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] recvData = new byte[2048];
                    try {
                        DatagramPacket packet = new DatagramPacket(recvData, recvData.length);
                        datagramSocket.receive(packet);
                        messageAdapter(packet);
                    } catch (Exception e) {
                        //log.error(e.getMessage(), e);
                    }
                }
            }
        });

    }

    private void messageAdapter(DatagramPacket packet) {
        byte[] data = packet.getData();
        Map<String, Object> decodeMap = null;
        try {
            decodeMap = bencode.decode(data, Type.DICTIONARY);
        } catch (Exception e) {
            //垃圾数据会导致解码异常
            return;
        }
        //判断消息类型
        DhtMessageTypeEnum dhtMessageTypeEnum = DhtMessageTypeEnum
                .getEnumByCode((String) decodeMap.get(DhtParamEnum.TYPE.getCode()));
        if (dhtMessageTypeEnum == null) {
            //数据包异常
            return;
        }
        switch (dhtMessageTypeEnum) {
            case REQUEST:
                handleRequest(packet, decodeMap);
                break;
            case RESPONSE:
                handleResponse(packet, decodeMap);
                break;
            case ERROR:
                //异常消息
                break;
            default:
                //无效消息
                break;
        }

    }

    private void handleRequest(DatagramPacket packet, Map<String, Object> decodeMap) {
        DhtMessageMethodEnum methodEnum = DhtMessageMethodEnum
                .getEnumByCode((String) decodeMap.get(DhtParamEnum.METHOD.getCode()));
        switch (methodEnum) {
            case PING:
                messageReceiveService.ping(packet, decodeMap);
                break;
            case FIND_NODE:
                messageReceiveService.findNode(packet, decodeMap);
                break;
            case ANNOUNCE_PEER:
                messageReceiveService.announcePeer(packet, decodeMap);
                break;
            case GET_PEERS:
                messageReceiveService.getPeers(packet, decodeMap);
                break;
            default:
                // TODO
                break;
        }
    }

    private void handleResponse(DatagramPacket packet, Map<String, Object> decodeMap) {
        //响应消息--取出transactionId进行匹配
        Object transactionId = decodeMap.get(DhtParamEnum.TRANSACTIONID.getCode());
        //删除并取出
        String methodCode = sendTable.remove(transactionId);
        if (methodCode == null) {
            //如果id不存在--手动解析
            @SuppressWarnings("unchecked")
            Map<String, String> responseMap = (Map<String, String>) decodeMap.get(DhtParamEnum.RESPONSE.getCode());
            if (responseMap.get(GetPeersResponse.NODES) != null && responseMap.get(GetPeersResponse.TOKEN) != null
                    && responseMap.get(GetPeersResponse.ID) != null) {
                methodCode = DhtMessageMethodEnum.GET_PEERS.getCode();
            } else if (responseMap.get(FindNodeResponse.ID) != null
                    && responseMap.get(FindNodeResponse.NODES) != null) {
                methodCode = DhtMessageMethodEnum.FIND_NODE.getCode();
            } else if (responseMap.get(PingResponse.ID) != null) {
                methodCode = DhtMessageMethodEnum.PING.getCode();
            } else {
                log.error("TransactionId不存在或已删除且无法匹配结果：{}", ByteUtils.bytes2String(packet.getData()));
                return;
            }
            log.info("TransactionId不存在或已删除--匹配结果：{}", methodCode);
        }
        DhtMessageMethodEnum dhtMessageMethodEnum = DhtMessageMethodEnum.getEnumByCode(methodCode);
        switch (dhtMessageMethodEnum) {
            case PING:
                messageResponseService.ping(packet, decodeMap);
                break;
            case FIND_NODE:
                messageResponseService.findNode(packet, decodeMap);
                break;
            case ANNOUNCE_PEER:
                // TODO
                break;
            case GET_PEERS:
                // TODO
                break;
            default:
                // TODO
                break;
        }
    }

}
