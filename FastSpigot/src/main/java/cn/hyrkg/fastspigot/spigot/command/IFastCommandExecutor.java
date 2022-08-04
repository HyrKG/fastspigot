package cn.hyrkg.fastspigot.spigot.command;

import cn.hyrkg.fastspigot.innercore.annotation.ImplService;
import cn.hyrkg.fastspigot.innercore.framework.interfaces.IServiceProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ImplService(impClass = FastCommandImpl.class)
public interface IFastCommandExecutor extends IServiceProvider {


    String[] getCommands();

    default String getCmdDescription() {
        return getCommands()[0];
    }

    default void handleException(Exception e) {
        e.printStackTrace();
    }

    default boolean isOp(CommandSender sender) {
        return sender.isOp();
    }

    default Player checkPlayer(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
            throwError(playerName + "不在线!");
        return player;
    }

    default <T> T checkNull(T object, String nullMsg) {
        if (object == null)
            throwError(nullMsg);
        return object;
    }

    default <T> T checkNull(T object) {
        if (object == null)
            throwError(object.getClass().getSimpleName() + " 为空!");
        return object;
    }

    default void throwError(String error) {
        ((FastCommandImpl) getImplementation(IFastCommandExecutor.class)).throwError(null, error);
    }

}
