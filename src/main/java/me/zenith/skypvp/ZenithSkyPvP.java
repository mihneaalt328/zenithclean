package me.zenith.skypvp;

import me.zenith.skypvp.command.ZenithCommand;
import me.zenith.skypvp.command.KitCommand;
import me.zenith.skypvp.command.CrateCommand;
import me.zenith.skypvp.command.CoinsCommand;
import me.zenith.skypvp.command.StatsCommand;
import me.zenith.skypvp.command.ShopCommand;
import me.zenith.skypvp.listener.GameListener;
import me.zenith.skypvp.manager.DataManager;
import me.zenith.skypvp.manager.ScoreboardManager;
import me.zenith.skypvp.manager.TabManager;
import me.zenith.skypvp.manager.SpawnManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZenithSkyPvP extends JavaPlugin {
    private DataManager data;
    private ScoreboardManager scoreboard;
    private TabManager tab;
    private SpawnManager spawn;

    @Override public void onEnable() {
        saveDefaultConfig();
        saveResource("locations.yml", false);
        saveResource("kits.yml", false);
        saveResource("shop.yml", false);
        saveResource("crates.yml", false);
        data = new DataManager(this); data.load();
        spawn = new SpawnManager(this);
        scoreboard = new ScoreboardManager(this);
        tab = new TabManager(this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getCommand("zspvp").setExecutor(new ZenithCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this)); getCommand("kits").setExecutor(new KitCommand(this));
        getCommand("crate").setExecutor(new CrateCommand(this)); getCommand("crates").setExecutor(new CrateCommand(this));
        getCommand("coins").setExecutor(new CoinsCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("setzspawn").setExecutor(new ZenithCommand(this)); getCommand("zspawn").setExecutor(new ZenithCommand(this));
        getServer().getScheduler().runTaskTimer(this, () -> { scoreboard.updateAll(); tab.updateAll(); }, 20L, 20L);
        getLogger().info("ZENITH SkyPvP 2.2.0 enabled. RankSystem and EssentialsX are left untouched.");
    }
    @Override public void onDisable() { if (data != null) data.save(); }
    public DataManager data(){return data;} public SpawnManager spawn(){return spawn;}
}
