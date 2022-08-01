package cn.hyrkg.fastspigot.fast.mysql.command;

import org.sqlite.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Select implements MysqlCommand {

    private long limit = 0;
    private boolean notFinish = false;

    public List<String> tables = new ArrayList<>();
    public List<String> selects = new ArrayList<>();
    public Where<Select> where = new Where<Select>(this);

    public Where<Select> where() {
        return where;
    }

    public void setNotFinish(boolean notFinish) {
        this.notFinish = notFinish;
    }

    public Select(Column... columns) {
        for (Column column : columns) {
            selects.add(column.result());
            tables.add(column.table.result());
        }
    }

    public Select limit(long limit) {
        this.limit = limit;
        return this;
    }


    @Override
    public String result() {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(StringUtils.join(selects, ","));
        builder.append(" FROM ");
        builder.append(StringUtils.join(tables, ","));
        builder.append(" ");
        builder.append(where.result());
        if (limit != 0)
            builder.append(" LIMIT " + limit);

        if (!notFinish)
            builder.append(";");
        return builder.toString();
    }
}
