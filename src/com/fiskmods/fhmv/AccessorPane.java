package com.fiskmods.fhmv;

import static com.fiskmods.fhmv.MappingGui.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.fiskmods.fhmv.MappingInput.MappingClass;
import com.fiskmods.fhmv.MappingInput.MappingMethod;
import com.fiskmods.fhmv.MappingInput.MappingParameter;

public class AccessorPane extends JSplitPane implements ListSelectionListener
{
    private static final long serialVersionUID = 1L;

    private final JList<String> accessorList;

    private final FormattedTextPane infobox;
    private final JSplitPane window;

    private final DefaultListModel<MappingMethod> methods = new DefaultListModel<>();
    private final JList<MappingMethod> methodList;
    private final JSplitPane methodPane;

    private final Table paramTable;

    public AccessorPane(JList<String> list)
    {
        super(JSplitPane.HORIZONTAL_SPLIT);
        setBorder(null);
        setDividerSize(3);
        setResizeWeight(0);
        setContinuousLayout(true);
        setMinimumSize(new Dimension(179, 80));
        setPreferredSize(new Dimension(179, 80));
        (accessorList = list).addListSelectionListener(this);

        infobox = new FormattedTextPane();
        infobox.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        window = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        window.setBorder(null);
        window.setDividerSize(3);
        window.setResizeWeight(0.5);
        window.setContinuousLayout(true);
        window.setMinimumSize(new Dimension(80, 179));
        window.setPreferredSize(new Dimension(80, 179));

        window.setTopComponent(withScroll(infobox, false));
        window.setBottomComponent(createTable(paramTable = new Table(), true, "Type", "Parameter", "Description"));

        methodList = new JList<>(methods);
        methodList.setCellRenderer(new ListMethodRenderer());
        methodPane = createList("Functions", methodList);
        methodList.addListSelectionListener(this::methodValueChanged);

        setLeftComponent(EMPTY);
        setRightComponent(EMPTY);
    }

    private void gotoAccessor(String link)
    {
        for (int i = 0; i < accessorList.getModel().getSize(); ++i)
        {
            String s = accessorList.getModel().getElementAt(i);

            if (s.equals(link))
            {
                accessorList.clearSelection();
                accessorList.addSelectionInterval(i, i);
                return;
            }
        }
    }

    private void methodValueChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            MappingMethod m = methodList.getSelectedValue();

            if (m != null)
            {
                MappingClass cl = MappingViewer.input.accessors.get(accessorList.getSelectedValue());
                infobox.clear();

                if (m.deprecated)
                {
                    infobox.addText("Deprecated\n", "bold-it");
                    infobox.addText("Deprecated functions are marked as unstable. They are kept in for the sake of back-compatibility, but may be removed in future releases.\n\n", "italic");
                }

                if (m.parent != cl)
                {
                    String name = MappingViewer.input.getAccessorName(m.parent);
                    infobox.addText("Inherited from ", "italic");
                    infobox.addLink(name, () -> gotoAccessor(name), false);
                    infobox.addText("\n\n", "regular");
                }

                infobox.addText(m.getFullName(), m.deprecated ? "header-depr" : "header");

                if (m.desc != null)
                {
                    infobox.addText("\n\n" + m.desc, "regular");
                }

                if (m.returns != null && !"void".equals(m.returns))
                {
                    infobox.addText("\n\nReturns: ", "italic");

                    if (MappingViewer.input.accessors.containsKey(m.returns))
                    {
                        infobox.addLink(m.returns, () -> gotoAccessor(m.returns), true);
                    }
                    else
                    {
                        infobox.addText(primitiveName(m.returns), "bold");
                    }
                }

                paramTable.clear();

                if (m.params != null)
                {
                    for (MappingParameter p : m.params)
                    {
                        paramTable.addRow(primitiveName(p.type), p.name, p.desc);
                    }
                }
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            String sel = accessorList.getSelectedValue();
            MappingClass cl = MappingViewer.input.accessors.get(sel);

            setLeftComponent(methodPane);
            setRightComponent(window);
            infobox.clear();
            methods.clear();

            if (cl != null)
            {
                cl.getAllMethods().forEach(methods::addElement);
            }
        }
    }

    private static class ListMethodRenderer extends DefaultListCellRenderer
    { 
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            MappingMethod m = (MappingMethod) value;
            setText(m.getFullName());

            if (m.deprecated)
            {
                c.setFont(DEPR);
                c.setForeground(Color.LIGHT_GRAY);
            }
            else if (m.inherited)
            {
                c.setFont(ITALIC);
                c.setForeground(isSelected ? new Color(0xCCCCCC) : new Color(0x666666));
            }
            else
            {
                c.setFont(FONT);
            }

            return c;
        }
    }
}
