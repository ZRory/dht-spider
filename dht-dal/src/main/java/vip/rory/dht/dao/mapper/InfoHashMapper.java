package vip.rory.dht.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import vip.rory.dht.dao.entity.InfoHash;
import vip.rory.dht.dao.util.MyMapper;

/**
 * @author zhanghangtian
 * @date 2019年7月10日 下午4:18:24
 */
public interface InfoHashMapper extends MyMapper<InfoHash> {

    @Select("<script>SELECT info_hash infoHash, source_ip sourceIp, source_port sourcePort, state state, create_time createTime, update_time updateTime FROM info_hash WHERE state IN (1, 2) ORDER BY create_time ASC LIMIT 0, 36;</script>")
    public List<InfoHash> selectHashs();

}
