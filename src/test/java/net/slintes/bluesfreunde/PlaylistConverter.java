package net.slintes.bluesfreunde;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaylistConverter {

    private String lastArtist = "";
    private String lastAlbum = "";
    private String lastLabel = "";

    public static void main(String args[]) throws Exception {
        new PlaylistConverter().run();
    }

    private void run() throws Exception {

        List<String> all = Files.readAllLines(Paths.get("/Users/msluiter/Downloads/Playlist.csv"), Charset.forName("windows-1250"));
        //all.forEach(line -> System.out.println((line)));

        MyDate date = parseDate(all.get(0));
        String dateString = date.toString();

        System.out.println(
                "Playlist " + dateString + "\n" +
                "<table class=\"table table-responsive\">\n" +
                "  <thead>\n" +
                "    <tr>\n" +
                "      <th>Interpret</th>\n" +
                "      <th>Album</th>\n" +
                "      <th>Titel</th>\n" +
                "      <th>Label</th>\n" +
                "    </tr>\n" +
                "  </thead>\n" +
                "  <tbody>");


        all.forEach(line -> parseLine(line));

        System.out.println(
                "  </tbody>\n" +
                "</table>\n" +
                "<h2>Die nächste Sendung ist am <strong></strong>!</h2>\n" +
                "&nbsp;");

    }

    private MyDate parseDate(String line) {
        Pattern p = Pattern.compile(".*(?<date>\\d\\d\\.\\d\\d\\.\\d\\d).*");
        Matcher m = p.matcher(line);
        if(m.find()) {
            String date = m.group("date");
            return new MyDate(date);
        }
        System.out.println("ACHTUNG: DATUM NICHT GEFUNDEN!");
        return null;
    }

    private void parseLine(String line) {
        String[] parts = line.split(";");
        if (parts.length >= 3) {
            if (parts[0].startsWith("Playlist")) {
                return;
            }
            if (parts[0].startsWith("Interpret")) {
                return;
            }
            if (parts[0].isEmpty() && parts[1].isEmpty() && parts[2].isEmpty() && parts[3].isEmpty()) {
                return;
            }

            String ditto = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"";

            boolean newArtist = true;
            String artist = parts[0];
            if (artist.equals(lastArtist) || artist.isEmpty() || artist.equals("\"") || artist.startsWith("\"\"")) {
                artist = ditto;
                newArtist = false;
            } else {
                lastArtist = artist;
            }

            String album = parts[1];
            if (!newArtist && (album.equals(lastAlbum) || album.isEmpty() || album.equals("\"") || album.startsWith("\"\""))) {
                album = ditto;
            } else {
                lastAlbum = album;
            }

            String song = parts[2];

            String label = "";
            if (parts.length == 4) {
                label = parts[3];
            }
            if (!newArtist && (label.equals(lastLabel) || label.isEmpty() || label.equals("\"") || label.startsWith("\"\""))) {
                label = ditto;
            } else {
                lastLabel = label;
            }

            System.out.println(String.format("    <tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", artist, album, song, label));
        }
    }

    static class MyDate {
        private String day;
        private String month;
        private String year;

        public MyDate(String date) {
            String[] parts = date.split("\\.");
            day = parts[0];
            month = parts[1];
            year = parts[2];

            if(day.length() == 1) {
                day = "0" + day;
            }
            if(month.length() == 1) {
                month = "0" + month;
            }
            if(year.length() == 2) {
                year = "20" + year;
            }
        }

        @Override
        public String toString() {
            return day + "." + month + "." + year;
        }
    }

}
