package dev.sakurakooi.spigot.meowilbox.listeners;

import com.saicone.rtag.RtagBlock;
import com.saicone.rtag.RtagItem;
import dev.sakurakooi.spigot.meowilbox.MeowilBox;
import dev.sakurakooi.spigot.meowilbox.inv.MeowilBoxUI;
import dev.sakurakooi.spigot.meowilbox.utils.DfuUtils;
import dev.sakurakooi.spigot.meowilbox.utils.InventoryUtils;
import dev.sakurakooi.spigot.meowilbox.utils.ItemBuilder;
import dev.sakurakooi.spigot.meowilbox.utils.MeowilBoxUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.isCancelled())
            return;
        if (MeowilBoxUtils.isMeowilBox(e.getItemInHand())) {
            RtagItem item = new RtagItem(e.getItemInHand());
            RtagBlock.edit(e.getBlockPlaced(), tag -> {
                tag.set(item.get("PublicBukkitValues", "meowilbox:mailbox_mark"), "PublicBukkitValues", "meowilbox:mailbox_mark");
            });
            Bukkit.getScheduler().runTaskLater(MeowilBox.getInstance(), () -> {
                var mailboxPlacedPlayers = MeowilBox.getInstance().getConfig().getStringList("mailboxPlacedPlayers");
                if (!mailboxPlacedPlayers.contains(e.getPlayer().getUniqueId().toString())) {
                    mailboxPlacedPlayers.add(e.getPlayer().getUniqueId().toString());
                    MeowilBox.getInstance().getConfig().set("mailboxPlacedPlayers", mailboxPlacedPlayers);
                    MeowilBox.getInstance().saveConfig();
                }
            }, 1);
        }
        if (MeowilBoxUtils.isMeowilBoxPackage(e.getItemInHand())) {
            RtagItem item = new RtagItem(e.getItemInHand());
            RtagBlock.edit(e.getBlockPlaced(), tag -> {
                tag.set("nya", "PublicBukkitValues", "meowilbox:package_mark");
                tag.set(DfuUtils.getDataVersion(), "PublicBukkitValues", "meowilbox:data_version");
                tag.set(item.get("PublicBukkitValues", "meowilbox:item_content"), "PublicBukkitValues", "meowilbox:item_content");
            });
        }
        if (MeowilBoxUtils.isMeowilBoxPetals(e.getItemInHand())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled())
            return;
        if (MeowilBoxUtils.isMeowilBox(e.getBlock())) {
            e.setDropItems(false);
            RtagBlock tag = new RtagBlock(e.getBlock());
            Material material = Material.valueOf(tag.get("PublicBukkitValues", "meowilbox:mailbox_mark"));
            Bukkit.getScheduler().runTaskLater(MeowilBox.getInstance(), () -> {
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation().toCenterLocation(), ItemBuilder.createMailbox(material));
            }, 1);
        }
        if (MeowilBoxUtils.isMeowilBoxPackage(e.getBlock())) {
            e.setDropItems(false);
            RtagBlock tag = new RtagBlock(e.getBlock());
            InventoryUtils.checkAndUpdateData(tag);
            var items = InventoryUtils.getItemContent(tag);
            Bukkit.getScheduler().runTaskLater(MeowilBox.getInstance(), () -> {
                items.forEach(item -> {
                    e.getBlock().getWorld().dropItem(e.getBlock().getLocation().toCenterLocation(), item);
                });
                e.getBlock().getWorld().playSound(e.getBlock().getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1f, 1f);
            }, 1);
        }
    }


    @EventHandler
    public void onBlockClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
                if (MeowilBoxUtils.isMeowilBox(e.getClickedBlock())) {
                    e.setCancelled(true);
                    if (MeowilBoxUI.openMailBox(e.getPlayer(), true)) {
                        e.getPlayer().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_CHEST_OPEN, 0.75f, 1.2f);
                    }
                    return;
                }
            }
            if (MeowilBoxUtils.isMeowilBoxPetals(e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);
                MeowilBoxUI.openPetalsInventory(e.getPlayer(), e.getPlayer().getInventory().getHeldItemSlot(), e.getPlayer().getInventory().getItemInMainHand());
            }
        }
    }
}
