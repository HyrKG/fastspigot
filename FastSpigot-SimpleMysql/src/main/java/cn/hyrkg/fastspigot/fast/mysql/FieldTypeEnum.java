package cn.hyrkg.fastspigot.fast.mysql;

public enum FieldTypeEnum {
    /**
     * NUMBER FAMILY
     */
    TINYINT,// +-127
    SMALLINT,// +-32767
    MEDIUMINT,// +-8388607
    INTEGER,// +-2147483647
    BIGINT,//
    FLOAT,//
    DOUBLE,//

    /**
     * CHAR FAMILY
     */
    CHAR,// 0-255 bytes
    VARCHAR,// 0-65535 bytes
    TINYTEXT,//	0-255 bytes
    TEXT,//	0-65 535 bytes
    MEDIUMTEXT,// 0-16 777 215 bytes
    LONGTEXT,// 0-4 294 967 295 bytes

    /**
     * BLOB FAMILY
     */
    TINYBLOB,//	0-255 bytes
    BLOB,// 0-65 535 bytes
    MEDIUMBLOB,// 0-16 777 215 bytes
    LONGBLOB;//	0-4 294 967 295 bytes
}
