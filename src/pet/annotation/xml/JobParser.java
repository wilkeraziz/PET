/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.xml;

import pet.usr.adapter.EditableUnit;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pet.annotation.Job;
import pet.annotation.Status;
import pet.annotation.Unit;
import pet.annotation.UnitResult;
import pet.annotation.adapter.JobAdapter;

/**
 *
 * @author waziz
 */
public class JobParser {

    // TODO: find a better way of doing this
    private static final boolean backCompatibilityOfUnits = true;
    
    private static NodeList getUnits(final Element xml){
        NodeList list = xml.getElementsByTagName(ParseHandler.UNIT);
        if (backCompatibilityOfUnits && (list == null || list.getLength() == 0)){ // back compatibility with older versions
            list = xml.getElementsByTagName(ParseHandler.TASK);
        }
        return list;
    }
    
    public static Job parse(final Element xml) throws PETParseException {
        final String jobid = xml.getAttribute(ParseHandler.ID);
        //final String progress = xml.getAttribute(ParseHandler.PROGRESS);
        final Status status = StatusParser.parse(xml);
        final NodeList unitList = getUnits(xml);
        final List<Unit> units = new ArrayList<Unit>();
        for (int i = 0; i < unitList.getLength(); i++) {
            final Element xmlUnit = (Element) unitList.item(i);
            units.add(UnitParser.parseTask(xmlUnit));
        }
        return new JobAdapter(jobid, status, units);

    }

    public static Job parse(final Element xml, final List<Unit> editable) throws PETParseException {
        editable.clear();
        final String jobid = xml.getAttribute(ParseHandler.ID);
        final Status status = StatusParser.parse(xml);
        final NodeList unitList = getUnits(xml);
        final List<Unit> units = new ArrayList<Unit>();
        for (int i = 0; i < unitList.getLength(); i++) {
            final Element xmlUnit = (Element) unitList.item(i);
            final Unit unit = UnitParser.parseTask(xmlUnit);
            final NodeList annotations = xmlUnit.getElementsByTagName(ParseHandler.ANNOTATIONS);
            if (annotations.getLength() == 0) {
                editable.add(new EditableUnit(unit));
            } else {
                final List<UnitResult> results = UnitParser.parseTaskResults(unit, (Element) annotations.item(0));
                editable.add(new EditableUnit(unit, results));
            }
        }
        return new JobAdapter(jobid, status, units);
    }
}
