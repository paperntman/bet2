package main.commands;

import main.BetManager;
import main.CurrencyManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

// STOPSHIP: 2022-08-07 다이아 멍청이 👍

public class bet {
    public bet(@NotNull SlashCommandInteractionEvent event) {
        try {
            event.deferReply(true).queue();
            CurrencyManager manager = new CurrencyManager(event.getGuild().getId());
            manager.setup();
            if(event.getOption("point").getAsLong() <= 0) {
                event.getHook().sendMessage("1 이상의 포인트를 입력해 주세요!").queue();
                return;
            }
            if(manager.getPoint(event.getMember().getId()) < event.getOption("point").getAsLong()) {
                event.getHook().sendMessageFormat("포인트가 없습니다! (현재 포인트 : %d)", manager.getPoint(event.getMember().getId())).queue();
                return;
            }

            if (BetManager.add(event.getChannel().asTextChannel(), event.getMember(), event.getOption("point").getAsLong(), event.getOption("option").getAsInt())) {
                manager.addPoint(event.getMember().getId(), -event.getOption("point").getAsLong());
                event.getHook().sendMessageFormat(event.getOption("point").getAsString()+" 포인트를 지불하셨습니다! (현재 포인트 : %d)", manager.getPoint(event.getMember().getId())).queue();
            }else{
                event.getHook().sendMessage("오류가 발생하였습니다.").complete();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
