/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.io;

import pet.annotation.xml.ParseHandler;
import pet.usr.adapter.EditableUnit;
import java.io.File;

import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import pet.annotation.Job;
import pet.annotation.Status;
import pet.annotation.xml.UnitParser;

/**
 *
 * @author Wilker
 */
public class XMLJobWriter {

    public void save(final Job job, final Status status, final int done, final List<EditableUnit> tasks, final File outFile) {
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document xml = docBuilder.newDocument();

            final Element xmlJob = xml.createElement(ParseHandler.JOB);
            xml.appendChild(xmlJob);
            xmlJob.setAttribute(ParseHandler.ID, job.getId());
            xmlJob.setAttribute(ParseHandler.STATUS, status.toString());
            xmlJob.setAttribute(ParseHandler.PROGRESS, done + "/" + tasks.size());

            for (final EditableUnit task : tasks) {
                final Element xmlTask = UnitParser.toXML(xml, task, task.getUnitResults());
                xmlJob.appendChild(xmlTask);
            }

            // Prepare the DOM document for writing
            final Source source = new DOMSource(xml);

            // Prepare the output file
            final Result result = new StreamResult(outFile);

            // Write the DOM document to the file
            final Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            xformer.transform(source, result);
        } catch (final Exception e) {
            System.out.println(e);
        }
    }
}
