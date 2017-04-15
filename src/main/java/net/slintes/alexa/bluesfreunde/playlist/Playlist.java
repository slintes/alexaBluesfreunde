package net.slintes.alexa.bluesfreunde.playlist;

import java.util.List;

/**
 * Created by msluiter on 15/04/2017.
 */
public class Playlist {

    public String title;
    public List<Song> songs;
    public String nextShow;

    @Override
    public String toString() {
        return "Playlist{" +
                "title='" + title + '\'' +
                ", songs=" + songs +
                ", nextShow='" + nextShow + '\'' +
                '}';
    }
}
