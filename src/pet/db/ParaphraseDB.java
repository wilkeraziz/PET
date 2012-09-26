/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.db;

import java.io.FileNotFoundException;
import java.util.*;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pet.annotation.xml.PETParseException;

/**
 *
 * @author waziz
 */
public class ParaphraseDB implements PETDataBase {

    private final Map<String, List<PETQueryResult>> data;
    private final String alias;

    private ParaphraseDB(final String alias) {
        this.data = new HashMap<String, List<PETQueryResult>>();
        this.alias = alias;
    }

    private Map<String, List<PETQueryResult>> getData() {
        return data;
    }

    public List<PETQueryResult> getData(final String query) {
        if (query != null) {
            final List<PETQueryResult> results = data.get(query.toLowerCase().trim());
            if (results != null) {
                return Collections.unmodifiableList(results);
            }
        }
        return Collections.emptyList();

    }

    public void getData(String query, Set<PETQueryResult> results) {
        if (query != null) {
            final List<PETQueryResult> r = data.get(query.toLowerCase().trim());
            if (r != null) {
                results.addAll(r);
            }
        }
    }

    public static ParaphraseDB parse(final String file) throws PETParseException, FileNotFoundException {
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document xml = docBuilder.parse(file);
            xml.getDocumentElement().normalize();
            final NodeList dbs = xml.getElementsByTagName("db");
            if (dbs.getLength() != 1) {
                JOptionPane.showMessageDialog(null, "Malformed XML: multiple databases", "Parse error", JOptionPane.ERROR_MESSAGE);
                throw new PETParseException("Malformed XML", "Multiple databases within a single PETDB.");
            }
            return parse((Element) dbs.item(0));
        } catch (final Exception e) {
            throw new FileNotFoundException(file);
        }
    }

    private static ParaphraseDB parse(final Element xml) {
        final String alias = xml.getAttribute("alias");
        final ParaphraseDB db = new ParaphraseDB(alias);
        final Map<String, List<PETQueryResult>> data = db.getData();
        final NodeList entries = xml.getElementsByTagName("entry");
        for (int i = 0; i < entries.getLength(); i++) {
            final Element entry = (Element) entries.item(i);
            final NodeList phrases = entry.getElementsByTagName("phrase");
            for (int j = 0; j < phrases.getLength(); j++) {
                final Element phrase = (Element) phrases.item(j);
                final NodeList paraphrases = entry.getElementsByTagName("paraphrase");

                List<PETQueryResult> results = data.get(phrase.getTextContent());
                if (results == null) {
                    results = new ArrayList<PETQueryResult>(paraphrases.getLength());
                    data.put(phrase.getTextContent().toLowerCase(), results); //TODO ask ContextHandler
                }

                for (int k = 0; k < paraphrases.getLength(); k++) {
                    final Element paraphrase = (Element) paraphrases.item(k);
                    final String score = paraphrase.getAttribute("score");
                    final String text = paraphrase.getTextContent();
                    final PETQueryResult result = new DefaultQueryResult("paraphrase", text, "score", score);
                    results.add(result);
                }

            }

        }
        return db;
    }

    public String getAlias() {
        return alias;
    }
}
