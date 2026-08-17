package me.zenith.skypvp.manager;

import me.zenith.skypvp.ZenithSkyPvP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Tab header/footer for Spigot 1.8.x without compiling against NMS.
 * RankSystem remains responsible for player rank/prefixes.
 */
public class TabManager {
    private final ZenithSkyPvP plugin;

    public TabManager(ZenithSkyPvP plugin) {
        this.plugin = plugin;
    }

    public void updateAll() {
        if (!plugin.getConfig().getBoolean("tab.enabled", true)) {
            return;
        }

        int online = Bukkit.getOnlinePlayers().size();
        int max = plugin.getConfig().getInt("max-players", 200);
        String domain = plugin.getConfig().getString("domain", "skypvp.zenithmc.net");

        String header = plugin.getConfig().getString("tab.header", "&b&lZENITH SKYPVP")
                .replace("%online%", String.valueOf(online))
                .replace("%max%", String.valueOf(max))
                .replace("%domain%", domain);

        String footer = plugin.getConfig().getString("tab.footer", "&7Online: &f%online%/%max%&r\n&b%domain%")
                .replace("%online%", String.valueOf(online))
                .replace("%max%", String.valueOf(max))
                .replace("%domain%", domain);

        header = color(header);
        footer = color(footer);

        for (Player player : Bukkit.getOnlinePlayers()) {
            sendHeaderFooter(player, header, footer);
        }
    }

    private void sendHeaderFooter(Player player, String header, String footer) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName()
                    .substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);

            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            Object handle = craftPlayer.getMethod("getHandle").invoke(player);

            Field connectionField = handle.getClass().getField("playerConnection");
            Object connection = connectionField.get(handle);

            Class<?> serializer = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer");
            Method parse = serializer.getMethod("a", String.class);

            Object headerComponent = parse.invoke(null, "{\"text\":\"" + jsonEscape(header) + "\"}");
            Object footerComponent = parse.invoke(null, "{\"text\":\"" + jsonEscape(footer) + "\"}");

            Class<?> packetClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutPlayerListHeaderFooter");
            Object packet = packetClass.getDeclaredConstructor().newInstance();

            Field headerField = packetClass.getField("a");
            Field footerField = packetClass.getField("b");
            headerField.set(packet, headerComponent);
            footerField.set(packet, footerComponent);

            Method sendPacket = connection.getClass().getMethod("sendPacket",
                    Class.forName("net.minecraft.server." + version + ".Packet"));
            sendPacket.invoke(connection, packet);
        } catch (Throwable ignored) {
            // Header/footer is cosmetic. Never prevent the rest of Zenith from loading.
        }
    }

    private String jsonEscape(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }
}
