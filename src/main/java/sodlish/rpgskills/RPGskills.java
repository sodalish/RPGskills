package sodlish.rpgskills;

import sodlish.rpgskills.client.Keybind;
import sodlish.rpgskills.client.Overlay;
import sodlish.rpgskills.client.screen.InventoryTabs;
import sodlish.rpgskills.client.Tooltip;
import sodlish.rpgskills.common.CuriosCompat;
import sodlish.rpgskills.common.EventHandler;
import sodlish.rpgskills.common.capabilities.SkillModel;
import sodlish.rpgskills.common.capabilities.SkillStorage;
import sodlish.rpgskills.common.commands.Commands;
import sodlish.rpgskills.common.network.NotifyWarning;
import sodlish.rpgskills.common.network.RequestLevelUp;
import sodlish.rpgskills.common.network.SyncToClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

@Mod("rpgskills")
public class RPGskills
{
    public static SimpleChannel NETWORK;
    
    public RPGskills()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.getConfig());
    }
    
    // Common Setup
    
    private void commonSetup(final FMLCommonSetupEvent event)
    {
        CapabilityManager.INSTANCE.register(SkillModel.class, new SkillStorage(), () -> { throw new UnsupportedOperationException("No Implementation!"); });
        Configuration.load();
        
        NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation("rpgskills", "main_channel"), () -> "1.0", s -> true, s -> true);
        NETWORK.registerMessage(1, SyncToClient.class, SyncToClient::encode, SyncToClient::new, SyncToClient::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        NETWORK.registerMessage(2, RequestLevelUp.class, RequestLevelUp::encode, RequestLevelUp::new, RequestLevelUp::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        NETWORK.registerMessage(3, NotifyWarning.class, NotifyWarning::encode, NotifyWarning::new, NotifyWarning::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new Commands());
        
        // Curios Compatibility
    
        if (ModList.get().isLoaded("curios"))
        {
            MinecraftForge.EVENT_BUS.register(new CuriosCompat());
        }
    }
    
    // Client Setup
    
    private void clientSetup(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new InventoryTabs());
        MinecraftForge.EVENT_BUS.register(new Tooltip());
        MinecraftForge.EVENT_BUS.register(new Keybind());
        MinecraftForge.EVENT_BUS.register(new Overlay());
    }
}