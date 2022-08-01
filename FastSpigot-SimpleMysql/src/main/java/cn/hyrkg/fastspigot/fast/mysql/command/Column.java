package cn.hyrkg.fastspigot.fast.mysql.command;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Column implements MysqlCommand {
    public final Table table;
    public final String columnName;

    public Select select() {
        return new Select(this);
    }

    @Override
    public String result() {
        return table.result() + "." + columnName;
    }
}
