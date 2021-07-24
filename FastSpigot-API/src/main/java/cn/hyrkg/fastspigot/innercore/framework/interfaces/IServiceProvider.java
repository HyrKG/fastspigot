package cn.hyrkg.fastspigot.innercore.framework.interfaces;

import cn.hyrkg.fastspigot.innercore.FastInnerCore;
import cn.hyrkg.fastspigot.innercore.framework.HandlerInfo;

public interface IServiceProvider {
    /**
     * Get the InnerCore of current handler
     *
     * @return return null if current handler wasn't registered by IoC container.
     **/
    default FastInnerCore getInnerCore() {
        return null;
    }

    /**
     * Get the handler info of current handler.
     *
     * @return return null if current handler wasn't registered by IoC container.
     */
    default HandlerInfo getHandlerInfo() {
        return getInnerCore().getHandlerInjector().getHandlerInfo(this);
    }

    /**
     * Get the implementation of current handler.
     *
     * @param implService The class of service witch was annotated by ImplService
     * @return return the implementation if current handler implement the implementation service.
     */
    default <T> T getImplementation(Class<? extends IServiceProvider> implService) {
        return getInnerCore().getFunctionInjector().getImplementation(this, implService);
    }
}
