package pl.plajer.buildbattle4.handlers;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import pl.plajer.buildbattle4.Main;
import pl.plajer.buildbattle4.arena.Arena;
import pl.plajer.buildbattle4.arena.ArenaRegistry;

public class ReportManager {

  public static boolean attemptReport(Arena Arena, Player Reporter) {
    if (Arena.getVotingPlot().getOwners().contains(Reporter)) {
      Reporter.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Cant-Report-Own-Plot"));
      return false;
    } /*maybe check if already reported*/
    return createReport(Arena, Reporter);
  }

  public static boolean createReport(Arena Arena, Player Reporter) {
    String reportID = this.generateReportID();
    List<UUID> reportedPlayers = new ArrayList<UUID>();
    Arena.getVotingPlot().getOwners().forEach(p -> reportedPlayers.add(p));
    UUID ReportedBy = Reporter.getUniqueId();
    //File schem = create SChematic Method
    if (/*check if report is saved in schematic*/) {
      Reporter.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Report-Successful"));
      return true;
    }
    Reporter.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Voting-Messages.Report-Failed"));
    return false;
  }

}