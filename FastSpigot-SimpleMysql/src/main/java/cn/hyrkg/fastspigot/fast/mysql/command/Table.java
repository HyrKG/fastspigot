package cn.hyrkg.fastspigot.fast.mysql.command;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Table implements MysqlCommand {

    public final String tableName;

    @Override
    public String result() {
        return tableName;
    }
}
