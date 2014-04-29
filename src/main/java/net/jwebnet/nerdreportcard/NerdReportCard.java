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

import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author joseph
 */
public final class NerdReportCard extends JavaPlugin {

    /**
     * onEnable is a method from JavaPlugin
     */
    @Override
    public void onEnable() {
        // This will throw a NullPointerException if you don't have the command defined in your plugin.yml file!
        getCommand("rcadd").setExecutor(new NerdReportCardCommandExecutor(this));
        getCommand("rcedit").setExecutor(new NerdReportCardCommandExecutor(this));
        getCommand("rcremove").setExecutor(new NerdReportCardCommandExecutor(this));
        getCommand("reportcard").setExecutor(new NerdReportCardCommandExecutor(this));
    }

    /**
     * onDisable is a method from JavaPlugin
     */
    @Override
    public void onDisable() {
    }

}
