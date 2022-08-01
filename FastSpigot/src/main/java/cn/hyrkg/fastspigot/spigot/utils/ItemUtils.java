package cn.hyrkg.fastspigot.spigot.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemUtils {
    public static final ItemStack setItem(ItemStack item, String title, List<String> lore) {
        ItemMeta itemMeta = item.getItemMeta();
        if (title != null)
            itemMeta.setDisplayName(title);
        if (lore != null)
            itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static final ItemStack addItemLore(ItemStack item, String... str) {
        ItemMeta itemMeta = item.getItemMeta();

        List<String> lore;
        if (itemMeta.hasLore())
            lore = itemMeta.getLore();
        else
            lore = new ArrayList<>();

        lore.addAll(Arrays.asList(str));
        itemMeta.setLore(lore);

        item.setItemMeta(itemMeta);

        return item;
    }

    public static String toInfo(ItemStack stack) {
        return stack.getAmount() + " X " + (stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : stack.getType().toString());
    }

    public static ItemStack translatorGlassPlant(String color) {
        String s = color.replaceAll("&", "");
        switch (s) {
            case "f":
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
            case "6":
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
            case "e":
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);
            case "c":
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
            case "9":
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
            case "a":
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
            case "2":
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
            case "b":
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
            case "7":
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
            case "0":
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
            case "brown":
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 12);
            case "pink":
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 6);
            default:
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
        }
    }

    public static ItemStack getGlassPlant(int color) {
        return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) color);
    }


}
