package cn.hyrkg.fastspigot.spigot.service.command;

import cn.hyrkg.fastspigot.innercore.framework.HandlerInfo;
import cn.hyrkg.fastspigot.innercore.framework.interfaces.IImplementation;
import cn.hyrkg.fastspigot.spigot.FastPlugin;
import cn.hyrkg.fastspigot.spigot.i18n.I18n;
import cn.hyrkg.fastspigot.spigot.utils.MsgHelper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FastCommandImpl implements IImplementation<IFastCommandExecutor>, CommandExecutor {

    @Getter
    private IFastCommandExecutor executorInterface;
    private Multimap<String, CommandMethod> commandInfoMultimap = ArrayListMultimap.create();
    private HashMap<Boolean, ArrayList<String>> commandHelpCache = new HashMap<>();

    @Override
    public void handleHandler(IFastCommandExecutor executor, HandlerInfo handlerInfo) {
        this.executorInterface = executor;


        if (handlerInfo.innerCore.getCreator() instanceof FastPlugin) {
            FastPlugin plugin = (FastPlugin) handlerInfo.innerCore.getCreator();

            try {
                for (String cmd : executor.getCommands())
                    plugin.getCommand(cmd).setExecutor(this);
            } catch (NullPointerException exception) {
                plugin.error(handlerInfo.originClass.getSimpleName() + ":" + "You may not have registered the command " + Arrays.asList(executor.getCommands()) + "!");
                return;
            } catch (Exception e) {
                throw e;
            }

            //todo parser commands
            parserCommands(executor, handlerInfo);
        }
    }

    private void parserCommands(IFastCommandExecutor executor, HandlerInfo handlerInfo) {
        for (Method method : handlerInfo.originClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(FastCommand.class)) {
                //is command!
                FastCommand command = method.getAnnotation(FastCommand.class);
                CommandMethod commandInfo = new CommandMethod(command, this).readMethod(method);
                if (commandInfo == null) {
                    executor.getInnerCore().getCreator().error(executor.getClass() + " error when load command method" + method.getName() + " : wrong parameters");
                    continue;
                }
                commandInfoMultimap.put(command.index(), commandInfo);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        try {
            String combineOfArgs = "";
            for (String str : strings)
                combineOfArgs += str + " ";
            combineOfArgs = combineOfArgs.trim();
            String finalCombineOfArgs = combineOfArgs;

            ArrayList<CommandMethod> hitMethods = new ArrayList<>();
            commandInfoMultimap.keys().stream().filter(j -> finalCombineOfArgs.startsWith(j)).forEach(j ->
                    hitMethods.addAll(commandInfoMultimap.get(j))
            );

            //cast hits
            for (CommandMethod j : hitMethods) {
                List<String> splitsList =
                        Arrays.asList(finalCombineOfArgs.replaceFirst(j.commandInfo.index(), "").split("\\s+")).stream().filter(
                                k -> !k.isEmpty()
                        ).collect(Collectors.toList());

                String[] splitWithoutIndex = splitsList.toArray(new String[splitsList.size()]);

                //check appropriates
                if (j.isAppropriate(commandSender, splitWithoutIndex)) {
                    if (j.commandInfo.requireOp() && !executorInterface.isOp(commandSender)) {
                        throwError(null, "OP ONLY");
                        return false;
                    }
                    j.handleCommandMethod(executorInterface, commandSender, splitWithoutIndex);
                    return true;
                }
            }

            //TODO return as error msg
            genHelpInfo(executorInterface.isOp(commandSender)).forEach(j -> commandSender.sendMessage(j));
            MsgHelper.to(commandSender).warm("你输入的指令有误!请检查指令!");
        } catch (Exception e) {
            if (e instanceof ErrorCommand) {
                MsgHelper.to(commandSender).warm("执行指令错误: " + ((ErrorCommand) e).getErrorMsg());
            } else {
                executorInterface.handleException(e);
                MsgHelper.to(commandSender).warm("发生了错误: " + e.getMessage());
            }
        }
        return false;
    }


    public ArrayList<String> genHelpInfo(boolean isOp) {
        if (commandHelpCache.containsKey(isOp))
            return commandHelpCache.get(isOp);

        ArrayList<String> infos = new ArrayList<>();

        infos.add(ChatColor.RED + "" + ChatColor.BOLD + ">>>" + executorInterface.getCmdDescription());

        if (isOp) {
            commandInfoMultimap.values().stream().filter(j -> j.commandInfo.requireOp()).sorted((j, k) -> {
                return ((Integer) j.commandInfo.order()).compareTo(k.commandInfo.order());
            }).forEach(j -> {
                infos.add(
                        (ChatColor.COLOR_CHAR + "6" + ChatColor.BOLD + "OP>" + ChatColor.BLUE + genHelpInfoOfCommandMethod(j))
                );
            });
        }

        commandInfoMultimap.values().stream().filter(j -> !j.commandInfo.requireOp()).sorted((j, k) -> {
            return ((Integer) j.commandInfo.order()).compareTo(k.commandInfo.order());
        }).forEach(j -> {
            infos.add(
                    (ChatColor.BLUE + genHelpInfoOfCommandMethod(j))
            );
        });

        if (infos.size() == 1)
            infos.add(ChatColor.RED + "" + ChatColor.BOLD + "No valid command!");

        commandHelpCache.put(isOp, infos);

        return infos;
    }

    public String genHelpInfoOfCommandMethod(CommandMethod commandMethod) {
        String cmd = "/" + executorInterface.getCommands()[0] + " " + commandMethod.commandInfo.index();
        cmd = cmd.trim();

        int index = 0;
        String[] paramsName = commandMethod.commandInfo.paramsName();

        for (Class param : commandMethod.getParameters()) {
            String paramName;
            if (index < paramsName.length)
                paramName = paramsName[index];
            else
                paramName = param.getSimpleName();
            cmd += " <" + paramName + ">";
            index += 1;
        }
        cmd = cmd.trim() + " - " + commandMethod.commandInfo.desc();
        return cmd;
    }

    public void tryOrThrow(Runnable runnable, String wrongMsg) {
        try {
            runnable.run();
        } catch (Exception exception) {
            throw new ErrorCommand(exception, wrongMsg);
        }
    }

    public void tryOrThrowI18n(Runnable runnable, String wrongMsg) {
        tryOrThrow(runnable, I18n.formatNature(wrongMsg));
    }

    public void throwError(Exception originError, String errorInfo) {
        throw new ErrorCommand(originError, errorInfo);
    }
}
