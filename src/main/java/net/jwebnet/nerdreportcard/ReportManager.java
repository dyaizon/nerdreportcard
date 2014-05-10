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

import java.io.IOException;
import java.util.List;
import net.jwebnet.nerdreportcard.utils.Database;
import net.jwebnet.nerdreportcard.utils.YAMLDatabase;

/**
 * Manages storage and retrieval of reports in the Database.
 * 
 * @author Matthew Green
 */
public class ReportManager {
    Database database;
    
    public ReportManager(NerdReportCard plugin) {
        database = new YAMLDatabase(plugin);
    }
    
    public ReportRecord getReport(Integer reportID) {
        return database.getReport(reportID);
    }
    
    public List<ReportRecord> getReports(String username) {
        return database.getReports(username);
    }
    
    /**
     * Create a new entry in the database.
     * 
     * @param playerName
     * @param points
     * @param reason
     * @param reporter
     * 
     * @throws IOException
     */
    public void addReport (String playerName, Integer points, String reason,
            String reporter) throws IOException
    {
        ReportRecord record;
        
        record = new ReportRecord(playerName, reporter, points, reason);

        database.setReport(record);
    }
    
    public void editReport(ReportRecord report, Integer points, String reason,
            String reporter) throws IOException
    {
        ReportRecord newReport = new ReportRecord(report.reportId,
                report.playerName, reporter, reason, report.getTimeString(),
                points);
        database.editReport(newReport);
    }
    
    public void deleteReport(Integer reportId) throws IOException
    {
        database.deleteReport(reportId);
    }
}
