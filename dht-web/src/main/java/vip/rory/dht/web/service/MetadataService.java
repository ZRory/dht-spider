package vip.rory.dht.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import tk.mybatis.mapper.entity.Example;
import vip.rory.dht.dao.entity.Metadata;
import vip.rory.dht.dao.mapper.MetadataMapper;

/**
 * @author zhanghangtian
 * @date 2019年7月15日 下午12:15:25
 */
@Service
public class MetadataService {

    @Autowired
    private MetadataMapper metadataMapper;

    /**
     * @param page
     * @param rows
     * @param keyword
     * @param keyword
     * @param sortField
     * @param sortOrder
     * @return
     */
    public PageInfo<Metadata> selectPage(Integer page, Integer rows, Long id, String magnet, String keyword,
                                         String sortField, String sortOrder) {
        PageHelper.startPage(page, rows);
        Example example = new Example(Metadata.class);
        if (sortField != null && sortOrder != null) {
            if (sortOrder.equalsIgnoreCase("asc")) {
                example.orderBy(sortField).asc();
            } else {
                example.orderBy(sortField).desc();
            }
        } else {
            example.orderBy("id").asc();
        }
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(keyword)) {
            if (!keyword.contains(" ")) {
                criteria.andLike("name", "%" + keyword + "%");
            } else {
                String[] split = keyword.split(" ");
                String keyString = "%";
                for (String string : split) {
                    keyString = keyString + string + "%";
                }
                criteria.andLike("name", keyString);
            }
        }
        if (id != null) {
            criteria.andEqualTo("id", id);
        }
        if (!StringUtils.isEmpty(magnet)) {
            magnet = magnet.replaceAll("magnet\\:\\?xt=urn\\:btih\\:", "");
            if (magnet.length() < 40) {
                return null;
            }
            criteria.andEqualTo("infoHash", magnet);
        }
        List<Metadata> metadatas = metadataMapper.selectByExample(example);
        PageInfo<Metadata> metadataPageInfo = new PageInfo<>(metadatas);
        return metadataPageInfo;
    }

}
