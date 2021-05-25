package cn.hyrkg.fastspigot.innercore.framework;

import cn.hyrkg.fastspigot.innercore.FastInnerCore;
import cn.hyrkg.fastspigot.innercore.annotation.Inject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;

@RequiredArgsConstructor
public class HandlerInfo {
    /**
     * The @Inject annotation instance.
     */
    public final Inject injectInfo;
    /**
     * The InnerCore of the handler.<p>
     * It is the key of handler!
     */
    public final FastInnerCore innerCore;
    /**
     * Exist if this info has parent handler or it'll be null!
     */
    public final HandlerInfo parentInfo;
    /**
     * Origin class.
     */
    public final Class<?> originClass;
    /**
     * Injected class.
     */
    public final Class<?> injectedClass;
    /**
     * Injected class instance and handler itself.
     */
    public final Object object;

    /**
     * The child info of this handler.
     */
    @Getter
    private final ArrayList<HandlerInfo> childInfo = new ArrayList<>();

    /**
     * The cache of this handler path.
     */
    private HandlerInfo[] pathCache = null;


    /**
     * To add child info of this handler!<p>
     * It is unsafe if you add it yourself!
     */
    public void addChildInfo(HandlerInfo info) {
        childInfo.add(info);
    }


    /**
     * Get path of this handler.<p>
     * It will cache itself in order to reduce resource costing
     */
    public HandlerInfo[] getHandlerPath() {
        if (pathCache != null)
            return pathCache;

        ArrayList<HandlerInfo> list = new ArrayList<>();
        list.add(this);

        HandlerInfo head = this;
        while (head.parentInfo != null) {
            head = head.parentInfo;
            list.add(head);
        }
        Collections.reverse(list);
        pathCache = list.toArray(new HandlerInfo[list.size()]);
        return pathCache;
    }

    /**
     * Get short path of this class path<p>
     * Examples:
     * a.b.c.Class
     */
    public String getShortClassPath() {
        String[] args = originClass.getTypeName().split("\\.");
        String combine = "";
        for (int i = 0; i < args.length; i++) {
            if (i + 1 >= args.length)
                combine += args[i];
            else
                combine += args[i].substring(0, 1) + ".";
        }
        return combine + (injectInfo.name().isEmpty() ? "" : "(" + injectInfo.name() + ")");
    }

}
