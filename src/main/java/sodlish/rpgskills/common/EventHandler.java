package sodlish.rpgskills.common;

import sodlish.rpgskills.Configuration;
import sodlish.rpgskills.common.capabilities.SkillModel;
import sodlish.rpgskills.common.capabilities.SkillProvider;
import sodlish.rpgskills.common.network.SyncToClient;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler
{
    // Left Click Block
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        PlayerEntity player = event.getPlayer();
        ItemStack item = event.getItemStack();
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        SkillModel model = SkillModel.get(player);
        
        if (!player.isCreative() && (!model.canUseItem(player, item) || !model.canUseBlock(player, block)))
        {
            event.setCanceled(true);
        }
    }
    
    // Right Click Block
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        PlayerEntity player = event.getPlayer();
        ItemStack item = event.getItemStack();
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        SkillModel model = SkillModel.get(player);
        
        if (!player.isCreative() && (!model.canUseItem(player, item) || !model.canUseBlock(player, block)))
        {
            event.setCanceled(true);
        }
    }
    
    // Right Click Item
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        PlayerEntity player = event.getPlayer();
        ItemStack item = event.getItemStack();
        
        if (!player.isCreative() && !SkillModel.get(player).canUseItem(player, item))
        {
            event.setCanceled(true);
        }
    }
    
    // Right Click Entity
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickEntity(PlayerInteractEvent.EntityInteract event)
    {
        PlayerEntity player = event.getPlayer();
        Entity entity = event.getTarget();
        ItemStack item = event.getItemStack();
        
        if (!player.isCreative())
        {
            if (!SkillModel.get(player).canUseEntity(player, entity) || !SkillModel.get(player).canUseItem(player, item))
            {
                event.setCanceled(true);
            }
        }
    }
    
    // Attack Entity
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttackEntity(AttackEntityEvent event)
    {
        PlayerEntity player = event.getPlayer();
        
        if (player != null)
        {
            ItemStack item = player.getMainHandItem();
    
            if (!player.isCreative() && !SkillModel.get(player).canUseItem(player, item))
            {
                event.setCanceled(true);
            }
        }
    }
    
    // Change Equipment
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChangeEquipment(LivingEquipmentChangeEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            
            if (!player.isCreative() && event.getSlot().getType() == EquipmentSlotType.Group.ARMOR)
            {
                ItemStack item = event.getTo();
                
                if (!SkillModel.get(player).canUseItem(player, item))
                {
                    player.drop(item.copy(), false);
                    item.setCount(0);
                }
            }
        }
    }
    
    // Entity Drops
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityDrops(LivingDropsEvent event)
    {
        if (Configuration.getDisableWool() && event.getEntity() instanceof SheepEntity)
        {
            event.getDrops().removeIf(item -> ItemTags.getAllTags().getTag(new ResourceLocation("minecraft", "wool")).contains(item.getItem().getItem()));
        }
    }
    
    // Player Death
    
    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if (Configuration.getDeathReset() && event.getEntity() instanceof PlayerEntity)
        {
            SkillModel.get((PlayerEntity) event.getEntity()).skillLevels = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
        }
    }
    
    // Capabilities
    
    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof PlayerEntity)
        {
            SkillModel skillModel = new SkillModel();
            SkillProvider provider = new SkillProvider(skillModel);
            
            event.addCapability(new ResourceLocation("rpgskills", "cap_skills"), provider);
            event.addListener(provider::invalidate);
        }
    }
    
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        SkillModel.get(event.getPlayer()).skillLevels = SkillModel.get(event.getOriginal()).skillLevels;
    }
    
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        SyncToClient.send(e.getPlayer());
    }
    
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e)
    {
        SyncToClient.send(e.getPlayer());
    }
    
    @SubscribeEvent
    public void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent e)
    {
        SyncToClient.send(e.getPlayer());
    }
}