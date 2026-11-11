package org.sharkk2.mcord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.sharkk2.mcord.commands.CommandManager;
import org.sharkk2.mcord.commands.discord.SetupCommand;

import java.time.Duration;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

public class MCordBot {
   private volatile JDA jda;
   private final MCord mcord;
   private final CompletableFuture<JDA> jdaFuture = new CompletableFuture<>();

   public MCordBot(MCord mcord) {this.mcord = mcord;}

   public void startBot() {
       String token = mcord.getConfig().getString("bot-token");

       if (token == null || token.isBlank()) {
           mcord.getLogger().severe("MCord bot failed to start! (empty token)");
           mcord.getLogger().severe("Please set a discord bot token! create one from (https://discord.com/developers/applications)");
           return;
       }

       try {
           jda = JDABuilder.createLight(token).enableIntents(
                   GatewayIntent.GUILD_MESSAGES,
                   GatewayIntent.MESSAGE_CONTENT,
                   GatewayIntent.GUILD_MEMBERS
           ).build().awaitReady();
           jdaFuture.complete(jda);
       } catch (Exception e) {
           jdaFuture.completeExceptionally(e);
           mcord.getLogger().severe("MCord bot failed to start! (" + e.getMessage() + ")");
           mcord.getLogger().severe("Make sure the token is valid, privileged gateway intents are enabled and that the server can reach discord");
           return;
       }
       mcord.getLogger().info("MCord bot logged in as " + jda.getSelfUser().getName() + "#" + jda.getSelfUser().getDiscriminator());
   }

   public void stopBot() {
       if (jda != null) {
           jda.shutdown();
           try {
               if (!jda.awaitShutdown(Duration.ofSeconds(6))) {
                   jda.shutdownNow();
                   jda.awaitShutdown();
               }
           } catch (InterruptedException ni) {jda.shutdownNow();}
       }
   }

   public JDA getJDA() {return jda;}

}
