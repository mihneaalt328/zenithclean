package me.zenith.skypvp.manager;
import me.zenith.skypvp.ZenithSkyPvP; import org.bukkit.configuration.file.YamlConfiguration; import java.io.File; import java.io.IOException; import java.util.*;
public class DataManager { private final ZenithSkyPvP p; private final File f; private YamlConfiguration y; public DataManager(ZenithSkyPvP p){this.p=p; f=new File(p.getDataFolder(),"players.yml");}
public void load(){if(!p.getDataFolder().exists())p.getDataFolder().mkdirs(); y=YamlConfiguration.loadConfiguration(f);}
public void save(){try{y.save(f);}catch(IOException e){p.getLogger().warning("Could not save players.yml: "+e.getMessage());}}
private String k(UUID u){return "players."+u.toString();}
public int coins(UUID u){return y.getInt(k(u)+".coins",p.getConfig().getInt("coins.starting",100));}
public void coins(UUID u,int v){y.set(k(u)+".coins",Math.max(0,v));}
public int kills(UUID u){return y.getInt(k(u)+".kills",0);} public int deaths(UUID u){return y.getInt(k(u)+".deaths",0);} public int streak(UUID u){return y.getInt(k(u)+".streak",0);} public int best(UUID u){return y.getInt(k(u)+".best",0);}
public void kill(UUID u){int s=streak(u)+1; y.set(k(u)+".kills",kills(u)+1); y.set(k(u)+".streak",s); if(s>best(u))y.set(k(u)+".best",s); coins(u,coins(u)+p.getConfig().getInt("coins.kill-reward",25));}
public void death(UUID u){y.set(k(u)+".deaths",deaths(u)+1); y.set(k(u)+".streak",0); coins(u,coins(u)-p.getConfig().getInt("coins.death-loss",0));}
}
