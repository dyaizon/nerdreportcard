/*
 * Copyright (C) 2014 Joseph W Becher <jwbecher@jwebnet.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jwebnet.regionsaudit;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author joseph
 */
public final class RegionAudit extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("onEnable has been invoked!");

    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable has been invoked!");
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }

    /**
     *
     * @param sender
     * @param cmd
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rgaudit")) { // If the player typed /rgaudit then do the following...
            if (!(sender instanceof Player)) {
                // Issued by console
                return false;
            } else {
                // Issued by player
                if (args.length < 1) {
                    sender.sendMessage("Not enough arguments!");
                    return false;
                }
                Player player = (Player) sender;
                RegionManager regionManager = getWorldGuard().getRegionManager(player.getWorld());
                String regionName = args[0];
                ProtectedRegion region = regionManager.getRegion(regionName);
                if (region == null) {
                    // region not found
                    sender.sendMessage("Region named " + regionName + " was not found. Please check the spelling.");
                } else {
                    // region was found
                    Set<String> regionOwnersSet = region.getOwners().getPlayers();
                    String[] regionOwners = regionOwnersSet.toArray(new String[0]);
                    sender.sendMessage(region.getId() + " - " + region.getOwners().toPlayersString());
                    if (regionOwners.length > 0) {
                        for (String regionOwner : regionOwners) {
                            // loop through owners
                            Player target = Bukkit.getServer().getPlayer(regionOwner);
                            Calendar mydate = Calendar.getInstance();
                            mydate.setTimeInMillis(target.getLastPlayed());
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            sender.sendMessage(regionOwner + " was last seen on  " + format.format(mydate.getTime()));
                        }
                    } else {
                        // region has no owners
                        sender.sendMessage("The region names " + regionName + " has no owners.");
                    }
                }
            }

            // do something
        }
        return true;
    }
}
