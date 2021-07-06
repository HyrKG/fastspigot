package cn.hyrkg.fastspigot.fast.mysql;

import lombok.Builder;
import lombok.Getter;

/**
 * Field Info
 */
@Builder
@Getter
public class Field {
    private String fieldName;
    private FieldTypeEnum typeEnum;

}
