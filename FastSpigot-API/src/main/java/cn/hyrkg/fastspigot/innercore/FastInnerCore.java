package cn.hyrkg.fastspigot.innercore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FastInnerCore {
    @Getter
    /**
     * Get creator of inner core but in logger type.
     * **/
    private final ICoreCreator creator;

    @Getter
    /**
     * Get asm injector for handler.
     * **/
    private final AsmInjector asmInjector = new AsmInjector(this);

    @Getter
    /**
     * Get handler injector for handler.
     * **/
    private final HandlerInjector handlerInjector = new HandlerInjector(this);

    @Getter
    /**
     * Get function injector for handler selected services.
     * **/
    private final FunctionInjector functionInjector = new FunctionInjector(this);


    public void disable() {
        handlerInjector.onDisable();
    }
}
