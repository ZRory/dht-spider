package vip.rory.dht.dao.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vip.rory.dht.common.enumerate.CodeEnum;

/**
 * info_hash状态枚举类
 * 
 * @author zhanghangtian
 * @date 2019年7月10日 下午4:44:05
 */
@Getter
@AllArgsConstructor
public enum InfoHashStateEnum implements CodeEnum<Integer> {
    ERROR(0, "error"),
    GET_PEERS(1, "get_peers"),
    ANNOUNCE_PEER(2, "announce_peer"),
    DOWNLOADING(3, "downloading"),
    DOWNLOADED(4, "downloaded"),
    XLTD(5, "xltd");

    private Integer code;
    private String  message;

}
