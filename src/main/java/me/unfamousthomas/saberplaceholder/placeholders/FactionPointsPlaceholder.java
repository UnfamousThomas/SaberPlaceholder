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
        if(params.isBlank() || params.isEmpty()) return generateFactionString(player);
        else if(params.equalsIgnoreCase("player")) return generateFactionRankingForPlayer(player);
        return generateSpecificFactionString(params);

    }

    private String generateFactionRankingForPlayer(OfflinePlayer player) {
        FPlayer fp = FPlayers.getInstance().getByOfflinePlayer(player);
        if(!fp.hasFaction() || isInWildSafeWar(ChatColor.stripColor(fp.getFaction().getTag())))
            return ChatColor.translateAlternateColorCodes('&', "&cYou are not part of any faction! Please join a faction to see its ranking");

        Faction f = fp.getFaction();
        int i = 1;
        for (Faction fact : Factions.getInstance().getAllFactions()) {
            if(fact.equals(f)) break;
            if(!isInWildSafeWar(ChatColor.stripColor(fact.getTag()))) i += 1;
        }

        return ChatColor.translateAlternateColorCodes('&', "&a" + i + ".&f " + ChatColor.stripColor(f.getTag()) + ": &a" + f.getPoints());

    }

    /**
     * Method to generate specific factions placeholder (when params)
     * @param params Parameters after "_"
     * @return String with colors
     */
    private String generateSpecificFactionString(String params) {
        if(isFactionRank(params)) {
            int num = Integer.parseInt(params);
            ArrayList<Faction> factions = Factions.getInstance().getAllFactions();
            //Sorts factions based on getPoints(). Very brute-force sorting, but should work in this scenario.
            factions.sort(Comparator.comparingInt(Faction::getPoints));
            Collections.reverse(factions); //Because col.sort sorts in ascending by default
            //Finds faction with correct num. This would probably possible with some logic of indexOf, but since we do not know where warzone, wilderness and safezone are exactly, did this solution for now.
            int i = 1;
            Faction factionResult = null;
            for (Faction faction : factions) {
                if(i == num) {
                    factionResult = faction;
                }
                if(!isInWildSafeWar(ChatColor.stripColor(faction.getTag()))) i +=1;
            }
            //If we did not find the faction sent it here.
            if(factionResult == null) {
                return ChatColor.translateAlternateColorCodes('&', "&cA faction with that rank does not exist");
            } else {
                //If we found one send the correct msg with rank + points
                return ChatColor.translateAlternateColorCodes('&', "&a" + num + ".&f " + ChatColor.stripColor(factionResult.getTag()) + ": &a" + factionResult.getPoints());
            }
        } else {
            return ChatColor.translateAlternateColorCodes('&', "&cA faction with that rank does not exist");
        }
    }

    /**
     * Basic checks: is params a num, and is there enough factions for such a faction rank to exist
     * @param params Parameter we check against
     * @return If such a ranking could exist
     */
    private boolean isFactionRank(String params) {
        try {
            int num = Integer.parseInt(params);
            int totalFac = Factions.getInstance().getAllFactions().size()-3;
            return totalFac >= num;
        } catch (NumberFormatException e) {
            return false;
        }
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
        Collections.reverse(factions); //Because col.sort sorts in ascending by default
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
                sb.append("&a").append(i).append(". ").append("&f").append(tag).append("&8: &a").append(faction.getPoints());
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
            sb.append("&a").append(personalFacPlacing).append(". ").append("&f").append(faction.getTag()).append("&8: &a").append(faction.getPoints());

        }
        return ChatColor.translateAlternateColorCodes('&', sb.toString());

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
        return tag.equalsIgnoreCase("safezone");
    }
}
