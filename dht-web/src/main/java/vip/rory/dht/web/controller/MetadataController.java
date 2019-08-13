package vip.rory.dht.web.controller;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vip.rory.dht.web.model.Response;
import vip.rory.dht.web.service.MetadataService;

/**
 * @author zhanghangtian
 * @date 2019年7月15日 下午12:25:15
 */
@Slf4j
@RestController
@RequestMapping(value = "/metadata")
public class MetadataController {

    @Autowired
    private MetadataService metadataService;

    @RequestMapping(value = "/selectPage")
    public Response selectPage(@NotNull Integer page, @NotNull Integer rows, String keyword, Long id, String magnet,
                               String sortField, String sortOrder) {
        if (rows > 100) {
            rows = 100;
        }
        log.info("请求metadata列表:page:{},rows{},keyword:{}", page, rows, keyword);
        Response response = new Response();
        response.setCode(200);
        response.setMessage("请求成功");
        response.setData(metadataService.selectPage(page, rows, id, magnet, keyword, sortField, sortOrder));
        return response;
    }

}
