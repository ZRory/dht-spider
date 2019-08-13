package vip.rory.dht.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import vip.rory.dht.dao.entity.File;
import vip.rory.dht.dao.util.MyMapper;

/**
 * @author zhanghangtian
 * @date 2019年7月10日 下午4:18:24
 */
public interface FileMapper extends MyMapper<File> {

    /**
     * @param code
     */
    @Select("<script>SELECT id id,file_name fileName,file_length fileLength,metadata_id metadataId,create_time createTime FROM FILE WHERE file_name LIKE #{code,jdbcType=VARCHAR} GROUP BY metadata_id;</script>")
    List<File> selectUnreadableRecord(String code);

}
