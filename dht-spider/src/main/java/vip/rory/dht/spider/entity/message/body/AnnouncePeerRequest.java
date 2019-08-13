package vip.rory.dht.spider.entity.message.body;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghangtian
 * @date 2019年7月1日 上午9:28:03
 */
@Getter
@Setter
public class AnnouncePeerRequest {

    /**
     * querying nodes id
     */
    private byte[]  id;

    /**
     * There is an optional argument called implied_port which value is either 0
     * or 1. If it is present and non-zero, the port argument should be ignored
     * and the source port of the UDP packet should be used as the peer's port
     * instead.
     */
    private Integer impliedPort;

    /**
     * 20-byte info hash of target torrent
     */
    private String  infoHash;

    /**
     * port number
     */
    private Integer port;
    /**
     * opaque token
     */
    private String  token;

}
