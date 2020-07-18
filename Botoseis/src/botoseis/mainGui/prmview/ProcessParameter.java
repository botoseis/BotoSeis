package botoseis.mainGui.prmview;

/*
 * ProcessParameter.java
 *
 * Created on December 27, 2007, 12:07 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

public class ProcessParameter {
    
    /** Creates a new instance of ProcessParameter */
    public ProcessParameter() {        
    }
    
    @Override
    public String toString(){
        return name;
    }

    public String name;    
    public String type;
    public String optionValues;
    public String keyvaluePair;
    public String optionsListSelectionType;
    public String brief;
    public String value;
    public String defaultValue;
    
    // Constants
    public static final String TEXT = "Text";
    public static final String NUMERIC = "Numeric";
    public static final String OPTIONS = "Options";
    public static final String FILE_PATH = "File path";
    public static final String MULTLINE_TEXT = "Multline text";
    public static final String OPTIONS_SELECTION_INDEX = "OptionsSelectionIndex";
    public static final String OPTIONS_SELECTION_TEXT = "OptionsSelectionText";
    public static final String KEYVALUEPAIR = "true";
    public static final String VALUE_ONLY = "false";
    
}
