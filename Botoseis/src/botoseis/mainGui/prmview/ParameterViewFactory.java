/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botoseis.mainGui.prmview;

public class ParameterViewFactory {

    public ParameterViewFactory() {
    }

    public ParameterView createParameter(ProcessParameter prm) {
        ParameterView newP = null;

        if (prm.type.equalsIgnoreCase(ProcessParameter.FILE_PATH)) {
            newP = new FilePathParameterView(prm);
        } else if (prm.type.equalsIgnoreCase(ProcessParameter.MULTLINE_TEXT)) {
        } else if (prm.type.equalsIgnoreCase(ProcessParameter.NUMERIC)) {
            newP = new NumericParameterView(prm);
        } else if (prm.type.equalsIgnoreCase(ProcessParameter.OPTIONS)) {
            newP = new OptionsParameterView(prm);
        } else if (prm.type.equalsIgnoreCase(ProcessParameter.TEXT)) {
            newP = new TextParameterView(prm);
        }

        return newP;

    }
}
