/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pet.annotation.Segment;
import pet.annotation.SegmentType;
import pet.annotation.adapter.SegmentTypeAdapter;
import pet.annotation.adapter.StringSentence;

/**
 *
 * @author waziz
 */
public class StringSentenceParser {

    public static Segment parseSource(final Element xml) {
        return parse(xml, SegmentTypeAdapter.SOURCE);
    }

    public static Segment parseReference(final Element xml) {
        return parse(xml, SegmentTypeAdapter.REFERENCE);
    }

    public static Segment parseMT(final Element xml) {
        return parse(xml, SegmentTypeAdapter.MT);
    }

    public static Segment parseHT(final Element xml) {
        return parse(xml, SegmentTypeAdapter.HT);
    }

    public static Segment parsePE(final Element xml) {
        return parse(xml, SegmentTypeAdapter.POST_EDITED_MT);
    }

    private static Segment parse(final Element xml, final SegmentType type) {
        final String producer = xml.getAttribute(ParseHandler.PRODUCER);
        return new StringSentence(type, xml.getTextContent().trim(), producer);
    }

    public static Element toXML(final Document xml,
            final Segment sentence) {
        final Element xmlSentence = xml.createElement(getTag(sentence));
        xmlSentence.setAttribute(ParseHandler.PRODUCER, sentence.getProducer());
        //Jsoup.parse(sentence.toString()).text()
        xmlSentence.setTextContent(sentence.toString());
        return xmlSentence;
    }

    private static String getTag(final Segment sentence) {
        if (sentence.getType().equals(SegmentTypeAdapter.SOURCE)) {
            return ParseHandler.S;
        } else if (sentence.getType().equals(SegmentTypeAdapter.REFERENCE)) {
            return ParseHandler.R;
        } else if (sentence.getType().equals(SegmentTypeAdapter.MT)) {
            return ParseHandler.MT;
        } else if (sentence.getType().equals(SegmentTypeAdapter.HT)) {
            return ParseHandler.HT;
        } else {
            return ParseHandler.PE;
        }
    }
}
