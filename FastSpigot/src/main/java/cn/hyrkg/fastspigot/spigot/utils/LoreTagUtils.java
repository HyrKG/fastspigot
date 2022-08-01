package cn.hyrkg.fastspigot.spigot.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LoreTagUtils {
    /**
     * Find lore key to value
     *
     * @param itemStack the item stack to find
     * @param split     split of lore to value
     * @param keys      keys
     * @return key to <strong>striped</strong> value
     */
    public static HashMap<String, String> findLoreKeyToValue(ItemStack itemStack, String split, String... keys) {
        return findLoreKeyToValue(itemStack, split, Arrays.asList(keys));
    }

    /**
     * Find lore key to value
     *
     * @param itemStack the item stack to find
     * @param split     split of lore to value
     * @param keys      keys
     * @return key to <strong>striped</strong> value
     */
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


    /**
     * Find lore by tag<br>
     * Returns when lore contains tag!
     *
     * @param itemStack the item stack to find
     * @param keys      keys
     * @return key to lore
     */
    public static HashMap<String, String> findLoreByTags(ItemStack itemStack, String... keys) {
        return findLoreByTags(itemStack, Arrays.asList(keys));
    }

    /**
     * Find lore by tag<br>
     * Returns when lore contains tag!
     *
     * @param itemStack the item stack to find
     * @param keys      keys
     * @return key to lore
     */
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

    /**
     * Find lore index by tag
     * Returns when lore contains tag!
     *
     * @param itemStack the item stack to find
     * @param keys      tag keys
     * @return lore index to lore
     */
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

    public static boolean modifyLoreToNew(ItemStack itemStack, int index, String split, String newLore) {
        if (!hasLore(itemStack))
            return false;

        List<String> loreList = itemStack.getItemMeta().getLore();

        if (index >= loreList.size())
            return false;

        split = split.trim();

        String translatedSplit = "\\ " + split;
        translatedSplit = translatedSplit.replaceAll("\\s+", "");


        String lore = loreList.get(index);
        String[] loreArgs = lore.split(translatedSplit);
        if (loreArgs.length != 2)
            return false;

        String modifiedLore = loreArgs[0].trim() + split + " " + newLore;
        loreList.set(index, modifiedLore);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(loreList);
        itemStack.setItemMeta(meta);
        return true;
    }

    public static boolean hasLore(ItemStack itemStack) {
        if (itemStack == null)
            return false;
        return itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore();
    }

    public static boolean hasLoreTag(ItemStack itemStack, String tag) {
        return !findLoreByTags(itemStack, Arrays.asList(tag)).isEmpty();
    }

}
