package main.commands;

import main.BetManager;
import main.CurrencyManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Objects;

public class ButtonManager {
    public static void vote(String team, String much, TextChannel channel, Member member) {
        try {
            CurrencyManager manager = new CurrencyManager(channel.getGuild().getId());
            manager.setup();


            int opt = (Objects.equals(team, "1") ? 0 : 1);
            long point = Long.parseLong(much);
            String mid = member.getId();

            if(manager.getPoint(mid) > point){
                if (BetManager.add(channel, member, point, opt)) {
                    manager.addPoint(mid, -point);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
