package sodlish.rpgskills.common.capabilities;

import sodlish.rpgskills.Configuration;
import sodlish.rpgskills.common.network.NotifyWarning;
import sodlish.rpgskills.common.skills.Requirement;
import sodlish.rpgskills.common.skills.Skill;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class SkillModel implements INBTSerializable<CompoundNBT>
{
    public int[] skillLevels = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
    
    // Get Level for Skill
    
    public int getSkillLevel(Skill skill)
    {
        return skillLevels[skill.index];
    }
    
    // Set Level for Skill
    
    public void setSkillLevel(Skill skill, int level)
    {
        skillLevels[skill.index] = level;
    }
    
    // Increase Level for Skill
    
    public void increaseSkillLevel(Skill skill)
    {
        skillLevels[skill.index]++;
    }
    
    // Can Player Use Item
    
    public boolean canUseItem(PlayerEntity player, ItemStack item)
    {
        return canUse(player, item.getItem().getRegistryName());
    }
    
    // Can Player Use Block
    
    public boolean canUseBlock(PlayerEntity player, Block block)
    {
        return canUse(player, block.getBlock().getRegistryName());
    }
    
    // Can Player Use Entity
    
    public boolean canUseEntity(PlayerEntity player, Entity entity)
    {
        return canUse(player, entity.getType().getRegistryName());
    }
    
    // Can Player Use
    
    private boolean canUse(PlayerEntity player, ResourceLocation resource)
    {
        Requirement[] requirements = Configuration.getRequirements(resource);
        
        if (requirements != null)
        {
            for (Requirement requirement : requirements)
            {
                if (getSkillLevel(requirement.skill) < requirement.level)
                {
                    if (player instanceof ServerPlayerEntity)
                    {
                        NotifyWarning.send(player, resource);
                    }
                
                    return false;
                }
            }
        }
    
        return true;
    }
    
    // Get Player Skills
    
    public static SkillModel get(PlayerEntity player)
    {
        return player.getCapability(SkillCapability.INSTANCE).orElseThrow(() ->
            new IllegalArgumentException("Player " + player.getName().getContents() + " does not have a Skill Model!")
        );
    }
    
    // Get Local Player Skills
    
    public static SkillModel get()
    {
        return Minecraft.getInstance().player.getCapability(SkillCapability.INSTANCE).orElseThrow(() ->
            new IllegalArgumentException("Player does not have a Skill Model!")
        );
    }
    
    // Serialise and Deserialise
    
    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt("mining", skillLevels[0]);
        compound.putInt("cooking", skillLevels[1]);
        compound.putInt("power", skillLevels[2]);
        compound.putInt("defense", skillLevels[3]);
        compound.putInt("crafting", skillLevels[4]);
        compound.putInt("farming", skillLevels[5]);
        compound.putInt("focus", skillLevels[6]);
        compound.putInt("magic", skillLevels[7]);
        
        return compound;
    }
    
    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        skillLevels[0] = nbt.getInt("mining");
        skillLevels[1] = nbt.getInt("cooking");
        skillLevels[2] = nbt.getInt("power");
        skillLevels[3] = nbt.getInt("defense");
        skillLevels[4] = nbt.getInt("crafting");
        skillLevels[5] = nbt.getInt("farming");
        skillLevels[6] = nbt.getInt("focus");
        skillLevels[7] = nbt.getInt("magic");
    }
}