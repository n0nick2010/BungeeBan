package net.craftminecraft.bungee.bungeeban.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.BungeeBan;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.util.Utils;
<<<<<<< HEAD
import net.md_5.bungee.api.ProxyServer;
=======
>>>>>>> a20ebe62b287721bf8c42889c3b3cfa994b38de3
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxiedPlayerListener implements Listener {
<<<<<<< HEAD
	private BungeeBan plugin;
	public ProxiedPlayerListener(BungeeBan plugin) {
		this.plugin = plugin;
	}
	
	@Subscribe
	public void onPlayerJoin(final LoginEvent e) {
		e.registerIntent(plugin);
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			@Override
			public void run() {
				BanEntry ban = BanManager.getBan(e.getConnection().getName(), "(GLOBAL)");
				if (ban != null) {
					e.setCancelled(true);
					e.setCancelReason(Utils.formatMessage(ban.getReason(), ban));
				}
				ban = BanManager.getBan(e.getConnection().getAddress().getAddress().getHostAddress(), "(GLOBAL)");
				if (ban != null) {
		            e.setCancelled(true);
		            e.setCancelReason(Utils.formatMessage(ban.getReason(), ban));
				}
				e.completeIntent(plugin);
			}
		});
	}
	
	@Subscribe
	public void onServerConnect(ServerConnectEvent e) {
		BanEntry ban = BanManager.getBan(e.getPlayer().getName(), e.getTarget().getName());
		if (ban != null) {
			// Ugly workaround the player joined... player left messages
			Server srv = e.getPlayer().getServer();
			if (srv != null)
				e.setTarget(srv.getInfo());
			else{
				// if a player join the Server
				if(!e.getTarget().getName().equalsIgnoreCase("login"))
				{
					// the player is not in default server
					
					// check ban on default server
					BanEntry banDefault = BanManager.getBan(e.getPlayer().getName(), "Login");
					
					if(banDefault == null)
					{
						// the player is not banned on default server => move to defaut
						ServerInfo target = ProxyServer.getInstance().getServerInfo("Login");
						e.setTarget(target);
						e.getPlayer().sendMessage("You have been moved to default server");
						return;
					}
				
				}
			}
			e.getPlayer().disconnect(Utils.formatMessage(ban.getReason(), ban));
			return;
		} 
		ban = BanManager.getBan(e.getPlayer().getAddress().getAddress().getHostAddress(), e.getTarget().getName());
		if (ban != null) {
			// Ugly workaround the player joined... player left messages
			Server srv = e.getPlayer().getServer();
			if (srv != null)
				e.setTarget(srv.getInfo());
			else{
				// if a player join the Server
				if(!e.getTarget().getName().equalsIgnoreCase("login"))
				{
					// the player is not in default server
					
					// check ban on default server
					BanEntry banDefault = BanManager.getBan(e.getPlayer().getName(), "Login");
					
					if(banDefault == null)
					{
						// the player is not banned on default server => move to defaut
						ServerInfo target = ProxyServer.getInstance().getServerInfo("Login");
						e.setTarget(target);
						e.getPlayer().sendMessage("You have been moved to default server");
						return;
					}
				
				}
			}
			e.getPlayer().disconnect(Utils.formatMessage(ban.getReason(), ban));
		}
		return;
	}
=======

    private BungeeBan plugin;

    public ProxiedPlayerListener(BungeeBan plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final LoginEvent e) {
        e.registerIntent(plugin);
        plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    BanEntry ban = BanManager.getBan(e.getConnection().getName(), "(GLOBAL)");
                    if (ban != null) {
                        e.setCancelled(true);
                        String reason = Utils.formatMessage(ban.getReason(), ban);
                        e.setCancelReason(reason != null ? reason : "");
                    }
                    ban = BanManager.getBan(e.getConnection().getAddress().getAddress().getHostAddress(), "(GLOBAL)");
                    if (ban != null) {
                        e.setCancelled(true);
                        String reason = Utils.formatMessage(ban.getReason(), ban);
                        e.setCancelReason(reason != null ? reason : "");
                    }
                } finally {
                    e.completeIntent(plugin);
                }
            }
        });
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        BanEntry ban = BanManager.getBan(e.getPlayer().getName(), e.getTarget().getName());
        if (ban != null) {
            // Ugly workaround the player joined... player left messages
            Server srv = e.getPlayer().getServer();
            ServerInfo target = e.getTarget();
            if (srv != null) {
                e.setTarget(srv.getInfo());
            }
            BanManager.silentKick(e.getPlayer().getName(), target, Utils.formatMessage(ban.getReason(), ban));
            return;
        }
        ban = BanManager.getBan(e.getPlayer().getAddress().getAddress().getHostAddress(), e.getTarget().getName());
        if (ban != null) {
            // Ugly workaround the player joined... player left messages
            Server srv = e.getPlayer().getServer();
            ServerInfo target = e.getTarget();
            if (srv != null) {
                e.setTarget(srv.getInfo());
            }
            BanManager.silentKick(e.getPlayer().getName(), target, Utils.formatMessage(ban.getReason(), ban));
        }
        return;
    }
>>>>>>> a20ebe62b287721bf8c42889c3b3cfa994b38de3
}
