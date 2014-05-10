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

package net.jwebnet.nerdreportcard.utils;

import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.util.LinkedList;
import java.util.List;
import net.jwebnet.nerdreportcard.NerdReportCard;
import net.jwebnet.nerdreportcard.ReportRecord;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Matthew Green
 */
public class YAMLDatabase implements Database {
    private final NerdReportCard plugin;
    
    public YAMLDatabase (NerdReportCard plugin) {
        this.plugin = plugin;
    }
    
    public ReportRecord getReport(Integer reportId)
    {
        ReportRecord record = null;
        ConfigurationSection recordData = plugin.getConfig().getConfigurationSection("reports." + reportId.toString());

        if (recordData != null) {
            record = new ReportRecord(parseInt(recordData.getName()),
                    recordData.getString("playerName"),
                    recordData.getString("reporterName"),
                    recordData.getString("reason"),
                    recordData.getString("reportDate"),
                    recordData.getInt("warningPoints"));
        }
        
        return record;
    }
    
    public List<ReportRecord> getReports(String username)
    {
        List<ReportRecord> reportList = new LinkedList<ReportRecord>();
        
        ConfigurationSection recordData = plugin.getConfig().getConfigurationSection("reports");

        for (String r : recordData.getKeys(false)) {
            ReportRecord record = getReport(parseInt(r));
            if ((record.playerName.toLowerCase().equals(username.toLowerCase())) && record.active) {
                reportList.add(record);
            }
        }
        
        return reportList;
    }
   
    public void setReport(ReportRecord record) throws IOException
    {
        // Get the id for this report
        Integer thisReportId = parseInt(plugin.getConfig().getString("nextReportId"));
        
        // Get the id for the next report
        Integer nextReportId = thisReportId + 1;

        // Create the new report secion
        ConfigurationSection reportData = plugin.getConfig().createSection("reports." + thisReportId);
        
        // Save the report
        reportData.set("playerName", record.playerName);
        reportData.set("warningPoints", record.getPoints());
        reportData.set("reason", record.reason);
        reportData.set("reporterName", record.reporterName);
        reportData.set("reportDate", record.getTimeString());
        reportData.set("active", record.active);

        // Update the next report id
        plugin.getConfig().set("nextReportId", nextReportId.toString());

        plugin.saveConfig();
    }
    
    public void editReport(ReportRecord record) throws IOException
    {
        // Create the new report secion
        ConfigurationSection reportData = plugin.getConfig().getConfigurationSection("reports." + record.reportId.toString());

        // Save the report
        reportData.set("playerName", record.playerName);
        reportData.set("warningPoints", record.getPoints());
        reportData.set("reason", record.reason);
        reportData.set("reporterName", record.reporterName);
        reportData.set("reportDate", record.getTimeString());
        reportData.set("active", record.active);

        plugin.saveConfig();
    }
    
    public void deleteReport(Integer reportId) throws IOException
    {
        ConfigurationSection reportData = plugin.getConfig().getConfigurationSection("reports." + reportId.toString());
        reportData.set("active", Boolean.FALSE);
        plugin.saveConfig();
    }
}
