package com.massivecraft.factions.cmd.shield;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.frame.fshield.FactionShieldMenuFrame;
import com.massivecraft.factions.zcore.util.TL;

public class CmdShield extends FCommand {

    /**
     * @author Cuprize
     */

    public CmdShield() {
        super();
        this.aliases.addAll(Aliases.shield);

        this.requirements = new CommandRequirements.Builder(Permission.SHIELD)
                .withAction(PermissableAction.SHIELD)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        new FactionShieldMenuFrame(context.player, context.faction).openGUI(FactionsPlugin.getInstance());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHIELD_DESCRIPTION;
    }

}
