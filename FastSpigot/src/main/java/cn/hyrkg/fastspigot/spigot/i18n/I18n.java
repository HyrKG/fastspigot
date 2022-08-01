package cn.hyrkg.fastspigot.spigot.i18n;

import com.google.common.base.Preconditions;

public class I18n {
    /**
     * format
     **/
    public static String formatNature(String input, Object... objects) {
        Preconditions.checkNotNull(input);
        String langInput = input.trim().replaceAll("\\s+", "_").toLowerCase();
        String output = format(langInput);
        if (output.equals(langInput))
            return input;
        return output;
    }

    public static String format(String input, Object... objects) {
        return input;
    }
}
