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
public final class RegionsAudit extends JavaPlugin {

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
                ProtectedRegion region = regionManager.getRegion(args[0]);
                Set<String> regionOwnersSet = region.getOwners().getPlayers();
                String[] regionOwners = regionOwnersSet.toArray(new String[0]);
                sender.sendMessage(region.getId() + " - " + region.getOwners().toPlayersString());
                if (regionOwners.length > 0) {
                    // First owner
                    Player target = Bukkit.getServer().getPlayer(regionOwners[0]);
                    Calendar mydate = Calendar.getInstance();
                    mydate.setTimeInMillis(target.getLastPlayed());
                    sender.sendMessage("Owner 1: " + mydate.get(Calendar.DAY_OF_MONTH) + "." + mydate.get(Calendar.MONTH) + "." + mydate.get(Calendar.YEAR));
                }
            }

            // do something
        }
        return true;
    }
}
