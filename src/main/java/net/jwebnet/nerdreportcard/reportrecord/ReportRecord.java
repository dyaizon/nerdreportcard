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
package net.jwebnet.nerdreportcard.reportrecord;

import static java.lang.Integer.parseInt;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Joseph W Becher <jwbecher@jwebnet.net>
 */
public class ReportRecord {

    private Integer reportId;
    private String playerName;
    private Integer warningPoints;
    private String reportReason;
    private String reporterName;
    private String reportDate;
    private Boolean active;
    private Boolean empty;

    public ReportRecord() {
        active = true;
        empty = true;
    }

    /**
     *
     * @param recordData
     */
    public void init(ConfigurationSection recordData) {
        reportId = parseInt(recordData.getName());
        playerName = recordData.getString("playerName");
        warningPoints = recordData.getInt("warningPoints");
        reportReason = recordData.getString("reason");
        reporterName = recordData.getString("reporterName");
        reportDate = recordData.getString("reportDate");
        active = recordData.getBoolean("active");
        empty = false;
    }

    /**
     *
     * @return
     */
    public Boolean isEmpty(){
        return empty;
    }
    
    /**
     *
     * @return
     */
    public Integer getReportId() {
        return reportId;
    }

    public String getPlayerName() {
        return playerName;
    }

    /**
     *
     * @return
     */
    public Integer getPoints() {
        return warningPoints;
    }

    /**
     *
     * @return
     */
    public String getReason() {
        return reportReason;
    }

    /**
     *
     * @return
     */
    public String getReporter() {
        return reporterName;
    }

    /**
     *
     * @return
     */
    public String getDate() {
        return reportDate;
    }
    
    /**
     *
     * @return
     */
    public Boolean getActive(){
        return active;
    }
    
    /**
     *
     * @param isActive
     */
    public void setActive(Boolean isActive){
        active = isActive;
    }

}
