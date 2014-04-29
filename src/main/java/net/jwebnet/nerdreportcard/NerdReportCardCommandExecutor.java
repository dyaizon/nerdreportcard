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
package net.jwebnet.nerdreportcard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import static org.bukkit.Bukkit.getOfflinePlayer;
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
public class NerdReportCardCommandExecutor implements CommandExecutor {

    /*
     TODO: Add command flag support from github issue #9
     http://pubs.opengroup.org/onlinepubs/009695399/basedefs/xbd_chap12.html
     */
    private final NerdReportCard pluginNerdReportCard;
    private String rgWorldName;

    public NerdReportCardCommandExecutor(NerdReportCard plugin) {
        this.pluginNerdReportCard = plugin;
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

        if (cmd.getName().equalsIgnoreCase("rcadd")) {
            // Add a report card line
        
            if (sender.hasPermission("nerdreportcard.add")) {

            } else {
                return true;
            }

        }
        if (cmd.getName().equalsIgnoreCase("rcedit")) {
            // Edit a report card line
            if (sender.hasPermission("nerdreportcard.edit")) {

            } else {
                return true;
            }

        }
        if (cmd.getName().equalsIgnoreCase("rcremove")) {
            // Remove a report card line
            
            if (sender.hasPermission("nerdreportcard.remove")) {

            } else {
                return true;
            }

        }

                if (cmd.getName().equalsIgnoreCase("rcid")) {
            // View a report card line
            
            if (sender.hasPermission("nerdreportcard.view")) {

            } else {
                return true;
            }

        }

        if (cmd.getName().equalsIgnoreCase("reportcard")) {
            // View a report card
            
            // Populate the reportcard line
            Integer report_id = 0;
            String report_reason = "foo";
            String reportcard_line = "";
            Integer point_count = 3;
            String reporter_name = "Islid";
            String reporter_date = "2014/05/01";

            if (sender.hasPermission("nerdreportcard.admin.*")) {
                // Sender is allowed to see all data
                reportcard_line = "#" + report_id + " (" + point_count + ") " + report_reason + " by " + reporter_name + " @ " + reporter_date;
                sender.sendMessage(reportcard_line);
                return true;
            } else if (sender.hasPermission("nerdreportcard.warnings")) {
                // Sender is allowed to see number of warning points
                // Show points, but not who did it or when
                reportcard_line = "#" + report_id + " (" + point_count + ") " + report_reason;
                sender.sendMessage(reportcard_line);
                return true;

            } else if (sender.hasPermission("nerdreportcard.report")) {
                // Sender is only allowed to see id and reason
                reportcard_line = "#" + report_id + " " + report_reason;
                sender.sendMessage(reportcard_line);
                return true;
            } else {
                // Sender has no permissions to run this commend, fail though.
                return true;
            }
        }
        return true;
    }
}
