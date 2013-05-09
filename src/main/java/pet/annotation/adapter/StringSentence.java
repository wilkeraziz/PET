package pet.annotation.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import pet.annotation.Segment;
import pet.annotation.SegmentType;

public class StringSentence implements Segment {

    private final SegmentType type;
    private final String producer;
    private final String sentence;
    private final List<String> words;
    private final Map<String, String> attributes;
    private static final StringSentence empty = new StringSentence(
            SegmentTypeAdapter.HT, "", "");

    public StringSentence(final SegmentType type, final String sentence, final String producer) {
        this.type = type;
        this.sentence = sentence.trim();
        final String[] tokens = sentence.split("\\s+");
        this.words = new ArrayList<String>(tokens.length);
        this.attributes = Collections.emptyMap();
        for (final String token : tokens) {
            this.words.add(token);
        }
        this.producer = producer;
    }

    public StringSentence(final SegmentType type, final String sentence, final String producer, final Map<String, String> attributes) {
        this.type = type;
        this.sentence = sentence.trim();
        final String[] tokens = sentence.split("\\s+");
        this.words = new ArrayList<String>(tokens.length);
        this.attributes = Collections.unmodifiableMap(new HashMap<String, String>(attributes));
        for (final String token : tokens) {
            this.words.add(token);
        }
        this.producer = producer;
    }

    public static Segment getCaption(final String label) {
        return new StringSentence(
                SegmentTypeAdapter.CAPTION, label, "");
    }

    public StringSentence(final Segment sentence) {
        this(sentence.getType(), sentence.toString(), sentence.getProducer());
    }

    @Override
    public SegmentType getType() {
        return type;
    }

    @Override
    public String toString() {
        return sentence;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof StringSentence)) {
            return false;
        }
        final StringSentence other = (StringSentence) o;
        return sentence.equals(other.sentence);
    }

    @Override
    public int hashCode() {
        return sentence.hashCode();
    }

    public static Segment getEmptySentence() {
        return empty;
    }

    @Override
    public List<String> asTokens() {
        return Collections.unmodifiableList(words);
    }

    @Override
    public String getProducer() {
        return producer;
    }
    
    @Override
    public Map<String, String> getAttributes(){
        return attributes;
    }
}
