package main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BetManager {

    static List<Betting> list = new ArrayList<>();

    static class Betting {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        TextChannel channel;
        Message embed;
        String title;
        String opt1Name;
        String opt2Name;
        Long time;
        boolean voting;
        Map<Member, Long> opt1 = new HashMap<>();
        Map<Member, Long> opt2 = new HashMap<>();
        Timer timer = new Timer();
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

        public TextChannel getChannel() {
            return channel;
        }

        public Betting(TextChannel channel ,String title, String opt1Name, String opt2Name, Long time) {
            this.channel = channel;
            this.title = title;
            this.opt1Name = opt1Name;
            this.opt2Name = opt2Name;
            this.time = time+1000;

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(title);
            embed = channel.sendMessageEmbeds(builder.build()).complete();
            voting = true;
            updateStart();
        }


        private void updateStart(){
            update();
            long delay = (time - System.currentTimeMillis())%1000 + 500;
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    final long seconds = (time - System.currentTimeMillis()) / 1000;
                    final long second = seconds % 60;
                    final long minute = seconds / 60;
                    if(minute <= 0){
                        if(second <= 0) end();
                        else if(second % 15 == 0) update();
                        else if(second <= 10) update();
                    }else if(second == 0){
                        update();
                    }

                }
            }, delay, 1000);
        }

        private MessageEmbed topEmbed(){
            opt1.forEach((member, aLong) -> logger.info("{}, {}", member.getId(), aLong));
            opt2.forEach((member, aLong) -> logger.info("{}, {}", member.getId(), aLong));

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(title);

            final int opt1Size = opt1.size();
            final int opt2Size = opt2.size();
            final int total = opt1Size+opt2Size;

            AtomicReference<Long> opt1CoinsAtomic = new AtomicReference<>((long) 0);
            opt1.values().forEach(aLong -> opt1CoinsAtomic.updateAndGet(v -> v + aLong));
            AtomicReference<Long> opt2CoinsAtomic = new AtomicReference<>((long) 0);
            opt2.values().forEach(aLong -> opt2CoinsAtomic.updateAndGet(v -> v + aLong));

            final long opt1Coins = opt1CoinsAtomic.get();
            final long opt2Coins = opt2CoinsAtomic.get();
            final long totalCoins = opt1Coins+opt2Coins;


            builder.addField(opt1Name, String.format(
                    " %d <:user:1002607879636910090> \n%f%% \n%d <:coin:1002608441791090718> \n1 : %f ",
                    opt1Size,
                    opt1Size == 0 ? 0 : (double) opt1Size/ (double) total  * 100,
                    opt1Coins,
                    opt1Coins == 0 ? 0 : (double)totalCoins / (double)opt1Coins
            ), true);
            builder.addField(opt2Name, String.format(
                    " %d <:user:1002607879636910090> \n%f%% \n%d <:coin:1002608441791090718> \n1 : %f ",
                    opt2Size,
                    opt2Size == 0 ? 0 : (double) opt2Size/ (double) total * 100,
                    opt2Coins,
                    opt2Coins == 0 ? 0 : (double)totalCoins / (double)opt2Coins
            ), true);
            long seconds = (time - System.currentTimeMillis())/1000;
            long second = seconds % 60;
            long minute = seconds / 60;
            if(minute > 0) builder.setFooter(minute+"분 남음");
            else if(second > 0) builder.setFooter(second+"초 남음");
            else {
                builder.setFooter("투표 종료!");
                voting = false;
            }
            return builder.build();
        }

        private MessageEmbed botEmbed(){
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("순위");

            List<Map.Entry<Member, Long>> opt1Entries = new ArrayList<>(opt1.entrySet().stream().toList());
            if(opt1Entries.size() > 0){
                if(opt1Entries.size() > 1)
                opt1Entries.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                final LinkedHashMap<Member, Long> opt1LinkedHashMap = opt1Entries.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (aLong, aLong2) -> aLong, LinkedHashMap::new));
                final Member[] opt1Members = opt1LinkedHashMap.keySet().toArray(Member[]::new);
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < 5 && opt1LinkedHashMap.size()-i > 0; i++){

                    stringBuilder
                            .append(opt1Members[i].getUser().getAsTag())
                            .append(" : ")
                            .append(opt1LinkedHashMap.get(opt1Members[i]))
                            .append(" (")
                            .append((int) ((double) opt1LinkedHashMap.get(opt1Members[i]) / (double) opt1.values().stream().mapToLong(j -> j).sum() * 100))
                            .append("%) \n");
                }
                builder.addField(opt1Name, stringBuilder.toString(), true);
            }


            List<Map.Entry<Member, Long>> opt2Entries = new ArrayList<>(opt2.entrySet().stream().toList());
            if(opt2Entries.size() > 0){
                if(opt2Entries.size() > 1)
                opt2Entries.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                final LinkedHashMap<Member, Long> opt2LinkedHashMap = opt2Entries.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (aLong, aLong2) -> aLong, LinkedHashMap::new));
                final Member[] opt2Members = opt2LinkedHashMap.keySet().toArray(Member[]::new);
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < 5 && opt2LinkedHashMap.size()-i > 0; i++) {

                    stringBuilder
                            .append(opt2Members[i].getUser().getAsTag())
                            .append(" : ")
                            .append(opt2LinkedHashMap.get(opt2Members[i]))
                            .append(" (")
                            .append((int) ((double) opt2LinkedHashMap.get(opt2Members[i]) / (double) opt2.values().stream().mapToLong(j -> j).sum() * 100))
                            .append("%) \n");
                }
                builder.addField(opt2Name, stringBuilder.toString(), true);
            }

            return builder.build();
        }

        private void update(){

            Message message = new MessageBuilder()
                    .setEmbeds(topEmbed(), botEmbed())
                    .setActionRows(primaries)
                    .build();

            embed.editMessage(message).queue();

        }


        private void end(){
            update();
            timer.cancel();
        }

        public void distribute(int winnerTeam){
            channel.sendMessage("**"+ (winnerTeam == 1 ? opt1Name : opt2Name)+"** 가 승리하였습니다!").queue();
            time = 0L;
            update();
            timer.cancel();
            Map<Member, Long> winTeam = winnerTeam == 1 ? opt1 : opt2;
            long totalCoin = opt1.values().stream().mapToLong(i -> i).sum() + opt2.values().stream().mapToLong(i -> i).sum();
            long winnerTotalCoin = winTeam.values().stream().mapToLong(i -> i).sum();
            try {
                final CurrencyManager manager = new CurrencyManager(channel.getGuild().getId());
                logger.info(manager.setup()+"");
                winTeam.forEach((member, aLong) -> {
                    double percent = (double) aLong / (double) winnerTotalCoin;
                    manager.addPoint(member.getId(), (int) (totalCoin * percent));
                    logger.info("gave {} points to {}", (int) (totalCoin * percent), member.getId());
                });
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }




        public boolean add(Member m, Long point, int opt){
            if(!voting) return false;
            Map<Member, Long> map = opt==1 ? opt1 : opt2;

            Map<Member, Long> appositeMap = opt==1 ? opt2 : opt1;
            if ((appositeMap.getOrDefault(m, -1L) != -1)) {
                return false;
            }
            if (map.getOrDefault(m, (long) -1) == -1) {
                map.put(m, point);
            }else {
                map.put(m, map.get(m)+point);
            }
            update();
            return true;
        }
    }



    public static boolean start(TextChannel channel, String title, String opt1, String opt2, Long time){
        if (list.stream().anyMatch(betting -> betting.channel.equals(channel))) return false;
        list.add(new Betting(channel, title, opt1, opt2, time));
        return true;
    }

    public static boolean add(TextChannel channel, Member m, Long point, int opt){
        AtomicBoolean voted = new AtomicBoolean(false);
        list.stream().filter(betting -> betting.channel.equals(channel)).forEach(betting -> voted.set(betting.add(m, point, opt)));
        return voted.get();
    }

    public static boolean end(TextChannel channel, int opt){

        if (list.stream().noneMatch(betting -> betting.channel.equals(channel))) return false;
        list.stream().filter(betting -> betting.channel.equals(channel)).forEach(betting -> betting.distribute(opt));
        list.removeIf(next -> next.getChannel().equals(channel));
        return true;
    }

    public static boolean isVoting(TextChannel channel){
        if (list.stream().noneMatch(betting -> betting.channel.equals(channel))) return false;
        AtomicBoolean ret = new AtomicBoolean(false);
        list.stream().filter(betting -> betting.channel.equals(channel)).forEach(betting -> ret.set(betting.voting));
        return ret.get();
    }

    public static boolean isVote(Message message){
        return list.stream().anyMatch(betting -> betting.embed.equals(message));
    }




}
