/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.xml;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pet.annotation.Assessment;
import pet.annotation.EffortIndicator;
import pet.annotation.Segment;
import pet.annotation.Unit;
import pet.annotation.UnitResult;
import pet.annotation.adapter.UnitResultAdapter;
import pet.annotation.adapter.TranslationUnit;
import pet.signal.PETEvent;

/**
 *
 * @author waziz
 */
public class UnitResultParser {

    public static Element toXML(final Document xml, final UnitResult result) {
        final Element xmlResult = xml.createElement(ParseHandler.ANNOTATION);
        xmlResult.setAttribute(ParseHandler.REVISION, Integer.toString(result.getRevision()));
        xmlResult.appendChild(StringSentenceParser.toXML(xml, result.getTranslation()));
        for (final Assessment assessment : result.getAssessments()) {
            xmlResult.appendChild(AssessmentParser.toXML(xml, assessment));
        }
        for (final EffortIndicator indicator : result.getEfforIndicators()) {
            xmlResult.appendChild(EffortIndicatorParser.toXML(xml, indicator));
        }
        final Element xmlEventGroup = xml.createElement(ParseHandler.EVENTS);
        final long t0 = (result.getEvents().size() > 0) ? result.getEvents().get(0).when() : 0;
        for (final PETEvent event : result.getEvents()) {
            xmlEventGroup.appendChild(EventParser.toXML(xml, event, t0));
        }
        xmlResult.appendChild(xmlEventGroup);
        return xmlResult;
    }

    public static UnitResult parseTaskResult(final Unit task, final Element xml) {
        Segment sentence = null;
        if (task instanceof TranslationUnit) {
            final NodeList editions = xml.getElementsByTagName(ParseHandler.HT);
            if (editions.getLength() == 1) {
                final Element xmlEdition = (Element) editions.item(0);
                sentence = StringSentenceParser.parseHT(xmlEdition);
            }
        } else {
            final NodeList editions = xml.getElementsByTagName(ParseHandler.PE);
            if (editions.getLength() == 1) {
                final Element xmlEdition = (Element) editions.item(0);
                sentence = StringSentenceParser.parsePE(xmlEdition);
            }
        }
        if (sentence != null) {
            final int revision = Integer.parseInt(xml.getAttribute(ParseHandler.REVISION));
            final NodeList xmlIndicators = xml.getElementsByTagName(ParseHandler.INDICATOR);
            final List<EffortIndicator> indicators = new ArrayList<EffortIndicator>(xmlIndicators.getLength());
            for (int i = 0; i < xmlIndicators.getLength(); i++) {
                final Element xmlIndicator = (Element) xmlIndicators.item(i);
                indicators.add(EffortIndicatorParser.parse(xmlIndicator, false));
            }

            final NodeList xmlAssessments = xml.getElementsByTagName(ParseHandler.ASSESSMENT);
            final List<Assessment> assessments = new ArrayList<Assessment>(xmlAssessments.getLength());
            for (int i = 0; i < xmlAssessments.getLength(); i++) {
                final Element xmlAssessment = (Element) xmlAssessments.item(i);
                assessments.add(AssessmentParser.parse(xmlAssessment));
            }


            // Parse the events
            final NodeList xmlEventGroups = xml.getElementsByTagName(ParseHandler.EVENTS);
            final List<PETEvent> events = new ArrayList<PETEvent>();
            if (xmlEventGroups.getLength() == 1) {
                final Element xmlEventGroup = (Element) xmlEventGroups.item(0);
                final NodeList xmlEvents = xmlEventGroup.getChildNodes();
                for (int j = 0; j < xmlEvents.getLength(); j++) {
                    if (xmlEvents.item(j) instanceof Element){
                        final Element xmlEvent = (Element) xmlEvents.item(j);
                        events.add(EventParser.parse(xmlEvent));
                    }
                }
            }

            return new UnitResultAdapter(revision, sentence, indicators, assessments, events);
        }
        return null;
    }
}
