package vip.rory.dht.spider.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vip.rory.dht.common.enumerate.CodeEnum;

/**
 * DHT请求类型枚举类
 * 
 * @author zhanghangtian
 * @date 2019年6月28日 下午1:58:19
 */
@Getter
@AllArgsConstructor
public enum DhtMessageMethodEnum implements CodeEnum<String> {
    /**
     * The most basic query is a ping. "q" = "ping" A ping query has a single
     * argument, "id" the value is a 20-byte string containing the senders node
     * ID in network byte order. The appropriate response to a ping has a single
     * key "id" containing the node ID of the responding node.
     */
    PING("ping", "ping请求,检测节点是否在线"),
    /**
     * Find node is used to find the contact information for a node given its
     * ID. "q" == "find_node" A find_node query has two arguments, "id"
     * containing the node ID of the querying node, and "target" containing the
     * ID of the node sought by the queryer. When a node receives a find_node
     * query, it should respond with a key "nodes" and value of a string
     * containing the compact node info for the target node or the K (8) closest
     * good nodes in its own routing table.
     */
    FIND_NODE("find_node", "find_node请求,获取目标节点或其自身路由表中8个最近的好节点"),
    /**
     * Get peers associated with a torrent infohash. "q" = "get_peers" A
     * get_peers query has two arguments, "id" containing the node ID of the
     * querying node, and "info_hash" containing the infohash of the torrent. If
     * the queried node has peers for the infohash, they are returned in a key
     * "values" as a list of strings. Each string containing "compact" format
     * peer information for a single peer. If the queried node has no peers for
     * the infohash, a key "nodes" is returned containing the K nodes in the
     * queried nodes routing table closest to the infohash supplied in the
     * query. In either case a "token" key is also included in the return value.
     * The token value is a required argument for a future announce_peer query.
     * The token value should be a short binary string.
     */
    GET_PEERS("get_peers", "get_peers请求,查找拥有某种子的目标主机"),
    /**
     * Announce that the peer, controlling the querying node, is downloading a
     * torrent on a port. announce_peer has four arguments: "id" containing the
     * node ID of the querying node, "info_hash" containing the infohash of the
     * torrent, "port" containing the port as an integer, and the "token"
     * received in response to a previous get_peers query. The queried node must
     * verify that the token was previously sent to the same IP address as the
     * querying node. Then the queried node should store the IP address of the
     * querying node and the supplied port number under the infohash in its
     * store of peer contact information. There is an optional argument called
     * implied_port which value is either 0 or 1. If it is present and non-zero,
     * the port argument should be ignored and the source port of the UDP packet
     * should be used as the peer's port instead. This is useful for peers
     * behind a NAT that may not know their external port, and supporting uTP,
     * they accept incoming connections on the same port as the DHT port.
     */
    ANNOUNCE_PEER("announce_peer", "announce_peer请求,通知其他主机,该主机有对某种子的上传下载");

    private String code;
    private String message;
    
    public static DhtMessageMethodEnum getEnumByCode(String code) {
        for (DhtMessageMethodEnum e : DhtMessageMethodEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

}
