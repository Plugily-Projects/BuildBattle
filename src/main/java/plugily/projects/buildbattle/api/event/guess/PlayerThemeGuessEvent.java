package plugily.projects.buildbattle.api.event.guess;

import org.bukkit.event.HandlerList;
import plugily.projects.buildbattle.arena.GuessArena;
import plugily.projects.buildbattle.old.menus.themevoter.BBTheme;
import plugily.projects.minigamesbox.classic.api.event.PlugilyEvent;


  /**
   * @author Tigerpanzer_02
   * @since 5.0.0
   * <p>
   * Called when player guess the theme right
   */
  public class PlayerThemeGuessEvent extends PlugilyEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final BBTheme theme;

    public PlayerThemeGuessEvent(GuessArena eventArena, BBTheme theme) {
      super(eventArena);
      this.theme = theme;
    }

    public static HandlerList getHandlerList() {
      return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
      return HANDLERS;
    }

    public BBTheme getTheme() {
      return theme;
    }
  }
}
