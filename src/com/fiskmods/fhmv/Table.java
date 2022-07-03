package com.fiskmods.fhmv;

import static com.fiskmods.fhmv.MappingGui.*;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Table extends JPanel
{
    private static final long serialVersionUID = 1L;

    public Table()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }

    private void add(JPanel row, String s)
    {
        JTextArea t = font(new JTextArea(s));
        t.setEditable(false);
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        t.setBackground(null);
        t.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, Color.lightGray));
        row.add(t);
    }

    public void clear()
    {
        removeAll();
        updateUI();
    }

    public void addRow(String... args)
    {
        JPanel row = new JPanel(new GridLayout(1, args.length));
        row.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        for (String s : args)
        {
            add(row, s);
        }

        add(row);
        updateUI();
    }
}
