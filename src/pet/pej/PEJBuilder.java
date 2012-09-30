/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.pej;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pet.annotation.xml.ParseHandler;
import pet.pej.PEJSegment.SegmentType;

/**
 * This builder should be used for generating a single PEJ.
 * That is, you should instantiate a different builder to build a different PEJ.
 * 
 * You can add customized units using addUnit or you can load data from plain text files
 * following the convetion "one segment per line".
 * 
 * @author waziz
 */
public class PEJBuilder {

    final String output;
    final List<PEJAttribute> unitsAttributes;
    final Map<Integer, Element> units;
    final Document xml;
    final Element job;
    boolean idUnitsSequentially;

    /**
     * This constructor creates an empty job.
     * The only two mandatory fields are
     * @param output the path to the output file (if it doesn't end in '.pej', the suffix will be added)
     * @param id the id of the job, which will be the name of the '.per' file that comes from this pej (i.e. "$id.per")
     * @throws ParserConfigurationException 
     */
    public PEJBuilder(final String output, final String id) throws ParserConfigurationException {
        if (output.endsWith(".pej")) {
            this.output = output;
        } else {
            this.output = output + ".pej";
        }
        this.unitsAttributes = new ArrayList<PEJAttribute>();
        this.units = new HashMap<Integer, Element>();
        this.idUnitsSequentially = false;
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        xml = docBuilder.newDocument();
        job = xml.createElement(ParseHandler.JOB);
        job.setAttribute("id", id);
        xml.appendChild(job);
    }

    /**
     * Returns how many units were written to the PEJ
     * @return 
     */
    public int getNUnits() {
        return units.size();
    }

    /**
     * Returns the path to the PEJ
     */
    public String getPathToPEJ() {
        return output;
    }

    /**
     * Adds one attribute to the job. Here it's possible to overwrite the id.
     * @param attr
     * @return 
     */
    public PEJBuilder addJobAttribute(final PEJAttribute attr) {
        job.setAttribute(attr.getKey(), attr.getValue());
        return this;
    }

    /**
     * Adds a list of attributes to the job. This method simply calls addJobAttribute to every attribute.
     * @param attrs
     * @return 
     */
    public PEJBuilder addJobAttributes(final List<PEJAttribute> attrs) {
        for (final PEJAttribute attr : attrs) {
            addJobAttribute(attr);
        }
        return this;
    }
    
    /**
     * This methods allows one to create their own units (as they like), and add
     * them to the job.
     * @param unit 
     * @return sequential position (starting at 1)
     */
    public int addUnit(final PEJUnit unit) {
        int position = units.size() + 1;
        units.put(position, unit.getXml(xml));
        return position;
    }

    /**
     * Overwrites any pre-defined id (if any) for the units by sequential integers.
     * It does not matter when this method is called in relation to loadUnitsAttributesFromFile,
     * this will be always applied in the end, so that any other id is overwritten.
     * @return 
     */
    public PEJBuilder idUnitsSequentially() {
        this.idUnitsSequentially = true;
        return this;
    }

    /**
     * Loads attributes for units from a file, can be called multiple times.
     * Each file should contain one line per unit though.
     * Each line can contain as many attributes as desired.
     * @param file containing attributes
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws FormatException 
     */
    public PEJBuilder loadUnitsAttributesFromFile(final String file) throws FileNotFoundException, IOException, FormatException {

        final BufferedReader text = new BufferedReader(new FileReader(file));
        String content = null;
        int n = 1;
        while ((content = text.readLine()) != null) {
            if (!content.trim().isEmpty()) {
                Element unit = units.get(n);

                if (unit == null) {
                    unit = xml.createElement(ParseHandler.UNIT);
                    units.put(n, unit);
                }
                try {
                    for (final PEJAttribute attr : PEJAttribute.parse(content)) {
                        unit.setAttribute(attr.getKey(), attr.getValue());
                    }
                } catch (final FormatException ex) {
                    throw new FormatException("In [" + file + "] attributes: an attribute must be formatted as <key>=<value>");
                }
                job.appendChild(unit);
            }
            n++;
        }
        return this;
    }

    /**
     * Gives one the chance to check whether the current units miss ids.
     * @return the number of units missing ids
     */
    public int isMissingIds() {
        int missing = 0;
        for (int i = 1; i <= units.size(); i++) {
            final Element unit = units.get(i);
            if (!unit.hasAttribute("id")) {
                missing++;
            }
        }
        return missing;
    }

    /**
     * Add segments of a given type. All the segments here are produced by a single producer.
     * @param type either S (source), R (reference) or T (target).
     * @param file file containing the segments. One segment per line.
     * @param producer a producer (mandatory attribute of the segments)
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public PEJBuilder addSegmentsFromOneProducer(final SegmentType type, final File file, final String producer) throws FileNotFoundException, IOException {
        final BufferedReader text = new BufferedReader(new FileReader(file));
        String tContent = null;
        int n = 1;
        while ((tContent = text.readLine()) != null) {
            if (!tContent.isEmpty()) {
                Element unit = units.get(n);
                if (unit == null) {
                    unit = xml.createElement(ParseHandler.UNIT);
                    units.put(n, unit);
                }
                final Element sentence = xml.createElement(type.toString());
                sentence.setAttribute("producer", producer);
                sentence.setTextContent(tContent);
                unit.appendChild(sentence);
            }
            n++;
        }
        return this;
    }

    /**
     * Add segments of a given type along with their attributes (amongst which the producers should be present)
     * @param type type of segments
     * @param file file containing segments, one per line
     * @param attrs file containing a list of attributes per line
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws FormatException 
     */
    public PEJBuilder addSegmentsWithAttributes(final SegmentType type, final File file, final File attrs) throws FileNotFoundException, IOException, FormatException {

        final BufferedReader text = new BufferedReader(new FileReader(file));
        final BufferedReader meta = new BufferedReader(new FileReader(attrs));
        String tContent = null;
        String mContent = null;
        int n = 1;
        while ((tContent = text.readLine()) != null && (mContent = meta.readLine()) != null) {
            if (!tContent.isEmpty() || !mContent.isEmpty()) {
                Element unit = units.get(n);
                if (unit == null) {
                    unit = xml.createElement(ParseHandler.UNIT);
                    units.put(n, unit);
                }
                final Element sentence = xml.createElement(type.toString());
                sentence.setTextContent(tContent);
                String[] pairs = mContent.split("\\s+");
                boolean hasProducer = false;
                for (int p = 0; p < pairs.length; p++) {
                    String pair = pairs[p];
                    String[] parts = pair.split("=");
                    if (parts.length == 2) {
                        sentence.setAttribute(parts[0], parts[1]);
                        if (parts[0].equals("producer")) {
                            hasProducer = true;
                        }
                    } else {
                        throw new FormatException("In [" + meta + "]: an attribute must be formatted as <key>=<value>");
                    }
                }
                if (!hasProducer) {
                    throw new FormatException("In [" + meta + "]: all units must have a producer.");
                }
                unit.appendChild(sentence);
            }
            n++;
        }
        return this;
    }

    /**
     * Builds the (xml) job with all its units and their segments.
     * Writes the job to the disk and returns the file.
     * @return
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws FormatException 
     */
    public File build() throws TransformerConfigurationException, TransformerException, FormatException {
        final File pej = new File(this.output);

        for (int i = 1; i <= units.size(); i++) {
            final Element unit = units.get(i);
            if (idUnitsSequentially) {
                unit.setAttribute("id", Integer.toString(i));
            }
            if (!unit.hasAttribute("id")) {
                throw new FormatException("All units must have an id. Unit #" + i + " doesn't have one.");
            }
            final String type = (unit.getElementsByTagName("MT").getLength() == 0) ? ParseHandler.HT_TYPE : ParseHandler.PE_TYPE;
            unit.setAttribute(ParseHandler.TYPE, type);

            job.appendChild(unit);
        }

        // Prepare the DOM document for writing
        final Source source = new DOMSource(xml);

        // Prepare the output file
        final javax.xml.transform.Result result = new StreamResult(pej);

        // Write the DOM document to the file
        final Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        xformer.transform(source, result);
        return pej;
    }
}
