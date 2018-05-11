package net.dzikoysk.wildskript;

import ch.njol.skript.Skript;
import net.dzikoysk.wildskript.events.PlayerJoin;
import net.dzikoysk.wildskript.events.PlayerMove;
import net.dzikoysk.wildskript.events.ServerPing;
import net.dzikoysk.wildskript.objects.inventory.InventoryEvent;
import net.dzikoysk.wildskript.objects.recipe.RecipesEvent;
import net.dzikoysk.wildskript.objects.recipe.ResultEvent;
import net.dzikoysk.wildskript.register.Register;
import net.dzikoysk.wildskript.util.IOUtils;
import net.dzikoysk.wildskript.util.Metrics;
import net.dzikoysk.wildskript.util.Metrics.Graph;
import net.dzikoysk.wildskript.util.User;
import net.dzikoysk.wildskript.util.WildSkriptTimer;
import net.dzikoysk.wildskript.util.data.Data;
import net.dzikoysk.wildskript.util.doc.Documentation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class WildSkript extends JavaPlugin {

    private static WildSkript wildskript;
    public static String version;
    public static boolean debug;

    private static boolean enable;
    private static boolean skript;
    private static boolean skQuery;
    private static boolean randomSk;

    private static WildSkriptTimer timer;

    @Override
    public void onEnable() {

        // Register this
        wildskript = this;

        // Check
        if (cannot()) {
            return;
        }

        // Data start
        Data.load();

        // BungeeCord channel
        bungee();

        // Init utils
        utils();

        // Register Events Listener
        Bukkit.getPluginManager().registerEvents(new ServerPing(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMove(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new RecipesEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ResultEvent(), this);

        // Register WildSkript
        Skript.registerAddon(this);

        // Register WildSkript Functions
        Register.register();

        // Documentation
        if (Data.doc) {
            Documentation.generate();
        }
        if (Data.mcstats) {
            metrics();
        }

        log("~ WildSkript made with \u2764 by Dzikoysk ~");
        log("~ You are running an Unofficial WildSkript build (1.8.x - 1.12.2) made by Sitieno14 ~");
    }

    private boolean cannot() {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase("skript")) {
                log("Hooked with Skript!");
                skript = true;
            }
            if (plugin.getName().equalsIgnoreCase("skQuery")) {
                log("Hooked with skQuery!");
                skQuery = true;
            }
            if (plugin.getName().equalsIgnoreCase("randomSk")) {
                log("Hooked with SkQuery!");
                randomSk = true;
            }
        }
        if (!skript) {
            error("Stopping registration because Skript wasn't found!");
            warning("You can download Skript from: https://docs.skunity.com/downloads");
            return true;
        }
        enable = true;
        return false;
    }

    private void metrics() {
        try {
            Metrics metrics = new Metrics(wildskript);
            Graph global = metrics.createGraph("Global Statistics Linear");
            global.addPlotter(new Metrics.Plotter("Servers") {
                @Override
                public int getValue() {
                    return 1;
                }
            });
            global.addPlotter(new Metrics.Plotter("Players") {
                @Override
                public int getValue() {
                    return Bukkit.getOnlinePlayers().size();
                }
            });
            metrics.addGraph(global);
            metrics.start();
        } catch (IOException e) {
            log(e.getMessage());
        }
    }

    private void bungee() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(wildskript, "BungeeCord");
    }

    private void utils() {
        timer = new WildSkriptTimer();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, timer, 1000L, 50L);
        for (Player p : Bukkit.getOnlinePlayers()) {
            User.get(p);
        }
    }

    public static WildSkript getInstance() {
        return wildskript;
    }

    public static String getVersion() {
        return version;
    }

    public static WildSkriptTimer getTimer() {
        return timer;
    }

    public static void log(String log) {
        Bukkit.getLogger().info("[WildSkript] " + log);
    }

    public static void severe(String log) {
        Bukkit.getLogger().severe("[WildSkript] " + log);
    }

    public static void warning(String log) {
        Bukkit.getLogger().severe("[WildSkript - Error] #!# ======={ WildSkript Warning }=======");
        Bukkit.getLogger().severe("[WildSkript - Error] #!# " + log);
        Bukkit.getLogger().severe("[WildSkript - Error] #!#");
    }
    public static void error(String log) {
        Bukkit.getLogger().severe("[WildSkript - Error] #!# =!!!======{ WildSkript Error }======!!!=");
        Bukkit.getLogger().severe("[WildSkript - Error] #!# " + log);
        Bukkit.getLogger().severe("[WildSkript - Error] #!#");
    }

    public static boolean skQuery() {
        return skQuery;
    }

    public static boolean randomSk() {
        return randomSk;
    }

    public static boolean enabled() {
        return enable;
    }

    public static boolean debug() {
        return debug;
    }
}
