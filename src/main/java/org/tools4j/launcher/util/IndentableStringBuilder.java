package org.tools4j.launcher.util;

import java.util.List;
import java.util.Map;

/**
 * User: ben
 * Date: 30/10/17
 * Time: 5:51 PM
 */
public class IndentableStringBuilder {
    private final String indent;
    private final StringBuilder sb;
    private boolean indentActivated;

    public IndentableStringBuilder(final String indent) {
        this.indent = indent;
        this.indentActivated = false;
        sb = new StringBuilder();
    }

    public IndentableStringBuilder append(String str){
        if(indentActivated) {
            appendIndentIfLastCharacterWasLineEnding();
            //Append indents to any other line endings which are NOT at the end of the line
            str = str.replaceAll("\n(?!$)", "\n" + indent);
        }
        sb.append(str);
        return this;
    }

    public IndentableStringBuilder append(final Object obj) {
        return append(obj.toString());
    }

    public void activateIndent(){
        indentActivated = true;
        appendIndentIfLastCharacterWasLineEnding();
    }

    private void appendIndentIfLastCharacterWasLineEnding() {
        if(sb.lastIndexOf("\n") == sb.length()-1){
            sb.append(indent);
        }
    }

    public void decactivateIndent(){
        if(sb.lastIndexOf("\n" + indent) == sb.length()-1-indent.length()){
            sb.replace(sb.length()-1-indent.length(), sb.length()-1, "\n");
        }
        indentActivated = false;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
