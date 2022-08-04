package cn.hyrkg.fastspigot.spigot.service.config;

public class ErrorAutoloadException extends RuntimeException {
    public final String errorMessage;


    public ErrorAutoloadException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
