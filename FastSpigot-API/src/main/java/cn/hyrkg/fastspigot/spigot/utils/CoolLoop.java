package cn.hyrkg.fastspigot.spigot.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class CoolLoop<T> {
    private HashMap<T, Integer> loop = new HashMap<>();
    public ArrayList<T> lastRemoved = new ArrayList<>();

    public boolean has(T obj) {
        return loop.containsKey(obj);
    }

    public void put(T obj, int time) {
        loop.put(obj, time);
    }

    public int get(T obj) {
        return loop.get(obj);
    }

    public void remove(T obj) {
        loop.remove(obj);
    }

    public Set<T> getKeys() {
        return loop.keySet();
    }

    public boolean check() {
        ArrayList<T> toRemove = null;

        for (T t : loop.keySet()) {
            int i = loop.get(t) - 1;
            if (i <= 0) {
                if (toRemove == null)
                    toRemove = new ArrayList<>();
                toRemove.add(t);
            } else {
                loop.put(t, i);
            }
        }

        if (toRemove != null) {
            lastRemoved = toRemove;
            for (T key : toRemove) {
                loop.remove(key);
            }
        } else {
            lastRemoved = null;
        }

        return lastRemoved != null;
    }
}
