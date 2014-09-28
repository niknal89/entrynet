/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.searchfield;

import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author teopetuk89
 */
public class SearchField implements CaretListener
    {

    private int textLength = 0;
    protected int selectedIndex;
    protected JTextField fieldSearch;
    private Searcher searcher;

    public interface Searcher {

        public void search(String text);
        public void clearSearch();

    }

    public SearchField(JTextField fieldSearch, Searcher searcher) {
        this.fieldSearch = fieldSearch;
        fieldSearch.addCaretListener(this);
        this.searcher = searcher;
    }

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    @Override
    public void caretUpdate(CaretEvent arg0) {
        if (fieldSearch.getText().length() != textLength) {
            int difference = fieldSearch.getText().length() - textLength;
            selectedIndex = -1;
            textLength = fieldSearch.getText().length();
            if (textLength > 2) { //  && (difference == 1 || difference == -1) the difference check is neccessary to block events that generate when text is pasted into the field
                searcher.search(fieldSearch.getText());
            } else {
                searcher.clearSearch();
            }
        }
    }
    
}
