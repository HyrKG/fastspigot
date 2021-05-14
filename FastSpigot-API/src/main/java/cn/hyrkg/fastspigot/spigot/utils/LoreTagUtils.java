package cn.hyrkg.fastspigot.spigot.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LoreTagUtils {
    public static HashMap<String, String> findLoreKeyToValue(ItemStack itemStack, String split, String... keys) {
        return findLoreKeyToValue(itemStack, split, Arrays.asList(keys));
    }

    public static HashMap<String, String> findLoreKeyToValue(ItemStack itemStack, String split, List<String> keys) {
        HashMap<String, String> result = new HashMap<>();
        if (!hasLore(itemStack))
            return result;

        keys = new ArrayList<>(keys);

        split = split.trim();

        String translatedSplit = "\\ " + split;
        translatedSplit = translatedSplit.replaceAll("\\s+", "");

        for (Map.Entry<String, String> stringStringEntry : findLoreByTags(itemStack, keys).entrySet()) {
            if (stringStringEntry.getValue().contains(split)) {

                String pureLore = ChatColor.stripColor(stringStringEntry.getValue()).trim();

                String[] args = pureLore.split(translatedSplit);

                result.put(stringStringEntry.getKey(), args[1].trim());
            }
        }

        return result;
    }

    public static HashMap<String, String> findLoreByTags(ItemStack itemStack, List<String> keys) {
        HashMap<String, String> result = new HashMap<>();
        if (!hasLore(itemStack))
            return result;

        keys = new ArrayList<>(keys);


        List<String> loreList = itemStack.getItemMeta().getLore();
        for (String lore : loreList) {
            for (String key : new ArrayList<>(keys)) {
                if (lore.contains(key)) {
                    //TODO find value
                    result.put(key, lore);
                    //delete key
                    keys.remove(key);
                    break;
                }
            }
        }
        return result;
    }

    public static HashMap<Integer, String> findIndexLoreByTags(ItemStack itemStack, List<String> keys) {
        HashMap<Integer, String> result = new HashMap<>();
        if (!hasLore(itemStack))
            return result;

        keys = new ArrayList<>(keys);

        List<String> loreList = itemStack.getItemMeta().getLore();
        int index = 0;
        for (String lore : loreList) {
            String sLore = ChatColor.stripColor(lore);

            for (String key : new ArrayList<>(keys)) {
                if (sLore.contains(key)) {
                    //TODO find value
                    result.put(index, lore);
                    //delete key
                    keys.remove(key);
                    break;
                }
            }
            index += 1;
        }
        return result;
    }

    public static boolean hasLore(ItemStack itemStack) {
        return itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore();
    }

    public static boolean hasLoreTag(ItemStack itemStack, String tag) {
        return !findLoreByTags(itemStack, Arrays.asList(tag)).isEmpty();
    }

}
