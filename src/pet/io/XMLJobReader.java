/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.io;

import pet.annotation.xml.ParseHandler;
import java.io.File;
import java.io.IOException;


import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import pet.annotation.Job;
import pet.annotation.Unit;
import pet.annotation.xml.JobParser;
import pet.annotation.xml.PETParseException;

/**
 *
 * @author Wilker
 */
public class XMLJobReader {

  
    public Job readJob(final File jobFile) throws PETParseException{

        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document xml = docBuilder.parse(jobFile);
            xml.getDocumentElement().normalize();
            final NodeList jobList = xml.getElementsByTagName(ParseHandler.JOB);
            if (jobList.getLength() != 1) {
                //JOptionPane.showMessageDialog(null, "Malformed XML: multiple jobs", "Parse error", JOptionPane.ERROR_MESSAGE);
                throw new PETParseException("Malformed PEJ", "Multiple jobs within a single PEJ.");
            }
            return JobParser.parse((Element) jobList.item(0));
        } catch (final ParserConfigurationException e) {
            throw new PETParseException("Parse Exception", e.toString());
        } catch (final SAXException e){
            throw new PETParseException("Parse Exception", e.toString());
        } catch (final IOException e){
            throw new PETParseException("Parse Exception", e.toString());
        }
    }

    public Job readEditedJob(final File jobFile, final List<Unit> editable) throws PETParseException {
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document xml = docBuilder.parse(jobFile);
            xml.getDocumentElement().normalize();
            final NodeList jobList = xml.getElementsByTagName(ParseHandler.JOB);
            if (jobList.getLength() != 1) {
                //JOptionPane.showMessageDialog(null, "Malformed XML: multiple jobs", "Parse error", JOptionPane.ERROR_MESSAGE);
                throw new PETParseException("Malformed PEJ", "Multiple jobs within a single PEJ.");
            }
            return JobParser.parse((Element) jobList.item(0), editable);
        } catch (final ParserConfigurationException e) {
            throw new PETParseException("Parse Exception", e.toString());
        } catch (final SAXException e){
            throw new PETParseException("Parse Exception", e.toString());
        } catch (final IOException e){
            throw new PETParseException("Parse Exception", e.toString());
        }
    }
}
