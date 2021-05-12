package cn.hyrkg.fastspigot.innercore.framework;

import cn.hyrkg.fastspigot.innercore.FastInnerCore;
import cn.hyrkg.fastspigot.innercore.annotation.Inject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;

@RequiredArgsConstructor
public class HandlerInfo {
    public final Inject injectInfo;
    public final FastInnerCore innerCore;
    public final HandlerInfo parentInfo;
    public final Class<?> originClass;
    public final Class<?> injectedClass;
    public final Object object;

    private HandlerInfo[] pathCache = null;

    @Getter
    private final ArrayList<HandlerInfo> childInfo = new ArrayList<>();

    public void addChildInfo(HandlerInfo info) {
        childInfo.add(info);
    }


    public HandlerInfo[] genPath() {
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
