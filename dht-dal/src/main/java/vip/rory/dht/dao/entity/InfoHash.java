package vip.rory.dht.dao.entity;

import java.time.LocalDateTime;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author zhanghangtian
 * @date 2019年7月10日 下午4:05:13
 */
@Data
@Getter
@Setter
@Accessors(chain = true)
@ToString
@Table(name = "info_hash")
public class InfoHash {

    @Id
    private String        infoHash;

    private String        sourceIp;

    private Integer       sourcePort;

    private Integer       state;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
