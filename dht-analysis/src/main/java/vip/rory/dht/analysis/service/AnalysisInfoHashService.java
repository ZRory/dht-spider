package vip.rory.dht.analysis.service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vip.rory.bencode.Bencode;
import vip.rory.bencode.Type;
import vip.rory.dht.dao.entity.File;
import vip.rory.dht.dao.entity.InfoHash;
import vip.rory.dht.dao.entity.Metadata;
import vip.rory.dht.dao.enumerate.InfoHashStateEnum;
import vip.rory.dht.dao.mapper.FileMapper;
import vip.rory.dht.dao.mapper.InfoHashMapper;
import vip.rory.dht.dao.mapper.MetadataMapper;

/**
 * @author zhanghangtian
 * @date 2019年7月10日 下午4:25:39
 */
@Slf4j
@Service
@EnableScheduling
public class AnalysisInfoHashService {

    @Autowired
    private InfoHashMapper infoHashMapper;

    @Autowired
    private Bencode        bencode;

    @Autowired
    private MetadataMapper metadataMapper;

    @Autowired
    private FileMapper     fileMapper;

    @SuppressWarnings("unchecked")
    private void analysisFile(Map<String, Object> infoMap, Metadata metadata, List<File> result, LocalDateTime now,
                              boolean useUtf8Flag) {
        if (infoMap.get("length") != null) {
            File tempFile = new File();
            tempFile.setCreateTime(now);
            tempFile.setFileLength((long) infoMap.get("length"));
            List<String> path = (List<String>) infoMap.get("path");
            if (useUtf8Flag && infoMap.get("path.utf-8") != null) {
                path = (List<String>) infoMap.get("path.utf-8");
            }
            String nameString = null;
            if (path == null) {
                nameString = (String) infoMap.get("name");
                if (useUtf8Flag && infoMap.get("name.utf-8") != null) {
                    nameString = (String) infoMap.get("name.utf-8");
                }
            } else {
                nameString = path.get(path.size() - 1);
            }
            tempFile.setFileName(nameString);
            if (metadata != null) {
                tempFile.setMetadataId(metadata.getId());
            }
            if (!tempFile.getFileName().contains("padding_file")) {
                result.add(tempFile);
            }
        } else {
            ArrayList<Map<String, Object>> filesList = (ArrayList<Map<String, Object>>) infoMap.get("files");
            for (Map<String, Object> fileMap : filesList) {
                analysisFile(fileMap, metadata, result, now, useUtf8Flag);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Scheduled(initialDelay = 20 * 1000, fixedDelay = 10 * 1000)
    public void analysisByThunder() throws Throwable {
        log.info("Thunder Start");
        //执行停止脚本
        Runtime.getRuntime().exec("TASKKILL /IM DownloadSDKServer.exe /F");
        Runtime.getRuntime().exec("TASKKILL /IM Thunder.exe /F");
        Thread.sleep(2 * 1000);
        //C:\Users\Administrator\AppData\Local\Temp\2\Thunder Network\Thunder7.9
        java.io.File file = new java.io.File(
                System.getProperty("user.home") + "\\AppData\\Local\\Temp\\2\\Thunder Network\\ThunderVIP");
        java.io.File[] files = file.listFiles();
        if (files != null) {
            for (java.io.File torrentFile : files) {
                try {
                    if (torrentFile.getName().contains("crashinfo")) {
                        continue;
                    }
                    //                    if (torrentFile.getName().contains(")")) {
                    //                        log.info("删除异常文件：{}", torrentFile.getName());
                    //                        torrentFile.delete();
                    //                        continue;
                    //                    }
                    String infohashString = torrentFile.getName().split("\\.")[0].toUpperCase();
                    InfoHash infoHash = infoHashMapper.selectByPrimaryKey(infohashString);
                    if (torrentFile.getName().contains("xltd")) {
                        //未完全下载 置为XLTD状态
                        torrentFile.delete();
                        infoHash = infoHash.setState(InfoHashStateEnum.XLTD.getCode());
                        infoHash.setUpdateTime(LocalDateTime.now());
                        infoHashMapper.updateByPrimaryKey(infoHash);
                        continue;
                    }
                    if (infoHash == null) {
                        torrentFile.delete();
                    }
                    //不用缓冲区
                    FileInputStream content = new FileInputStream(torrentFile);
                    ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                    byte[] buff = new byte[1024 * 512]; //buff用于存放循环读取的临时数据 
                    int rc = 0;
                    try {
                        while ((rc = content.read(buff, 0, buff.length)) > 0) {
                            swapStream.write(buff, 0, rc);
                        }
                    } catch (IOException e) {
                        log.error("流程异常", e);
                        try {
                            content.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        continue;
                    }
                    content.close();
                    byte[] metadataBytes = swapStream.toByteArray(); //in_b为转换之后的结果
                    Map<String, Object> metadataMap = null;
                    try {
                        metadataMap = bencode.decode(metadataBytes, Type.DICTIONARY);
                    } catch (Exception e) {
                        log.error("Bencode解码异常：{}", infoHash);
                        torrentFile.delete();
                        infoHash = infoHash.setState(InfoHashStateEnum.ERROR.getCode());
                        infoHash.setUpdateTime(LocalDateTime.now());
                        infoHashMapper.updateByPrimaryKey(infoHash);
                        continue;
                    }
                    Map<String, Object> infoMap = (Map<String, Object>) metadataMap.get("info");
                    boolean useUtf8Flag = true;
                    LocalDateTime now = LocalDateTime.now();
                    if (infoMap.get("name.utf-8") == null) {
                        if (metadataMap.get("encoding") != null
                                && !((String) metadataMap.get("encoding")).contains("utf8")) {
                            useUtf8Flag = false;
                            metadataMap = new Bencode(Charset.forName((String) metadataMap.get("encoding")))
                                    .decode(metadataBytes, Type.DICTIONARY);
                            infoMap = (Map<String, Object>) metadataMap.get("info");
                        }
                        //第一遍解析文件判断是否乱码
                        if (((String) infoMap.get("name")).contains("�")) {
                            useUtf8Flag = false;
                            metadataMap = new Bencode(Charset.forName("GBK")).decode(metadataBytes, Type.DICTIONARY);
                            infoMap = (Map<String, Object>) metadataMap.get("info");
                        } else {
                            ArrayList<File> arrayList = new ArrayList<File>();
                            analysisFile(infoMap, null, arrayList, now, useUtf8Flag);
                            for (File file2 : arrayList) {
                                if (file2.getFileName().contains("�")) {
                                    useUtf8Flag = false;
                                    metadataMap = new Bencode(Charset.forName("GBK")).decode(metadataBytes,
                                            Type.DICTIONARY);
                                    infoMap = (Map<String, Object>) metadataMap.get("info");
                                    break;
                                }
                            }
                        }

                        //第二遍解析文件判断是否乱码
                        if (((String) infoMap.get("name")).contains("�") || ((String) infoMap.get("name")).contains("?")) {
                            useUtf8Flag = true;
                            metadataMap = bencode.decode(metadataBytes, Type.DICTIONARY);
                            infoMap = (Map<String, Object>) metadataMap.get("info");
                        } else {
                            ArrayList<File> arrayList = new ArrayList<File>();
                            analysisFile(infoMap, null, arrayList, now, useUtf8Flag);
                            for (File file2 : arrayList) {
                                if (file2.getFileName().contains("�") || file2.getFileName().contains("?")) {
                                    useUtf8Flag = true;
                                    metadataMap = bencode.decode(metadataBytes, Type.DICTIONARY);
                                    infoMap = (Map<String, Object>) metadataMap.get("info");
                                    break;
                                }
                            }
                        }
                        //\u0000-\u00FF                        
                        //第三遍解析文件判断是否乱码
                        if (infoMap.get("name.utf-8") == null) {
                            if (((String) infoMap.get("name")).contains("�")) {
                                String tempName = ((String) infoMap.get("name"));
                                if (StringUtils.isEmpty(tempName.replaceAll("[\u0000-\u00ff]+", "").replaceAll("�", ""))) {
                                    useUtf8Flag = false;
                                    metadataMap = new Bencode(Charset.forName("ISO-8859-1")).decode(metadataBytes,
                                            Type.DICTIONARY);
                                    infoMap = (Map<String, Object>) metadataMap.get("info");
                                }
                            } else {
                                ArrayList<File> arrayList = new ArrayList<File>();
                                analysisFile(infoMap, null, arrayList, now, useUtf8Flag);
                                for (File file2 : arrayList) {
                                    if (file2.getFileName().contains("�")) {
                                        if (StringUtils.isEmpty(file2.getFileName().replaceAll("[\u0000-\u00ff]+", "")
                                                .replaceAll("�", ""))) {
                                            useUtf8Flag = false;
                                            metadataMap = new Bencode(Charset.forName("ISO-8859-1")).decode(metadataBytes,
                                                    Type.DICTIONARY);
                                            infoMap = (Map<String, Object>) metadataMap.get("info");
                                            break;
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // 开始解析数据
                    Metadata metadata = new Metadata();
                    metadata.setAnnounce((String) metadataMap.get("announce"));
                    metadata.setComment((String) metadataMap.get("comment"));
                    if (useUtf8Flag && infoMap.get("comment.utf-8") != null) {
                        metadata.setName((String) infoMap.get("comment.utf-8"));
                    }
                    metadata.setCreatedBy((String) metadataMap.get("created by"));
                    metadata.setCreateTime(now);
                    if (metadataMap.get("creation date") != null) {
                        metadata.setCreationTime(LocalDateTime.ofEpochSecond((long) metadataMap.get("creation date"), 0,
                                ZoneOffset.UTC));
                    }
                    metadata.setInfoHash(infoHash.getInfoHash());
                    metadata.setUpdateTime(now);
                    //解析info
                    metadata.setName((String) infoMap.get("name"));
                    if (useUtf8Flag && infoMap.get("name.utf-8") != null) {
                        metadata.setName((String) infoMap.get("name.utf-8"));
                    }
                    try {
                        metadataMapper.insert(metadata);
                    } catch (DuplicateKeyException e) {
                        torrentFile.delete();
                        infoHash = infoHash.setState(InfoHashStateEnum.DOWNLOADED.getCode());
                        infoHash.setUpdateTime(LocalDateTime.now());
                        infoHashMapper.updateByPrimaryKey(infoHash);
                        continue;
                    } catch (DataIntegrityViolationException e) {
                        log.error("数据解析异常：{}", infoHash);
                        torrentFile.delete();
                        infoHash = infoHash.setState(InfoHashStateEnum.ERROR.getCode());
                        infoHash.setUpdateTime(LocalDateTime.now());
                        infoHashMapper.updateByPrimaryKey(infoHash);
                        continue;
                    }
                    ArrayList<File> files1 = new ArrayList<File>();
                    if (metadata.getId() != null) {
                        analysisFile(infoMap, metadata, files1, now, useUtf8Flag);
                        fileMapper.insertList(files1);
                        torrentFile.delete();
                        infoHash = infoHash.setState(InfoHashStateEnum.DOWNLOADED.getCode());
                        infoHash.setUpdateTime(LocalDateTime.now());
                        infoHashMapper.updateByPrimaryKey(infoHash);
                    } else {
                        log.error("metadataId为null：{}", infoHash);
                        torrentFile.delete();
                        infoHash = infoHash.setState(InfoHashStateEnum.ERROR.getCode());
                        infoHash.setUpdateTime(LocalDateTime.now());
                        infoHashMapper.updateByPrimaryKey(infoHash);
                        continue;
                    }
                } catch (Exception e) {
                    torrentFile.delete();
                    log.error("未预料异常：", e);
                    continue;
                }
            }
        }
        Runtime.getRuntime().exec(".\\Thunder\\Program\\Thunder.exe");
        Thread.sleep(4 * 1000);

        //再次提交一批数据进行下载
        List<InfoHash> infoHashs = infoHashMapper.selectHashs();
        if (!infoHashs.isEmpty()) {
            for (InfoHash infoHash : infoHashs) {
                infoHash = infoHash.setState(InfoHashStateEnum.DOWNLOADING.getCode());
                infoHash.setUpdateTime(LocalDateTime.now());
                infoHashMapper.updateByPrimaryKey(infoHash);
                String infoHashString = infoHash.getInfoHash();
                //提交任务
                Thread.sleep(1000);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Runtime.getRuntime()
                                    .exec(".\\Thunder\\Program\\Thunder.exe magnet:?xt=urn:btih:" + infoHashString);
                        } catch (Exception e) {
                        }
                    }
                }).run();
            }
        }
        log.info("Thunder Stop");
    }

}
