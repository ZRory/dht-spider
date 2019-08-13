package vip.rory.dht.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import vip.rory.dht.dao.entity.Metadata;
import vip.rory.dht.dao.util.MyMapper;

/**
 * @author zhanghangtian
 * @date 2019年7月10日 下午4:18:24
 */
public interface MetadataMapper extends MyMapper<Metadata> {

    /**
     * @param code
     * @return
     */
    @Select("<script>SELECT id,NAME,announce,COMMENT,created_by createdBy,creation_time creationTime,info_hash infoHash,create_time createTime,update_time updateTime FROM metadata WHERE NAME LIKE #{code,jdbcType=VARCHAR};</script>")
    List<Metadata> selectUnreadableRecord(String code);

}
