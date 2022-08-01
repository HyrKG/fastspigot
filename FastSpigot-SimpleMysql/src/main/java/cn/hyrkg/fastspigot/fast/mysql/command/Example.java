package cn.hyrkg.fastspigot.fast.mysql.command;

public class Example {
    public static void main(String[] args) {
        Column column = new Column(new Table("table1"), "test");
        Column player = new Column(new Table("table2"), "player");

        System.out.println(new Select(column, player).where().add(player, "test").whereFinish().result());

    }


}
