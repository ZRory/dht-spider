package vip.rory.dht.common.enumerate;

/**
 * 通用的带code/message的枚举接口
 * 
 * @author zhanghangtian
 * @date 2019年6月28日 下午1:58:05
 */
public interface CodeEnum<T> {

    T getCode();

    String getMessage();

}
