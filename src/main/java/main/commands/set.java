package main.commands;

import main.CurrencyManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class set {
    public set(@NotNull SlashCommandInteractionEvent event) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        event.deferReply(true).queue();
        if(!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.getHook().sendMessage("권한이 없습니다!").queue();
            return;
        }
        try {
            CurrencyManager manager = new CurrencyManager(event.getGuild().getId());
            manager.setup();
            final IMentionable mention = event.getOption("member").getAsMentionable();
            final long point = event.getOption("point").getAsLong();
            if(mention instanceof Role){
                Role role = (Role) mention;
                logger.info(role.getGuild().getMembersWithRoles(role).size()+"");
                role.getGuild().getMembersWithRoles(role).forEach(member -> {
                    manager.setPoint(member.getId(), point);
                    logger.info(member.getId());
                });
                event.getHook().sendMessage(mention +"의 포인트를 "+point+"로 설정하였습니다!").queue();
            }else if(mention instanceof Member) {
                final Member member = (Member) mention;
                manager.setPoint(member.getId(), point);
                event.getHook().sendMessage(mention +"의 포인트를 "+point+"로 설정하였습니다!").queue();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
