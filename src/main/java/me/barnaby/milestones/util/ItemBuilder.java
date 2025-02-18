package me.barnaby.milestones.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ItemBuilder {
    private final ItemStack item;

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder data(byte data) {
        item.setDurability(data);
        return this;
    }

    public ItemBuilder type(Material material) {
        item.setType(material);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends ItemMeta> ItemBuilder meta(Consumer<T> metaConsumer) {
        ItemMeta meta = item.getItemMeta();
        try {
            metaConsumer.accept((T) meta);
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withMeta(ItemMeta meta) {
        if(!Bukkit.getItemFactory().isApplicable(meta, item.getType()))
            throw new IllegalArgumentException("Tried to set invalid ItemMeta for " + item.getType().name());

        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder name(String name) {
        return meta(meta -> meta.setDisplayName(name));
    }

    public ItemBuilder lore(String... lore) {
        return meta(meta -> meta.setLore(Arrays.asList(lore)));
    }

    public ItemBuilder lore(List<String> lore) {
        List<String> newLore = new ArrayList<>();
        lore.forEach(line -> newLore.add(StringUtil.format(line)));

        return meta(meta -> meta.setLore(newLore));
    }

    public ItemBuilder damage(short damage) {
        item.setDurability(damage);
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        return meta(meta -> meta.addItemFlags(flags));
    }

    public ItemBuilder unsafeEnchant(Enchantment enchant, int level) {
        item.addUnsafeEnchantment(enchant, level);
        return this;
    }

    public ItemStack build() {
        return item;
    }
}
