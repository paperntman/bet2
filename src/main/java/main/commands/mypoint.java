package main.commands;

import main.CurrencyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class mypoint {
    public mypoint(@NotNull SlashCommandInteractionEvent event) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        event.deferReply().queue();
        try {
            CurrencyManager manager = new CurrencyManager(event.getGuild().getId());
            final long point = manager.getPoint(event.getMember().getId());
            final String avatarUrl = event.getMember().getEffectiveAvatarUrl();
            final String mention = event.getMember().getUser().getAsTag();


            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("당신의 포인트 : "+point);
            builder.setAuthor(mention, "https://cdn.discordapp.com/attachments/972446584036003860/972489593733664818/unknown.png", avatarUrl);

            event.getHook().sendMessageEmbeds(builder.build()).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// 다야멍청이