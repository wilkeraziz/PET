/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pet.annotation.xml.PETParseException;

/**
 *
 * @author waziz
 */
public class PETEditOperationEvent extends PETAbstractEvent {
    /**
     *  An event of this kind is fired when an "active/artificial" edit operation was performed.
     *  I mean, rather than identifying the type of operation by interpreting the "changes", this event marks those
     *  for which the user actively tagged the change as an operation (for instance by using shortcuts or the dropdown menu)
     */

    public static final String TYPE = "operation";

    public static interface EditOperation {

        public String getType();

        public void writeXML(final Document xml, final Element xmlEvent);
    };

    public static class Trim implements EditOperation {

        final int reduction;
        public static final String TYPE = "trim";

        public Trim(final int reduction) {
            this.reduction = reduction;
        }

        @Override
        public void writeXML(final Document xml, final Element xmlEvent) {
            final Element trim = xml.createElement("reduction");
            trim.setTextContent(Integer.toString(reduction));
            xmlEvent.appendChild(trim);
        }

        public static Trim getOperation(final Element xml) throws PETParseException {
            final NodeList list = xml.getElementsByTagName("reduction");
            if (list.getLength() == 1) {
                final Element xmlRed = (Element) list.item(0);
                final int r = Integer.parseInt(xmlRed.getTextContent());
                return new Trim(r);
            }
            throw new PETParseException("Parse Exception", "Missing reduction in trim");
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

    public static class Deletion implements EditOperation {

        final int offset;
        final String out;
        public static final String TYPE = "deletion";

        public Deletion(final int offset, final String out) {
            this.offset = offset;
            this.out = out;
        }

        @Override
        public void writeXML(final Document xml, final Element xmlEvent) {
            final Element xmlOut = xml.createElement("out");
            xmlOut.setAttribute("offset", Integer.toString(offset));
            xmlOut.setTextContent(out);
            xmlEvent.appendChild(xmlOut);
        }

        public static Deletion getOperation(final Element xml) throws PETParseException {
            final NodeList list = xml.getElementsByTagName("out");
            if (list.getLength() == 1) {
                final Element xmlOut = (Element) list.item(0);
                return new Deletion(Integer.parseInt(xmlOut.getAttribute("offset")), xmlOut.getTextContent());
            }
            throw new PETParseException("Parse Exception", "Missing reduction in trim");
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

    public static class Insertion implements EditOperation {

        final int offset;
        final String in;
        public static final String TYPE = "insertion";

        public Insertion(final int offset, final String in) {
            this.offset = offset;
            this.in = in;
        }

        @Override
        public void writeXML(final Document xml, final Element xmlEvent) {
            final Element xmlIn = xml.createElement("in");
            xmlIn.setAttribute("offset", Integer.toString(offset));
            xmlIn.setTextContent(in);
            xmlEvent.appendChild(xmlIn);
        }

        public static Insertion getOperation(final Element xml) throws PETParseException {
            final NodeList list = xml.getElementsByTagName("in");
            if (list.getLength() == 1) {
                final Element xmlIn = (Element) list.item(0);
                return new Insertion(Integer.parseInt(xmlIn.getAttribute("offset")), xmlIn.getTextContent());
            }
            throw new PETParseException("Parse Exception", "Missing reduction in trim");
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

    public static class Substitution implements EditOperation {

        final String in;
        final String out;
        final int offset;
        public static final String TYPE = "substitution";

        public Substitution(final int offset, final String out, final String in) {
            this.offset = offset;
            this.out = out;
            this.in = in;
        }

        @Override
        public void writeXML(final Document xml, final Element xmlEvent) {
            final Element xmlOut = xml.createElement("out");
            xmlOut.setAttribute("offset", Integer.toString(offset));
            xmlOut.setTextContent(out);
            final Element xmlIn = xml.createElement("in");
            xmlIn.setTextContent(in);
            xmlEvent.appendChild(xmlOut);
            xmlEvent.appendChild(xmlIn);
        }

        public static Substitution getOperation(final Element xml) throws PETParseException {
            final NodeList olist = xml.getElementsByTagName("out");
            final NodeList ilist = xml.getElementsByTagName("in");
            if (olist.getLength() == 1 && ilist.getLength() == 1) {
                final Element xmlOut = (Element) olist.item(0);
                final Element xmlIn = (Element) ilist.item(0);
                return new Substitution(Integer.parseInt(xmlOut.getAttribute("offset")),
                        xmlOut.getTextContent(),
                        xmlIn.getTextContent());
            }
            throw new PETParseException("Parse Exception", "Missing reduction in trim");
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }

    public static class Shift implements EditOperation {
        /**
         * The active Shift is always seen as deletion followed by an insertion elsewhere.
         */

        final String text;
        final int from;
        final int to;
        public static final String TYPE = "shift";

        public Shift(final String text, final int from, final int to) {
            this.from = from;
            this.to = to;
            this.text = text;
        }

        @Override
        public void writeXML(final Document xml, final Element xmlEvent) {
            final Element xmlText = xml.createElement("text");
            xmlText.setAttribute("from", Integer.toString(from));
            xmlText.setAttribute("to", Integer.toString(to));
            xmlText.setTextContent(text);
            xmlEvent.appendChild(xmlText);
        }

        public static Shift getOperation(final Element xml) throws PETParseException {
            final NodeList list = xml.getElementsByTagName("text");
            if (list.getLength() == 1) {
                final Element xmlText = (Element) list.item(0);
                return new Shift(xmlText.getTextContent(),
                        Integer.parseInt(xmlText.getAttribute("from")),
                        Integer.parseInt(xmlText.getAttribute("to")));
            }
            throw new PETParseException("Parse Exception", "Missing reduction in trim");
        }

        @Override
        public String getType() {
            return TYPE;
        }
    }
    private final EditOperation operation;

    public PETEditOperationEvent(final EditOperation operation) {
        super();
        this.operation = operation;
    }

    public PETEditOperationEvent(final Element xml) throws PETParseException {
        super(xml);
        final String type = xml.getAttribute("type");
        if (type.equals(Trim.TYPE)) {
            this.operation = Trim.getOperation(xml);
        } else if (type.equals(Insertion.TYPE)) {
            this.operation = Insertion.getOperation(xml);
        } else if (type.equals(Deletion.TYPE)) {
            this.operation = Deletion.getOperation(xml);
        } else if (type.equals(Substitution.TYPE)) {
            this.operation = Substitution.getOperation(xml);
        } else if (type.equals(Shift.TYPE)) {
            this.operation = Shift.getOperation(xml);
        } else {
            throw new PETParseException("Parse Exception", "Unknown PETEditOperationEvent.EditOperation type: " + type);
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public EditOperation getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return operation.toString();
    }

    @Override
    public void writeXML(final Document xml, final Element xmlEvent, final long t0) {
        super.writeXML(xml, xmlEvent, t0);
        xmlEvent.setAttribute("type", operation.getType());
        operation.writeXML(xml, xmlEvent);
    }
}
