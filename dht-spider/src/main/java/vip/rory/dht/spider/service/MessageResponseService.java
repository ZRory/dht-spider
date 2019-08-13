package vip.rory.dht.spider.service;

import java.net.DatagramPacket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vip.rory.dht.common.util.ByteUtils;
import vip.rory.dht.spider.entity.Node;
import vip.rory.dht.spider.entity.RoutingTable;
import vip.rory.dht.spider.entity.message.body.FindNodeResponse;
import vip.rory.dht.spider.entity.message.body.PingResponse;
import vip.rory.dht.spider.enumerate.DhtParamEnum;

/**
 * @author zhanghangtian
 * @date 2019年7月9日 上午10:47:31
 */
@Service
public class MessageResponseService {

    @Autowired
    private RoutingTable         routingTable;
    @Autowired
    private MessageSenderService messageSenderService;

    /**
     * ping响应 权重1
     * 
     * @param packet
     * @param decodeMap
     */
    public void ping(DatagramPacket packet, Map<String, Object> decodeMap) {
        @SuppressWarnings("unchecked")
        Map<String, String> responseMap = (Map<String, String>) decodeMap.get(DhtParamEnum.RESPONSE.getCode());
        Node node = new Node(packet.getAddress().getHostAddress(), packet.getPort(),
                ByteUtils.string2Bytes(responseMap.get(PingResponse.ID)), LocalDateTime.now(), 1).encode();
        //将返回数据者加入我们自己的路由表中
        routingTable.add(node);
    }

    /**
     * find_node响应 权重10
     * 
     * @param packet
     * @param decodeMap
     */
    public void findNode(DatagramPacket packet, Map<String, Object> decodeMap) {
        //解析数据
        @SuppressWarnings("unchecked")
        Map<String, String> responseMap = (Map<String, String>) decodeMap.get(DhtParamEnum.RESPONSE.getCode());
        //将返回数据者加入我们自己的路由表中
        Node node = new Node(packet.getAddress().getHostAddress(), packet.getPort(),
                ByteUtils.string2Bytes(responseMap.get(FindNodeResponse.ID)), LocalDateTime.now(), 10).encode();
        routingTable.add(node);
        //解析其返回的nodes节点
        String nodesStr = responseMap.get(FindNodeResponse.NODES);
        List<Node> nodes = resolveNodes(nodesStr);
        //对返回的nodes继续发送find_node请求
        for (Node tempNode : nodes) {
            messageSenderService.findNode(tempNode);
        }
    }

    private List<Node> resolveNodes(String nodesStr) {
        List<Node> nodes = new ArrayList<Node>();
        if (StringUtils.isEmpty(nodesStr)) {
            return nodes;
        }
        byte[] nodesBytes = ByteUtils.string2Bytes(nodesStr);
        if (nodesBytes.length % 26 != 0) {
            return nodes;
        }

        for (int i = 0; i < nodesBytes.length; i += 26) {
            byte[] nodeBytesInfo = new byte[26];
            System.arraycopy(nodesBytes, i, nodeBytesInfo, 0, 26);
            Node node = new Node(nodeBytesInfo).decode();
            if (!node.check()) {
                //node检查不通过
                continue;
            }
            node.setLastContactTime(LocalDateTime.now());
            nodes.add(node);
        }
        return nodes;
    }

}
