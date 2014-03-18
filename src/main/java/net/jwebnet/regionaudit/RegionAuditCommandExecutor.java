/*
 * Copyright (C) 2014 joseph
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
package net.jwebnet.regionaudit;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import static org.bukkit.Bukkit.getOfflinePlayer;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.Bukkit.getWorlds;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author joseph
 */
public class RegionAuditCommandExecutor implements CommandExecutor {

    /*
     TODO: Add command flag support from github issue #9
     http://pubs.opengroup.org/onlinepubs/009695399/basedefs/xbd_chap12.html
     */
    private final RegionAudit pluginRegionAudit;
    private String rgWorldName;

    public RegionAuditCommandExecutor(RegionAudit plugin) {
        this.pluginRegionAudit = plugin;
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin pluginWorldGuard = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (pluginWorldGuard == null || !(pluginWorldGuard instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) pluginWorldGuard;
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
        if (cmd.getName().equalsIgnoreCase("rgaudit")) {
            // If the player typed /rgaudit then do the following...
            World rgWorld = null;

            /*
             possible command syntax
             /rgaudit <plotname> {<worldname>}
             /rgaudit <plotname> <worldname>
            
             worldname is required for console
             */
            if (!(sender instanceof Player)) {
                // Issued by console
                if (args.length < 2 || args.length > 2) {
                    // not correct number of arguments
                    sender.sendMessage("usage: /rgaudit <plotname> <worldname>");
                    return true;
                } else {
                    /* prep for command execution
                     * args[0] = plotname
                     * args[1] = worldname
                     */

                    // set the world name
                    this.rgWorldName = args[1];
                }
            } else {
                // Issued by player
                if (args.length < 2 || args.length > 2) {
                    // not correct number of arguments
                    sender.sendMessage("usage: /rgaudit <plotname> [<worldname>]");
                    return true;
                }
                /* prep for command execution
                 * args[0] = plotname
                 * args[1] = worldname
                 */
                Player player = (Player) sender;
                if (args.length == 2) {
                    // set the world name from args[1]
                    this.rgWorldName = args[1];
                } else {
                    // set the worldname as the current world for the sender
                    this.rgWorldName = player.getWorld().getName();
                }
            }

            // prep work done 
            /*
             check if we are searching in a valid world
             */
            List<World> worldList = getWorlds();
            for (World i : worldList) {
                if (i.getName().equals(this.rgWorldName)) {
                    rgWorld = i;
                    break;
                }
            }
            if (rgWorld == null) {
                sender.sendMessage("World named " + this.rgWorldName + " was not found. Please check the spelling.");
                return true;
            }

            RegionManager regionManager = getWorldGuard().getRegionManager(rgWorld);
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

                if (regionOwnersSet.size() > 0) {
                    /*
                     if the regionOwnersSet size is greater then 0, loop though the set
                     */
                    // List name of region
                    sender.sendMessage("Region named " + regionName + " has the foloowing owners.");
                    for (String regionOwner : regionOwners) {
                        // loop through owners
                        /* TODO: github issue #1
                         Find out why the owners display in reverse order of how they are displayed
                         */

                        Calendar mydate = Calendar.getInstance();
                        /*
                         http://jd.bukkit.org/beta/apidocs/org/bukkit/Server.html#getOfflinePlayer(java.lang.String)
                         This will return an object even if the player does not exist. To this method, all players will exist.
                         */
                        OfflinePlayer ownerPlayer = getOfflinePlayer(regionOwner);
                        /*
                         Check if this is an actual player
                         */
                        if (ownerPlayer.hasPlayedBefore() == true) {
                            mydate.setTimeInMillis(ownerPlayer.getLastPlayed());
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            sender.sendMessage(regionOwner + " was last seen on  " + format.format(mydate.getTime()));

                        } else {
                            // This player name is not registered on the server
                            sender.sendMessage("Owner named " + regionOwner + " not found on server. Please check spelling.");
                            return true;
                        }

                    }
                } else {
                    /*
                     this region has no owners
                     */
                    sender.sendMessage("The region named " + regionName + " has no owners.");
                }
            }

            // do something
        }
        return true;
    }

}
