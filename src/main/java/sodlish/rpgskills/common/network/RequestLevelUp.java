package sodlish.rpgskills.common.network;

import sodlish.rpgskills.Configuration;
import sodlish.rpgskills.RPGskills;
import sodlish.rpgskills.common.capabilities.SkillModel;
import sodlish.rpgskills.common.skills.Skill;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestLevelUp
{
    private final int skill;
    
    public RequestLevelUp(Skill skill)
    {
        this.skill = skill.index;
    }
    
    public RequestLevelUp(PacketBuffer buffer)
    {
        skill = buffer.readInt();
    }
    
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(skill);
    }
    
    public void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = context.get().getSender();
            SkillModel skillModel = SkillModel.get(player);
            Skill skill = Skill.values()[this.skill];
            
            int cost = Configuration.getStartCost() + (skillModel.getSkillLevel(skill) - 1) * Configuration.getCostIncrease();
            
            if (skillModel.getSkillLevel(skill) < Configuration.getMaxLevel() && (player.isCreative() || player.experienceLevel >= cost))
            {
                if (!player.isCreative()) player.giveExperienceLevels(-cost);
                skillModel.increaseSkillLevel(skill);
                
                SyncToClient.send(player);
            }
        });
        
        context.get().setPacketHandled(true);
    }
    
    // Send Packet
    
    public static void send(Skill skill)
    {
        RPGskills.NETWORK.sendToServer(new RequestLevelUp(skill));
    }
}