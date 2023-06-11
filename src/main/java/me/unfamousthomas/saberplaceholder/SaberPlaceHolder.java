package me.unfamousthomas.saberplaceholder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.unfamousthomas.saberplaceholder.placeholders.FactionPointsPlaceholder;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author thomp (11/06/2023)
 */
public class SaberPlaceHolder extends JavaPlugin implements Listener {

    /**
     * What to do when plugin starts
     */
    @Override
    public void onEnable() {
        new FactionPointsPlaceholder().register();

        //Bukkit.getPluginManager().registerEvents(this,this); //NB: This is used for testing. Unncoment it for testing again.
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        String join = "%custompointslist%";
        join = PlaceholderAPI.setPlaceholders(event.getPlayer(), join);

        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', join));

    }
}
