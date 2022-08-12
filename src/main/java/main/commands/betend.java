package main.commands;

import main.BetManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class betend {
    public betend(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        if(!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.getHook().sendMessage("권한이 없습니다!").queue();
            return;
        }
        if (BetManager.end(event.getChannel().asTextChannel(), event.getOption("winner").getAsInt())) {
            event.getHook().sendMessage("베팅을 끝냈습니다!").queue();
        }else event.getHook().sendMessage("투표가 진행 중이지 않습니다.").queue();
    }
}
