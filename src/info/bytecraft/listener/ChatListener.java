package info.bytecraft.listener;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.*;
import info.bytecraft.api.BytecraftPlayer.ChatState;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.api.PlayerReport.Action;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.ILogDAO;
import info.bytecraft.database.IReportDAO;

public class ChatListener implements Listener
{
    private Bytecraft plugin;
    
    public ChatListener(Bytecraft plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        String message = event.getMessage();
        
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        
        if(player.getChatState() == null){
            player.setChatState(ChatState.CHAT);
            event.setCancelled(true);
            return;
        }
        
        if(player.getChatState() != ChatState.CHAT){
            event.setCancelled(true);
            return;
        }
        if(player.hasFlag(Flag.MUTE)){
            try(IContext ctx = plugin.createContext()){
                IReportDAO dao = ctx.getReportDAO();
                for(PlayerReport report: dao.getReports(player)){
                    if(report.getAction() != Action.MUTE){
                        continue;
                    }
                    Date valid = report.getValidUntil();
                    if(valid == null){
                        continue;
                    }
                    
                    if(valid.getTime() < System.currentTimeMillis()){
                        player.setFlag(Flag.MUTE, false);
                        player.sendMessage(ChatColor.AQUA + "You are no longer muted");
                        event.setCancelled(true);
                        return;
                    }else{
                        player.sendMessage(ChatColor.RED + "You are currently muted.");
                        event.setCancelled(true);
                        return;
                    }
                }
            }catch(DAOException e){
                throw new RuntimeException(e);
            }
        }
        BytecraftPlayer to;
        ChatColor color = ChatColor.WHITE;
        for(Player delegate: Bukkit.getOnlinePlayers()){
            to = plugin.getPlayer(delegate);
            if(to == player){
                color = ChatColor.GRAY;
            }else{
                color = ChatColor.WHITE;
            }
            
            if(player.getRank().canUseColoredChat()){
                message = ChatColor.translateAlternateColorCodes('&', message);
            }
            
            String coloredMessage = "<" + player.getTemporaryChatName() + ChatColor.WHITE + "> " + color + message;
            
            if (player.getRank() == Rank.LORD) {
                coloredMessage =
                        "<" + ChatColor.GREEN + "[Lord]" + player.getTemporaryChatName() + ChatColor.WHITE + "> "
                                + color + message;
            }
            
            if(to.getChatChannel().equalsIgnoreCase(player.getChatChannel())){
                if(to.getChatChannel().equalsIgnoreCase("GLOBAL")){
                    to.sendMessage(coloredMessage);
                }else{
                    to.sendMessage(to.getChatChannel() + coloredMessage);
                }
            }
            
        }
        try (IContext ctx = plugin.createContext()){
            ILogDAO dao = ctx.getLogDAO();
            dao.insertChatMessage(player, player.getChatChannel(), message);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        plugin.getLogger().info(player.getChatChannel() + " <"+ player.getName() + "> " + message);
        event.setCancelled(true);
    }
    
}
