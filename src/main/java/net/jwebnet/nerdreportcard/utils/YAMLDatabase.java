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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.util.LinkedList;
import java.util.List;
import net.jwebnet.nerdreportcard.NerdReportCard;
import net.jwebnet.nerdreportcard.ReportRecord;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Matthew Green
 */
public class YAMLDatabase implements Database {
    private final NerdReportCard plugin;
    private final File dataFile;
    private final FileConfiguration configFile;
    private int nextReportId;
    
    public YAMLDatabase(NerdReportCard plugin) {       
        String nextIdStr;
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "reports.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        configFile = new YamlConfiguration();
        try {
            configFile.load(dataFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        
        nextIdStr = configFile.getString("nextReportId");
        if (nextIdStr == null) {
            configFile.set("nextReportId", 1);
            nextReportId = 1;
            configFile.createSection("reports");
        } else {
            nextReportId = parseInt(nextIdStr);
        }
    }
    
    public ReportRecord getReport(Integer reportId)
    {
        ReportRecord record = null;
        ConfigurationSection recordData = configFile.getConfigurationSection("reports." + reportId.toString());

        if (recordData != null) {
            record = new ReportRecord(parseInt(recordData.getName()),
                    recordData.getString("playerName"),
                    recordData.getString("reporterName"),
                    recordData.getString("reason"),
                    recordData.getString("reportDate"),
                    recordData.getInt("warningPoints"),
                    recordData.getBoolean("active"));
        }
        
        return record;
    }
    
    public List<ReportRecord> getReports(String username)
    {
        List<ReportRecord> reportList = new LinkedList<ReportRecord>();
        
        ConfigurationSection recordData = configFile.getConfigurationSection("reports");

        for (String r : recordData.getKeys(false)) {
            ReportRecord record = getReport(parseInt(r));
            if ((record.playerName.toLowerCase().equals(username.toLowerCase())) && record.active) {
                reportList.add(record);
            }
        }
        
        return reportList;
    }
   
    public void addReport(ReportRecord record) throws IOException
    {
        // Get the id for this report
        Integer thisReportId = parseInt(configFile.getString("nextReportId"));
        
        // Get the id for the next report
        Integer nextReportId = thisReportId + 1;

        // Create the new report secion
        ConfigurationSection reportData = configFile.createSection("reports." + thisReportId);
        
        // Save the report
        reportData.set("playerName", record.playerName);
        reportData.set("warningPoints", record.getPoints());
        reportData.set("reason", record.reason);
        reportData.set("reporterName", record.reporterName);
        reportData.set("reportDate", record.getTimeString());
        reportData.set("active", record.active);

        // Update the next report id
        configFile.set("nextReportId", nextReportId.toString());

        configFile.save(dataFile);
    }
    
    public void editReport(ReportRecord record) throws IOException
    {
        // Create the new report secion
        ConfigurationSection reportData = configFile.getConfigurationSection("reports." + record.reportId.toString());

        // Save the report
        reportData.set("playerName", record.playerName);
        reportData.set("warningPoints", record.getPoints());
        reportData.set("reason", record.reason);
        reportData.set("reporterName", record.reporterName);
        reportData.set("reportDate", record.getTimeString());
        reportData.set("active", record.active);

        configFile.save(dataFile);
    }
    
    public void deleteReport(Integer reportId) throws IOException
    {
        ConfigurationSection reportData = configFile.getConfigurationSection("reports." + reportId.toString());
        reportData.set("active", false);
        configFile.save(dataFile);
    }
}
