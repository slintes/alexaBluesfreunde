package net.slintes.alexa.bluesfreunde.playlist;

/**
 * Created by msluiter on 15/04/2017.
 */
public class Song {

    public String artist;
    public String album;
    public String title;

    @Override
    public String toString() {
        return "Song{" +
                "artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
