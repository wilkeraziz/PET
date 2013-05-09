/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.xml;

import pet.usr.adapter.EditableUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pet.annotation.Segment;
import pet.annotation.Status;
import pet.annotation.Unit;
import pet.annotation.UnitResult;
import pet.annotation.adapter.PostEditingUnit;
import pet.annotation.adapter.TranslationUnit;

/**
 *
 * @author waziz
 */
public class UnitParser {

    public static Element toXML(final Document xml,
            final EditableUnit unit, final List<UnitResult> results) {
        final Element xmlUnit = xml.createElement(ParseHandler.UNIT);
        xmlUnit.setAttribute(ParseHandler.ID, unit.getId());
        xmlUnit.setAttribute(ParseHandler.STATUS, unit.getStatus().toString());
        for (final Entry<String, String> pair : unit.getAttributes().entrySet()) {
            xmlUnit.setAttribute(pair.getKey(), pair.getValue());
        }
        for (final Segment source : unit.getSources()) {
            xmlUnit.appendChild(StringSentenceParser.toXML(xml, source));
        }
        for (final Segment reference : unit.getReferences()) {
            xmlUnit.appendChild(StringSentenceParser.toXML(xml, reference));
        }

        if (unit.getOriginalUnit() instanceof PostEditingUnit) {
            xmlUnit.setAttribute(ParseHandler.TYPE, ParseHandler.PE_TYPE);
            for (final Segment target : unit.getOriginalUnit().getTargets()) {
                xmlUnit.appendChild(StringSentenceParser.toXML(xml, target));
            }
        } else {
            xmlUnit.setAttribute(ParseHandler.TYPE, ParseHandler.HT_TYPE);
        }
        if (!results.isEmpty()) {
            final Element xmlResults = xml.createElement(ParseHandler.ANNOTATIONS);
            xmlUnit.appendChild(xmlResults);
            xmlResults.setAttribute(ParseHandler.REVISIONS, Integer.toString(results.size()));
            for (final UnitResult result : results) {
                xmlResults.appendChild(UnitResultParser.toXML(xml, result));
            }
        }
        return xmlUnit;
    }

    public static Unit parseTask(final Element xml) throws PETParseException {
        final String id = xml.getAttribute(ParseHandler.ID);
        final String type = xml.getAttribute(ParseHandler.TYPE);
        final Status status = StatusParser.parse(xml);

        final NodeList sourceList = xml.getElementsByTagName(ParseHandler.S);
        final List<Segment> sources = new ArrayList<Segment>(sourceList.getLength());
        for (int i = 0; i < sourceList.getLength(); i++) {
            final Element xmlS = (Element) sourceList.item(i);
            sources.add(StringSentenceParser.parseSource(xmlS));
        }

        final Map<String, String> attributes = new HashMap<String, String>();
        for (int i = 0; i < xml.getAttributes().getLength(); i++) {
            final Node node = xml.getAttributes().item(i);
            if (!node.getNodeName().equals(ParseHandler.ID) && !node.getNodeName().equals(ParseHandler.TYPE) && !node.getNodeName().equals(ParseHandler.STATUS)) {
                attributes.put(node.getNodeName(), node.getNodeValue());
            }
        }

        final NodeList referenceList = xml.getElementsByTagName(ParseHandler.R);
        final List<Segment> references = new ArrayList<Segment>(referenceList.getLength());
        for (int i = 0; i < referenceList.getLength(); i++) {
            final Element xmlR = (Element) referenceList.item(i);
            references.add(StringSentenceParser.parseReference(xmlR));
        }

        if (type.equals(ParseHandler.PE_TYPE)) {
            final NodeList xmlMTs = xml.getElementsByTagName(ParseHandler.MT);
            final List<Segment> mts = new ArrayList<Segment>();
            for (int i = 0; i < xmlMTs.getLength(); i++) {
                mts.add(StringSentenceParser.parseMT((Element) xmlMTs.item(i)));
            }
            if (mts.isEmpty()) {
                throw new PETParseException("Invalid PEJ", "Task " + id + " is declared \"pe\" however it does not contain MTs.");
            }
            return new PostEditingUnit(id, sources, references, mts, status, attributes);
        } else {
            return new TranslationUnit(id, sources, references, status, attributes);
        }
    }

    public static List<UnitResult> parseTaskResults(final Unit task, final Element xml) {
        final int revisions = Integer.parseInt(xml.getAttribute(ParseHandler.REVISIONS));
        final NodeList annotationList = xml.getElementsByTagName(ParseHandler.ANNOTATION);
        final List<UnitResult> results = new ArrayList<UnitResult>(revisions);
        for (int i = 0; i < annotationList.getLength(); i++) {
            final Element xmlAnnotation = (Element) annotationList.item(i);
            final UnitResult result = UnitResultParser.parseTaskResult(task, xmlAnnotation);
            if (result != null) {
                results.add(result);
            }
        }
        return results;
    }
}
