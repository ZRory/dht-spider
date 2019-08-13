package vip.rory.dht.treating.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;
import vip.rory.dht.dao.entity.File;
import vip.rory.dht.dao.entity.InfoHash;
import vip.rory.dht.dao.entity.Metadata;
import vip.rory.dht.dao.enumerate.InfoHashStateEnum;
import vip.rory.dht.dao.mapper.FileMapper;
import vip.rory.dht.dao.mapper.InfoHashMapper;
import vip.rory.dht.dao.mapper.MetadataMapper;

/**
 * @author zhanghangtian
 * @date 2019年7月19日 上午9:14:38
 */
@Service
public class DispositionService {

    @Autowired
    private FileMapper     fileMapper;
    @Autowired
    private MetadataMapper metadataMapper;
    @Autowired
    private InfoHashMapper infoHashMapper;

    public void handleUnreadableFileRecord() {
        //String code = "%�%";
        String code = "%?%";
        List<File> files = fileMapper.selectUnreadableRecord(code);
        for (File file : files) {
            Long metadataId = file.getMetadataId();
            //删除file记录
            Example fileExample = new Example(File.class);
            Criteria fileCriteria = fileExample.createCriteria();
            fileCriteria.andEqualTo("metadataId", metadataId);
            fileMapper.deleteByExample(fileExample);
            //删除metadata记录
            Metadata metadata = metadataMapper.selectByPrimaryKey(metadataId);
            System.out.println("删除记录：" + metadata.getName() + ":" + metadata.getInfoHash());
            metadataMapper.deleteByPrimaryKey(metadataId);
            //更新infohash记录
            InfoHash infoHash = infoHashMapper.selectByPrimaryKey(metadata.getInfoHash());
            infoHash.setState(InfoHashStateEnum.ANNOUNCE_PEER.getCode());
            infoHash.setUpdateTime(LocalDateTime.now());
            infoHashMapper.updateByPrimaryKey(infoHash);
        }
    }

    public void handleUnreadableMetadataRecord() {
        //String code = "%�%";
        String code = "%?%";
        List<Metadata> metadatas = metadataMapper.selectUnreadableRecord(code);
        for (Metadata metadata : metadatas) {
            //删除file记录
            Example fileExample = new Example(File.class);
            Criteria fileCriteria = fileExample.createCriteria();
            fileCriteria.andEqualTo("metadataId", metadata.getId());
            fileMapper.deleteByExample(fileExample);
            //删除metadata记录
            System.out.println("删除记录：" + metadata.getName() + ":" + metadata.getInfoHash());
            metadataMapper.deleteByPrimaryKey(metadata.getId());
            //更新infohash记录
            InfoHash infoHash = infoHashMapper.selectByPrimaryKey(metadata.getInfoHash());
            infoHash.setState(InfoHashStateEnum.ANNOUNCE_PEER.getCode());
            infoHash.setUpdateTime(LocalDateTime.now());
            infoHashMapper.updateByPrimaryKey(infoHash);
        }
    }

    public void handleUnreadableMetadataRecordWithIds() {
        long[] metadataIds = { 34 };
        for (long metadataId : metadataIds) {
            Metadata metadata = metadataMapper.selectByPrimaryKey(metadataId);
            //删除file记录
            Example fileExample = new Example(File.class);
            Criteria fileCriteria = fileExample.createCriteria();
            fileCriteria.andEqualTo("metadataId", metadata.getId());
            fileMapper.deleteByExample(fileExample);
            //删除metadata记录
            System.out.println("删除记录：" + metadata.getName() + ":" + metadata.getInfoHash());
            metadataMapper.deleteByPrimaryKey(metadata.getId());
            //更新infohash记录
            InfoHash infoHash = infoHashMapper.selectByPrimaryKey(metadata.getInfoHash());
            infoHash.setState(InfoHashStateEnum.ANNOUNCE_PEER.getCode());
            infoHash.setUpdateTime(LocalDateTime.now());
            infoHashMapper.updateByPrimaryKey(infoHash);
        }
    }

}
