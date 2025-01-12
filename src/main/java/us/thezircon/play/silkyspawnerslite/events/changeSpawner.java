package us.thezircon.play.silkyspawnerslite.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import us.thezircon.play.silkyspawnerslite.SilkySpawnersLITE;
import us.thezircon.play.silkyspawnerslite.utils.HexFormat;

public class changeSpawner implements Listener {

    SilkySpawnersLITE plugin = SilkySpawnersLITE.getPlugin(SilkySpawnersLITE.class);

    @EventHandler
    public void onClickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        String itemDiabledAnvil = HexFormat.format(plugin.getLangConfig().getString("itemDiabledAnvil"));

        ItemStack denyItem = new ItemStack(Material.BARRIER);
        ItemMeta meta = denyItem.getItemMeta();
        meta.setDisplayName(itemDiabledAnvil);
        denyItem.setItemMeta(meta);

        String msgPrefix = HexFormat.format(plugin.getLangConfig().getString("msgPrefix"));
        String msgDiabledAnvil = HexFormat.format(plugin.getLangConfig().getString("msgDiabledAnvil"));
        Boolean anvilRename = plugin.getConfig().getBoolean("anvilRename");

        if (anvilRename) {
            return;
        }

        if (e.getInventory().getType().equals(InventoryType.ANVIL)) {
            if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.SPAWNER)) {
                e.getInventory().setItem(3, denyItem);
                e.setCancelled(true);
                player.sendMessage(msgPrefix + " " + msgDiabledAnvil);
            } else if (e.getCurrentItem() != null && e.getCurrentItem().equals(denyItem)) {
                e.setCancelled(true);
            }
        }


    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawnerChangeAttempt(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !(item.getItemMeta() instanceof SpawnEggMeta)) {
            return;
        }
        String msgPrefix = HexFormat.format(plugin.getLangConfig().getString("msgPrefix"));
        player.sendMessage(msgPrefix + " You cannot change spawner types with spawn eggs.");
        event.setCancelled(true);
    }

}