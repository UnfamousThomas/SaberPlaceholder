package me.unfamousthomas.saberplaceholder.placeholders;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Declaration of PlaceHolder using PlaceholderAPI
 */
public class FactionPointsPlaceholder extends PlaceholderExpansion {


    /**
     * What the id for the expansion is.
     * @return ID
     */
    @Override
    public @NotNull String getIdentifier() {
        return "custompointslist";
    }

    /**
     * Method to set author.
     * @return ME
     */
    @Override
    public @NotNull String getAuthor() {
        return "UnfamousThomas";
    }

    /**
     * Version of expansion.
     * @return Version
     */
    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    /**
     * A very simple boolean method. Tells placeholderapi to persist or not to persist placeholder through reload .
     * @return TRUE since we want to persist.
     */
    @Override
    public boolean persist() {
        return true;
    }


    /**
     * Triggers on placeholder request
     * @param player Player who is requesting placeholder
     * @param params Parameters
     * @return String with placeholder replaced.
     */
    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        return generateFactionString(player);

    }

    /**
     * Internal method to get the list of top 10 + added the users factions ranking
     * @param player Player who we are looking for
     * @return string of top10 ... players faction
     */
    private String generateFactionString(OfflinePlayer player) {
        FPlayer fp = FPlayers.getInstance().getByOfflinePlayer(player);
        //Holds list of all factions
        ArrayList<Faction> factions = Factions.getInstance().getAllFactions();
        //Sorts factions based on getPoints(). Very brute-force sorting, but should work in this scenario.
        factions.sort(Comparator.comparingInt(Faction::getPoints));
        if(factions.isEmpty() || factions.size() < 4) return "&c&lNo factions found.";
        StringBuilder sb = new StringBuilder();
        int i = 1;
        int personalFacPlacing = -1;
        //Loops through factions
        for (Faction faction : factions) {
            //If user has no faction and top 10 found cancel.
            if(i >= 11 && !fp.hasFaction()) break;
            //Factions name without colors
            String tag = ChatColor.stripColor(faction.getTag());
            //Do not include default stuff
            if(isInWildSafeWar(tag)) continue;
            //If we have not added 10 add to ranking
            if(i <= 11) {
                sb.append("&a").append(i).append(". ").append("&f").append(tag).append(": &a").append(faction.getPoints());
                sb.append("\n");
            }
            //Add players faction in last line.
            if(fp.hasFaction() && fp.getFaction().equals(faction)) {
                personalFacPlacing = i;
                if(i > 11) break;
            }
            i+=1;
        }

        //This isn't in the for loop, because it would cause issues sometimes. Needs to always be added as the last thing.
        if(fp.hasFaction() && !isInWildSafeWar(fp.getFaction().getTag())) {
            sb.append("&f...");
            sb.append("\n");
            Faction faction = fp.getFaction();
            sb.append("&a").append(personalFacPlacing).append(". ").append("&f").append(faction.getTag()).append(": &a").append(faction.getPoints());

        }
        return sb.toString();

    }

    /**
     * Simple method to check if we are in the factions we ignore
     * @param tag Tag to check
     * @return True value of being in the factions
     */
    private boolean isInWildSafeWar(String tag) {
        tag = ChatColor.stripColor(tag);
        if(tag.equalsIgnoreCase("wilderness")) return true;
        if(tag.equalsIgnoreCase("warzone")) return true;
        if(tag.equalsIgnoreCase("safezone")) return true;

        return false;
    }
}
