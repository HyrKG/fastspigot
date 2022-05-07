package cn.hyrkg.fastspigot.fast.actionjam;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class FastJam implements Listener {
    private static FastJam instance;

    private static HashMap<UUID, IResultHandlerContainer<Object>> jamChatMap = new HashMap<>();
    private static HashMap<UUID, IResultHandlerContainer<Integer>> jamMoveMap = new HashMap<>();
    private static HashMap<UUID, IResultHandlerContainer<Integer>> jamHurtMap = new HashMap<>();

    private static HashMap<JamType, HashMap<UUID, ?>> jamTypeMap = new HashMap<>();

    private FastJam(Plugin plugin) {
        jamTypeMap.put(JamType.chat, jamChatMap);
        jamTypeMap.put(JamType.move, jamMoveMap);
        jamTypeMap.put(JamType.hurt, jamHurtMap);

        runnable.runTaskTimer(plugin, 0, 20L);
    }

    public static void init(Plugin plugin) {
        instance = new FastJam(plugin);
        plugin.getServer().getPluginManager().registerEvents(instance, plugin);
    }

    private static boolean isJamming(JamType type, UUID uuid) {
        if (jamTypeMap.containsKey(type))
            return jamTypeMap.get(type).containsKey(uuid);
        return false;
    }

    private static IResultHandler getJamming(JamType type, UUID uuid) {
        if (jamTypeMap.containsKey(type))
            return ((IResultHandlerContainer<?>) jamTypeMap.get(type).get(uuid)).handler;
        return null;
    }

    private static void clear(JamType type, UUID uuid) {
        if (jamTypeMap.containsKey(type))
            jamTypeMap.get(type).remove(uuid);
    }

    public static void jamMsg(Player player, IResultHandler handler) {
        if (jamChatMap.containsKey(player.getUniqueId())) {
            respond(player.getUniqueId(), JamType.chat, new JamResult(null, JamResult.extrude, player.getUniqueId()));
        }
        jamChatMap.put(player.getUniqueId(), new IResultHandlerContainer<Object>(handler, null));
    }

    public static void jamMove(Player player, IResultHandler handler, int second) {
        if (jamMoveMap.containsKey(player.getUniqueId())) {
            respond(player.getUniqueId(), JamType.move, new JamResult(null, JamResult.extrude, player.getUniqueId()));
        }
        jamMoveMap.put(player.getUniqueId(), new IResultHandlerContainer<Integer>(handler, second));
    }

    public static void jamHurt(Player player, IResultHandler handler, int second) {
        if (jamHurtMap.containsKey(player.getUniqueId())) {
            respond(player.getUniqueId(), JamType.hurt, new JamResult(null, JamResult.extrude, player.getUniqueId()));
        }
        jamHurtMap.put(player.getUniqueId(), new IResultHandlerContainer<Integer>(handler, second));

    }

    private static boolean respond(UUID uuid, JamType type, JamResult obj) {
        if (isJamming(type, uuid)) {
            IResultHandler handler = getJamming(type, uuid);
            clear(type, uuid);
            if (handler != null)
                handler.handld(type, obj);
            else
                return false;
            return true;
        }
        return false;
    }

    private static boolean respond(UUID uuid, JamResult obj) {
        for (JamType type : JamType.values()) {
            if (isJamming(type, uuid)) {
                IResultHandler handler = getJamming(type, uuid);
                clear(type, uuid);
                if (handler != null)
                    handler.handld(type, obj);
                else
                    return false;
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            UUID pid = e.getEntity().getUniqueId();
            if (isJamming(JamType.hurt, pid)) {
                respond(pid, JamType.hurt, new JamResult(null, JamResult.failure, pid));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        UUID pID = event.getPlayer().getUniqueId();
        if (isJamming(JamType.chat, pID)) {
            event.setCancelled(true);
            String msg = event.getMessage();
            respond(pID, JamType.chat, new JamResult(msg, JamResult.successful, pID));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID uid = event.getPlayer().getUniqueId();
        if (isJamming(JamType.move, uid)) {
            Location f = event.getFrom();
            Location t = event.getTo();
            if (f.distance(t) >= 0.2)
                respond(uid, JamType.move, new JamResult(null, JamResult.failure, uid));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID pUUID = event.getPlayer().getUniqueId();
        respond(pUUID, new JamResult(null, JamResult.exit, pUUID));
    }

    private BukkitRunnable runnable = new BukkitRunnable() {

        @Override
        public void run() {
            //handle Time
            for (JamType type : jamTypeMap.keySet()) {
                if (type.equals(JamType.hurt) || type.equals(JamType.move)) {
                    ArrayList<UUID> removal = new ArrayList<>();

                    @SuppressWarnings("unchecked")
                    HashMap<UUID, IResultHandlerContainer<Integer>> map = (HashMap<UUID, IResultHandlerContainer<Integer>>) jamTypeMap.get(type);

                    for (UUID uid : map.keySet()) {
                        int t = --map.get(uid).obj;
                        if (t <= 0)
                            removal.add(uid);
                        else {
                            IResultHandler handler = getJamming(type, uid);
                            if (handler != null) {
                                handler.handld(type, new JamResult(t, JamResult.tick, uid));
                            }
                        }
                    }

                    for (UUID uid : removal)
                        respond(uid, type, new JamResult(null, JamResult.successful, uid));
                }
            }

        }
    };

}
