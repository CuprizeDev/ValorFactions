package com.massivecraft.factions.zcore.frame.fshield;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.SaberGUI;
import com.massivecraft.factions.util.serializable.InventoryItem;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TimeFormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class FactionShieldSelectorFrame extends SaberGUI {

    /**
     * @Author: Cuprize
     */

    private final Player player;
    private final Faction faction;
    TimeFormatUtil timeUtil = new TimeFormatUtil();

    public FactionShieldSelectorFrame(Faction faction, Player player) {

        super(player, CC.translate(FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getString("selector-gui.name")),
                FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getInt("selector-gui.rows")*9);

        this.player = player;
        this.faction = faction;
    }

    private ItemStack buildDummyItem() {
        final ConfigurationSection config =
                FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getConfigurationSection("selector-gui.dummyitem");
        final ItemStack item = XMaterial.matchXMaterial(config.getString("type")).get().parseItem();
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(CC.translate(config.getStringList("lore")));
        meta.setDisplayName(CC.translate(config.getString("name")));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildInfoItem() {
        final ConfigurationSection config =
                FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getConfigurationSection("selector-gui.infoitem");
        final ItemStack item = XMaterial.matchXMaterial(config.getString("type")).get().parseItem();
        final ItemMeta meta = item.getItemMeta();
        int length = FactionsPlugin.getInstance().getConfig().getInt("shield.shield-length");
        List<String> lore = new LinkedList<>();

        for (String line: config.getStringList("lore")) {
            if (faction.getShield() >= 0) {
                lore.add(line
                        .replace("%end%", timeUtil.calculateEndTime(faction.getShield(), length))
                        .replace("%start%", timeUtil.calculateStartTime(faction.getShield()))
                        .replace("%time%", timeUtil.getCurrentTimeInNewYorkFormatted())
                        .replace("%status%", timeUtil.getStatus(
                                timeUtil.calculateStartTimeFormat(faction.getShield()),
                                timeUtil.calculateEndTimeFormat(faction.getShield(), length),
                                timeUtil.getCurrentTime(),
                                timeUtil.deserializeLocalDateTime(faction.getSetShieldDate()))));
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
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack shieldDummyItem(int i) {
        final ConfigurationSection config =
                FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getConfigurationSection("selector-gui.shielditem");
        final ItemStack item = XMaterial.matchXMaterial(config.getString("type")).get().parseItem();
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(CC.translate(config.getStringList("lore")));
        meta.setDisplayName(CC.translate(config.getString("name")
                .replace("%end%", timeUtil.calculateEndTime(i, FactionsPlugin.getInstance().getConfig().getInt("shield.shield-length")))
                .replace("%start%", timeUtil.calculateStartTime(i))));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack currentShieldItem(int i) {
        final ConfigurationSection config =
                FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getConfigurationSection("selector-gui.currentshielditem");
        final ItemStack item = XMaterial.matchXMaterial(config.getString("type")).get().parseItem();
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(CC.translate(config.getStringList("lore")));
        meta.setDisplayName(CC.translate(config.getString("name")
                .replace("%end%", timeUtil.calculateEndTime(i, FactionsPlugin.getInstance().getConfig().getInt("shield.shield-length")))
                .replace("%start%", timeUtil.calculateStartTime(i))));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void redraw() {

        ConfigurationSection config = FactionsPlugin.getInstance().getFileManager().getShield().getConfig().getConfigurationSection("selector-gui");
        if (config != null) {

            // Dummy Item

            for (Integer slot : config.getIntegerList("dummyitem.slots")) {
                this.setItem(slot, new InventoryItem(buildDummyItem()));
            }

            // Info Item

            this.setItem(config.getInt("infoitem.slot"), new InventoryItem(buildInfoItem()));

            // Shield Dummy Item

            int totalSlots = 24;
            List<Integer> unusedSlots = new LinkedList<>();
            for (int i = 0; i < (totalSlots-FactionsPlugin.getInstance().getConfig().getInt("shield.shield-length"))+FactionsPlugin.getInstance().getConfig().getInt("shield.shield-length"); i++) {
                if (!config.getIntegerList("dummyitem.slots").contains(i)) {
                    unusedSlots.add(i);
                    continue;
                }
                totalSlots++;
            }
            int timeInteger = 0;
            for (int i: unusedSlots) {
                int finalTimeInteger = timeInteger;

                if (faction.getShield() == finalTimeInteger) {
                    this.setItem(i, new InventoryItem(currentShieldItem(timeInteger)));
                    timeInteger++;
                    continue;
                }

                this.setItem(i, new InventoryItem(shieldDummyItem(timeInteger)).click(ClickType.LEFT, () -> {
                    player.closeInventory();
                    faction.setShieldDate(timeUtil.serializeLocalDateTime(LocalDateTime.now()));
                    faction.setShield(finalTimeInteger);
                    player.sendMessage(TL.COMMAND_SHIELD_SET.toString()
                            .replace("%end%", timeUtil.calculateEndTime(finalTimeInteger, FactionsPlugin.getInstance().getConfig().getInt("shield.shield-length")))
                            .replace("%start%", timeUtil.calculateStartTime(finalTimeInteger)));
                }));
                timeInteger++;
            }
        }
    }
}
