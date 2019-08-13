package vip.rory.dht.web.service;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import tk.mybatis.mapper.entity.Example;
import vip.rory.dht.dao.entity.File;
import vip.rory.dht.dao.mapper.FileMapper;

/**
 * @author zhanghangtian
 * @date 2019年7月15日 下午12:15:25
 */
@Service
public class FileService {

    @Autowired
    private FileMapper fileMapper;

    public List<File> listFiles(Long metadataId) {
        Example example = new Example(File.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("metadataId", metadataId);
        List<File> metadatas = fileMapper.selectByExample(example);
        return metadatas;
    }

    /**
     * @param page
     * @param rows
     * @param keyword
     * @param sortField
     * @param sortOrder
     * @return
     */
    public Object selectPage(@NotNull Integer page, @NotNull Integer rows, String keyword, String sortField,
                             String sortOrder) {
        PageHelper.startPage(page, rows);
        Example example = new Example(File.class);
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
                criteria.andLike("fileName", "%" + keyword + "%");
            } else {
                String[] split = keyword.split(" ");
                String keyString = "%";
                for (String string : split) {
                    keyString = keyString + string + "%";
                }
                criteria.andLike("fileName", keyString);
            }
        }
        List<File> files = fileMapper.selectByExample(example);
        PageInfo<File> filesPageInfo = new PageInfo<>(files);
        return filesPageInfo;
    }

}
