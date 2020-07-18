package botoseis.iview.utils;

/**
 *
 * @author Williams Lima, williams_al@gmx.com
 */
public class NumericDoc extends javax.swing.text.PlainDocument {

    public NumericDoc() {

    }

    @Override
    public void insertString(int offset, String str,
            javax.swing.text.AttributeSet a)
            throws javax.swing.text.BadLocationException {
        String userText = "";
        try {
            str = str.replace(',', '.');

            super.insertString(offset, str, a);

            userText = getText(0, getLength());
            Float.parseFloat(userText);
        } catch (NumberFormatException e) {
            if (("-".equalsIgnoreCase(str) && (userText.length() == 1)) ||
                    ("+".equalsIgnoreCase(str) &&
                    (userText.length() == 1)) ||
                    (".".equalsIgnoreCase(str) &&
                    (userText.length() == 1))) {
            // "-", "+" and "." must be allowed if they ae at the start
            // of the input text.
            } else {
                // Remove text that invalidated the input.
                // and beep to alert the user.
                remove(offset, str.length());
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        }
    }
}
