package lexer;

import lexer.symbol.sym;

public class Yytoken {
    Yytoken (int index, String text, int line, int charBegin, int charEnd) {
        m_index = index;
        m_text = new String(text);
        m_line = line;
        m_charBegin = charBegin;
        m_charEnd = charEnd;
    }

    public int m_index;
    public String m_text;
    public int m_line;
    public int m_charBegin;
    public int m_charEnd;

    public String toString() {
        return "<" + ++m_charBegin + "," + m_charEnd + "," + m_text + "," + sym.getTokenName(m_index) + ">";
    }
}
