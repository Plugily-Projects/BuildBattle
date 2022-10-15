/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2021 Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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
 *
 */

package plugily.projects.buildbattle.boot;

import plugily.projects.buildbattle.Main;
import plugily.projects.minigamesbox.classic.handlers.language.Message;
import plugily.projects.minigamesbox.classic.handlers.language.MessageManager;
import plugily.projects.minigamesbox.classic.utils.services.locale.Locale;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleRegistry;

import java.util.Arrays;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.10.2022
 */
public class MessageInitializer {
  private final Main plugin;

  public MessageInitializer(Main plugin) {
    this.plugin = plugin;
    registerLocales();
  }

  public void registerMessages() {
    getMessageManager().registerMessage("COMMANDS_THEME_BLACKLISTED", new Message("Commands.Theme-Blacklisted", ""));
    getMessageManager().registerMessage("COMMANDS_ADMIN_ADDED_PLOT", new Message("Commands.Admin.Added-Plot", ""));
    getMessageManager().registerMessage("SCOREBOARD_THEME_UNKNOWN", new Message("Scoreboard.Theme-Unknown", ""));
    // scoreboard ingame classic/teams/guess-the-build/guess-the-build-waiting | ending classic/guess-the-build
    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_WINNER", new Message("In-Game.Messages.Game-End.Placeholders.Winner", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_PLACE", new Message("In-Game.Messages.Game-End.Placeholders.Place", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_OWN", new Message("In-Game.Messages.Game-End.Placeholders.Own", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ADMIN_CHANGED_THEME", new Message("In-Game.Messages.Admin.Changed-Theme", ""));

    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_", new Message("In-Game.Messages.Plot.", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_PARTICLE_ADDED", new Message("In-Game.Messages.Plot.Added-Particle", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_NOBODY", new Message("In-Game.Messages.Plot.Nobody", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_TIME_LEFT_TITLE", new Message("In-Game.Messages.Plot.Time-Left.Title", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_TIME_LEFT_CHAT", new Message("In-Game.Messages.Plot.Time-Left.Chat", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_LIMIT_ENTITIES", new Message("In-Game.Messages.Plot.Limit.Entities", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_LIMIT_PARTICLES", new Message("In-Game.Messages.Plot.Limit.Particles", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_PERMISSION_HEAD", new Message("In-Game.Messages.Plot.Permission.Head", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_PERMISSION_PARTICLE", new Message("In-Game.Messages.Plot.Permission.Particle", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_PERMISSION_BIOME", new Message("In-Game.Messages.Plot.Permission.Biome", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_PERMISSION_OUTSIDE", new Message("In-Game.Messages.Plot.Permission.Outside", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_PERMISSION_FLOOR_ITEM", new Message("In-Game.Messages.Plot.Permission.Floor-Item", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_FULL", new Message("In-Game.Messages.Plot.Selector.Full", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_EMPTY", new Message("In-Game.Messages.Plot.Selector.Empty", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_INSIDE", new Message("In-Game.Messages.Plot.Selector.Inside", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_NAME", new Message("In-Game.Messages.Plot.Selector.Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_MENU_NAME", new Message("In-Game.Messages.Plot.Selector.Menu-Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_PLOT_CHOOSE", new Message("In-Game.Messages.Plot.Selector.Plot-Choose", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_SELECTOR_MEMBER", new Message("In-Game.Messages.Plot.Selector.Member", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_NAME", new Message("In-Game.Messages.Plot.Voting.Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_CHAT", new Message("In-Game.Messages.Plot.Voting.Plot-Owner.Chat", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_TITLE", new Message("In-Game.Messages.Plot.Voting.Plot-Owner.Title", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_NEXT", new Message("In-Game.Messages.Plot.Voting.Plot-Owner.Next", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_WAS", new Message("In-Game.Messages.Plot.Voting.Plot-Owner.Was", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_PLOT_OWNER_OWN", new Message("In-Game.Messages.Plot.Voting.Plot-Owner.Own", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_SUCCESS", new Message("In-Game.Messages.Plot.Voting.Success", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_VOTING_WINNER", new Message("In-Game.Messages.Plot.Voting.Winner", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_BUILDER", new Message("In-Game.Messages.Plot.Guess-The-Build.Builder", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_ROUND", new Message("In-Game.Messages.Plot.Guess-The-Build.Round", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_CHARS", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Chars", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_NAME", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_WAS", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Was", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_TITLE", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Title", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESSED", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Guessed", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_BEING_SELECTED", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Being-Selected", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_TITLE", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Guess.Title", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_POINTS", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Guess.Points", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_GUESSED", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Guess.Guessed", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_CANT_TALK", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Guess.Cant-Talk", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_GTB_THEME_GUESS_BUILDER", new Message("In-Game.Messages.Plot.Guess-The-Build.Theme.Guess.Builder", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_NPC_NAME", new Message("In-Game.Messages.Plot.NPC.Name", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_NPC_CREATED", new Message("In-Game.Messages.Plot.NPC.Created", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_PLOT_NPC_CITIZENS", new Message("In-Game.Messages.Plot.NPC.Install-Citizens", ""));


    getMessageManager().registerMessage("MENU_PERMISSION", new Message("Menu.Permission", ""));
    getMessageManager().registerMessage("MENU_BUTTONS_BACK_ITEM_NAME", new Message("Menu.Buttons.Back.Item.Name", ""));
    getMessageManager().registerMessage("MENU_BUTTONS_BACK_ITEM_LORE", new Message("Menu.Buttons.Back.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_LOCATION", new Message("Menu.Location", ""));
    getMessageManager().registerMessage("MENU_OPTION_INVENTORY", new Message("Menu.Option.Inventory", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_INVENTORY", new Message("Menu.Option.Content.Particle.Inventory", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_ITEM_NAME", new Message("Menu.Option.Content.Particle.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_ITEM_LORE", new Message("Menu.Option.Content.Particle.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_ITEM_REMOVE_NAME", new Message("Menu.Option.Content.Particle.Item.Remove.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_ITEM_REMOVE_LORE", new Message("Menu.Option.Content.Particle.Item.Remove.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_ADDED", new Message("Menu.Option.Content.Particle.Added", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_PARTICLE_REMOVED", new Message("Menu.Option.Content.Particle.Removed", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_HEADS_INVENTORY", new Message("Menu.Option.Content.Heads.Inventory", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_HEADS_ITEM_NAME", new Message("Menu.Option.Content.Heads.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_HEADS_ITEM_LORE", new Message("Menu.Option.Content.Heads.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_FLOOR_ITEM_NAME", new Message("Menu.Option.Content.Floor.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_FLOOR_ITEM_LORE", new Message("Menu.Option.Content.Floor.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_FLOOR_CHANGED", new Message("Menu.Option.Content.Floor.Changed", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_INVENTORY", new Message("Menu.Option.Content.Time.Inventory", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_ITEM_NAME", new Message("Menu.Option.Content.Time.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_ITEM_LORE", new Message("Menu.Option.Content.Time.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_WORLD", new Message("Menu.Option.Content.Time.Type.World", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_DAY", new Message("Menu.Option.Content.Time.Type.Day", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_NOON", new Message("Menu.Option.Content.Time.Type.Noon", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_SUNSET", new Message("Menu.Option.Content.Time.Type.Sunset", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_NIGHT", new Message("Menu.Option.Content.Time.Type.Night", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_MIDNIGHT", new Message("Menu.Option.Content.Time.Type.MidNight", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_TYPE_SUNRISE", new Message("Menu.Option.Content.Time.Type.Sunrise", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_TIME_CHANGED", new Message("Menu.Option.Content.Time.Changed", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BIOME_INVENTORY", new Message("Menu.Option.Content.Biome.Inventory", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BIOME_ITEM_NAME", new Message("Menu.Option.Content.Biome.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BIOME_ITEM_LORE", new Message("Menu.Option.Content.Biome.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BIOME_CHANGED", new Message("Menu.Option.Content.Biome.Changed", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_WEATHER_INVENTORY", new Message("Menu.Option.Content.Weather.Inventory", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_WEATHER_ITEM_NAME", new Message("Menu.Option.Content.Weather.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_WEATHER_ITEM_LORE", new Message("Menu.Option.Content.Weather.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_WEATHER_TYPE_DOWNFALL", new Message("Menu.Option.Content.Weather.Type.Downfall", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_WEATHER_TYPE_CLEAR", new Message("Menu.Option.Content.Weather.Type.Clear", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_WEATHER_CHANGED", new Message("Menu.Option.Content.Weather.Changed", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_INVENTORY_COLOR", new Message("Menu.Option.Content.Banner.Inventory.Color", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_INVENTORY_LAYER", new Message("Menu.Option.Content.Banner.Inventory.Layer", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_INVENTORY_LAYER_COLOR", new Message("Menu.Option.Content.Banner.Inventory.Layer-Color", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_ITEM_NAME", new Message("Menu.Option.Content.Banner.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_ITEM_LORE", new Message("Menu.Option.Content.Banner.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_ITEM_CREATE_NAME", new Message("Menu.Option.Content.Banner.Item.Create.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_ITEM_CREATE_LORE", new Message("Menu.Option.Content.Banner.Item.Create.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_BANNER_CHANGED", new Message("Menu.Option.Content.Banner.Changed", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_RESET_ITEM_NAME", new Message("Menu.Option.Content.Reset.Item.Name", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_RESET_ITEM_LORE", new Message("Menu.Option.Content.Reset.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_OPTION_CONTENT_RESET_CHAT", new Message("Menu.Option.Content.Reset.Chat", ""));
    getMessageManager().registerMessage("MENU_THEME_INVENTORY", new Message("Menu.Theme.Inventory", ""));
    getMessageManager().registerMessage("MENU_THEME_ITEM_NAME", new Message("Menu.Theme.Item.Name", ""));
    getMessageManager().registerMessage("MENU_THEME_ITEM_LORE", new Message("Menu.Theme.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_THEME_VOTE_SUCCESS", new Message("Menu.Theme.Vote.Success", ""));
    getMessageManager().registerMessage("MENU_THEME_VOTE_ALREADY", new Message("Menu.Theme.Vote.Already", ""));
    getMessageManager().registerMessage("MENU_THEME_VOTE_SUPER_ITEM_NAME", new Message("Menu.Theme.Vote.Super.Item.Name", ""));
    getMessageManager().registerMessage("MENU_THEME_VOTE_SUPER_ITEM_LORE", new Message("Menu.Theme.Vote.Super.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_THEME_VOTE_SUPER_USED", new Message("Menu.Theme.Vote.Super.Used", ""));
    getMessageManager().registerMessage("MENU_THEME_GTB_INVENTORY", new Message("Menu.Theme.Guess-The-Build.Inventory", ""));
    getMessageManager().registerMessage("MENU_THEME_GTB_ITEM_NAME", new Message("Menu.Theme.Guess-The-Build.Item.Name", ""));
    getMessageManager().registerMessage("MENU_THEME_GTB_ITEM_LORE", new Message("Menu.Theme.Guess-The-Build.Item.Lore", ""));
    getMessageManager().registerMessage("MENU_THEME_GTB_DIFFICULTIES_EASY", new Message("Menu.Theme.Guess-The-Build.Difficulties.Easy", ""));
    getMessageManager().registerMessage("MENU_THEME_GTB_DIFFICULTIES_MEDIUM", new Message("Menu.Theme.Guess-The-Build.Difficulties.Medium", ""));
    getMessageManager().registerMessage("MENU_THEME_GTB_DIFFICULTIES_HARD", new Message("Menu.Theme.Guess-The-Build.Difficulties.Hard", ""));

    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_POINTS_HIGHEST_WIN", new Message("Leaderboard.Statistics.Highest-Win", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_POINTS_HIGHEST", new Message("Leaderboard.Statistics.Highest-Points", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_POINTS_TOTAL", new Message("Leaderboard.Statistics.Total-Points-Earned", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_BLOCKS_PLACED", new Message("Leaderboard.Statistics.Blocks-Placed", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_BLOCKS_BROKEN", new Message("Leaderboard.Statistics.Blocks-Broken", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_PARTICLES_USED", new Message("Leaderboard.Statistics.Particles-Placed", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_SUPER_VOTES", new Message("Leaderboard.Statistics.Super-Votes", ""));

  }

  private void registerLocales() {
    Arrays.asList(new Locale("Chinese (Traditional)", "简体中文", "zh_HK", "POEditor contributors", Arrays.asList("中文(傳統)", "中國傳統", "chinese_traditional", "zh")),
            new Locale("Chinese (Simplified)", "简体中文", "zh_CN", "POEditor contributors", Arrays.asList("简体中文", "中文", "chinese", "chinese_simplified", "cn")),
            new Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")),
            new Locale("Dutch", "Nederlands", "nl_NL", "POEditor contributors", Arrays.asList("dutch", "nederlands", "nl")),
            new Locale("English", "English", "en_GB", "Tigerpanzer_02", Arrays.asList("default", "english", "en")),
            new Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")),
            new Locale("German", "Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")),
            new Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors", Arrays.asList("hungarian", "magyar", "hu")),
            new Locale("Indonesian", "Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id")),
            new Locale("Italian", "Italiano", "it_IT", "POEditor contributors", Arrays.asList("italian", "italiano", "it")),
            new Locale("Korean", "한국의", "ko_KR", "POEditor contributors", Arrays.asList("korean", "한국의", "kr")),
            new Locale("Lithuanian", "Lietuviešu", "lt_LT", "POEditor contributors", Arrays.asList("lithuanian", "lietuviešu", "lietuviesu", "lt")),
            new Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")),
            new Locale("Portuguese (BR)", "Português Brasileiro", "pt_BR", "POEditor contributors", Arrays.asList("brazilian", "brasil", "brasileiro", "pt-br", "pt_br")),
            new Locale("Romanian", "Românesc", "ro_RO", "POEditor contributors", Arrays.asList("romanian", "romanesc", "românesc", "ro")),
            new Locale("Russian", "Pусский", "ru_RU", "POEditor contributors", Arrays.asList("russian", "pусский", "pyccknn", "russkiy", "ru")),
            new Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")),
            new Locale("Thai", "Thai", "th_TH", "POEditor contributors", Arrays.asList("thai", "th")),
            new Locale("Turkish", "Türk", "tr_TR", "POEditor contributors", Arrays.asList("turkish", "turk", "türk", "tr")),
            new Locale("Vietnamese", "Việt", "vn_VN", "POEditor contributors", Arrays.asList("vietnamese", "viet", "việt", "vn")))
        .forEach(LocaleRegistry::registerLocale);
  }

  private MessageManager getMessageManager() {
    return plugin.getMessageManager();
  }

}
