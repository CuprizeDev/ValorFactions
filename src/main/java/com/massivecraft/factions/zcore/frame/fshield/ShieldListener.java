package com.massivecraft.factions.zcore.frame.fshield;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TimeFormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;


public class ShieldListener implements Listener {

    /**
     * @Author: Cuprize
     */

    @EventHandler
    public void onExplosion(EntityExplodeEvent e) {

        FLocation fLocation = new FLocation(e.getLocation());
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        TimeFormatUtil timeUtil = new TimeFormatUtil();

        if (!faction.isWilderness()) {
            if (!faction.isSafeZone()) {
                if (!faction.isWarZone()) {
                    if (!faction.getRaidClaims().contains(fLocation.toFastChunk())) {
                        if (timeUtil.getBooleanStatus(
                                timeUtil.calculateStartTimeFormat(faction.getShield()),
                                timeUtil.calculateEndTimeFormat(faction.getShield(), FactionsPlugin.getInstance().getConfig().getInt("shield.shield-length")),
                                timeUtil.getCurrentTime(),
                                timeUtil.deserializeLocalDateTime(faction.getSetShieldDate()))) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }

        List<Block> blocks = e.blockList(); // Get the list of blocks broken by the explosion
        List<Block> blocksToRemove = new ArrayList<>();

        for (Block block : blocks) {

            FLocation blockLocation = new FLocation(block.getLocation());
            Faction blockFaction = Board.getInstance().getFactionAt(blockLocation);

            if (blockFaction.isWilderness() || blockFaction.isWarZone() || blockFaction.isSafeZone()) {
                blocksToRemove.add(block);
                continue;
            }

            if (blockFaction.getRaidClaims().contains(blockLocation.toFastChunk())) {
                blocksToRemove.add(block);
            }
        }
        blocks.clear();
        blocks.addAll(blocksToRemove);
    }
}
