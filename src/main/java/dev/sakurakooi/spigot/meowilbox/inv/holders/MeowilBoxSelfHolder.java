package dev.sakurakooi.spigot.meowilbox.inv.holders;

import dev.sakurakooi.spigot.meowilbox.inv.MeowilBoxHolder;
import dev.sakurakooi.spigot.meowilbox.utils.MeowilBoxItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MeowilBoxSelfHolder implements MeowilBoxHolder {
    @Getter
    private Inventory inventory;

    @Getter
    private Player player;

    public MeowilBoxSelfHolder(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 45, Component.text("Petals")); // FIXME text

        // 0-26
        // TODO load inventory

        for (int i = 27; i < 36; i++) {
            inventory.setItem(i, MeowilBoxItemBuilder.createPaddingPane());
        }

        // 36-44
        inventory.setItem(36, MeowilBoxItemBuilder.createPlayerListButton());
        if (hasPrevPage()) {
            inventory.setItem(42, MeowilBoxItemBuilder.createPrevPageButton());
        } else {
            inventory.setItem(42, MeowilBoxItemBuilder.createPageStopButton(false));
        }
        if (hasNextPage()) {
            inventory.setItem(44, MeowilBoxItemBuilder.createNextPageButton());
        } else {
            inventory.setItem(44, MeowilBoxItemBuilder.createPageStopButton(true));
        }
    }

    private boolean hasPrevPage() {
        return true;
    }

    private boolean hasNextPage() {
        return false;
    }

    @Override
    public void saveData() {

    }
}
