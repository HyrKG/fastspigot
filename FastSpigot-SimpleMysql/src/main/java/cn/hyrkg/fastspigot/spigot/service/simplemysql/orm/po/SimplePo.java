package cn.hyrkg.fastspigot.spigot.service.simplemysql.orm.po;

import cn.hyrkg.fastspigot.fast.mysql.IndexTypeEnum;
import cn.hyrkg.fastspigot.spigot.service.simplemysql.orm.annotations.TableField;
import lombok.Data;

@Data
public class SimplePo {

    @TableField(type = "TINYTEXT", indexType = IndexTypeEnum.UNIQUE)
    public String uuid;

    @TableField(type = "TINYINT")
    public Integer test;

    @TableField(type = "TINYBLOB")
    public byte[] data;

}
