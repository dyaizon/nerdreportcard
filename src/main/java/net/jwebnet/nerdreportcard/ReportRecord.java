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
package net.jwebnet.nerdreportcard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class representing a single report.
 * 
 * @author Matthew Green
 */
public class ReportRecord {
    public Integer reportId;
    private Integer playerId;
    public final String playerName;
    private Integer playerPoints;
    private Integer reporterId;
    public final String reporterName;
    private final ReportType reportType;
    public final String reason;
    private Date reportTime;
    private final Integer warningPoints;
    public Boolean active;
    private final String dateFormatStr = "yy-MM-dd HH:mm";
    
    private enum ReportType {
        LEGACY,
        Warning,
        Mute,
        Kick,
        TempBan,
        PermaBan,
    }

    public ReportRecord(String playerName, String reporter, Integer points,
            String reason)
    {
        this.playerName = playerName;
        this.reporterName = reporter;
        this.warningPoints = points;
        this.reason = reason;
        this.reportTime = new Date();
        reportType = ReportType.LEGACY;
    }
    
    public ReportRecord(Integer reportId, String playerName,
            String reporterName, String reportReason, String reportTime,
            Integer warningPoints)
    {
        this.reportId = reportId;
        this.playerName = playerName;
        this.reporterName = reporterName;
        this.reportType = ReportType.LEGACY;
        this.reason = reportReason;
        if (reportTime == null) {
            this.reportTime = new Date();
        } else {
            this.reportTime = timeStringToDate(reportTime);
        }
        this.warningPoints = warningPoints;
        this.active = true;
    }
    
    public ReportRecord(Integer reportId, Integer playerId, String playerName,
            Integer playerPoints, Integer reporterId, String reporterName,
            String reportType, String reportReason, String reportTime, 
            Integer warningPoints)
    {
        this.reportId = reportId;
        this.playerId = playerId;
        this.playerName = playerName;
        this.playerPoints = playerPoints;
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.reportType = ReportType.valueOf(reportType);
        this.reason = reportReason;
        if (reportTime == null) {
            this.reportTime = new Date();
        } else {
            this.reportTime = timeStringToDate(reportTime);
        }
        this.warningPoints = warningPoints;
        this.active = true;
    }

    /**
     * Retrieves the number of warning points for this report.
     * 
     * @return Integer
     * The warning points for the report.
     */
    public Integer getPoints()
    {
        Integer points;
        /*
         * If the warning points are set to zero, use the config to calculate
         * the warning points for this report.
         *
         * Otherwise, just return the custom number of points.
         */
        if (warningPoints == 0) {
            points = calculatePoints();
        } else {
            points = warningPoints;
        }
        
        return points;
    }

    /**
     * Retrieves the time this report was entered, in UTC.
     * 
     * @return String
     * String-formatted time of the report.
     */
    public String getTimeString()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        
        return dateFormat.format(reportTime);
    }
    
    private Date timeStringToDate (String timestring) {
        Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        
        try {
            date = dateFormat.parse(timestring);
        } catch (ParseException e) {
            /*
            Maybe the date is in the old format.
            */
            dateFormat.applyPattern("yyyy/MM/dd");
            try {
                date = dateFormat.parse(timestring);
            } catch (ParseException f) {
                date = new Date(0);
            }
        }
        
        return date;
    }
    
    /**
     * Retreives the time this report was entered, in seconds since the Epoch.
     * 
     * @return Long
     * Seconds since the epoch.
     */
    public Long getTime()
    {
        return (reportTime.getTime() / 1000);
    }
    
    /*
     * Calculate the number of warning points for this report.
     */
    private Integer calculatePoints()
    {
        Integer points = 0;
        
        return points;
    }
}
