/*
 *  Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.buildbattle3.arena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marcel S.
 * @version 1.0
 * @website https://marcely.de/
 * You can do whatever you want with this class but I will call the police if you edit or remove these Doc Comments
 */
public class ArenaBoard {

    private final String name, criterion;

    private final Scoreboard bukkitScoreboard;
    private final Objective obj;
    private String title;
    private Row[] rows = new Row[0];
    private List<Row> rowCache = new ArrayList<>();
    private boolean finished = false;

    public ArenaBoard(String name, String criterion, String title){
        this.name = name;
        this.criterion = criterion;
        this.title = title;

        this.bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.obj = this.bukkitScoreboard.registerNewObjective(name, criterion);

        this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.obj.setDisplayName(title);
    }

    public void display(Player player){
        player.setScoreboard(this.bukkitScoreboard);
    }

    public String getName() {
        return name;
    }

    public String getCriterion() {
        return criterion;
    }

    public String getTitle() {
        return title;
    }

    public Row[] getRows() {
        return rows;
    }

    public @Nullable Row addRow(String message){
        if(this.finished){
            new NullPointerException("Can not add rows if scoreboard is already finished").printStackTrace();
            return null;
        }

        try{
            final Row row = new Row(this, message, rows.length);

            this.rowCache.add(row);

            return row;
        }catch(Exception e){
            return null;
        }
    }

    public void finish(){
        if(this.finished){
            new NullPointerException("Can not finish if scoreboard is already finished").printStackTrace();
            return;
        }

        this.finished = true;

        for(int i=rowCache.size()-1; i>=0; i--){
            final Row row = rowCache.get(i);

            final Team team = this.bukkitScoreboard.registerNewTeam(name + "." + criterion + "." + (i+1));
            team.addEntry(ChatColor.values()[i] + "");
            this.obj.getScore(ChatColor.values()[i] + "").setScore(rowCache.size()-i);

            row.team = team;
            row.setMessage(row.message);
        }

        this.rows = rowCache.toArray(new Row[rowCache.size()]);
    }


    public static class Row {

        private final ArenaBoard scoreboard;
        private Team team;
        private final int rowInScoreboard;
        private String message;

        public Row(ArenaBoard sb, String message, int row){
            this.scoreboard = sb;
            this.rowInScoreboard = row;
            this.message = message;
        }

        public void setMessage(String message){
            this.message = message;

            if(scoreboard.finished){
                final String[] parts = splitStringWithChatcolorInHalf(message);

                this.team.setPrefix(parts[0]);
                this.team.setSuffix(parts[1]);
            }
        }

        public ArenaBoard getScoreboard() {
            return scoreboard;
        }

        public int getRowInScoreboard() {
            return rowInScoreboard;
        }

        public String getMessage() {
            return message;
        }

        private static String[] splitStringWithChatcolorInHalf(String str){
            final String[] strs = new String[2];

            ChatColor cc1 = ChatColor.WHITE, cc2 = null;
            Character lastChar = null;

            strs[0] = "";
            for(int i=0; i<str.length()/2; i++){
                final char c = str.charAt(i);

                if(lastChar != null){
                    final ChatColor cc = charsToChatColor(new char[]{ lastChar, c });

                    if(cc != null){
                        if(cc.isFormat())
                            cc2 = cc;
                        else{
                            cc1 = cc;
                            cc2 = null;
                        }
                    }
                }

                strs[0] += c;
                lastChar = c;
            }

            strs[1] = (cc1 != null ? cc1 : "") + "" + (cc2 != null ? cc2 : "") + str.substring(str.length()/2);

            return strs;
        }

        private static @Nullable ChatColor charsToChatColor(char[] chars){
            for(ChatColor cc:ChatColor.values()){
                final char[] ccChars = cc.toString().toCharArray();

                int same=0;
                for(int i=0; i<2; i++){
                    if(ccChars[i] == chars[i])
                        same++;
                }

                if(same == 2) return cc;
            }

            return null;
        }
    }
}
