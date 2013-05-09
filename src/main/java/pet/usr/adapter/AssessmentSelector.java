/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.usr.adapter;

import java.util.ArrayList;
import pet.annotation.Unit;
import java.util.List;
import org.apache.commons.collections.keyvalue.MultiKey;
import pet.annotation.AssessmentDescriptor;
import pet.config.ContextHandler;

/**
 *
 * @author waziz
 */
public class AssessmentSelector {

    public static List<MultiKey> getSummary(final Unit t){
        final EditableUnit task = (EditableUnit)t;
        final List<MultiKey> summary = new ArrayList<MultiKey>();
        if (task.getOriginalUnit() instanceof pet.annotation.adapter.TranslationUnit){
            summary.add(new MultiKey("Source", task.getSource().toString()));
            summary.add(new MultiKey("HT", task.getTarget().toString()));
        } else{
            summary.add(new MultiKey("MT", task.getOriginalTarget().toString()));
            summary.add(new MultiKey("Post-edited MT", task.getTarget().toString()));
        }
        return summary;
    }

    public static String getSentence1(final Unit t){
        final EditableUnit task = (EditableUnit)t;
        if (task.getOriginalUnit() instanceof pet.annotation.adapter.TranslationUnit){
            return task.getSource().toString();
        } else{
            return task.getOriginalTarget().toString();
        }
    }

    public static String getSentence2(final Unit task){
        return task.getTarget().toString();
    }


    public static String getS1Label(final Unit t){
        final EditableUnit task = (EditableUnit)t;
        if (task.getOriginalUnit() instanceof pet.annotation.adapter.TranslationUnit){
            return "Source";
        } else{
            return "MT";
        }
    }
    public static String getS2Label(final Unit t){
        final EditableUnit task = (EditableUnit)t;
        if (task.getOriginalUnit() instanceof pet.annotation.adapter.TranslationUnit){
            return "HT";
        } else{
            return "Post-edited MT";
        }
    }

    public static List<AssessmentDescriptor> getAssessmentDescriptors(final Unit t){
        final EditableUnit task = (EditableUnit)t;
        if (task.getOriginalUnit() instanceof pet.annotation.adapter.TranslationUnit){
            return ContextHandler.htAssessments();
        } else{
            return ContextHandler.peAssessments();
        }
    }
    

}
