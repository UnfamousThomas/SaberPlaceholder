package me.unfamousthomas.saberplaceholder;

import me.unfamousthomas.saberplaceholder.placeholders.FactionPointsPlaceholder;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author thomp (11/06/2023)
 */
public class SaberPlaceHolder extends JavaPlugin{

    /**
     * What to do when plugin starts
     */
    @Override
    public void onEnable() {
        new FactionPointsPlaceholder().register();

    }

}
