package com.massivecraft.factions.zcore.frame.fshield;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.SaberGUI;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.factions.util.serializable.InventoryItem;
import com.massivecraft.factions.zcore.util.TimeFormatUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;


public class FactionShieldMenuFrame extends SaberGUI {

    /**
     * @Author: Cuprize
     */

    public static YamlConfiguration section;
    private final Player player;
    private final Faction faction;

    public FactionShieldMenuFrame(Player player, Faction faction) {
        super(player, CC.translate(FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getString("menu-gui.name")),
                FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getInt("menu-gui.rows")*9);

        this.player = player;
        this.faction = faction;

    }

    private ItemStack buildDummyItem() {
        final ConfigurationSection config =
                FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getConfigurationSection("menu-gui.dummyitem");
        final ItemStack item = XMaterial.matchXMaterial(config.getString("type")).get().parseItem();
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(CC.translate(config.getStringList("lore")));
        meta.setDisplayName(CC.translate(config.getString("name")));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildStatusItem() {
        final ConfigurationSection config =
                FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getConfigurationSection("menu-gui.statusitem");
        final ItemStack item = XMaterial.matchXMaterial(config.getString("type")).get().parseItem();
        final ItemMeta meta = item.getItemMeta();
        int length = FactionsPlugin.getInstance().getConfig().getInt("shield.shield-length");
        TimeFormatUtil timeUtil = new TimeFormatUtil();
        List<String> lore = new LinkedList<>();

        for (String line: config.getStringList("lore")) {
            if (faction.getShield() >= 0) {
                lore.add(line
                        .replace("%end%", timeUtil.calculateEndTime(faction.getShield(), length))
                        .replace("%start%", timeUtil.calculateStartTime(faction.getShield()))
                        .replace("%time%", timeUtil.getCurrentTimeInNewYorkFormatted())
                        .replace("%status%",
                                timeUtil.getStatus(
                                        timeUtil.calculateStartTimeFormat(faction.getShield()),
                                        timeUtil.calculateEndTimeFormat(faction.getShield(), length),
                                        timeUtil.getCurrentTime(),
                                        timeUtil.deserializeLocalDateTime(faction.getSetShieldDate()))
                                ));
            } else {
                lore.add(line
                        .replace("%status%", "&4&lNOT SET")
                        .replace("%end%", "N/A")
                        .replace("%start%", "N/A")
                        .replace("%time%", timeUtil.getCurrentTimeInNewYorkFormatted()));
            }
        }

        meta.setLore(CC.translate(lore));
        meta.setDisplayName(CC.translate(config.getString("name")));
        meta.getItemFlags().add(ItemFlag.HIDE_ATTRIBUTES);
        meta.getItemFlags().add(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildEditItem() {
        final ConfigurationSection config =
                FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getConfigurationSection("menu-gui.controlitem");
        final ItemStack item = XMaterial.matchXMaterial(config.getString("type")).get().parseItem();
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(CC.translate(config.getStringList("lore")));
        meta.setDisplayName(CC.translate(config.getString("name")));
        meta.getItemFlags().add(ItemFlag.HIDE_ATTRIBUTES);
        meta.getItemFlags().add(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void redraw() {

        ConfigurationSection config = FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getConfigurationSection("menu-gui");
        if (config != null) {

            // Filler Item

            ItemStack dummy = buildDummyItem();
            for (Integer slot : config.getIntegerList("dummyitem.slots")) {
                this.setItem(slot, new InventoryItem(dummy));
            }

            // Status Item

            this.setItem(config.getInt("statusitem.slot"), new InventoryItem(buildStatusItem()));

            // Edit Item

            this.setItem(config.getInt("controlitem.slot"), new InventoryItem(buildEditItem()).click(ClickType.LEFT, () -> {
                onClick(faction, player);
            }));

        }
    }

    private void onClick(Faction faction, Player player) {
        new FactionShieldSelectorFrame(faction, player).openGUI(FactionsPlugin.getInstance());
    }
}
