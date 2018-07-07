package com.hbalak.luckyplugin;

import cn.nukkit.item.ItemSwordDiamond;
import cn.nukkit.plugin.PluginLogger;
/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSwordPower extends ItemSwordDiamond {
    PluginLogger logger;
    public ItemSwordPower(PluginLogger logger) {
        super(0, 1);
        this.name = "Lucky Sword";
    }

   
    @Override
    public int getAttackDamage() {
        logger.info("Someone asked for Lucky Sword attack damage");
        return 50;
    }
}
