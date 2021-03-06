/*
 *
 *  * Library - Simple plugin which allows you to place books in book shelves!
 *  * Copyright (C) 2018 Max Berkelmans AKA LemmoTresto
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package me.max.library;

import me.max.library.bookshelves.BookShelfManager;
import me.max.library.listeners.BlockBreakListener;
import me.max.library.listeners.BlockPlaceListener;
import me.max.library.listeners.InventoryCloseListener;
import me.max.library.listeners.PlayerInteractListener;
import me.max.library.utils.ConfigUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class Library extends JavaPlugin {

    private BookShelfManager bookShelfManager = null;
    private boolean loaded = false;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        ConfigUtil.saveDefaultConfig(this);
        reloadConfig();

        info("Initialising manager..");
        try {
            bookShelfManager = new BookShelfManager(this);
        } catch (Exception e){
            error("Could not initialise manager!");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        info("Initialising listeners..");
        try {
            new PlayerInteractListener(this);
            //new InventoryClickListener(this);
            new InventoryCloseListener(this);
            new BlockBreakListener(this);
            new BlockPlaceListener(this);
            info("Successfully initialised listeners!");
        } catch (Exception e){
            error("Could not initialise listeners");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        loaded = true;
        info("Started successfully in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        if (!loaded) return; // do not want to save when we did not startup correctly

        info("Saving data..");
        try {
            bookShelfManager.saveData();
            info("Successfully saved data!");
        } catch (Exception e){
            error("Could not save data! All non-saved data will be lost.");
            e.printStackTrace();
        }
    }

    public void info(String s){
        getLogger().info(s);
    }

    public void error(String s){
        getLogger().severe(s);
    }

    public BookShelfManager getBookShelfManager() {
        return bookShelfManager;
    }
}
