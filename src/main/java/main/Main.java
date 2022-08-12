package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static JDA jda;
    static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws Exception{

        jda = JDABuilder
                .createDefault("OTQyNjY0ODYyNjk2MzcwMTc3.GAMt8-.wRS0qFi5HLobElfLBGUwbeedC5iC6lYOiDf2Ds", GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new Listener())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build().awaitReady();

        boolean commandchange = false;
        if(commandchange)
        jda.getGuilds().forEach(guild -> {
            final Thread thread = new Thread(() -> {
                try {
                    logger.info("setting commands of {}({})", guild.getName(), guild.getId());
                    for (Command command : guild.retrieveCommands().complete()) {
                        if (command.getApplicationId().equals(jda.getSelfUser().getApplicationId()))
                            command.delete().complete();
                    }

                    guild.upsertCommand(Commands.slash("betstart", "베팅을 시작합니다.").addOption(OptionType.STRING, "title", "베팅의 제목입니다!", true).addOption(OptionType.STRING, "option_1", "1번 선택지입니다.", true).addOption(OptionType.STRING, "option_2", "2번 선택지입니다.", true).addOption(OptionType.INTEGER, "time", "투표를 진행할 시간(단위: 초)입니다. 선택하지 않을 시 1분 동안 진행됩니다.")).queue();
                    guild.upsertCommand(Commands.slash("betend", "베팅을 끝냅니다.").addOption(OptionType.INTEGER, "winner", "1번과 2번, 어느 쪽의 승리인가요?", true)).queue();
                    guild.upsertCommand(Commands.slash("bet", "베팅합니다!").addOption(OptionType.INTEGER, "option", "1번과 2번 중 골라주세요!", true).addOption(OptionType.INTEGER, "point", "얼마나 많은 포인트를 거시겠어요?", true)).queue();
                    guild.upsertCommand(Commands.slash("set", "포인트를 설정합니다.").addOption(OptionType.MENTIONABLE, "member", "포인트를 설정할 멤버입니다.", true).addOption(OptionType.INTEGER, "point", "얼마로 설정하시겠어요?", true)).queue();
                    guild.upsertCommand(Commands.slash("mypoint", "내 포인트를 확인합니다")).queue();
                    guild.upsertCommand(Commands.slash("test", "test")).queue();
                    logger.info("finished setting commands of {}({})", guild.getName(), guild.getId());
                } catch (ErrorResponseException e) {
                    logger.info("no permission {}({})", guild.getName(), guild.getId());
                }
            });
            thread.start();
        });
    }
}