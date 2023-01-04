package cn.hyrkg.fastspigot.fast.forgeui;

public enum PacketType {
    message("msg"), display("property"), update("update"), close;

    public final String propertyPath;

    PacketType() {
        this.propertyPath = null;
    }

    PacketType(String propertyPath) {
        this.propertyPath = propertyPath;
    }
}
