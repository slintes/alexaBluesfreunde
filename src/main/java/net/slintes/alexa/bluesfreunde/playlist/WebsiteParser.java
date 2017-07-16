package net.slintes.alexa.bluesfreunde.playlist;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by msluiter on 15/04/2017.
 */
public class WebsiteParser {

    private static final Logger log = LoggerFactory.getLogger(WebsiteParser.class);

    public static final void main(String args[]) {
        try {
            WebsiteParser wp = new WebsiteParser();
            wp.parse();
        } catch (Throwable t) {
            log.error("error parsing website", t);
        }


    }

    public Playlist parse() throws Exception {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }

        Playlist p = new Playlist();
        p.songs = new ArrayList<>();

        log.info("reading website...");
//        Document doc = Jsoup.parse(new File("bluesfreunde.htm"), "UTF-8");
        Document doc = Jsoup.connect("https://www.bluesfreunde.de/category/radio").get();

        log.info("parsing website...");
        Elements articles = doc.select("article.type-post");
        Element playlist = articles.stream()
                .filter(a -> !a.select("header > h1 > a:contains(playlist)").isEmpty())
                .findFirst().orElse(null);

        if (playlist == null) {
            throw new Exception("didn't find playlist article");
        }

        p.title = playlist.select("header > h1 > a").text();

        Elements songs = playlist.select("tr");
        for (Element song : songs) {
            Song s = new Song();

            Elements data = song.select("td");
            if (data.size() < 3) {
                continue;
            }
            s.artist =  trim(data.get(0).text().replace(" ft. ", " featuring "));
            s.album =  trim(data.get(1).text());
            s.title =  trim(data.get(2).text());

            p.songs.add(s);
        }

        p.nextShow = playlist.select("h2").last().text();

        log.info(p.toString());

        return p;
    }

    private String trim(String s) {
        // remove leading and trailing non breaking spaces
        String result = s.replaceAll("(^\\h*)|(\\h*$)", "");
        result = result.trim();
        if(result.equals("\"")) {
            // used for "ditto"
            return "";
        }
        return result;
    }

}
