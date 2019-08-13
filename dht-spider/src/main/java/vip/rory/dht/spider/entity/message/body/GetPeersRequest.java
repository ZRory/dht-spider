package vip.rory.dht.spider.entity.message.body;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghangtian
 * @date 2019年6月28日 下午7:09:55
 */
@Getter
@Setter
public class GetPeersRequest {

    public static final String ID = "id";
    public static final String INFO_HASH = "info_hash";
    /**
     * querying nodes id
     */
    private byte[] id;
    /**
     * id of target node
     */
    private String infoHash;

}
