package net.slintes.alexa.bluesfreunde;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by msluiter on 15/04/2017.
 */
public class BluesfreundeHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> supportedApplicationIds = new HashSet<>();

    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds.add("amzn1.ask.skill.1487c831-ba17-4fae-ab96-fdba45019133");
    }

    public BluesfreundeHandler() {
        super(new BluesfreundeSpeechlet(), supportedApplicationIds);
    }

}
