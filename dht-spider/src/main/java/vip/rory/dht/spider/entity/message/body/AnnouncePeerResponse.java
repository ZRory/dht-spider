package vip.rory.dht.spider.entity.message.body;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghangtian
 * @date 2019年7月1日 上午10:16:02
 */
@Getter
@Setter
public class AnnouncePeerResponse {

    /**
     * queried nodes id
     */
    private byte[] id;

}
