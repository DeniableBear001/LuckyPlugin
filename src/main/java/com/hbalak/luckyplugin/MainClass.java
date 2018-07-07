package com.hbalak.luckyplugin;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.*;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.entity.weather.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemAppleGoldEnchanted;
import cn.nukkit.item.ItemArrow;
import cn.nukkit.item.ItemAxeDiamond;
import cn.nukkit.item.ItemBed;
import cn.nukkit.item.ItemBlazeRod;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemBone;
import cn.nukkit.item.ItemBootsDiamond;
import cn.nukkit.item.ItemBow;
import cn.nukkit.item.ItemChestplateDiamond;
import cn.nukkit.item.ItemDiamond;
import cn.nukkit.item.ItemEnderPearl;
import cn.nukkit.item.ItemHelmetDiamond;
import cn.nukkit.item.ItemHoeGold;
import cn.nukkit.item.ItemIngotGold;
import cn.nukkit.item.ItemIngotIron;
import cn.nukkit.item.ItemLeggingsDiamond;
import cn.nukkit.item.ItemMelon;
import cn.nukkit.item.ItemNetherWart;
import cn.nukkit.item.ItemPickaxeGold;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.item.ItemSwordDiamond;
import cn.nukkit.item.enchantment.bow.EnchantmentBowFlame;
import cn.nukkit.item.enchantment.bow.EnchantmentBowInfinity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockGold;
import cn.nukkit.block.BlockIronBars;
import cn.nukkit.block.BlockLava;
import cn.nukkit.block.BlockObsidian;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.item.enchantment.bow.EnchantmentBowPower;
import cn.nukkit.item.enchantment.damage.EnchantmentDamageAll;
import cn.nukkit.item.enchantment.protection.EnchantmentProtectionAll;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * author: MagicDroidX
 * NukkitExamplePlugin Project
 */
public class MainClass extends PluginBase implements Listener{

    private static final Random random = new Random(System.currentTimeMillis());
    @Override
    public void onLoad() {
        this.getLogger().info(TextFormat.WHITE + "I've been loaded!");
    }

    @Override
    public void onEnable() {
        this.getLogger().info(TextFormat.DARK_GREEN + "I've been enabled!");

        this.getLogger().info(String.valueOf(this.getDataFolder().mkdirs()));

        //Register the EventListener
        this.getServer().getPluginManager().registerEvents(this, this);

        //PluginTask
        this.getServer().getScheduler().scheduleRepeatingTask(new BroadcastPluginTask(this), 200);

        //Save resources
        this.saveResource("string.txt");

        //Config reading and writing
        Config config = new Config(
                new File(this.getDataFolder(), "config.yml"),
                Config.YAML,
                //Default values (not necessary)
                new LinkedHashMap<String, Object>() {
                    {
                        put("this-is-a-key", "Hello! Config!");
                        put("another-key", true); //you can also put other standard objects!
                    }
                });
        //Now try to get the value, the default value will be given if the key isn't exist!
        this.getLogger().info(String.valueOf(config.get("this-is-a-key", "this-is-default-value")));
        //Don't forget to save it!
        config.save();
    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.DARK_RED + "I've been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.getLogger().info("Command received:" + command.getName());
        if (command.getName().toLowerCase().equals("lucky")) {
            Player player = (Player) sender;
            Position pos = player.getPosition();
            FullChunk chunk = player.getLevel().getChunk((int) pos.x >> 4, (int) pos.z >> 4, true);
            if (!chunk.isGenerated()) {
                chunk.setGenerated();
            }
            if (!chunk.isPopulated()) {
                chunk.setPopulated();
            }
    
            CompoundTag nbt = new CompoundTag().putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", pos.x)).add(new DoubleTag("", pos.y)).add(new DoubleTag("", pos.z)))
                    .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0)).add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                    .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", 0)).add(new FloatTag("", 0)));
            
            Vector3 source = new Vector3(pos.x,pos.y,pos.z);
            if(args.length==0){
                Item luckyBlock = new ItemBlock(new BlockGold());
                Item pickaxeGold = new ItemPickaxeGold();
                dropItem(pickaxeGold, player.getLevel(), source);
                for (int i = 0; i<5;++i){
                    dropItem(luckyBlock, player.getLevel(), source);
                }
            }
            else {
                implementRules(Integer.parseInt(args[0]), chunk, nbt, source, player);
            }
            
            
        }
        return true;
    }
    
    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent ev) {
        //this.getLogger().info("handling block break event" + ev.getBlock().getName());
        
        if (ev.isCancelled()) {
            return;
        }
        Block block = ev.getBlock();        
        if (block.getId() == Block.GOLD_BLOCK )
        {
            this.getLogger().info("block broken for:" + block.getName());
            FullChunk chunk = block.getLevel().getChunk((int) block.x >> 4, (int) block.z >> 4, true);
            if (!chunk.isGenerated()) {
                chunk.setGenerated();
            }
            if (!chunk.isPopulated()) {
                chunk.setPopulated();
            }
    
            CompoundTag nbt = new CompoundTag().putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", block.x)).add(new DoubleTag("", block.y)).add(new DoubleTag("", block.z)))
                    .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0)).add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                    .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", 0)).add(new FloatTag("", 0)));
            
            int rand = rand(1,30);
            ListTag pos = nbt.getList("Pos");
            Vector3 source = new Vector3(((DoubleTag)pos.get(0)).getData()+1,((DoubleTag)pos.get(1)).getData(),((DoubleTag)pos.get(2)).getData());
                        
            implementRules(rand, chunk, nbt, source, ev.getPlayer());           
            
        }
    }

    private void spawnEntity(Entity entity) {
        //
        if (entity != null) {
            this.getLogger().info("entity spawned:" + entity.getName());
            entity.spawnToAll();
        }
        else {
            this.getLogger().info("entity null." );
        }
    }

    private void dropItem(Item item, Level level, Vector3 source) {
        level.dropItem(source, item);
    }

    private void implementRules(int input,FullChunk chunk, CompoundTag nbt, Vector3 source, Player player) {
        switch (input) {
            case 1:                
                spawnEntity(Entity.createEntity("Creeper", chunk, nbt));
                break;
            case 2:
                for (int i = 0; i < 4; i++) {
                    Entity entity = Entity.createEntity("Skeleton", chunk, nbt); 
                    Vector3 pos = new Vector3(entity.x+2*i, entity.y, entity.z);
                    entity.setPosition(pos);
                    spawnEntity(entity);
                }
                break;
            case 3:
                spawnEntity(new EntityLightning(chunk, nbt));
                break;
            case 4:
                Block block = new BlockLava();                
                placeBlock(block, source, player.getLevel(), player);
                break;

            case 5:
                Block blockbr = new BlockBedrock();                    
                placeBlock(blockbr,source, player.getLevel(), player);
                Block blocksp = new BlockSignPostWithText("Well, here's your","problem!","","");                
                source.y+=1;
                placeBlock(blocksp, source, player.getLevel(), player);
                
                break;
            case 6:
                Item itemArmour = new ItemHelmetDiamond();
                itemArmour.addEnchantment(new EnchantmentProtectionAll());
                dropItem(itemArmour, player.getLevel(), source);
                itemArmour = new ItemChestplateDiamond();
                itemArmour.addEnchantment(new EnchantmentProtectionAll());
                dropItem(itemArmour, player.getLevel(), source);
                itemArmour = new ItemLeggingsDiamond();
                itemArmour.addEnchantment(new EnchantmentProtectionAll());
                dropItem(itemArmour, player.getLevel(), source);
                itemArmour = new ItemBootsDiamond();
                itemArmour.addEnchantment(new EnchantmentProtectionAll());
                dropItem(itemArmour, player.getLevel(), source);
                break;
            case 7:
                Item itemSword = new ItemSwordDiamond();
                itemSword.addEnchantment(new EnchantmentFireAspect(), new EnchantmentDamageAll());
                dropItem(itemSword, player.getLevel(), source);
            case 8:
                Level level = player.getLevel();
                Item itemHoe = new ItemHoeGold();
                dropItem(itemHoe, level, source);
                break;
            case 9:
                Item itemBow = new ItemBow();
                Item itemArrrow = new ItemArrow();
                itemBow.addEnchantment(new EnchantmentBowInfinity());
                itemBow.addEnchantment(new EnchantmentBowPower());
                dropItem(itemBow, player.getLevel(), source);
                dropItem(itemArrrow, player.getLevel(), source);
                break;
            case 10:
                Item itemAxe = new ItemAxeDiamond();
                dropItem(itemAxe, player.getLevel(), source);
                break;
            case 11:
                spawnEntity(Entity.createEntity("Bat", chunk, nbt));
                break;
            case 12:
                // Location needs to include Level in addition to x,y,z
                Location locationTP1 = new Location(0,100,0,player.getLevel());
                player.teleport(locationTP1, TeleportCause.PLUGIN);
                break;
            case 13:
                for(int i=0; i<2; i++){                 
                    Block blockiron = new BlockIronBars();
                    source.x+=1;                
                    placeBlock(blockiron,source, player.getLevel(), player);
                    Block blockiron2 = new BlockIronBars();
                    source.z+=1;
                    placeBlock(blockiron2,source, player.getLevel(), player);
                    Block blockiron3 = new BlockIronBars();
                    source.x-=1;                
                    placeBlock(blockiron3,source, player.getLevel(), player);
                    Block blockiron4 = new BlockIronBars();
                    source.x-=1;       
                    placeBlock(blockiron4,source, player.getLevel(), player);
                    Block blockiron5 = new BlockIronBars();
                    source.z-=1;                
                    placeBlock(blockiron5,source, player.getLevel(), player);
                    Block blockiron6 = new BlockIronBars();
                    source.z-=1;
                    placeBlock(blockiron6,source, player.getLevel(), player);
                    Block blockiron7 = new BlockIronBars();
                    source.x+=1;   
                    placeBlock(blockiron7,source, player.getLevel(), player);
                    Block blockiron8 = new BlockIronBars();
                    source.x+=1;    
                    placeBlock(blockiron8,source, player.getLevel(), player);
                    source.x-=1;
                    source.y+=1;
                    source.z+=1;
                }
                Block blocklava = new BlockLava();    
                placeBlock(blocklava, source, player.getLevel(), player);
                break;
            case 14:
                for(int i=0;i<64;++i){
                    Item itemMelon = new ItemMelon();
                    dropItem(itemMelon, player.getLevel(), source);
                }
                break;
            case 15:
                for(int i=0;i<4;++i){
                    Item itemTree = new ItemBlock( new BlockSapling());
                    dropItem(itemTree, player.getLevel(), source);
                }
                for(int i=0;i<4;++i){
                    Item itemBone = new ItemBone();
                    dropItem(itemBone, player.getLevel(), source);
                }
                break;
            case 16:
                for(int i=0;i<144;++i){
                    Item itemDiamondblock = new ItemDiamond();
                    dropItem(itemDiamondblock, player.getLevel(), source);
                }
                break;
            case 17:
                Location locationTP2 = new Location(200,100,0,player.getLevel());
                player.teleport(locationTP2, TeleportCause.PLUGIN);
                break;
            case 18:
                for(int i=0;i<18;++i){
                    Item itemGoldingots = new ItemIngotGold();
                    dropItem(itemGoldingots, player.getLevel(), source);
                }
                break;
            case 19:
                spawnEntity(Entity.createEntity("Ghast", chunk, nbt));
                break;
            case 20:
                spawnEntity(Entity.createEntity("Witch", chunk, nbt));
                break; 
            case 21:
                spawnEntity(new EntityPrimedTNT(chunk, nbt));
                break;
            case 22:
                for(int i=0;i<5;++i){
                    Item itemRiches = new ItemBlock(new BlockObsidian());
                    dropItem(itemRiches, player.getLevel(), source);
                }
                for(int i=0;i<12;++i){
                    Item itemRiches2 = new ItemDiamond();
                    dropItem(itemRiches2, player.getLevel(), source);
                }
                for(int i=0;i<24;++i){
                    Item itemRiches3 = new ItemIngotIron();
                    dropItem(itemRiches3, player.getLevel(), source);
                }
                for(int i=0;i<24;++i){
                   Item itemRiches4 = new ItemIngotGold();
                    dropItem(itemRiches4, player.getLevel(), source);
                }
                for(int i=0;i<5;++i){
                    Item itemRiches5 = new ItemEnderPearl();
                    dropItem(itemRiches5, player.getLevel(), source);
                }
                break;
            case 23:
                for(int i=0;i<5;++i){
                    Item Potion1 = new ItemPotion(rand(5, 36));
                    dropItem(Potion1, player.getLevel(), source);
                }
                break;
            case 25:
                Item itemBrewing = new ItemBlazeRod();
                dropItem(itemBrewing, player.getLevel(), source);
                itemBrewing = new ItemNetherWart();
                dropItem(itemBrewing, player.getLevel(), source);
                break;
            case 26:
                for(int i=0;i<5;++i){
                    Item itemApple = new ItemAppleGoldEnchanted();
                    dropItem(itemApple, player.getLevel(), source);
                }
                break;
            case 27:
                Item itemDiamondSword = new ItemSwordDiamond();
                dropItem(itemDiamondSword, player.getLevel(), source);
                break;   

        }
    }
    private void placeBlock(Block blockToPlace, Vector3 source, Level level, Player player)
    {
        blockToPlace.setComponents(source.x, source.y, source.z);
        blockToPlace.setLevel(level);
        
        blockToPlace.place(new ItemHoeGold(), blockToPlace, blockToPlace, BlockFace.UP, 0, 0, 0, player);
        

    }
    public int rand(int min, int max) {
        if (min == max) {
            return max;
        }
        return min + random.nextInt(max - min);
    }
    

}
