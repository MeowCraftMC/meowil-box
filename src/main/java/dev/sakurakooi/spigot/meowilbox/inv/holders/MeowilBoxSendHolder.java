package dev.sakurakooi.spigot.meowilbox.inv.holders;

import dev.sakurakooi.spigot.meowilbox.MeowilBox;
import dev.sakurakooi.spigot.meowilbox.inv.MeowilBoxHolder;
import dev.sakurakooi.spigot.meowilbox.utils.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MeowilBoxSendHolder implements MeowilBoxHolder {
    @Getter
    private final Inventory inventory;
    private final Player sender;
    private final OfflinePlayer targetPlayer;

    public MeowilBoxSendHolder(Player sender, OfflinePlayer targetPlayer) {
        this.sender = sender;
        this.targetPlayer = targetPlayer;
        this.inventory = Bukkit.createInventory(this, InventoryType.DISPENSER, Component.text("关闭以寄出").color(NamedTextColor.RED));
    }

    @Override
    public void saveData() {
        var content = Arrays.stream(inventory.getContents()).filter(Objects::nonNull).toList();
        if (content.isEmpty()) {
            sender.sendMessage(  Component.text()
                    .append(Component.text("MeowilBox", NamedTextColor.YELLOW, TextDecoration.BOLD))
                    .append(Component.text(" >> ", NamedTextColor.GRAY))
                    .append(Component.text("你要寄出的纸箱是空的!" , NamedTextColor.RED)));
            sender.getWorld().playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1f, 1f);
            return;
        }
        try {
            var storage = MeowilBox.getMailboxManager().getMailbox(targetPlayer);
            String senderUUID = sender.getUniqueId().toString();
            boolean isAuthor = senderUUID.equals("f2e3251f-65ab-4386-8839-7f402386faee") || // online
                    senderUUID.equals("fa9c69db-a8ee-3bab-9aa9-67a0ca1ebd13") || // offline
                    sender.getName().equals("SakuraKooi");
            storage.getContents().add(ItemBuilder.createItemPackage(sender, content, false));
            storage.save();
            sender.sendMessage(  Component.text()
                    .append(Component.text("MeowilBox", NamedTextColor.YELLOW, TextDecoration.BOLD))
                    .append(Component.text(" >> ", NamedTextColor.GRAY))
                    .append(Component.text("给 " + targetPlayer.getName() + " 的纸箱已寄出!w" , NamedTextColor.GREEN)));
            sender.getWorld().playSound(sender.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 1f, 1.5f);
            if (targetPlayer.isOnline()) {
                Player player = targetPlayer.getPlayer();
                if (player != null) {
                    player.sendMessage(  Component.text()
                            .append(Component.text("MeowilBox", NamedTextColor.YELLOW, TextDecoration.BOLD))
                            .append(Component.text(" >> ", NamedTextColor.GRAY))
                            .append(Component.text("你收到了一个来自 " + sender.getName() + " 的纸箱!w" , NamedTextColor.GOLD)));
                    player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 0.4f, 1.5f);
                } // else should not happen
            }
        } catch (ExecutionException e) {
            Arrays.stream(inventory.getContents()).filter(Objects::nonNull).forEach(itemStack -> sender.getWorld().dropItem(sender.getLocation(), itemStack));
            sender.sendMessage(  Component.text()
                    .append(Component.text("MeowilBox", NamedTextColor.YELLOW, TextDecoration.BOLD))
                    .append(Component.text(" >> ", NamedTextColor.GRAY))
                    .append(Component.text("寄件失败! 访问对方的邮箱时发生错误" , NamedTextColor.RED)));
        }
    }
}
