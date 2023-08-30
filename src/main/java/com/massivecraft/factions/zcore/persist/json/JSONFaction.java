package com.massivecraft.factions.zcore.persist.json;

import com.massivecraft.factions.zcore.persist.MemoryFaction;

public class JSONFaction extends MemoryFaction {

    public JSONFaction(MemoryFaction arg0) {
        super(arg0);
    }

    public JSONFaction() {
    }

    public JSONFaction(String id) {
        super(id);
    }

    @Override
    public int getRaidClaimsCount() {
        return this.getRaidClaimCount();
    }

    @Override
    public void setShieldDate(long time) {
        this.lastSetShieldTime = time;
    }
}
