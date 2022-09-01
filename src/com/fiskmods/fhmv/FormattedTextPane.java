package com.fiskmods.fhmv;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class FormattedTextPane extends JTextPane
{
    private static final long serialVersionUID = 1L;

    private final StyledDocument doc;

    public FormattedTextPane()
    {
        setEditable(false);
        setBackground(null);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addStylesToDocument(doc = getStyledDocument());
        addMouseMotionListener(new MouseMotionListener()
        {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                Element element = doc.getCharacterElement(viewToModel(e.getPoint()));
                LinkListener link = (LinkListener) element.getAttributes().getAttribute("link");

                if (link != null)
                {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                else
                {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }

            @Override public void mouseDragged(MouseEvent e) {}
        });

        addMouseListener(new MouseListener()
        {
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseClicked(MouseEvent e)
            {
                Element element = doc.getCharacterElement(viewToModel(e.getPoint()));
                LinkListener link = (LinkListener) element.getAttributes().getAttribute("link");

                if (link != null)
                {
                    link.runnable.run();
                }
            }
        });
    }

    private void addStylesToDocument(StyledDocument doc)
    {
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontSize(def, 14);

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold-it", s);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("header", regular);
        StyleConstants.setFontSize(s, 16);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("header-depr", s);
        StyleConstants.setStrikeThrough(s, true);
        StyleConstants.setItalic(s, true);
    }

    public void clear()
    {
        try
        {
            doc.remove(0, doc.getLength());
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
    }

    public void addText(String text, String style)
    {
        try
        {
            doc.insertString(doc.getLength(), text, doc.getStyle(style));
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
    }

    public void addLink(String url, Runnable r, boolean type)
    {
        try
        {
            Style s = doc.addStyle(null, doc.getStyle("regular"));
            StyleConstants.setUnderline(s, true);
            StyleConstants.setBold(s, true);

            if (type)
            {
                StyleConstants.setForeground(s, Color.BLUE);
            }
            else
            {
                StyleConstants.setItalic(s, true);
            }

            s.addAttribute("link", new LinkListener(r));
            doc.insertString(doc.getLength(), url, s);
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
    }

    public void addColor(int color)
    {
        try
        {
            Style s = doc.addStyle(null, doc.getStyle("regular"));
            StyleConstants.setBold(s, true);
            StyleConstants.setBackground(s, new Color(color));
            doc.insertString(doc.getLength(), "    ", s);
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
    }

    private class LinkListener extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        private final Runnable runnable;

        private LinkListener(Runnable r)
        {
            runnable = r;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            runnable.run();
        }
    }
}
