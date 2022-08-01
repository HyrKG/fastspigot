package cn.hyrkg.fastspigot.fast.mysql.command.column;

public class ColumnArgs {
    public static String notNull() {
        return "NOT NULL";
    }

    public static String defaultValue(String value) {
        return "DEFAULT " + value;
    }
}
