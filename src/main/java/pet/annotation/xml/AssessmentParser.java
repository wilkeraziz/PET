/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pet.annotation.Assessment;
import pet.annotation.adapter.StringAssessment;

/**
 *
 * @author waziz
 */
public class AssessmentParser {

    public static Element toXML(final Document xml,
            final Assessment assessment) {
        final Element xmlAssessment = xml.createElement(ParseHandler.ASSESSMENT);
        xmlAssessment.setAttribute(ParseHandler.ID, assessment.getId());
        final Element xmlScore = xml.createElement(ParseHandler.SCORE);
        xmlScore.setTextContent(assessment.toString()); // TODO: use toStringList()
        xmlAssessment.appendChild(xmlScore);
        if (!assessment.getComment().isEmpty()) {
            final Element xmlComment = xml.createElement(ParseHandler.COMMENT);
            xmlComment.setTextContent(assessment.getComment());
            xmlAssessment.appendChild(xmlComment);
        }
        return xmlAssessment;
    }

    public static Assessment parse(final Element xml) {
        final String id = xml.getAttribute(ParseHandler.ID);
        String score = "";
        final NodeList xmlScores = xml.getElementsByTagName(ParseHandler.SCORE);
        if (xmlScores.getLength() == 1) {
            final Element xmlScore = (Element) xmlScores.item(0);
            score = xmlScore.getTextContent();
        }
        String comment = "";
        final NodeList xmlComments = xml.getElementsByTagName(ParseHandler.COMMENT);
        if (xmlComments.getLength() == 1) {
            final Element xmlComment = (Element) xmlComments.item(0);
            comment = xmlComment.getTextContent();
        }
        return new StringAssessment(id, score, comment);
    }
}
