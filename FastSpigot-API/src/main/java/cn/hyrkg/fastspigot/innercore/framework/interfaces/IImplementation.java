package cn.hyrkg.fastspigot.innercore.framework.interfaces;

import cn.hyrkg.fastspigot.innercore.framework.HandlerInfo;

/**
 * Implementation of service,it will bind to handler which implement impl service!
 */
public interface IImplementation<T> {
    /**
     * Handler the handler in your ways.
     *
     * @param handlerInstance Handler itself.
     * @param handlerInfo     Handler info.
     */
    void handleHandler(T handlerInstance, HandlerInfo handlerInfo);

}
