package cn.hyrkg.fastspigot.fast.mysql.command;

import java.util.HashMap;
import java.util.Map;

public class Where<T> implements MysqlCommand {
    public final T from;
    public HashMap<Column, Object> wheres = new HashMap<>();

    public Where(T from) {
        this.from = from;
    }

    public Where<T> add(Column column, Object target) {
        wheres.put(column, target);
        return this;
    }

    public T whereFinish() {
        return from;
    }

    @Override
    public String result() {
        StringBuilder stringBuilder = new StringBuilder("WHERE ");
        for (Map.Entry<Column, Object> columnObjectEntry : wheres.entrySet()) {
            Object value = columnObjectEntry.getValue();
            stringBuilder.append(columnObjectEntry.getKey().result() + "=" + ((value instanceof Number) ? value : "'" + value.toString() + "',"));
        }
        if (wheres.size() > 0)
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        return stringBuilder.toString();
    }
}
