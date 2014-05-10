/*
 * Copyright (C) 2014 Matthew Green
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

import static java.lang.Integer.parseInt;
import java.io.IOException;
import java.util.List;
import static net.jwebnet.nerdreportcard.i18n.I18n.tl;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Matthew Green
 */
public class ReportCommands implements CommandExecutor {

    private final NerdReportCard plugin;
    private final ReportManager manager;

    public ReportCommands(NerdReportCard plugin) {
        this.plugin = plugin;
        manager = plugin.getManager();
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
    public boolean onCommand(CommandSender sender, Command cmd, String label,
            String[] args) {

        // Add a report card
        if (cmd.getName().equalsIgnoreCase("rcadd")) {
            return cmdAdd(sender, cmd, label, args);

        }

        // Edit a report card
        if (cmd.getName().equalsIgnoreCase("rcedit")) {
            return cmdEdit(sender, cmd, label, args);
        }

        // Reload config
        if (cmd.getName().equalsIgnoreCase("rcreload")) {
            return cmdReload(sender, cmd, label, args);

        }

        // List a report card
        if (cmd.getName().equalsIgnoreCase("rclist")) {
            return cmdList(sender, cmd, label, args);
        }

        // List a report card by id
        if (cmd.getName().equalsIgnoreCase("rcid")) {
            return cmdId(sender, cmd, label, args);
        }

        // Delete a report card by id
        if (cmd.getName().equalsIgnoreCase("rcremove")) {
            return cmdRemove(sender, cmd, label, args);
        }

        return true;
    }
    
    private boolean checkPermArgs(CommandSender sender, String permission,
            int min_args, int arg_len)
    {
        boolean success = true;
        
        /*
         * Check permission
         */
        if (success) {
            if (!sender.hasPermission(permission)) {
                success = false;
                sender.sendMessage("You do not have permission to do this!");
            }
        }
        
        /*
         * Check enough arguments were specified.
         */
        if (success) {
            if (arg_len < min_args) {
                success = false;
                sender.sendMessage("Not enough arguments specified!");
            }
        }
        
        return success;
    }

    private Boolean cmdAdd(CommandSender sender, Command cmd, String label, String[] args) {
        boolean success = true;
        int i = 0;
        int warningPoints = 0;
        String playerName;
        StringBuilder sb = new StringBuilder();
        String reason;
        
        success = checkPermArgs(sender, "nerdreportcard.edit", 2, args.length);
        
        /*
         * Iterate through the arguments.
         */
        if (success) {
            /*
            Attempt to parse to integer:
            - success --> warning points
            - error   --> username (no warning points specified)
             */
            
            try {
                warningPoints = parseInt(args[0]);
            } catch (NumberFormatException exception) {
                i--;
            }
            i++;
            
            playerName = args[i];
            i++;

            // Check if valid player
            //if (Bukkit.getOfflinePlayer(playerName).hasPlayedBefore() == false) {
            //    sender.sendMessage(tl("errPlayerNotSeenOnServer"));
            //    return true;
            //}

            // Get reason
            for (; i < args.length; i++) {
                sb.append(args[i]);
                sb.append(" ");
            }
            reason = sb.toString();
            reason = reason.trim();

            try {
                manager.addReport(playerName, warningPoints, reason,
                        sender.getName());
            } catch (IOException e) {
                success = false;
            }
            
            if (success) {
                sender.sendMessage(tl("reportAddSuccess"));
            }
        }

        return success;
    }

    private Boolean cmdEdit(CommandSender sender, Command cmd, String label, String[] args) {
        boolean success = true;
        
        success = checkPermArgs(sender, "nerdreportcard.edit", 2, args.length);
        
        if (success) {
            Integer reportId;
            // Check if valid id
            if (!args[0].startsWith("#")) {
                sender.sendMessage(tl("errReportIdInvalidPrefix"));
                return false;
            }
            try {
                reportId = parseInt(args[0].substring(1));

            } catch (NumberFormatException err) {
                sender.sendMessage(tl("errReportIdNotANumber"));
                return true;

            }

            ReportRecord record = manager.getReport(reportId);
            if (record == null) {
                // No record found by that id
                sender.sendMessage(tl("errReportIdNotFound"));
                return true;
            }

            // Check if points were provided
            boolean pointsProvided;
            Integer warningPoints;
            try {
                warningPoints = parseInt(args[1]);
                pointsProvided = true;

            } catch (NumberFormatException exception) {
                warningPoints = 0;
                pointsProvided = false;

            }

            // Get reason
            String[] reasonArr = args;
            int newLen;
            if (pointsProvided) {
                newLen = 2;
            } else {
                newLen = 1;
            }
            String[] tmpArr = new String[reasonArr.length - newLen];
            System.arraycopy(reasonArr, newLen, tmpArr, 0, tmpArr.length);

            // Combine the reason into a single string
            String reason = "";
            for (String s : tmpArr) {
                reason += s + " ";
            }

            // remove the training space
            reason = reason.trim();

            try {
                manager.editReport(record, warningPoints, reason, sender.getName());
            } catch (IOException e) {
                success = false;
            }
            
            if (success) {
                sender.sendMessage(tl("reportEditSuccess"));
            }

        }

        return success;
    }

    private Boolean cmdReload(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("nerdreportcard.admin")) {
            plugin.reloadConfig();
            plugin.i18n.updateLocale("en");
            sender.sendMessage(tl("reloadSuccess"));
        }
        return true;
    }

    private Boolean cmdList(CommandSender sender, Command cmd, String label, String[] args) {
        boolean success = true;
        String requestedPlayer;

        if (args.length > 0 && sender.hasPermission("nerdreportcard.list.others")) {
            requestedPlayer = args[0];
        } else {
            // Sender can only list themselves
            requestedPlayer = sender.getName();
        }

        // View a report card
        if (sender.hasPermission("nerdreportcard.admin")) {
            // Sender is allowed to see all data and search for others

            List<ReportRecord> reports = manager.getReports(requestedPlayer);

            if (reports.isEmpty()) {
                sender.sendMessage(tl("errNoReportsFound"));
                return true;
            }
            sender.sendMessage(tl("reportsFound", reports.size()));

            sender.sendMessage(tl("reportFullTop", requestedPlayer));
            for (ReportRecord r : reports) {
                if (r.active) {
                    sender.sendMessage(tl("reportLineFull", r.reportId, r.getPoints(), r.reason, r.reporterName, r.getTimeString()));
                }
            }
            sender.sendMessage(tl("reportFullBottom", requestedPlayer));
        } else if (sender.hasPermission("nerdreportcard.list")) {
            // Sender is only allowed to see id and reason and their own
            List<ReportRecord> reports = manager.getReports(requestedPlayer);

            if (reports.isEmpty()) {
                sender.sendMessage(tl("errNoReportsFound"));
                return true;
            }
            sender.sendMessage(tl("reportsFound", reports.size()));

            for (ReportRecord r : reports) {
                if (r.active) {
                    sender.sendMessage(tl("reportLineLite", r.reportId, r.reason));

                }
            }
        }
        return true;
    }

    private Boolean cmdId(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("nerdreportcard.admin")) {
            if (args.length > 0) {
                // Check if valid id
                Integer reportId;
                if (!args[0].startsWith("#")) {
                    sender.sendMessage(tl("errReportIdInvalidPrefix"));
                    return true;
                }
                try {
                    reportId = parseInt(args[0].substring(1));

                } catch (NumberFormatException err) {
                    sender.sendMessage(tl("errReportIdNotANumber"));
                    return true;

                }
                ReportRecord record = manager.getReport(reportId);
                if (record == null) {
                    // No record found by that id
                    sender.sendMessage(tl("errReportIdNotFound"));
                    return true;
                }
                sender.sendMessage(tl("reportIdTop", sender.getName()));
                sender.sendMessage(tl("reportLineFull", record.reportId, record.getPoints(), record.reason, record.reporterName, record.getTimeString()));

            }

        }
        return true;
    }

    private Boolean cmdRemove(CommandSender sender, Command cmd, String label, String[] args) {
        boolean success = true;
        Integer reportId = 0;
        
        success = checkPermArgs(sender, "nerdreportcard.admin", 1, args.length);
        
        if (success) {
            // Check if valid id
            if (!args[0].startsWith("#")) {
                sender.sendMessage(tl("errReportIdInvalidPrefix"));
                success = false;
            }
            try {
                reportId = parseInt(args[0].substring(1));
            } catch (NumberFormatException err) {
                sender.sendMessage(tl("errReportIdNotANumber"));
                success = false;
            }
        }
            
        if (success) {
            ReportRecord record = manager.getReport(reportId);
            if (record == null) {
                // No record found by that id
                sender.sendMessage(tl("errReportIdNotFound"));
            } else {
                try {
                    manager.deleteReport(reportId);
                } catch (IOException e) {
                    success = false;
                }
                if (success) {
                    sender.sendMessage(tl("reportDeleted", reportId));
                }
            }
        }
        
        return success;
    }
}
