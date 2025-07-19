package dev.wildy.mossad.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StatsCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(StatsCommand.class);

    public StatsCommand() {
        super("stats", "Returns a user's 6b6t stats.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("user", StringArgumentType.string()).executes(context -> {
            String user = StringArgumentType.getString(context, "user");

            getStats(user);

            return SINGLE_SUCCESS;
        }));
    }

    private void getStats(String user) {
        String API_URL = "https://www.6b6t.org/en/stats/" + user;

        try {
            Document doc = Jsoup.connect(API_URL).get();
            Elements rows = doc.select("tr");
            Elements gapElements = doc.select(".gap-2");
            String rank = "";
            String joindate;
            String playtime;
            int columnIndex;

            if (doc.text().contains("Unable to find any stats for user")) {
                info("§cUser hasn't joined 6b6t.org");
                return;
            }
            info("--- Stats for: (highlight)%s(default) ---", user);

            if (gapElements.size() >= 3) {
                Element userInfo = gapElements.get(2);
                Elements spanElements = userInfo.select("span");

                if (spanElements.getFirst().text().contains("Member since")) {
                    rank = "Unranked";
                    joindate = spanElements.getFirst().text().substring(13);
                } else {
                    rank = spanElements.getFirst().text();
                    joindate = spanElements.get(1).text().substring(13);
                }
                info("Rank: (highlight)%s", rank);
                info("Join Date: (highlight)%s", joindate);
            }

            Element row = rows.get(1);
            Elements columns = row.select("td");

            if (rank.isEmpty() || rank.equals("Unranked")) {
                columnIndex = 2;
            } else {
                columnIndex = 3;
            }
            playtime = columns.get(columnIndex).text();

            info("Playtime: (highlight)%s", playtime);
        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
    }
}
