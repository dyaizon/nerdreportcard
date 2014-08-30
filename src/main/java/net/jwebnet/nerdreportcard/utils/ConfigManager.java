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

import net.jwebnet.nerdreportcard.NerdReportCard;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Retrieves and sets configuration.
 *
 * @author Matthew Green
 */
public class ConfigManager {

    public boolean useSql = false;
    public String sqlAddress;
    public String sqlPort;
    public String sqlUsername;
    public String sqlPassword;
    public String sqlDb;
    public String sqlPrefix;
    public String yamlDbFile;

    public ConfigManager(NerdReportCard plugin) {
        FileConfiguration config = plugin.getConfig();

        useSql = config.getBoolean("database.sql.use", false);
        sqlAddress = config.getString("database.sql.address", "localhost");
        sqlPort = config.getString("database.sql.port", "3306");
        sqlUsername = config.getString("database.sql.username", "user");
        sqlPassword = config.getString("database.sql.password", "pass");
        sqlDb = config.getString("database.sql.dbname", "nerdrc");
        sqlPrefix = config.getString("database.sql.prefix", "nrc_");

        yamlDbFile = config.getString("database.yamlfile", "reports.yml");
    }
}
