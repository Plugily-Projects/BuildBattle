package plugily.projects.buildbattle.handlers.misc;

import plugily.projects.buildbattle.Main;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.07.2025
 */
public class HeadDatabaseManager {

  private final Main plugin;
  private final ArrayList<String> headCatalog = new ArrayList<>(Arrays.asList("alphabet", "animals", "blocks", "decoration", "food-drinks", "humanoid", "humans", "miscellaneous", "monsters", "plants"));

  public HeadDatabaseManager(Main plugin) {
    this.plugin = plugin;
    try(Scanner scanner = new Scanner(requestHeadFetch(null), "UTF-8").useDelimiter("\\A")) {
      String data = scanner.hasNext() ? scanner.next() : "";
      File file = new File(plugin.getDataFolder().getPath() + "/heads/database/head_data.yml");
      if(!file.exists()) {
        new File(plugin.getDataFolder().getPath() + "/heads/").mkdir();
        new File(plugin.getDataFolder().getPath() + "/heads/database/").mkdir();
        if(!file.createNewFile()) {
          plugin.getDebugger().debug(Level.WARNING, "Couldn't create heads folder! We must disable heads support.");
          return;
        }
      }
      Files.write(file.toPath(), data.getBytes());
      plugin.getDebugger().debug(Level.WARNING, "Fetched latest heads file from repository.");
    } catch(IOException ignored) {
      //ignore exceptions
      plugin.getDebugger().debug(Level.WARNING, "Couldn't access heads fetcher service or there is other problem! You should notify author!");
    }
  }

  public DownloadStatus getDatabase(String databaseName) {
    if(!headCatalog.contains(databaseName)) {
      return DownloadStatus.FAIL;
    }
    return download(plugin, databaseName);
  }

  public ArrayList<String> getHeadCatalog() {
    return headCatalog;
  }

  private DownloadStatus download(Main plugin, String name) {
    DownloadStatus status = demandHeadDownload(name);
    if(status == DownloadStatus.FAIL) {
      plugin.getDebugger().debug(Level.WARNING, "&cHeads service couldn't download latest heads for plugin! Reduced heads will be used instead!");
    } else if(status == DownloadStatus.SUCCESS) {
      plugin.getDebugger().debug(Level.WARNING, "&aDownloaded heads " + name + " properly!");
    } else if(status == DownloadStatus.LATEST) {
      plugin.getDebugger().debug(Level.WARNING, "&aHeads " + name + " is latest! Awesome!");
    }
    return status;
  }


  private InputStream requestHeadFetch(String head) {
    try {
      URL url = new URL("https://api.plugily.xyz/onlineservices/v1/fetch.php");
      HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("User-Agent", "PlugilyProjectsOnlineServices/1.0");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setRequestProperty("Accept-Charset", "UTF-8");
      conn.setDoOutput(true);

      OutputStream os = conn.getOutputStream();
      if(head == null) {
        os.write(("pass=onlineservice").getBytes("UTF-8"));
      } else {
        os.write(("pass=onlineservice&head=" + head).getBytes("UTF-8"));
      }
      os.flush();
      os.close();
      return conn.getInputStream();
    } catch(IOException e) {
      plugin.getDebugger().debug(Level.SEVERE, "Could not fetch heads from plugily.xyz api! Cause: {0} ({1})", new Object[]{e.getCause(), e.getMessage()});
      return new InputStream() {
        @Override
        public int read() {
          return -1;
        }
      };
    }
  }

  private DownloadStatus demandHeadDownload(String head) {
    File headFile = new File(plugin.getDataFolder() + "/heads/database/" + head + ".yml");
    if(!headFile.exists() || !isExact(head, headFile)) {
      return writeFile(head);
    }
    return DownloadStatus.LATEST;
  }

  private boolean isExact(String head, File file) {
    try(Scanner scanner = new Scanner(requestHeadFetch(head), "UTF-8").useDelimiter("\\A");
        Scanner localScanner = new Scanner(file, "UTF-8").useDelimiter("\\A")) {
      String onlineData = scanner.hasNext() ? scanner.next() : "";
      String localData = localScanner.hasNext() ? localScanner.next() : "";

      return onlineData.equals(localData);
    } catch(IOException ignored) {
      return false;
    }
  }

  private DownloadStatus writeFile(String head) {
    try(Scanner scanner = new Scanner(requestHeadFetch(head), "UTF-8").useDelimiter("\\A")) {
      String data = scanner.hasNext() ? scanner.next() : "";
      try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(plugin.getDataFolder().getPath() + "/heads/database/" + head + ".yml"), "UTF-8")) {
        writer.write(data);
      }
      return DownloadStatus.SUCCESS;
    } catch(IOException ignored) {
      plugin.getDebugger().debug(Level.WARNING, "Demanded head " + head + " cannot be downloaded! You should notify author!");
      return DownloadStatus.FAIL;
    }
  }

/*
  [ARCHIVE] Convert Heads with Json to YML Code
  private void loadFileFromJSONde(String input) {
    long start = System.currentTimeMillis();
     JsonElement element = getJsonElement("https://raw.githubusercontent.com/Plugily-Projects/online-services/refs/heads/master/buildbattle/headdatabase/raw/" + input + ".yml");
   or
    JsonElement element = getJsonElement("https://minecraft-heads.com/scripts/api.php?cat=decoration");
    if(element == null) {
      return;
    }
    JsonArray outputJson = element.getAsJsonArray();
    AtomicInteger i = new AtomicInteger();
    i.set(0);
    outputJson.forEach(categoryElement -> {
      JsonObject category = categoryElement.getAsJsonObject();
      if(category.get("name") == null) {
        return;
      }
      if(category.get("value") == null) {
        return;
      }
      String name = category.get("name").getAsString();
      String value = category.get("value").getAsString();
      if(heads.get(name) != null) {
        heads.put(name + " (dup) " + i.get(), value);
        i.getAndIncrement();
      } else
        heads.put(name, value);
    });
    System.out.println(heads);
    FileConfiguration categoryConfig = ConfigUtils.getConfig(plugin, "heads/menus/hd/" + input);
    for(Map.Entry<String, String> entry : heads.entrySet()) {
      // System.out.println("Trying to save -> " + entry.getKey() + " "+ entry.getValue());
      categoryConfig.set(entry.getKey().replace(".", " "), entry.getValue());
    }
    ConfigUtils.saveConfig(plugin, categoryConfig, "heads/menus/hd/" + input);
    System.out.println("[System] [Plugin] Head finished took ms" + (System.currentTimeMillis() - start));
    heads.clear();
  }

  private final String USER_AGENT = "Plugily Projects Converter v1";

  public JsonElement getJsonElement(String fullURL) {
    try {
      URL url = new URL(fullURL);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.addRequestProperty("User-Agent", USER_AGENT);
      InputStream inputStream = connection.getInputStream();
      InputStreamReader reader = new InputStreamReader(inputStream);
      return new JsonParser().parse(reader);
    } catch(IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  */

  public enum DownloadStatus {
    SUCCESS, FAIL, LATEST
  }


}
