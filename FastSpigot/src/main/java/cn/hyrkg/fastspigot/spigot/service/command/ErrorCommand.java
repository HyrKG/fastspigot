package cn.hyrkg.fastspigot.spigot.service.command;

import lombok.Getter;

public class ErrorCommand extends RuntimeException {

    @Getter
    private final Exception originException;

    @Getter
    private final String errorMsg;

    public ErrorCommand(Exception originException, String errorMsg) {
        super(errorMsg);
        this.originException = originException;
        this.errorMsg = errorMsg;
    }
}
