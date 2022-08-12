package main.commands;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class test {
    private final ActionRow primaries = ActionRow.of(
            Button.primary("row_0_button_0_","+100"),
            Button.primary("row_0_button_1_","+500"),
            Button.primary("row_0_button_2_","+1000"),
            Button.primary("row_0_button_4_","+50%"),
            Button.primary("row_0_button_5_","+100%"));
    private final ActionRow secondaries = ActionRow.of(
            Button.danger("row_1_button_0_","+100"),
            Button.danger("row_1_button_1_","+500"),
            Button.danger("row_1_button_2_","+1000"),
            Button.danger("row_1_button_4_","+50%"),
            Button.danger("row_1_button_5_","+100%"));

    public test(SlashCommandInteractionEvent event) {
        event.reply("good").complete().deleteOriginal().queue();
        MessageBuilder builder1 = new MessageBuilder()
                .setContent(" ")
                .setActionRows(primaries);
        MessageBuilder builder2 = new MessageBuilder()
                .setContent(" ")
                .setActionRows(secondaries);
        event.getChannel().sendMessage(builder1.build()).queue();
        event.getChannel().sendMessage(builder2.build()).queue();
    }
}
