package net.slintes.alexa.bluesfreunde;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import net.slintes.alexa.bluesfreunde.playlist.Playlist;
import net.slintes.alexa.bluesfreunde.playlist.Song;
import net.slintes.alexa.bluesfreunde.playlist.WebsiteParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.*;

/**
 * Created by msluiter on 15/04/2017.
 */
public class BluesfreundeSpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(BluesfreundeSpeechlet.class);

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}",
                request.getRequestId(), session.getSessionId());
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}",
                request.getRequestId(), session.getSessionId());
        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}",
                request.getRequestId(), session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        log.info("intentName {}", intentName);

        if ("LetztePlaylistIntent".equals(intentName)) {
            return getLetztePlaylistResponse();
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else if ("AMAZON.CancelIntent".equals(intentName)) {
            return getStopResponse();
        } else if ("AMAZON.StopIntent".equals(intentName)) {
            return getStopResponse();
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }


    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}",
                request.getRequestId(), session.getSessionId());
    }

    private SpeechletResponse getWelcomeResponse() {
        return getReprompt("Willkommen bei den Bluesfreunden. Du kannst mich nach der letzten Playlist" +
                "unserere Sendung Bluesstammtisch auf der Ems vechte Welle fragen!");
    }

    private SpeechletResponse getHelpResponse() {
        return getReprompt("Du kannst mich nach der letzten Playlist" +
                "unserere Sendung Bluesstammtisch auf der Ems vechte Welle fragen!");
    }

    private SpeechletResponse getReprompt(String speechText) {
        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Ich habe dich nicht verstanden. Bitte wiederhole deine Frage!");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt);
    }

    private SpeechletResponse getStopResponse() {
        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText("Auf Wiederhören bei den Bluesfreunden.");

        return SpeechletResponse.newTellResponse(speech);
    }

    private SpeechletResponse getLetztePlaylistResponse() {

        Playlist playlist = null;
        try {
            playlist = new WebsiteParser().parse();
        } catch (Exception e) {
            // Create the plain text output.
            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText("Beim ermitteln der Playlist ist leider ein Fehler aufgetreten. Bitte versuchen sie es später noch einmal. Auf Wiederhören.");
            return SpeechletResponse.newTellResponse(speech);
        }

        StringBuilder text = new StringBuilder();
        text.append("Hier ist die " + playlist.title + ". ");

        String lastArtist = "";
        String lastAlbum = "";

        for (Song song : playlist.songs) {
            if (!song.artist.isEmpty() && !song.artist.equalsIgnoreCase(lastArtist)) {
                text.append("Von " + song.artist + ": ");
                lastArtist = song.artist;
                lastAlbum = "";
            } else {
                text.append("Sowie ");
            }
            if (!song.album.isEmpty() && !song.album.equalsIgnoreCase(lastAlbum)) {
                text.append("aus dem Album " + song.album + ": ");
                lastAlbum = song.album;
            }
            if (song.title.isEmpty()) {
                // no title? this is something else than a song... skip it
                continue;
            }
            text.append(song.title + ". ");
        }
        text.append(" Das wars. Die nächste Sendung ist am " + playlist.nextShow + ".");

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Bluesfreunde");
        card.setContent(text.toString());

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(text.toString());

        return SpeechletResponse.newTellResponse(speech, card);
    }

}
