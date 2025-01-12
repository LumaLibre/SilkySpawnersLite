package us.thezircon.play.silkyspawnerslite.events;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import us.thezircon.play.silkyspawnerslite.SilkySpawnersLITE;
import us.thezircon.play.silkyspawnerslite.utils.HexFormat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static us.thezircon.play.silkyspawnerslite.utils.SpawnerGiver.capitalizeWord;

public class breakSpawner implements Listener{

    SilkySpawnersLITE plugin = SilkySpawnersLITE.getPlugin(SilkySpawnersLITE.class);

    private DecimalFormat f = new DecimalFormat("#0.00");
    private final static List<UUID> playerBeenWarned = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e){
        Block block = e.getBlock(); // Why would this guy do a full config pull before even checking if the block is a spawner????
        // Edited by Jsinco
        if (!block.getType().equals(Material.SPAWNER)){
            return;
        }

        boolean requireMinePerm = plugin.getConfig().getBoolean("requireMineperm");
        boolean doDrop2Ground = plugin.getConfig().getBoolean("doDrop2Ground");
        boolean doPreventBreaking = plugin.getConfig().getBoolean("doPreventBreaking");
        boolean requireSilk = plugin.getConfig().getBoolean("requireSilk");
        boolean chargeOnBreak = plugin.getConfig().getBoolean("chargeOnBreak.enabled");
        boolean sendMSG = plugin.getConfig().getBoolean("chargeOnBreak.sendMSG");
        double priceOnBreak = plugin.getConfig().getDouble("chargeOnBreak.price");
        String msgFullInv = HexFormat.format(plugin.getLangConfig().getString("msgFullInv"));
        String msgPrefix = HexFormat.format(plugin.getLangConfig().getString("msgPrefix"));
        String msgChargedOnMine = HexFormat.format(plugin.getLangConfig().getString("msgChargedOnMine"));
        String msgFundsNeeded = HexFormat.format(plugin.getLangConfig().getString("msgFundsNeeded"));
        String defaultSpawnerName = HexFormat.format(plugin.getLangConfig().getString("spawnerName"));
        String msgYouMayNotBreakThis = HexFormat.format(plugin.getLangConfig().getString("msgYouMayNotBreakThis"));

        Player player = e.getPlayer();
        Location loc = e.getBlock().getLocation();

        //Check if world is blacklisted
        World world = player.getWorld();
        List<String> blacklistedWorlds = plugin.getConfig().getStringList("blacklist");
        if (blacklistedWorlds.contains(world.getName())) {
            return;
        }

        //Drop %
        double spawnerDropChance = plugin.getConfig().getDouble("spawnerDropChance");
        if (spawnerDropChance != 1.00) {
            double dropNum = Math.random();
            if (dropNum >= spawnerDropChance) {
                return;
            }
        }

        // Jsinco
        if (requireMinePerm && !player.hasPermission("silkyspawners.mine")) {
            return;
        } else if (requireSilk && !player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            if (doPreventBreaking) {
                if (!playerHasBeenWarned(player) || !player.isSneaking()) {
                    player.sendMessage(msgPrefix + " " + msgYouMayNotBreakThis);
                    e.setCancelled(true);
                }
            }
            return;
        }

        // Check if a tool type is required
        Material material = Material.getMaterial(plugin.getConfig().getString("requiredTool"));
        if (material!=Material.AIR && material!=player.getInventory().getItemInMainHand().getType()) {
            return; // No message needed?
        }

        // Stop spawners from dropping xp
        e.setExpToDrop(0);

        if (requireMinePerm && !player.hasPermission("silkyspawners.mine")) {
            return;
        }

        e.setExpToDrop(0); //Disabled XP

        if (chargeOnBreak && !player.hasPermission("silkyspawners.charge.exempt")) {
            EconomyResponse r = plugin.getEconomy().withdrawPlayer(player, priceOnBreak);
            if(r.transactionSuccess()) {
                if (sendMSG) {
                    player.sendMessage(msgPrefix + " " + msgChargedOnMine.replace("{PRICE}", f.format(priceOnBreak)));
                }
            } else {
                player.sendMessage(msgPrefix + " " + msgFundsNeeded);
                e.setCancelled(true);
                return;
            }

        }

        //Get Spawner
        CreatureSpawner cs = (CreatureSpawner) block.getState();

        //Give or Drop Spawner
        ItemStack spawner_to_give = new ItemStack(Material.SPAWNER);
        BlockStateMeta meta = (BlockStateMeta) spawner_to_give.getItemMeta();
        CreatureSpawner csm = (CreatureSpawner) meta.getBlockState();

        csm.setSpawnedType(cs.getSpawnedType());

        //Spawners Meta
        meta.setBlockState(csm);
        //meta.setDisplayName(ChatColor.AQUA + (cs.getSpawnedType().toString().replace("_", " ")) + " Spawner");
        defaultSpawnerName = defaultSpawnerName.replace("{TYPE-Minecraft}", capitalizeWord(csm.getSpawnedType().toString().toLowerCase().replace("_", " ")));
        defaultSpawnerName = defaultSpawnerName.replace("{TYPE}", csm.getSpawnedType().toString().replace("_", " "));
        meta.setDisplayName(defaultSpawnerName);
        meta.addItemFlags();

        spawner_to_give.setItemMeta(meta); // Set Meta

        //Apply NBT Data
        ItemStack finalSpawner = plugin.getNMS().set("SilkyMob", spawner_to_give, cs.getSpawnedType().toString());

        if (doDrop2Ground) { // Drops Spawner to ground
            block.getWorld().dropItemNaturally(loc, finalSpawner);
        } else { // Gives spawner to inventory
            if (player.getInventory().firstEmpty() == -1) {
                block.getWorld().dropItemNaturally(loc, finalSpawner);
                player.sendMessage(msgPrefix+" "+msgFullInv);
            } else {
                player.getInventory().addItem(finalSpawner);
            }
        }

    }

    private boolean playerHasBeenWarned(Player player) {
        if (playerBeenWarned.contains(player.getUniqueId())) {
            playerBeenWarned.remove(player.getUniqueId());
            return true;
        } else {
            playerBeenWarned.add(player.getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> playerBeenWarned.remove(player.getUniqueId()), 200L);
            return false;
        }
    }
}