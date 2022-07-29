package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Listener extends ListenerAdapter {
    Logger logger = LoggerFactory.getLogger(Listener.class);
    public static JDA jda = Main.jda;
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "bet" -> {

                break;
            }
            case "betstart" -> {
                event.deferReply().queue();
                BetManager.start(event.getChannel().asTextChannel(), event.getOption("title").getAsString(), event.getOption("option_1").getAsString(), event.getOption("option_2").getAsString(), event.getOption("time") == null ? System.currentTimeMillis() + 60000 : System.currentTimeMillis() + event.getOption("time").getAsLong()*1000);
                event.getHook().sendMessage("베팅을 시작합니다!").complete().delete().queueAfter(5, TimeUnit.SECONDS);
                break;
            }
            case "betend" -> {
                break;
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        logger.info(event.getMessage().getContentRaw());
    }
}


/*
    /bet start
    /bet 1 (포인트)
    /bet 2 (포인트)


 */