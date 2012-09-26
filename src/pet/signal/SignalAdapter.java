/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

/**
 *
 * @author waziz
 */
public enum SignalAdapter implements Signal {
    
    DONE,
    ASSESSING_START,
    ASSESSING_END,
    EDITING_START,
    EDITING_END,
    KEYSTROKE,
    KEYSTYPED_WHITE,
    KEYSTYPED_NON_WHITE,
    KEYSTYPED_ISO,
    KEYSTYPED_PUNCT,
    KEYSTYPED_DIGIT,
    KEYSTYPED_ARROW, // navigation with arrows
    KEYSTYPED_JUMP, //navigation with HOME, END, PAGE_UP and PAGE_DOWN
    KEYSTYPED_COPY,
    KEYSTYPED_CUT,
    KEYSTYPED_PASTE,
    KEYSTYPED_DELETE,
    KEYSTYPED_BACKSPACE,
    KEYSTYPED_LETTER,
    KEYSTYPED_SYMBOL,
    IMPOSSIBLE,
    ACCEPT,
    TEXT_INSERTION,
    TEXT_DELETION,
    REPLACEMENT,
    TEXT_ASSIGNMENT,
    NO_WORD_SELECTED,
    PHRASE_SELECTED,
    AUTO_ACCEPT,
    UNNECESSARY,
    DICTIONARY_LOOKPUP
}
