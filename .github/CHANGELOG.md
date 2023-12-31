## Build Battle Changelog

Changelog is followed by special scheme which is required in order to allow automatic discord
deploy webhooks to print changelog successfully

## Log scheme

`### <current version from pom.xml> <anything else here>`
`<update log line>`

That's all, matcher will stop when detects next line started with `###` match

### 5.0.2 Release (31.12.2023)
* Fixed gtb round reset on player leave
* Fixed gtb "could not pass event AsyncPlayerChatEvent" if spectators ingame
* Fixed bouncing and mouse pointer reset on themevoting
* Added 1.20.3 & 1.20.4 support
* Updated to minigamesbox 1.3.4

### 5.0.1 Release (21.08.2023)
* Fixed rewards enabling
* Fixed bossbar disabling
* Fixed locale registration if service unavailable

### 5.0.0 Release (09.08.2023)
* Added up to 1.20 compatibility
* Changed arena solo and team to classic, with different team sizes
* Changed supported languages [https://translate.plugily.xyz]
* Changed plugin base is based on MinigamesCore [https://github.com/Plugily-Projects/MiniGamesBox]
* Changed native java building to java17, java 8 downloadable on our discord [https://discord.plugily.xyz]
* Fixed gtb mode issues
* Fixed all known bugs

### 4.5.3 Release (04.08.2021 - xx.xx.2021)

* Fixed when players didn't got rewards after plot reset
* Now you can configure the notification of the remaining build time
* Fixed issue when player's inventories did not get cleared before teleport
* An attempt to fix executing console reward commands multiple times
* Fixed mysql create table statement
* Added %ARENATYPE% as placeholder for messages
* Fixed NPE when player's plot was invalid after NPC click
* Added option for the third first guessers to receive more points
* Fixed IndexOutOfBoundsException when retrieving currentBuilder at next round

### 4.5.2 Release (02.08.2021 - 03.08.2021)

* Fixed issues with spectators get added multiple times to the players list
* Fixed Cannot invoke because "userPlot" is null

### 4.5.1 Release (15.04.2021 - 02.08.2021)

* Added 1.17 support
* Added adjustable team mode plot size
* Added placeholder total_points_earned and highest_points
* Added plot selector for team mode
* Added greek as language (Thanks to poeditor contributors)
* Renamed class paths from commonsbox
* Reuse XMaterial for specialitems
* Fixed teamarena does not stop when only two players are left from the same team
* Fixed spectatormode does not display summary and players

> > > b1

* Added forcestart item
* Added new message In-Game.Floor-Item-Blacklisted
* Added /bb guess <word> argument (useful for servers with bungee handled chat)
* Added placeholder arena_players_online
* Added /bb join maxplayers type which tries to get the baseArena with the highest amount of players
* Added special_items.yml
* Added changeable options menu item
* Changed randomjoin mechanics -> Searches for starting -> random join -> Searches for waiting -> random join
* Changed Now builders will also get points if the building is guessed
* Changed gtb word hints are now randomly chosen
* Changed gtb word hints will stop if there are only 2 chars left to show
* Changed empty message is no longer sent
* Fixed gtb guessers got options menu on there inventory
* Fixed on gtb the time and weather is not synced with all players
* Fixed minecarts can go outside of plot
* Fixed item frames can be rotated outside plot
* Fixed some bugs on particle menu
* Fixed when plugins scoreboards not re-appeared after game end
* Fixed players can report more than one time
* Fixed spectator mode does not work correctly
* Fixed gtb guesser players can teleport to all players on the server
* Removed lobbyitems.yml
* Removed temp added scoreboard rewards

### 4.5.0 Release (21.03.2021 - 13.04.2021)

* Automatically disable bossbar support on 1.8 to prevent issue if bossbar is enabled on config.yml
* Fix when baseArena selector GUI does not opened
* Fixed HolidayManager that crashes themes on Teams and GTB mode
* Fixed NPE on ArrowEvents and other version improvements

### 4.4.9 Release (19.03.2021)

* Added configurable baseArena selector items (per state)
* Fixed particle issues on some versions

### 4.4.8 Release (08.02.2021 - 17.03.2021)

* Added option to perform command after specific amount of reports
* Added list of restricted entities that can't be spawned in game
* Added legacy support
* Added super_votes papi placeholder
* Added TeleportArgument (/bba tp)
* Added new RewardType scoreboard_remove
* Added modifiable baseArena state names to language
* Fix when sign instances section is null in some cases
* Fix when some inventory can't be opened in game
* Moved statistic save method inside onDisable
* Changed prevent interaction with enderchest

### 4.4.7 Release (24.01.2021 - 28.01.2021)

* Fixed Join SubTitle not being sent
* Fixed stack trace array length is 0 when trying to send exception to service
* Fix NPE related to citizens NPCRegistry method return value
* Fixed spectators can not see each other after joining game while being ingame
* Added 2 new Plot times - Noon, MidNight

### 4.4.6 Release (18.01.2021 - 22.01.2021)

* Fixed bb items are not removed if inventory manager is enabled and the server is stopping
* Fixed particle refresh scheduler not worked on game start

### 4.4.5 Release (31.12.2020 - 18.01.2021)

* ArgumentsRegistry now accessible from outside
* Fixed some reported NPEs about setup inventory
* Added start game reward action
* Added join title and subtitle
* Added customizable item name in baseArena selector gui (by ajgeiss0702)
* Fixed asynchronous issue when performing commands from async chat
* Optimized plot particle refresh scheduler, now it only runs when a game is running
* Only add points to plot that do not have points already

### 4.4.4 Release (18.12.2020 - 29.12.2020)

* Rewritten scoreboardlib (fixed flickering and color matching) -> 122 chars for 1.14 up and 48 for lower versions
* Fix async catch exception in old versions when teleporting players to their plots
* Fixed setup menu on "normal" spigot servers

### 4.4.3 Release (30.11.2020 - 13.12.2020)

* Fixed joining through a sign while hotbar slot for leave-item is active
* Fixed cast exception when banner meta is not item
* Fixed NoSuchMethodError when CommandSender#spigot does not exists on 1.11
* Fixed BiomeChange specially on 1.15 (PacketPlayOutMapChunk)
* Fixed Command-Instead-Of-Head-Menu is not executed on the first menu
* Fixed lore is not updating on setup inventory
* Fixed ANNOUNCE_PLOTOWNER_LATER did not show both owners on team mode

### 4.4.2 Release (02.10.2020 - 12.11.2020)

* Fixed spectators are able to damage entities
* Fixed NPE related to baseArenas config not loaded correctly
* Fixed for 1.11 player heads
* Fixed particles can't activate again after they got removed
* Fixed spectators can't fly after changing world
* Signs performance improvement
* Fixed (some related) script NPE on rewards.yml
* Now you can disable spectators from joining into game
* Now walk and fly speed will reset on join and leave
* Fixed particle remover does not auto update
* Fixed particle remover locations are multiply times listed
* Fixed debug messages to console are not colored
* Fixed NPE related to vote menu updating

### 4.4.1 Release (15.08.2020 - 20.09.2020)

* (Developer Alert - Class rename)
* Added spectator mode
* Added GTB to the SetUp inventory
* Added baseArena selector
* Added support for 1.16 hex colors
* Fixed NPE when teleporting players to plots
* Fixed IllegalArgumentException when some particles have "custom class data"
* Fixed voting plot owners is empty
* Fixed NoSuchMethodException for player chunk map
* Fixed NPE during giving rewards to players
* Fixed LanguageMigrator breaking language.yml
* Fixed NoClassDefFoundError on pre 1.16 servers
* Fixed NPE during vote event
* Fixed votes command
* Fixed for new 1.16 wall signs
* Fixed NPE regarding giving rewards to players and resetting the plot areas
* Fixed when blacklisted items & floor material is not exists and throws errors
* Fixed async catch from bukkit when performing commands
* Fixed IllegalPluginAccessException on plugin disable
* Fixed NPE on Plotreset (GTB)
* Fixed player being kicked when trying to join an in-game baseArena
* Fixed NPE when trying to teleport players to lobby location
* Fixed PlaceholderAPI placeholders not works on scoreboard in guessTheBuild mode
* Fixed problems with materials
* Reworked version checker
* Improved the tab complete handling
* Updated PlaceholderAPI dependency
* Updated locales to latest version
* Added the possibility to change messages of locales on language.yml

### 4.4.0 Release (01.07.2020 - 29.07.2020)

* GuessTheBuild release
* Reworked whole Rewards
* New file rewards.yml
* Added short commands such as start and leave
* Added option to disable separate chat
* Added possibility to change table name of mysql database
* Added possibility to run command for report item
* Added the new particles from 1.13 and up
* Added an option to disable party features
* Added missing 1.14 & 1.15 & 1.16 Materials/Biomes
* Added 1.16.x compatibility
* Added adjustable floor blacklist
* Added bungeemode Arena shuffler
* Added players will get kicked on bungeemode when baseArena is full and has no rights
* Added an event to cancel fire spread on the plot
* Added option to announce plot owner after voting
* Fixed force start command when setting forced theme didn't set build time properly
* Fixed bungeemanager enable null text error
* Fixed biomes on 1.16 (nosuchmethoderror)
* Fixed last plot on teammode can get double points
* Fixed rewards are ignoring the place
* Fixed HandlerList Error
* Fixed water and lava as floor material
* Fixed team member leave plot reset
* Fixed water/lava flow and piston outside of plot in worst cases
* Fixed nether star (options menu) duplication
* Tried to fix Server crash on 1.15
* Migrator for new config file as there are some more changes
* Update setup tips Feedback page link
* Randomjoin command will now longer send proper message when no baseArena is available
* Changed mysql updates to do only one instead of more than 10 (should improve performance)
* Updated particles.yml (Thanks Fabian Adrian #6234)
* Changed increased npc finder to radius of 5 to the plot
* Optimized floor change to get all materials

### 4.3.2 Release

* Added per particle disabler
* Fixed Already-Playing message on randomjoin command
* Fixed Type-Arena-Name language

### 4.3.1 Release (31.03.2020)

* Fixed Heads in lower version than 1.15.X
* Fixed bungee value Shutdown-When-Game-Ends
* Fixed automatic bungee baseArena restart (Now scoreboard and Bossbar applies)
* Tried to fix parties compatibility
* Fixed broken language migrator

### 4.3.0 Release (17.03.2020)

* PlaceholderAPI placeholders are no longer case sensitive
* Added baseArena state placeholders for PlaceholderAPI:
    * %buildbattle_{baseArena id}:{data}% where data types are:
        * players - amount of players in baseArena
        * max_players - max players in baseArena
        * state - current state raw enum name eg. STARTING
        * state_pretty - prettified name of enum eg. Waiting (instead WAITING_FOR_PLAYERS)
        * mapname - name of baseArena map
        * arenatype - raw enum name of baseArena (SOLO, TEAM etc)
        * arenatype_pretty - prettified name of enum eg. Classic (instead SOLO)
* Fixed VoteMenu NullPointerException
* Added Parties support for PAF and Parties
* Now if user don´t vote the plot will get 3 points of this player to prevent unfair game behaviors
* FIXED 1.15.X Head issues
* Added Connect-To-Hub boolean to bungee.yml
* Added End-Location-Hub boolean to bungee.yml
* Fixed mysql database - Create a new one and your stats should be saved correctly

### 4.2.0 Release (16.11.2019 - 13.12.2019) (by Tigerpanzer)

* Fixed baseArena deletion
* Added a item to go to main options page
* Fixed Holiday themes
* Added a new MOTD Manager in the bungee.yml (Now you can define the message on your self)
* Now the values in bungee.yml will work
* Now the biomes permissions will work
* Added new check for setup validation (now registered plots amount will be checked)
* Added 1.15 compatibility

### 4.1.0 Release (13.07.2019 - 18.10.2019) (by Tigerpanzer and Plajer)

* Added new permission to join full games "buildbattle.fullgames"
* Added the option to join when a game is full (you will kick a player without perm)
* Fixed issue that you could join full games before they started now proper full game
  permission check will occur and do the thing to allow or disallow you to join
* Fixed file creating for bungee.yml
* /bb randomjoin will now really join random baseArena not first one it find good to join
* Added a new option to block commands ingame
* Fixed the NoSuchMethodException on baseArena sign load when the sign isn´t a wallsign
* Added Chinese (Traditional) locale (zh_tw prefix)
* Changed Chinese (Simplified) locale prefix from zh to cn
* Added 1.14 support
* Added sounds when using vote items (reset voteItems.yml to see changes)
* Added banner builder in GUI
* Entities can't be damaged anymore in voting stage
* Players hiding feature is now fully removed, it was buggy

### 4.0.2 Release (15.06.2019 - 13.07.2019)

* Join permission message outputs required permission node now
* Biome menu won't throw errors when using it now
* Fixed firework spawn after the game if they were disabled in config
* Fixed odd game behaviour when game couldn't start because there were some lacking plots
  when player left game before he was removed from the plot
* Locales with special characters like Russian or Korean will now work properly and won't be seen as `?` characters
* Fixed locales might not work in fresh install of plugin
* Added 6 new locales: Portuguese (BR), Slovenian, Dutch, Lithuanian, Japanese and Italian (thanks a lot to our POEditor
  contributors!)
* Fixed Russian locale didn't work

### 4.0.1 Release (11.06.2019)

* Fixed game wouldn't start if there were 2 players or more, sorry
* Fixed error occurred when you didn't look at sign while adding sign via setup GUI

### 4.0.0 Release (10.06.2019)

* Fixed typo in message accessor for guess the build game mode message
* Now players aren't saved async in onDisable method which led to exceptions in disable stage
* Fixed bad logic of assigning players to teams in teams game mode
* Teams game mode require now to have at least 3 minimum players to play, it will set it to 3 if
  it's set lower
* Improved faster and more efficient scoreboard library (thanks to TigerHix)
* Reload and baseArena delete commands now require confirmation to execute, you must type the command twice to confirm
  the action
* Implemented faster Hikari database connection pool library instead of BoneCP, jar is now 3 times smaller
* First time database creation is now made async not in main server thread
* Apache Commons-io library is shaded into the jar now, 1.14 removed it
* You cannot set non occluding blocks as a floor block
* **CRITICAL** Players received packets that would break their chunks nearby them due to wrong baseArena plot reset
  check, now it's fixed
* Debug in config is no longer visible by default, no one need it
* Removed hiding/showing players in-game and outside game thing, it was bugged and better to avoid problems with it
* Fixed game ending message was never showed when using any of the locales (accessor was broken)
* /bba reload command was undeprecated and it's usage is no longer discouraged
* Made /bb help and /bba help commands actually working
* Added not playing message node to language.yml, locales already had it somehow

### 4.0.0 Beta Pre Releases 3-15 (03.10.2018 - 06.06.2019)

> RC 2

* Fixed plugin couldn't start if Cloudflare was blocked in your country (or services were offline)
* Removed locale suggester, it will no longer spam console when English (default) locale is used
* Fixed issue with plot adding via setup menu

> RC 1

* Fixed animals couldn't spawn and breed outside baseArenas
* Fixed spawning animals on baseArena even if option for disable-spawning was false
* Fixed /bba addnpc didn't work
* Fixed setup GUI didn't work properly
* Fixed [#612 exception when registering baseArenas](https://www.plajer.xyz/errorservice/viewer.php?id=612) when
  baseArenas.yml file was empty
*
Fixed [#610 exception when special item didn't have material-name section set](https://www.plajer.xyz/errorservice/viewer.php?id=610)
now by default
it will have BEDROCK value if absent
* Added pro tips about user voice and /bba votes executable from console too

> Pre 15

* Fixed [#595 /bb randomjoin command exception](https://www.plajer.xyz/errorservice/viewer.php?id=595) when no baseArena
  type
  args were specified
* Fixed [#592 /bba addplot command exception](https://www.plajer.xyz/errorservice/viewer.php?id=592) when no baseArena
  name
  was typed
* Fixed [#589 options menu click exceptions](https://plajer.xyz/errorservice/viewer.php?id=589) when click was on
  air or outside inventoryView slots
* Fixed Signs weren't updating if Signs-Block-States-Enabled option was false
* Abandoned the direct try-catch exception handling in most of methods and event listeners in favour of
  logger listening, code looks better now
* Implemented Holiday Events for April Fools, Christmas and Halloween, special themes
  for solo and teams will be applied if enabled

> Pre 14

* Fixed time couldn't be set for plots because we were checking raw slot click instead of normal slot
* Made particles.yml particle items more user friendly (lores and names looks now better)
* Fixed bba addplot command didn't work properly
* Added fully configurable biomes in biomes.yml
* Fixed biomes are now properly reset in plot reset option and after baseArena reset
* Added "Go Back" button in particles remove menu

> Pre 13

* Added GuessTheBuild game mode (alpha)
* When using super vote you won't see two game prefixes now
* Merged /bba addvotes and /bba setvotes into /bba votes add/set command
* Added message sent to player when receives super votes

> Pre 12

* Added BBPlayersPlotReceiveEvent to API
* Fixed /bba forcestart wasn't working properly
* Added BBPlotResetEvent to API
* Particles speed is now 1, they may behave better for builders now
* Now you can properly execute all /bb and /bba commands in-game while being non op player
* Somehow plugin was lacking lobby events and you could lose food and health in lobby
* Added chat events so in-game chat is no separated from server chat (I thought it's obvious)
* Now after selection of head in heads inventoryView won't be closed, user will close it when he wants
* Now language manager will use cached language.yml file so plugin will perform bit better
* Theme vote GUI is now properly listening for click events, you don't need to click few times to make it vote
* Added useSSL=false argument in mysql.yml address to avoid mysql warnings in console (not harmful)
* Whole plugin runs now definitely faster and code execution time and plugin load decreases in timings
* Setup GUI option for setting min/max players amount now runs smoothly

> Pre 11

* Added biomes in Options menu
* Added time in Options menu
* Code improvements and changes
* Fixed baseArena signs were incorrectly saved via setup menu (4.0.0 pre issue)

> Pre 10

* Added setup tutorial link to setup menu
* Added tip about downloadable maps when no baseArenas are set up while typing /bba list
* Fixed exp not saving properly with InventoryManager

> Pre 9

* Fixed NPE when player was null (https://plajer.xyz/errorservice/viewer.php?id=347)
* Code must be changed to avoid those NPE's in the future

> Pre 8

* Fixed NPE because of wrong string access in code
* Locales will be now automatically disabled in this version - ppl don't read warnings about locales so I force them
  to disable
* Now /bba reload /bba addvotes/setvotes and /bba list can be now properly executed from console

> Pre 7

* Updates are now checked async without freezing the main thread which caused TPS to drop a bit

> Pre 6

* Particles in menu are now incrementing automatically
* Particle redstone requires DustOptions and causes errors so it was removed from menu
* Fixed possible NPEs in block spread event (https://plajer.xyz/errorservice/viewer.php?id=282)
* Fixed NPE when teleporting to plot, now players will be teleported 1 x and z block away from center if
  height reach Y plot limit (https://plajer.xyz/errorservice/viewer.php?id=283)

> Pre 5

* You cannot join game now infinitely through command (lol????)
* Now players that will leave game will be visible by other players outside game

> Pre 4

* API change - now you can access BuildBattle API via pl.plajer.buildbattle.api
* Maybe a fix for https://plajer.xyz/errorservice/viewer.php?id=244

> Pre 3

* Removed annoying "Please enable bStats" message
* Code improvements
* Added PR0 TIPS when editing baseArena

### 4.0.0 Release (20.08.2018 - 28.09.2018)

* Fixed NullPointerException for users who where no longer online
* Now using default values when they not exist in config to avoid NullPointerException from ConfigPreferences
* Fixed IllegalArgumentException when you add invalid item name to the in-game items blacklist - now it will notify you
  in console
* Fixed %player% placeholder wasn't parsed in /bb stats <player>
* Added PlaceholderAPI placeholders in scoreboard
* Added Russian, Czech, Romanian and Estonian locales (thanks to POEditor contributors!)
* Added dynamic locale manager system - you can now get latest locales on demand from our repository
* Fixed scoreboard color bugs (see https://i.imgur.com/kaZy5s2.png)
* Fixed update checker bugs while using my other minigames
* /bba help command is now executable via players only
* Temporarily merged PLCore to prevent issues with other plugins
* Added blacklisted themes
* Dropped 1.9-1.10 support
* Fixed bug where plot floor wasn't reset while using Citizens
* Fixed message You became 1th/2th/3th was displayed - it shouldn't and it wasn't grammatically okay
* Added extra permission "buildbattle.command.bypass"
* Added solo and team themes
* Added cancel lobbystart when there are not enough players
* Fixed bedrock was displayed in plot floor change option menu
* Head blocks textures are now loading instantly and do not cause server to freeze at the start now
* Fixed /bb top was reversed while MySQL was enabled
* Disabled game end rewards by default because they were confusing for users
* Fixed server was stopped using Shutdown-When-Game-Ends option but player just left not started baseArena
* Player will be given now survival gamemode on server stop
* Removed unused code for entities

### 3.5.1 Release (17.08.2018)

* Fixed NumberFormatException for language.yml migrator - this error is very rare to occur in normal environment but it
  was reported so I fixed it
* Fixed NullPointerException for plot adding via selector wand due to lack of code stop when message with not full
  selection was sent
* Fixed Can't fly outside plot message but you were in plot
* Fixed scoreboard contents were always in Waiting state
* Fixed IndexOutOfBoundsException in join event while bungee is enabled
* Fixed NullPointerException in scoreboard formatting while player was null
* Fixed NoDefClassFoundException in 1.13 while using sign game states

### 3.5.0 Release (08/16.08.2018)

* Built against PLCore API
* Hooked code with Error reporter service
* Fixed MySQL error
* Removed WorldEdit from baseArena usages
* Added ability to edit floor in game (requires baseArenas.yml edit or new setup!)
* Added joints when users have same points in voting time
* Fixed small problem when game was started and voting for theme started, timer was set for build time instead of voting
  time and it
  could be seen in scoreboard for a second
* Plot reset now include weather reset
* Added Turkish and Indonesian locales (thanks to POEditor contributors)
* Added /bba removeplot command
* Added new command /bba plotwand

### 3.4.1 Release (30.07/03.08.2018)

* Added public access to MySQL executeUpdate method
* Added BBPlayerStatisticChangeEvent event
* Updated Spanish and Korean locales
* Added French locale (thanks to POEditor contributors!)
* Removed scoreboard saving in InventoryManager due to errors

### 3.4.0 Release (25/29.07.2018)

* Added super votes
* Added /bb top command
* Fixed locales and updated them
* Added Korean language (thanks to POEditor contributors!)
* Added /bba forcestart <theme> to start with predefined theme (suggested by ColaIan)

### 3.3.0 Release (24.07.2018)

* Now while teleporting to the plot you won't fall down
* Fixed xp was given wrong using inventoryView manager
* Removed unnecessary listeners from spectator code
* Use-Name-Instead-Of-UUID-In-Database option was removed, it's no longer supported
* Removed Particle-Offset option, was useless, default offset should stay same for every server
* Removed Disable-Scoreboard-Ingame option as scoreboard is integral part of the game
* Removed Hook-Into-Vault option as plugin automatically hooks with Vault if found
  (this hook really doesn't add anything except an extra placeholder on the scoreboard which is anyway medium useful in
  this game)
* Using item flags instead of empty custom enchant which didn't work in 1.13
* **Added 1.13 support**
  (still lots of code uses deprecated code which shouldn't be used in 1.13 but because 1.13 has got backwards
  compatibility
  we will keep that for a while as an temporary workaround)
  **Keep in mind that 1.13 forces me to do changes with ID's in BuildBattle and those changes will be done soon**

### 3.2.2 Release (20.07.2018)

* Fixed NoSuchMethod error in 1.9.4 for title
* Added support for Vietnamese, Hungarian and Chinese simplified locales
* Added ability to change weather in plot

### 3.2.1 Release (14.07.2018)

* Fixed NPE when clicking not named Villager
* Fixed migration error

### 3.2.0 Release (06/12.07.2018)

* Added video tutorial link while creating new baseArena
* Added new game mode: TEAM
* Fixed error on disabling caused by disabled boss bar feature
* Item rewards at the end of the game now will be properly given after clearing in-game inventoryView of players
* Vote items now look like Hypixel ones
* Worst vote will now count as 1 point not 0 (so every other vote is +1 point now)
* Implemented theme voting feature like hypixel, before game starts players will vote for theme
* Added localization support via POEditor
* Fixed language migration from version 1 (very old)
* Added missing plugin prefixes to some command messages
* Added Spanish locale (thanks to TheLordDarkYT for POEditor contribution!)
* Separate game timer for team mode

### 3.1.2 Release (05.07.2018)

* Fixed error when 1st winner UUID was null (somehow)
* Added subtitle "x seconds left" in game

### 3.1.0 Release (08.06.2018)

* New good looking summary message at the end of the game
* Added game sign block states
* /bb command is now translatable via language.yml
* Admin commands (/bba) are now better with hover and click event (JSON messages)
* Default map name when creating new baseArena is baseArena ID not "0" now
* Now warning message "can't save language.yml because already exists!" won't occur anymore
* Fixed error in console when player left the baseArena and his plot was cleared even if he didn't have it
* Now you can't change your floor by NPC during the voting time

### 3.0.4 Release (04.06.2018)

* Rewards will be given now only once after the game
* Now you can leave started game without spamming infinitely errors in console
* Fixed broken heads lores in heads menu and changed some strings there
* Fixed wrong displayed winners names at the end in summary
* Now sending "You've became xth" message for 4th winners and lower
* Voting for player plot title is now displayed longer
* Added missing plugin prefix in voting messages
* Immediately voting after game timer ends now

### 3.0.2 Release (03.06.2018)

* Fixed /bb create command not working
* Added /bba settheme command
* Fixed game boss bar not removing after plugin force disable

### 3.0.0 Release (14.05.2018 - 01.06.2018)

* Add everything here