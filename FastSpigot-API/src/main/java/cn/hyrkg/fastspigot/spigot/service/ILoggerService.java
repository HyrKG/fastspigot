package cn.hyrkg.fastspigot.spigot.service;

import cn.hyrkg.fastspigot.innercore.framework.HandlerInfo;
import cn.hyrkg.fastspigot.innercore.framework.interfaces.IServiceProvider;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public interface ILoggerService extends IServiceProvider, ILogger {
    default void info(String str) {
        String combine = locatePath();

        Object creator = getInnerCore().getCreator();
        if (creator instanceof Plugin) {
            ((Plugin) creator).getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "(INFO)" + combine + ": " + str);
        } else {
            System.out.println(combine + ": " + str);
        }
    }

    default void warm(String str) {

        String combine = locatePath();

        Object creator = getInnerCore().getCreator();
        if (creator instanceof Plugin) {
            ((Plugin) creator).getServer().getConsoleSender().sendMessage(ChatColor.COLOR_CHAR + "6" + "(WARM)" + combine + ": " + str);
        } else {
            System.out.println(combine + ": " + str);
        }
    }

    default void error(String str) {
        String combine = locatePath();

        Object creator = getInnerCore().getCreator();
        if (creator instanceof Plugin) {
            ((Plugin) creator).getServer().getConsoleSender().sendMessage(ChatColor.COLOR_CHAR + "c" + ChatColor.COLOR_CHAR + "l" + "(ERROR)" + combine + ": " + str);
        } else {
            System.out.println(combine + ": " + str);
        }
    }

    default void debug(String str) {
        if (isDebugging()) {
            String combine = locatePath();

            Object creator = getInnerCore().getCreator();
            if (creator instanceof Plugin) {
                ((Plugin) creator).getServer().getConsoleSender().sendMessage(ChatColor.COLOR_CHAR + "4" + ChatColor.COLOR_CHAR + "l" + "(!DEBUG!)" + combine + ": " + str);
            } else {
                System.out.println(combine + ": " + str);
            }
        }
    }

    default boolean isDebugging() {
        return getInnerCore().getCreator().isDebugging(getClass());
    }

    default String locatePath() {
        HandlerInfo info = getHandlerInfo();
        String combine = "";
        for (HandlerInfo listInfo : info.getHandlerPath()) {
            combine += (listInfo.injectInfo.name().isEmpty() ? listInfo.originClass.getSimpleName() : listInfo.injectInfo.name()) + ">";
        }
        combine = combine.trim();
        return combine.substring(0, combine.length() - 1);
    }
}
