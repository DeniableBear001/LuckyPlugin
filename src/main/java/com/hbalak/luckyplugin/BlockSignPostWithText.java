package com.hbalak.luckyplugin;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.block.BlockWallSign;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.Player;

public class BlockSignPostWithText extends BlockSignPost {
    String text1,text2,text3,text4;
    public BlockSignPostWithText(String text1, String text2, String text3, String text4) {
        super(0);
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (face != BlockFace.DOWN) {
            CompoundTag nbt = new CompoundTag()
                    .putString("id", BlockEntity.SIGN)
                    .putInt("x", (int) block.x)
                    .putInt("y", (int) block.y)
                    .putInt("z", (int) block.z)
                    .putString("Text1", text1)
                    .putString("Text2", text2)
                    .putString("Text3", text3)
                    .putString("Text4", text4);
            int meta;
            if (face == BlockFace.UP) {
                meta = (int) Math.floor(((player.yaw + 180) * 16 / 360) + 0.5) & 0x0f;
                getLevel().setBlock(block, new BlockSignPost(meta), true);
            } else {
                meta = face.getIndex();
                getLevel().setBlock(block, new BlockWallSign(meta), true);
            }

            if (player != null) {
                nbt.putString("Creator", player.getUniqueId().toString());
            }

            if (item.hasCustomBlockData()) {
                for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                    nbt.put(aTag.getName(), aTag);
                }
            }

            BlockEntitySign sign = new BlockEntitySign(getLevel().getChunk((int) block.x >> 4, (int) block.z >> 4), nbt);
            sign.spawnToAll();
            return true;
        }

        return false;
    }
}