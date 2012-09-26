/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import pet.annotation.InfoPrinter;
import pet.annotation.Unit;
import pet.config.ContextHandler;
import pet.db.ExternalInfoParams;
import pet.db.PETDataBase;
import pet.db.PETQueryResult;

/**
 * 
 *
 * @author waziz
 */
public class DefaultInfoPrinter implements InfoPrinter {

    private int sMaxOrder;
    private int sMinOrder;
    private int sMinLen;
    private int tMaxOrder;
    private int tMinOrder;
    private int tMinLen;
    private boolean sNoLonger;
    private boolean tNoLonger;
    private boolean init = false;

    private void lazyInit() {
        if (!init) {
            final ExternalInfoParams params = ContextHandler.externalInfoParams();
            sMaxOrder = params.sourceMaxOrder();
            sMinOrder = params.sourceMinOrder();
            sMinLen = params.sourceMinLength();
            tMaxOrder = params.targetMaxOrder();
            tMinOrder = params.targetMinOrder();
            tMinLen = params.targetMinLengt();
            sNoLonger = params.sourceNoLonger();
            tNoLonger = params.targetNoLonger();
            init = true;
        }
    }

    public String getGeneralInfo(final Unit task) {
        lazyInit();
        String info = "";
        final Map<String, String> general = ContextHandler.generalInfo();
        for (final Entry<String, String> pair : task.getAttributes().entrySet()) {
            final String color = general.get(pair.getKey());
            if (color != null) {
                String prefix = "";
                String suffix = "";
                if (!color.isEmpty()) {
                    prefix = "<font color=" + color + ">";
                    suffix = "</font>";
                }
                if (info.isEmpty()) {
                    info = prefix + "<b>" + pair.getKey() + "</b> = <i> " + pair.getValue() + " </i> " + suffix;
                } else {
                    info = info + "; " + prefix + "<b>" + pair.getKey() + "</b> = <i> " + pair.getValue() + " </i> " + suffix;
                }

            }
        }
        return "<html>" + info + "</html>";
    }

    public String getExternalTargetInfo(final Unit task) {
        lazyInit();
        String output = "";
        final String sentence = task.getTarget().toString();
        final List<String> ngrams = getNGrams(tMinOrder, tMaxOrder, sentence); //TODO ask ContextHandler
        final Set<String> seen = new HashSet<String>();
        for (final String gram : ngrams) {
            if (gram.length() >= tMinLen && !seen.contains(gram)) { //TODO ask the ContextHandler
                seen.add(gram);
                final int len = gram.length();
                final String header = "<b>" + gram + "</b> (" + len + ")<b>:</b> ";
                String list = "";
                for (final PETDataBase db : ContextHandler.externalTargetInfo()) {
                    final List<PETQueryResult> results = db.getData(gram);
                    if (results.isEmpty()) {
                        continue;
                    }
                    for (final PETQueryResult result : results) {
                        final int rlen = result.getValue().length();
                        if (!tNoLonger || rlen <= len) {
                            if (list.isEmpty()) {
                                list = "<i><font color=green>" + result.getValue() + "</font> (" + rlen + ")</i>";
                            } else {
                                list = list + "<b>;</b><i><font color=green>" + result.getValue() + "</font> (" + rlen + ")</i>";
                            }
                        }
                    }
                }
                final String bottom = "<br/>";
                if (!list.isEmpty()) {
                    if (output.isEmpty()) {
                        output = header + list + bottom;
                    } else {
                        output = output + header + list + bottom;
                    }
                }
            }
        }
        return "<html>" + output + "</html>";



    }

    public String getExternalSourceInfo(final Unit task) {
        lazyInit();
        String output = "";
        final String sentence = task.getSource().toString();
        final List<String> ngrams = getNGrams(sMinOrder, sMaxOrder, sentence); //TODO ask ContextHandler
        final Set<String> seen = new HashSet<String>();
        for (final String gram : ngrams) {
            if (gram.length() >= sMinLen && !seen.contains(gram)) { //TODO ask the ContextHandler
                seen.add(gram);
                final int len = gram.length();
                final String header = "<b>" + gram + "</b> (" + len + ")<b>:</b> ";
                String list = "";
                for (final PETDataBase db : ContextHandler.externalSourceInfo()) {
                    final List<PETQueryResult> results = db.getData(gram);
                    if (results.isEmpty()) {
                        continue;
                    }
                    for (final PETQueryResult result : results) {
                        final int rlen = result.getValue().length();
                        if (!sNoLonger || rlen <= len) {
                            if (list.isEmpty()) {
                                list = "<i><font color=green>" + result.getValue() + "</font> (" + rlen + ")</i>";
                            } else {
                                list = list + "<b>;</b><i><font color=green>" + result.getValue() + "</font> (" + rlen + ")</i>";
                            }
                        }
                    }
                }
                final String bottom = "<br/>";
                if (!list.isEmpty()) {
                    if (output.isEmpty()) {
                        output = header + list + bottom;
                    } else {
                        output = output + header + list + bottom;
                    }
                }
            }
        }
        return "<html>" + output + "</html>";
    }

    private List<String> getNGrams(final int min, final int max, String sentence) {
        sentence = sentence.toLowerCase(); //TODO ask the ContextHandler
        sentence = sentence.replaceAll("-", "- ");
        sentence = sentence.replaceAll("'", "' ");
        final String words[] = sentence.split("[.,!?:;\" ]");
        final List<String> list = new ArrayList<String>(words.length);
        list.addAll(Arrays.asList(words));
        for (int i = 0; i < words.length; i++) {
            String ngram = words[i];
            for (int j = 1; j < max && (i + j) < words.length; j++) { //TODO ask the ContextHandler
                ngram = ngram + " " + words[i + j];
                if (j >= min) {
                    list.add(ngram);
                }
            }
        }
        return list;
    }
}
