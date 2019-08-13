package vip.rory.dht.spider.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vip.rory.bencode.Bencode;
import vip.rory.dht.common.util.ByteUtils;
import vip.rory.dht.common.util.GenerateUtils;
import vip.rory.dht.dao.entity.InfoHash;
import vip.rory.dht.dao.enumerate.InfoHashStateEnum;
import vip.rory.dht.dao.mapper.InfoHashMapper;
import vip.rory.dht.spider.config.ThreadPool;
import vip.rory.dht.spider.entity.Node;
import vip.rory.dht.spider.entity.RoutingTable;
import vip.rory.dht.spider.entity.message.ResponseMessage;
import vip.rory.dht.spider.entity.message.body.AnnouncePeerResponse;
import vip.rory.dht.spider.entity.message.body.FindNodeRequest;
import vip.rory.dht.spider.entity.message.body.FindNodeResponse;
import vip.rory.dht.spider.entity.message.body.GetPeersRequest;
import vip.rory.dht.spider.entity.message.body.GetPeersResponse;
import vip.rory.dht.spider.entity.message.body.PingRequest;
import vip.rory.dht.spider.entity.message.body.PingResponse;
import vip.rory.dht.spider.enumerate.DhtParamEnum;

/**
 * @author zhanghangtian
 * @date 2019年7月9日 下午3:00:24
 */
@Service
public class MessageReceiveService {

    @Autowired
    private Node           localNode;
    @Autowired
    private Bencode        bencode;
    @Autowired
    private DatagramSocket datagramSocket;
    @Autowired
    private RoutingTable   routingTable;
    @Autowired
    private InfoHashMapper infoHashMapper;

    /**
     * 收到并回应ping请求
     * 
     * @param packet
     * @param decodeMap
     */
    public void ping(DatagramPacket packet, Map<String, Object> decodeMap) {
        @SuppressWarnings("unchecked")
        Map<String, String> responseMap = (Map<String, String>) decodeMap.get(DhtParamEnum.ARGUMENTS.getCode());
        //构建node
        Node node = new Node(packet.getAddress().getHostAddress(), packet.getPort(),
                ByteUtils.string2Bytes(responseMap.get(PingRequest.ID)), LocalDateTime.now(), 1).encode();
        //将返回数据者加入我们自己的路由表中
        routingTable.add(node);
        PingResponse pingResponse = new PingResponse();
        pingResponse.setId(localNode.getNodeId());
        ResponseMessage<PingResponse> responseMessage = new ResponseMessage<PingResponse>(pingResponse);
        responseMessage.setT((String) decodeMap.get(DhtParamEnum.TRANSACTIONID.getCode()));
        sendMessage(node, responseMessage);
    }

    /**
     * 收到并回应get_peers请求 权重 100
     * 
     * @param packet
     * @param decodeMap
     */
    public void getPeers(DatagramPacket packet, Map<String, Object> decodeMap) {
        @SuppressWarnings("unchecked")
        Map<String, String> responseMap = (Map<String, String>) decodeMap.get(DhtParamEnum.ARGUMENTS.getCode());
        //构建node
        Node node = new Node(packet.getAddress().getHostAddress(), packet.getPort(),
                ByteUtils.string2Bytes(responseMap.get(GetPeersRequest.ID)), LocalDateTime.now(), 100).encode();
        //将返回数据者加入我们自己的路由表中
        routingTable.add(node);
        String infoHashString = ByteUtils.string2UpperNodeId((responseMap.get(GetPeersRequest.INFO_HASH)));
        if (!infoHashMapper.existsWithPrimaryKey(infoHashString)) {
            try {
                InfoHash infohash = new InfoHash();
                infohash.setInfoHash(infoHashString);
                infohash.setCreateTime(LocalDateTime.now());
                infohash.setSourceIp(node.getIp());
                infohash.setSourcePort(node.getPort());
                infohash.setUpdateTime(LocalDateTime.now());
                infohash.setState(InfoHashStateEnum.GET_PEERS.getCode());
                infoHashMapper.insert(infohash);
            } catch (Exception e) {
            }
        }
        //构建返回值
        GetPeersResponse getPeersResponse = new GetPeersResponse();
        getPeersResponse.setId(localNode.getNodeId());
        getPeersResponse.setToken(GenerateUtils.generateToken());
        getPeersResponse.setNodes(routingTable.getEightNodes(false));
        ResponseMessage<GetPeersResponse> responseMessage = new ResponseMessage<GetPeersResponse>(getPeersResponse);
        responseMessage.setT((String) decodeMap.get(DhtParamEnum.TRANSACTIONID.getCode()));
        sendMessage(node, responseMessage);
    }

    /**
     * find_node请求 权重10
     * 
     * @param packet
     * @param decodeMap
     */
    public void findNode(DatagramPacket packet, Map<String, Object> decodeMap) {
        @SuppressWarnings("unchecked")
        Map<String, String> responseMap = (Map<String, String>) decodeMap.get(DhtParamEnum.ARGUMENTS.getCode());
        //构建node
        Node node = new Node(packet.getAddress().getHostAddress(), packet.getPort(),
                ByteUtils.string2Bytes(responseMap.get(FindNodeRequest.ID)), LocalDateTime.now(), 10).encode();
        //将返回数据者加入我们自己的路由表中
        routingTable.add(node);
        FindNodeResponse findNodeResponse = new FindNodeResponse();
        findNodeResponse.setId(localNode.getNodeId());
        findNodeResponse.setNodes(routingTable.getEightNodes(true));
        ResponseMessage<FindNodeResponse> responseMessage = new ResponseMessage<FindNodeResponse>(findNodeResponse);
        responseMessage.setT((String) decodeMap.get(DhtParamEnum.TRANSACTIONID.getCode()));
        sendMessage(node, responseMessage);
    }

    /**
     * anncunce_peer 权重300
     * 
     * @param packet
     * @param decodeMap
     */
    public void announcePeer(DatagramPacket packet, Map<String, Object> decodeMap) {
        @SuppressWarnings("unchecked")
        Map<String, String> responseMap = (Map<String, String>) decodeMap.get(DhtParamEnum.ARGUMENTS.getCode());
        //构建node
        Node node = new Node(packet.getAddress().getHostAddress(), packet.getPort(),
                ByteUtils.string2Bytes(responseMap.get(GetPeersRequest.ID)), LocalDateTime.now(), 300).encode();
        //将返回数据者加入我们自己的路由表中
        routingTable.add(node);
        //存储info_hash
        String infoHashString = ByteUtils.string2UpperNodeId(responseMap.get(GetPeersRequest.INFO_HASH));
        InfoHash tempHash = infoHashMapper.selectByPrimaryKey(infoHashString);
        if (tempHash == null) {
            //插入
            InfoHash infohash = new InfoHash();
            infohash.setInfoHash(infoHashString);
            infohash.setSourceIp(node.getIp());
            infohash.setSourcePort(node.getPort());
            infohash.setState(InfoHashStateEnum.ANNOUNCE_PEER.getCode());
            infohash.setCreateTime(LocalDateTime.now());
            infohash.setUpdateTime(LocalDateTime.now());
            infoHashMapper.insert(infohash);
        } else if (tempHash.getState().equals(InfoHashStateEnum.GET_PEERS.getCode())) {
            //只有状态是GET_PEERS才允许更新
            tempHash.setSourceIp(node.getIp());
            tempHash.setSourcePort(node.getPort());
            tempHash.setState(InfoHashStateEnum.ANNOUNCE_PEER.getCode());
            tempHash.setUpdateTime(LocalDateTime.now());
            infoHashMapper.updateByPrimaryKeySelective(tempHash);
        }
        AnnouncePeerResponse announcePeerResponse = new AnnouncePeerResponse();
        announcePeerResponse.setId(localNode.getNodeId());
        ResponseMessage<AnnouncePeerResponse> responseMessage = new ResponseMessage<>(announcePeerResponse);
        sendMessage(node, responseMessage);
    }

    private void sendMessage(Node node, ResponseMessage<?> message) {
        ThreadPool.messageSend.execute(new Runnable() {
            @Override
            public void run() {
                //Bencode要发送的数据
                byte[] data = bencode.encode(message);
                //System.out.println(new String(data, Charset.forName("ISO-8859-1")));
                try {
                    datagramSocket.send(
                            new DatagramPacket(data, data.length, InetAddress.getByName(node.getIp()), node.getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
