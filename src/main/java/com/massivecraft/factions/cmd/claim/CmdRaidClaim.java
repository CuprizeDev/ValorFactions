package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.FastChunk;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;

public class CmdRaidClaim extends FCommand {

    public CmdRaidClaim() {
        super();
        this.aliases.addAll(Aliases.raidClaim);

        this.requirements = new CommandRequirements.Builder(Permission.RAID_CLAIM)
                .withAction(PermissableAction.RAID_CLAIM)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction fac = context.faction;
        Location location = context.player.getLocation();
        FLocation fLocation = FLocation.wrap(location);
        FastChunk fastChunk = new FastChunk(fLocation);
        if (fac.getRaidClaimsCount() < fac.getAllowedRaidClaims()) {
            if (context.fPlayer.attemptClaim(fac, FLocation.wrap(context.player.getLocation()), true)) {
                if (fac.getRaidClaims().add(fastChunk)) {
                    context.fPlayer.msg(TL.COMMAND_RAIDCHUNK_CLAIM_SUCCESSFUL);
                } else {
                    context.fPlayer.msg(TL.COMMAND_RAIDCHUNK_ALREADY_CHUNK);
                }
            }
        } else {
            context.fPlayer.msg(TL.COMMAND_RAIDCHUNK_PAST_LIMIT, fac.getAllowedRaidClaims());
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_RAIDCHUNK_DESCRIPTION;
    }
}