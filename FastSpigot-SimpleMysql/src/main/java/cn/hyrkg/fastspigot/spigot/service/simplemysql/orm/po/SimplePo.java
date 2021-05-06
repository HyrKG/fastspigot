package cn.hyrkg.fastspigot.spigot.service.simplemysql.orm.po;

import cn.hyrkg.fastspigot.spigot.service.simplemysql.orm.IndexType;
import cn.hyrkg.fastspigot.spigot.service.simplemysql.orm.annotations.TableField;
import lombok.Data;

@Data
public class SimplePo {

    @TableField(type = "TINYTEXT", indexType = IndexType.UNIQUE)
    public String uuid;

    @TableField(type = "TINYINT")
    public Integer test;

    @TableField(type = "TINYBLOB")
    public byte[] data;

}
