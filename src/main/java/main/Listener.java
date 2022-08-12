package main;

import main.commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Listener extends ListenerAdapter {
    Logger logger = LoggerFactory.getLogger(Listener.class);
    public static JDA jda = Main.jda;
    CurrencyManager manager;
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "set" -> new set(event);
            case "bet" -> new bet(event);
            case "betstart" -> new betstart(event);
            case "betend" -> new betend(event);
            case "mypoint" -> new mypoint(event);
            case "test" -> new test(event);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (BetManager.isVote(event.getMessage()) && BetManager.isVoting(event.getChannel().asTextChannel())) {
                Pattern pattern = Pattern.compile("[_](.*?)[_]");
                Matcher matcher = pattern.matcher(event.getComponentId());
                String team = "", much = "";
                while(matcher.find()){
                    if(!Objects.equals(team, "")) much = matcher.group(1);
                    else team = matcher.group(1);
                    if(matcher.group(1) == null) break;
                }

                ButtonManager.vote(team, much, event.getChannel().asTextChannel(), event.getMember());
                event.deferReply(true).complete().sendMessage("")
        }
    }
}


/*
    /bet start
    /bet 1 (포인트)
    /bet 2 (포인트)


 */