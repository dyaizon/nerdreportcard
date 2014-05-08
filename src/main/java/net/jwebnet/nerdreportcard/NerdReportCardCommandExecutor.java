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

import static java.lang.Integer.parseInt;
import java.util.Set;
import static net.jwebnet.nerdreportcard.i18n.I18n.tl;
import net.jwebnet.nerdreportcard.reportrecord.ReportRecord;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author joseph
 */
public class NerdReportCardCommandExecutor implements CommandExecutor {

    private final NerdReportCard pluginNerdReportCard;

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

    private Boolean cmdAdd(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("nerdreportcard.edit")) {
            if (args.length > 1) {

                /*
                 usage: /rcadd {points} <player_name> <reason>
                 */
                // Check if points were provided
                boolean pointsProvided;
                Integer warningPoints;
                try {
                    warningPoints = parseInt(args[0]);
                    pointsProvided = true;
                } catch (NumberFormatException exception) {
                    warningPoints = 0;
                    pointsProvided = false;

                }

                // Set player
                String playerName;
                if (pointsProvided) {
                    playerName = args[1];
                } else {
                    playerName = args[0];
                }

                // Check if valid player
                if (Bukkit.getOfflinePlayer(playerName).hasPlayedBefore() == false) {
                    sender.sendMessage(tl("errPlayerNotSeenOnServer"));
                    return true;
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

                // remove the trailing space
                reason = reason.trim();

                this.pluginNerdReportCard.addNewReportcard(playerName, warningPoints, reason, sender.getName());
                sender.sendMessage(tl("reportAddSuccess"));

            }
        }

        return true;

    }

    private Boolean cmdEdit(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("nerdreportcard.edit")) {
            if (args.length > 1) {

                /*
                 usage: /rcedit <#report_id> {points} <note>
                 */
                // Check if valid id
                Integer reportId;
                // Check if valid id
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

                ReportRecord record = this.pluginNerdReportCard.getReportById(reportId);
                if (record.isEmpty()) {
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

                this.pluginNerdReportCard.editReportcard(reportId, warningPoints, reason, sender.getName());
                sender.sendMessage(tl("reportEditSuccess"));

            }
        }

        return true;

    }

    private Boolean cmdReload(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("nerdreportcard.admin")) {
            this.pluginNerdReportCard.reloadConfig();
            this.pluginNerdReportCard.i18n.updateLocale("en");
            sender.sendMessage(tl("reloadSuccess"));
        }
        return true;

    }

    private Boolean cmdList(CommandSender sender, Command cmd, String label, String[] args) {

        String requestedPlayer;

        if (args.length > 0 && sender.hasPermission("nerdreportcard.list.others")) {
                // Sender is allowed to list other players

            // Check if valid player
            String playerName = args[0];
            if (Bukkit.getOfflinePlayer(playerName).hasPlayedBefore() == false) {
                sender.sendMessage(tl("errPlayerNotSeenOnServer"));
            }
            requestedPlayer = playerName;
        } else {
            // Sender can only list themselves
            requestedPlayer = sender.getName();

        }

        // View a report card
        if (sender.hasPermission("nerdreportcard.admin")) {
            // Sender is allowed to see all data and search for others

            Set<ReportRecord> reports = this.pluginNerdReportCard.getReportsByPlayerName(requestedPlayer);

            if (reports.isEmpty()) {
                sender.sendMessage(tl("errNoReportsFound"));
                return true;
            }
            sender.sendMessage(tl("reportsFound", reports.size()));

            sender.sendMessage(tl("reportFullTop", requestedPlayer));
            for (ReportRecord r : reports) {
                if (r.getActive()) {
                    sender.sendMessage(tl("reportLineFull", r.getReportId(), r.getPoints().toString(), r.getReason(), r.getReporter(), r.getDate()));

                }
            }
            sender.sendMessage(tl("reportFullBottom", requestedPlayer));

            return true;
        } else if (sender.hasPermission("nerdreportcard.list")) {
            // Sender is only allowed to see id and reason and their own
            Set<ReportRecord> reports = this.pluginNerdReportCard.getReportsByPlayerName(requestedPlayer);

            if (reports.isEmpty()) {
                sender.sendMessage(tl("errNoReportsFound"));
                return true;
            }
            sender.sendMessage(tl("reportsFound", reports.size()));

            for (ReportRecord r : reports) {
                if (r.getActive()) {
                    sender.sendMessage(tl("reportLineLite", r.getReportId(), r.getReason()));

                }
            }

            return true;
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
                ReportRecord record = this.pluginNerdReportCard.getReportById(reportId);
                if (record.isEmpty()) {
                    // No record found by that id
                    sender.sendMessage(tl("errReportIdNotFound"));
                    return true;
                }
                sender.sendMessage(tl("reportIdTop", sender.getName()));
                sender.sendMessage(tl("reportLineFull", record.getReportId(), record.getPoints().toString(), record.getReason(), record.getReporter(), record.getDate()));

            }

        }
        return true;

    }

    private Boolean cmdRemove(CommandSender sender, Command cmd, String label, String[] args) {
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

                ReportRecord record = this.pluginNerdReportCard.getReportById(reportId);
                if (record.isEmpty()) {
                    // No record found by that id
                    sender.sendMessage(tl("errReportIdNotFound"));
                    return true;
                } else {
                    ConfigurationSection reportData = this.pluginNerdReportCard.getConfig().getConfigurationSection("reports." + reportId.toString());
                    reportData.set("active", Boolean.FALSE);
                    this.pluginNerdReportCard.saveConfig();
                    sender.sendMessage(tl("reportDeleted", reportId));
                    return true;

                }

            } else {
                sender.sendMessage(tl("errreportDeleteNoId"));
                return true;

            }

        }
        return true;
    }
}
