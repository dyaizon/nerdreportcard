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

    /*
     We need a plugin that allows us to make notes on players that only other 
     staff members can see. These notes should be able to be stored in a text file or files of some kind
     that someone can then go back later and read/edit as they see fit. 
    
    
    
     They should be able to add notes in that would then show up in-game. 
     Those notes should also be able to be larger than the regular text limit you 
     can enter in-game, and the command to enter the note should be as small as possible 
     (something like /add username note would work fine).
    
     ***** /rcadd

     These notes should pop up when a player logs in (but NOT when a player FAILS to log in because they're banned. 
     Our current one does this and it's ULTRA annoying because they can spam our screen over and over, and give us 
     false indication as to whether they logged in or not). 
    
     ***** PlayerJoinEvent
    
     We should be able to add new notes, delete or modify existing notes, 
     and ideally see who created the note and when the note was added 
     (although the latter of those is sort of optional, it would just be nice to have that additional data). 
     Each note should have it's own unique number assigned to it, and not just numbers unique to the player - 
     they should have a globally unique number.
     Something like UUID.dat (except with their real UUID, 
     like HFf503q4Fsrf9q483w8vj0d4.dat or something) would be fine for each player.
    
     ****** /rcadd
     ****** /rcedit
     ****** /rcremove

     For example, I want to be able to go /note info 528 and see that the specified note was assigned by CLyane to playerguy123 on April 23rd, 2014 at 5:12 AM server time, and the note was that this guy's a dick.
    
     ***** /rcid

     A good example of this is how the ReportRTS system works. We can use a command (/list) 
     to see a list of tickets with numbers attached to them, the numbers are globally assigned, 
     and the information is arrayed in a simple but very helpful manner. 
     We can use further commands to find out more detailed information about it as well, 
     but I don't think we'd need to know anything else.

     Finally, we should be able to assign "warning points" to players. 
     They should be able to check their own warning points, 
     and we should be able to check the warning points of all players. 
     We'd like it to be able to send configurable warnings at certain amounts of 
     warning points, and possibly even the ability to execute certain console commands 
     (such as modifying their rank or removing specific permissions from them).

     Example permissions:

     notes.create
     **** /rcadd
     notes.modify
     **** /rcedit
     notes.delete
     ***** /rcremove
     notes.list
     ***** /reportcard
     notes.warnings
     **** not currently a command
     notes.admin - everything above, of course.
     **** PERM: nerdreportcard.admin.*

     A note about the list and warnings permissions - the command to list the 
     information about themselves should be something like /notes list or something 
     (I don't know, I'm not creative) and depending on the permissions they've got 
     assigned, they should see different information. 
     So for example, a player with notes.list should see this when they type /list:

     #466 - Griefing in Mineworld
     #489 - Harassing Players

     But a person with notes.list and notes.warnings should see this by typing the exact same command (/list):

     #466 - Griefing in Mineworld - 2 warning points
     #489 - Harassing Players - 5 warning points

     And a person with notes.list, notes.warnings, AND notes.admin should see this when they type /list playerdude123:

     ~~~~Warnings for playerdude123~~~~
     #466 - Griefing in Mineworld - 2 warning points - RoryTheRoman94 on Apr. 23rd, 2014 @ 5:21 AM
     #489 - Harassing players - 5 warning points - CLyane on Apr 25th, 2014 @ 4:32 PM
     ~~~~Warnings for playerdude123~~~~

     Like I said, a much more ambitious project, but if you can get it working then we'd really owe you, lol. What do you think?
     */
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

        new NerdReportCardListener(this);
    }

    /**
     * onDisable is a method from JavaPlugin
     */
    @Override
    public void onDisable() {
    }

}
