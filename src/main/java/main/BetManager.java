package main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class BetManager {

    static List<Betting> list = new ArrayList<>();

    static class Betting {
        TextChannel channel;
        Message embed;
        String title;
        String opt1Name;
        String opt2Name;
        Long time;
        Map<Member, Long> opt1 = new HashMap<>();
        Map<Member, Long> opt2 = new HashMap<>();

        public TextChannel getChannel() {
            return channel;
        }

        public Betting(TextChannel channel ,String title, String opt1Name, String opt2Name, Long time) {
            this.channel = channel;
            this.title = title;
            this.opt1Name = opt1Name;
            this.opt2Name = opt2Name;
            this.time = time;

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(title);
            embed = channel.sendMessageEmbeds(builder.build()).complete();
            update();
        }

        private void update(){
            final MessageEmbed messageEmbed = embed.getEmbeds().get(0);
            String title = messageEmbed.getTitle();
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(title);

            final int opt1Size = opt1.size();
            final int opt2Size = opt2.size();
            final int total = opt1Size+opt2Size;

            AtomicReference<Long> opt1CoinsAtomic = new AtomicReference<>((long) 0);
            opt1.values().forEach(aLong -> opt1CoinsAtomic.updateAndGet(v -> v + aLong));
            AtomicReference<Long> opt2CoinsAtomic = new AtomicReference<>((long) 0);
            opt1.values().forEach(aLong -> opt2CoinsAtomic.updateAndGet(v -> v + aLong));

            final long opt1Coins = opt1CoinsAtomic.get();
            final long opt2Coins = opt2CoinsAtomic.get();
            final long totalCoins = opt1Coins+opt2Coins;


            builder.addField(opt1Name, String.format(
                    " %d <:user:1002607879636910090> \n%f%% \n%d <:coin:1002608441791090718> \n1 : %f ",
                    opt1Size,
                    opt1Size == 0 ? 0 : (double) total/ (double) opt1Size * 100,
                    opt1Coins,
                    opt1Coins == 0 ? 0 : (double)totalCoins / (double)opt1Coins
                    ), true);
            builder.addField(opt2Name, String.format(
                    " %d <:user:1002607879636910090> \n%f%% \n%d <:coin:1002608441791090718> \n1 : %f ",
                    opt2Size,
                    opt2Size == 0 ? 0 : (double) total/ (double) opt2Size * 100,
                    opt2Coins,
                    opt2Coins == 0 ? 0 : (double)totalCoins / (double)opt2Coins
            ), true);
            long seconds = (time - System.currentTimeMillis())/1000;
            Long second = seconds % 60;
            Long minute = seconds / 60;
            builder.setFooter(minute > 0 ? minute+"분 " : "" + second+"초 남음");

            embed.editMessageEmbeds(builder.build()).queue();
        }

        public void add(Member m, Long point, int opt){
            Map<Member, Long> map = opt==1 ? opt1 : opt2;
            if (map.getOrDefault(m, (long) -1) == -1) {
                map.put(m, point);
            }else {
                map.put(m, map.get(m)+point);
            }
        }
    }



    public static boolean start(TextChannel channel, String title, String opt1, String opt2, Long time){
        if (list.stream().anyMatch(betting -> betting.channel.equals(channel))) return false;
        list.add(new Betting(channel, title, opt1, opt2, time));
        return true;
    }

    public static void add(TextChannel channel, Member m, Long point, int opt){
        list.stream().filter(betting -> betting.channel.equals(channel)).forEach(betting -> betting.add(m, point, opt));
    }




}
