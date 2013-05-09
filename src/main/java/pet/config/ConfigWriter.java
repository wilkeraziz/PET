/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.config;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author waziz
 */
public class ConfigWriter {

    public static void example(final File config) throws IOException{
        config.createNewFile();
        final PrintWriter write = new PrintWriter(config);
        write.println("# PET example config file");
        write.println("");
        write.println("# please report bugs to wilker.aziz@gmail.com");
        write.println("");
        write.println("# The workspace is the directory where everything happens");
        write.println("workspace=jobs");
        write.println("");
        write.println("# Source language");
        write.println("source=language");
        write.println("");
        write.println("# Target language");
        write.println("target=language");
        write.println("");
        write.println("# You may want to automatically save progress");
        write.println("autoSave");
        write.println("");
        write.println("# You may want to change the annotation page");
        write.println("# how many sentences get displayed");
        write.println("#sentencesByPage=11");
        write.println("");
        write.println("# hide the bottom pane to get more space");
        write.println("#hideBottomPane");
        write.println("");
        write.println("# show target sentences on the left-hand side and source sentences on the right-hand side");
        write.println("#displayTS");
        write.println("");
        write.println("# enable blind post-editing, that is, show only the machine translations");
        write.println("#blindPE");
        write.println("");
        write.println("# If you have multiple MTs, you may want to show a preview of the alternatives");
        write.println("#showMTPreview");
        write.println("");
        write.println("# You can set assessment options for translation and post-editing tasks");
        write.println("# T) Translation");
        write.println("#translationAssessment=id|question|maxAnswers[|assessments]{2,}");
        write.println("translationAssessment=difficulty|How hard was to translate the source?|1|1. Easy|2. Regular|3. Difficult");
        write.println("translationAssessment=issues|Highlight some issues of this unit|*|1. Rare words|2. Passive voice|3. Semantic ambiguity");
        write.println("");
        write.println("# PE) Post-Editing");
        write.println("#postEditingAssessment=id|question|maxAnswers[|assessments]{2,}");
        write.println("postEditingAssessment=necessity|How much post-editing was necessary to fix the translation?|1|4. No modification was performed|3. A little post editing wasneeded to fix small problems|2. A lot of post editing was needed (but it was still quicker than retranslation)|1. It required complete retranslation");
        write.println("# You may set more than one question: they will be shown in order");
        write.println("postEditingAssessment=accuracy|How accurate was the MT?|1|0|1|2|3|4");
        write.println("# You may present multiple questions per page");
        write.println("assessmentsByPage=2");
        write.println("");
        write.println("# You can disable assessments switching on the following flag:");
        write.println("disableAssessment");
        write.println("# Assessments come with optional comments, you may disable them");
        write.println("#disableCommentOnAssessment");
        write.println("");
        write.println("# You can set how the current job is rendered:");
        write.println("# i) you can hide the current job while the user is not working on it");
        write.println("hideIfNotEditing=always");
        write.println("# ii) you can hide the current job if never done");
        write.println("#hideIfNotEditing=undone");
        write.println("# iii) you may want to never hide");
        write.println("#hideIfNotEditing=never");
        write.println("");
        write.println("# You can extend the scope of this option to all units on the screen");
        write.println("#applyHideIfNotEditingToAll");
        write.println("");
        write.println("# While the job is hidden you can display a message");
        write.println("# i) never done jobs");
        write.println("editableMessageUndone=Click here to start ...");
        write.println("# ii) at least once finished jobs");
        write.println("editableMessageDone=Click here to redo ...");
        write.println("");
        write.println("# You may render HTML");
        write.println("#renderHTML");
        write.println("");
        write.println("# You may want to changes fonts");
        write.println("standardFont=times,12");
        write.println("editingFont=times,12");
        write.println("editableFont=times,12");
        write.println("");
        write.println("# You may want to display a reference translation");
        write.println("#showReference");
        write.println("");
        write.println("# You may want to display the id of the tasks");
        write.println("#showSentenceId");
        write.println("#idFont=times,8");
        write.println("");
        write.println("# You may want to display the producer of each unit");
        write.println("#showProducers");
        write.println("");
        write.println("# You may want to enable the user to accept the MT");
        write.println("#enableAutoAccept");
        write.println("");
        write.println("# You may want to enable the user to discard the segment (tag it as impossible to be post-edited)");
        write.println("#enableDiscard");
        write.println("#back compatible with enableImpossible");
        write.println("");
        write.println("# Usually you will want to skip the assessment of two special cases: auto-accept and discard");
        write.println("skipAssessmentOnAutoAccept");
        write.println("skipAssessmentOnDiscard");
        write.println("# if you don't, just comment these two lines above out");
        write.println("");

        write.println("# For very specific uses of the tool you may want to prevent the user from editing =O");
        write.println("#blockEditing");
        write.println("");

        write.println("# You may want to disable the ability of outputing a time stamp in the result file");
        write.println("hideOutputTimeStampCheckBox");
        write.println("");
        write.println("# You may want to output the timestamp");
        write.println("#outputTimeStamp");
        write.println("");

        write.println("# You may want to collect some automatic feedback");
        write.println("enableKeystrokes");
        
        write.println("enableUnchanged");
       // write.println("#enableUnnecessary"); //unsupported
        write.println("");
        write.println("# You may want to give the user additional information");
        write.println("# additional info is given via the task's attributes or the special tag <info>");
        write.println("#generalInfo=attribute[,color]");
        write.println("#generalInfoFont=font[,size]");
        write.println("#externalTargetInfo=file");
        write.println("# You may want to specify some parameters");
        write.println("#externalSourceInfoMinOrder=2");
        write.println("#externalSourceInfoMaxOrder=4");
        write.println("#externalSourceInfoMinLength=5");
        write.println("#externalTargetInfoMinOrder=1");
        write.println("#externalTargetInfoMaxOrder=3");
        write.println("#externalTargetInfoMinLength=4");
        write.println("#externalTargetInfoNoLonger");
        write.println("#externalSourceInfoNoLonger");
        write.println("");
        
        write.println("# You may want to load monolingual and bilingual dictionaries");
        write.println("#s2s=file");
        write.println("#s2t=file");
        write.println("#t2t=file");
        write.println("#t2s=file");

        write.println("# You may want to activate length constraints");
        write.println("# by giving the name of 3 attributes that should be interpreted as the following");
        write.println("#lengthConstraints=ideal,preferable,max");
        write.println("");
        write.println("# You may want to log all the changes");
        write.println("trackChanges");
        write.println("# If you can think of a useful parameter please let me know: wilker.aziz@gmail.com");
        write.close();
    }
}
