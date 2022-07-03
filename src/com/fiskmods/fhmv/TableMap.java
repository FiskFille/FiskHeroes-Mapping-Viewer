package com.fiskmods.fhmv;

import static com.fiskmods.fhmv.MappingGui.*;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class TableMap extends JPanel
{
    private static final long serialVersionUID = 1L;

    public TableMap()
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

    public void addRow(String key, Map<String, String> values)
    {
        JPanel row = new JPanel(new GridLayout(1, 2));
        row.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        add(row, key);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (Map.Entry<String, String> e : values.entrySet())
        {
            JPanel row1 = new JPanel(new GridLayout(0, 2));
            add(row1, e.getKey());
            add(row1, e.getValue());
            panel.add(row1);
        }

        row.add(panel);
        add(row);
        updateUI();
    }
}
