package cn.bigcoder.plugin.objecthelper.common.exception;

/**
 * @author: Jindong.Tian
 * @date: 2024-05-05
 **/
public class ObjectHeplerPluginException extends RuntimeException{

    public ObjectHeplerPluginException() {
    }

    public ObjectHeplerPluginException(String message) {
        super(message);
    }

    public ObjectHeplerPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectHeplerPluginException(Throwable cause) {
        super(cause);
    }

    public ObjectHeplerPluginException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
