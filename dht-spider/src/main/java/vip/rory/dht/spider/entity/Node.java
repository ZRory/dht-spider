package vip.rory.dht.spider.entity;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import lombok.Data;
import vip.rory.dht.common.util.ByteUtils;

/**
 * @author zhanghangtian
 * @date 2019年7月8日 下午3:40:37
 */
@Data
public class Node implements Comparable<Node> {

    /**
     * nodeId.20个字节 BEP-3
     */
    public static final int NODE_ID_LENGTH = 20;

    /**
     * ip 长度.4个字节
     */
    public static final int IP_LENGTH      = 4;

    /**
     * 端口长度.2个字节
     */
    public static final int PORT_LENGTH    = 2;

    /**
     * Node节点总字节数
     */
    public static final int NODE_LENGTH    = NODE_ID_LENGTH + IP_LENGTH + PORT_LENGTH;

    /**
     * 节点ip地址
     */
    private String          ip;

    /**
     * 节点端口号
     */
    private Integer         port;

    /**
     * 节点nodeId 16进制字符串
     */
    private String          nodeIdHex;

    /**
     * 节点id
     */
    private byte[]          nodeId;

    /**
     * 节点信息encode
     */
    private byte[]          nodeInfo;

    /**
     * 最近联系时间
     */
    private LocalDateTime   lastContactTime;

    /**
     * rank
     */
    private Integer         rank;

    /**
     * 
     */
    public Node() {
        super();
    }

    /**
     * @param nodeInfo
     */
    public Node(byte[] nodeInfo) {
        super();
        this.nodeInfo = nodeInfo;
    }

    /**
     * @param ip
     * @param port
     */
    public Node(String ip, Integer port) {
        super();
        this.ip = ip;
        this.port = port;
    }

    /**
     * @param ip
     * @param port
     * @param nodeIdHex
     * @param nodeId
     * @param nodeInfo
     * @param lastContactTime
     * @param rank
     */
    public Node(String ip, Integer port, byte[] nodeId, LocalDateTime lastContactTime, Integer rank) {
        super();
        this.ip = ip;
        this.port = port;
        this.nodeIdHex = ByteUtils.byte2HexString(nodeId);
        this.nodeId = nodeId;
        this.lastContactTime = lastContactTime;
        this.rank = rank;
    }

    /**
     * 会同时设置nodeId
     * 
     * @param nodeIdHex the nodeIdHex to set
     */
    public void setNodeIdHex(String nodeIdHex) {
        this.nodeId = ByteUtils.hexStr2Bytes(nodeIdHex);
        this.nodeIdHex = nodeIdHex;
    }

    /**
     * 会同时设置nodeIdHex
     * 
     * @param nodeId the nodeId to set
     */
    public void setNodeId(byte[] nodeId) {
        this.nodeIdHex = ByteUtils.byte2HexString(nodeId);
        this.nodeId = nodeId;
    }

    /**
     * 将nodeInfo解码到基础信息
     * 
     * @return
     */
    public Node decode() {
        if (this.nodeInfo.length < NODE_LENGTH) {
            return null;
        }
        byte[] nodeId = ArrayUtils.subarray(this.nodeInfo, 0, 20);
        this.nodeId = nodeId;
        this.nodeIdHex = ByteUtils.byte2HexString(nodeId);
        this.ip = ByteUtils.bytes2Ip(ArrayUtils.subarray(this.nodeInfo, 20, 24));
        this.port = ByteUtils.bytes2Int(ArrayUtils.subarray(this.nodeInfo, 24, 26));
        return this;
    }

    /**
     * 将基础信息编码到nodeInfo
     * 
     * @return
     */
    public Node encode() {
        nodeInfo = new byte[NODE_LENGTH];
        byte[] ipBytes = ByteUtils.ip2Bytes(this.ip);
        //转换端口字节
        byte[] portBytes = ByteUtils.int2TwoBytes(this.port);
        System.arraycopy(nodeId, 0, nodeInfo, 0, 20);
        System.arraycopy(ipBytes, 0, nodeInfo, 20, 4);
        System.arraycopy(portBytes, 0, nodeInfo, 24, 2);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (!Arrays.equals(nodeId, other.nodeId))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + Arrays.hashCode(nodeId);
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        return result;
    }

    @Override
    public int compareTo(Node o) {
        if (this.equals(o)) {
            return 0;
        }
        return this.rank > o.rank ? -1 : 1;
    }

    public boolean check() {
        if ("0.0.0.0".equals(this.ip) || "0000000000000000000000000000000000000000".equals(this.nodeIdHex)
                || this.port.equals(0)) {
            return false;
        }
        return true;
    }

}
