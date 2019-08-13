package vip.rory.dht.web.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vip.rory.dht.dao.entity.File;
import vip.rory.dht.web.model.Response;
import vip.rory.dht.web.service.FileService;

/**
 * @author zhanghangtian
 * @date 2019年7月15日 下午12:25:15
 */
@Slf4j
@RestController
@RequestMapping(value = "/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/listFiles")
    public Response listFiles(Long metadataId) {
        if (metadataId == null) {
            return null;
        }
        List<File> listFile = fileService.listFiles(metadataId);
        Response response = new Response();
        response.setCode(200);
        response.setData(listFile);
        response.setMessage("请求成功");
        return response;
    }

    @RequestMapping(value = "/selectPage")
    public Response selectPage(@NotNull Integer page, @NotNull Integer rows, String keyword, String sortField,
                               String sortOrder) {
        if (rows > 100) {
            rows = 100;
        }
        log.info("请求file列表:page:{},rows{},keyword:{}", page, rows, keyword);
        Response response = new Response();
        response.setCode(200);
        response.setMessage("请求成功");
        response.setData(fileService.selectPage(page, rows, keyword, sortField, sortOrder));
        return response;
    }

}
