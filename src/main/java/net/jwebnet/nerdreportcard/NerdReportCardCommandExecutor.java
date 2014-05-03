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
import net.jwebnet.nerdreportcard.reportrecord.ReportRecord;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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

            if (sender.hasPermission("nerdreportcard.edit")) {
                if (args.length > 2) {

                    // Check if valid player
                    String playerName = args[0];
                    if (Bukkit.getOfflinePlayer(playerName).hasPlayedBefore() == false) {
                        sender.sendMessage("Not a valid player");
                        return true;
                    }

                    Integer warningPoints = parseInt(args[1]);
                    String[] reasonArr = args;
                    String[] tmpArr = new String[reasonArr.length - 2];
                    System.arraycopy(reasonArr, 2, tmpArr, 0, tmpArr.length);

                    // Combine the reason into a single string
                    String reason = "";
                    for (String s : tmpArr) {
                        reason += s + " ";
                    }

                    // remove the training space
                    reason = reason.trim();

                    this.pluginNerdReportCard.addNewReportcard(playerName, warningPoints, reason, sender.getName());
                    sender.sendMessage("Report added.");

                }
            }

            return true;
        }

        // Edit a report card
        if (cmd.getName().equalsIgnoreCase("rcedit")) {

            if (sender.hasPermission("nerdreportcard.edit")) {
                if (args.length > 2) {

                    // Check if valid id
                    Integer reportId;
                    try {
                        reportId = parseInt(args[0]);

                    } catch (NumberFormatException err) {
                        sender.sendMessage("Invalid report id given");
                        return true;

                    }

                    ReportRecord record = this.pluginNerdReportCard.getReportById(reportId);
                    if (record.isEmpty()) {
                        // No record found by that id
                        sender.sendMessage("No report found by that id.");
                        return true;
                    }

                    Integer warningPoints = parseInt(args[1]);
                    String[] reasonArr = args;
                    String[] tmpArr = new String[reasonArr.length - 2];
                    System.arraycopy(reasonArr, 2, tmpArr, 0, tmpArr.length);

                    // Combine the reason into a single string
                    String reason = "";
                    for (String s : tmpArr) {
                        reason += s + " ";
                    }

                    // remove the training space
                    reason = reason.trim();

                    this.pluginNerdReportCard.editReportcard(reportId, warningPoints, reason, sender.getName());
                    sender.sendMessage("Report edited.");

                }
            }

            return true;
        }

        // Reload config
        if (cmd.getName().equalsIgnoreCase("rcreload")) {
            if (sender.hasPermission("nerdreportcard.admin")) {
                this.pluginNerdReportCard.reloadConfig();
            }

        }

        // List a report card
        if (cmd.getName().equalsIgnoreCase("rclist")) {

            String requestedPlayer;

            if (args.length > 0 && sender.hasPermission("nerdreportcard.list.others")) {
                // Sender is allowed to list other players

                // Check if valid player
                String playerName = args[0];
                if (Bukkit.getOfflinePlayer(playerName).hasPlayedBefore() == false) {
                    sender.sendMessage("Not a valid player");
                    return true;
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
                    sender.sendMessage("Zero reports found");
                    return true;
                }
                sender.sendMessage(reports.size() + " reports found");

                for (ReportRecord r : reports) {
                    sender.sendMessage("#" + r.getReportId() + " (" + r.getPoints().toString() + ") " + r.getReason() + " by " + r.getReporter() + " @ " + r.getDate());
                }

                return true;
            } else if (sender.hasPermission("nerdreportcard.list")) {
                // Sender is only allowed to see id and reason and their own
                Set<ReportRecord> reports = this.pluginNerdReportCard.getReportsByPlayerName(requestedPlayer);

                if (reports.isEmpty()) {
                    sender.sendMessage("Zero reports found");
                    return true;
                }
                sender.sendMessage(reports.size() + " reports found");

                for (ReportRecord r : reports) {
                    sender.sendMessage("#" + r.getReportId() + " " + r.getReason());
                }

                return true;
            }
        }

        // List a report card by id
        if (cmd.getName().equalsIgnoreCase("rcid")) {
            if (sender.hasPermission("nerdreportcard.admin")) {
                if (args.length > 0) {
                    Integer reportId = parseInt(args[0]);
                    ReportRecord record = this.pluginNerdReportCard.getReportById(reportId);
                    if (record.isEmpty()) {
                        // No record found by that id
                        sender.sendMessage("No report found by that id.");
                        return true;
                    }
                    sender.sendMessage("#" + record.getReportId() + " (" + record.getPoints().toString() + ") " + record.getReason() + " by " + record.getReporter() + " @ " + record.getDate());

                }

            }

        }

        // List a report card by id
        if (cmd.getName().equalsIgnoreCase("rcremove")) {
            if (sender.hasPermission("nerdreportcard.admin")) {
                if (args.length > 1) {
                    if (!args[0].equals(args[1]) || args.length == 1) {
                        // Ids do not match or second id not entered
                        sender.sendMessage("Please enter the id twice to comfirm deletion.");
                        return true;
                    }
                    String reportId = args[0];
                    this.pluginNerdReportCard.getConfig().set("reports." + reportId, null);
                    this.pluginNerdReportCard.saveConfig();
                    sender.sendMessage("Report #" + reportId + " was deleted. (If it existed)");
                    return true;

                }

            }

        }

        return true;
    }
}
