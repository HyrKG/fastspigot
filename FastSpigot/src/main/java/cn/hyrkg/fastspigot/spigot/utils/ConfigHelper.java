package cn.hyrkg.fastspigot.spigot.utils;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ConfigHelper {
    public final ConfigurationSection section;

    public OptionalResult key(String key) {
        return new OptionalResult(key);
    }

    public static ConfigHelper of(ConfigurationSection section) {
        return new ConfigHelper(section);
    }

    @RequiredArgsConstructor
    public class OptionalResult {
        public final String key;

        public boolean ofBool(boolean def) {
            if (section.contains(key) && section.isBoolean(key))
                return section.getBoolean(key);
            return def;
        }


        public boolean ofBool() {
            return ofBool(false);
        }

        public String ofStr(String def) {
            if (section.contains(key) && section.isString(key))
                return section.getString(key);
            return def;
        }

        public String ofStr() {
            return ofStr(null);
        }


        public int ofInt(int def) {
            if (section.contains(key) && section.isInt(key))
                return section.getInt(key);
            return def;
        }

        public int ofInt() {
            return ofInt(0);
        }


        public double ofDouble(double def) {
            if (section.contains(key) && section.isDouble(key))
                return section.getDouble(key);
            return def;
        }

        public double ofDouble() {
            return ofDouble(0.0d);
        }


        public List<String> ofStrList() {
            if (section.contains(key) && section.isList(key))
                return section.getStringList(key);
            return new ArrayList<>();
        }

    }
}
