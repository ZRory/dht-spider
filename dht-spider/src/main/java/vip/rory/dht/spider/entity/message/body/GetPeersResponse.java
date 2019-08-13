package vip.rory.dht.spider.entity.message.body;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghangtian
 * @date 2019年7月1日 上午9:03:04
 */
@Getter
@Setter
public class GetPeersResponse {

    public static final String ID     = "id";
    public static final String TOKEN  = "token";
    public static final String VALUES = "values";
    public static final String NODES  = "nodes";
    /**
     * queried nodes id
     */
    private byte[]             id;
    /**
     * opaque write token
     */
    private String             token;
    /**
     * peer info strings
     */
    private String[]           values;
    /**
     * nodes
     */
    private byte[]             nodes;

}
