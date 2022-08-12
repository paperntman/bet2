package main.commands;

import main.BetManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class betstart {

    public betstart(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        if(!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.getHook().sendMessage("권한이 없습니다!").queue();
            return;
        }
        if (BetManager.start(event.getChannel().asTextChannel(), event.getOption("title").getAsString(), event.getOption("option_1").getAsString(), event.getOption("option_2").getAsString(), event.getOption("time") == null ? System.currentTimeMillis() + 60000 : System.currentTimeMillis() + event.getOption("time").getAsLong()*1000)) {
            event.getHook().sendMessage("베팅을 시작합니다!").queue();
        }else event.getHook().sendMessage("이미 투표가 진행 중입니다.").queue();
    }
}
