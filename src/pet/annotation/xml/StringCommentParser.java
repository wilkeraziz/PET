/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pet.annotation.Comment;
import pet.annotation.adapter.StringComment;

/**
 *
 * @author waziz
 */
public class StringCommentParser {

    public static Element toXML(final Document xml,
            final Comment comment) {
        final Element xmlComment = xml.createElement(ParseHandler.COMMENT);
        xmlComment.setTextContent(comment.toString());
        return xmlComment;
    }
    
    public static Comment parse(final Element xml) {
        final String value = xml.getTextContent();
        return new StringComment(value);
    }
}
