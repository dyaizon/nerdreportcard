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

import java.io.File;
import static java.lang.Integer.parseInt;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.jwebnet.nerdreportcard.i18n.I18n;
import static net.jwebnet.nerdreportcard.i18n.I18n.tl;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin class.
 * 
 * @author Joseph W Becher <jwbecher@jwebnet.net>
 */
public final class NerdReportCard extends JavaPlugin implements Listener {
    protected transient I18n i18n;
    private ReportManager manager;

    /**
     * onEnable is a method from JavaPlugin
     */
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        manager = new ReportManager(this);
        getServer().getPluginManager().registerEvents(this, this);
        // This will throw a NullPointerException if you don't have the command defined in your plugin.yml file!
        /* Reload rhe data fale
         * usage: /rcreload
         */
        getCommand("rcreload").setExecutor(new ReportCommands(this));
        /* Add a new report
         * usage: /rcadd <player_name> <points> <reason>
         */
        getCommand("rcadd").setExecutor(new ReportCommands(this));
        /* Edit an existing report by id
         * usage: /rcedit <report_id> <note>
         */
        getCommand("rcedit").setExecutor(new ReportCommands(this));
        /* Remove a report by id
         * usage: /rcremove <report_id> <report_id>
         */
        getCommand("rcremove").setExecutor(new ReportCommands(this));
        /* List a report
         * usage: /rclist {player}
         */
        getCommand("rclist").setExecutor(new ReportCommands(this));
        /* List a report by id
         * usage: /rcid <reportcard_id>
         */
        getCommand("rcid").setExecutor(new ReportCommands(this));

        // Load the messages file
        saveDefaultResource("messages_en.properties");
        i18n = new I18n(this);
        i18n.onEnable();
        i18n.updateLocale("en");
    }

    /**
     * onDisable is a method from JavaPlugin
     */
    @Override
    public void onDisable() {
    }
    
    public ReportManager getManager() {
        return manager;
    }

    @EventHandler
    public void normalJoin(PlayerJoinEvent event) {
        List<ReportRecord> recordList = manager.getReports(event.getPlayer().getName());
        int totalPoints = 0;
        
        for (ReportRecord record : recordList) {
            totalPoints += record.getPoints();
        }
        
        if (totalPoints != 0) {
            Bukkit.broadcast(tl("playerLoginBannerAdmin", event.getPlayer().getName(), totalPoints), "nerdreportcard.admin");
        }
    }

    public void saveDefaultResource(String fileName) {
        File customConfigFile = new File(getDataFolder(), fileName);

        if (!customConfigFile.exists()) {
            saveResource(fileName, false);
        }
    }
}
