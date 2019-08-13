package vip.rory.dht.dao.util;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author zhanghangtian
 * @date 2019年7月9日 下午5:36:26
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {

}
