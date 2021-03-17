package gg.solarmc.kitpvp;

import gg.solarmc.kitpvp.kill.DamageListener;
import gg.solarmc.kitpvp.kill.damage.DamageMap;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.clans.ClanManager;
import gg.solarmc.loader.clans.ClansKey;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import gg.solarmc.loader.kitpvp.KitPvpManager;
import me.aurium.beetle.spigot.SpigotBeetle;
import me.aurium.beetle.spigot.SpigotBeetleFactory;
import org.bukkit.plugin.java.JavaPlugin;

import javax.activation.DataHandler;

//TODO decoupling
public class KitpvpPlugin extends JavaPlugin {

    private DataCenter dataCenter;

    private KitPvpManager kitPvpManager;
    private ClanManager clanManager;

    private DataHandler dataHandler;
    private RewardHandler rewardHandler;
    private DamageMap damageMap;

    private final SpigotBeetle spigotBeetle = new SpigotBeetleFactory(this,false).build();

    @Override
    public void onEnable() {
       this.dataCenter = this.getServer().getDataCenter();
       this.kitPvpManager = this.dataCenter.getDataManager(KitPvpKey.INSTANCE);
       this.clanManager = this.dataCenter.getDataManager(ClansKey.INSTANCE);

       this.dataHandler = new DataHandler(dataCenter,kitPvpManager,clanManager);
       this.rewardHandler = new RewardHandler();
       this.damageMap = new DamageMap(this);

       this.getServer().getPluginManager().registerEvents(new DamageListener(this,dataHandler, damageMap,rewardHandler),this);
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public KitPvpManager getKitPvpManager() {
        return kitPvpManager;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public SpigotBeetle getBeetle() {
        return spigotBeetle;
    }




}
