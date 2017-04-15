package net.slintes.alexa.bluesfreunde.playlist;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by msluiter on 15/04/2017.
 */
public class WebsiteParser {

    private static final Logger log = LoggerFactory.getLogger(WebsiteParser.class);

    public static final void main(String args[]) {
        try {
            new WebsiteParser().parse();
        } catch (Throwable t) {
            log.error("error parsing website", t);
        }
    }

    public Playlist parse() throws Exception {

        Playlist p = new Playlist();
        p.songs = new ArrayList<>();

        log.info("reading website...");
        Document doc = Jsoup.connect("http://www.bluesfreunde.de").get();
        log.info("parsing website...");
        Elements article = doc.select("article[id=radio_playlists]");
        Elements divs = article.select("div[id^=radio]");
        Element playlist = divs.last();

        p.title = playlist.select("h4").text();
        Elements songs = playlist.select("tr");
        for (Element song : songs) {
            Song s = new Song();

            Elements data = song.select("td");
            if (data.size() < 3) {
                continue;
            }
            s.artist =  data.get(0).text().replace(" ft. ", " featuring ");
            s.album =  data.get(1).text();
            s.title =  data.get(2).text();

            p.songs.add(s);
        }

        p.nextShow = article.select("section").last().select("b").text();

        log.info(p.toString());

        return p;
    }
}
