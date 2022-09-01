package com.fiskmods.fhmv;

import static com.fiskmods.fhmv.MappingGui.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.fiskmods.fhmv.MappingInput.MappingClass;
import com.fiskmods.fhmv.MappingInput.MappingField;
import com.fiskmods.fhmv.MappingInput.MappingMethod;
import com.fiskmods.fhmv.MappingInput.MappingParameter;
import com.fiskmods.fhmv.TypePane.AccessorView;

public class AccessorPane extends JSplitPane implements ListSelectionListener
{
    private static final long serialVersionUID = 1L;

    private final Map<String, MappingClass> accessorMap;
    private final TypePane parentPane;
    private final JList<String> accessorList;

    private final FormattedTextPane infobox;
    private final JSplitPane window;
    private final JSplitPane members;

    private final DefaultListModel<MappingMethod> methods = new DefaultListModel<>();
    private final JList<MappingMethod> methodList;
    private final JSplitPane methodPane;

    private final DefaultListModel<MappingField> fields = new DefaultListModel<>();
    private final JList<MappingField> fieldList;
    private final JSplitPane fieldPane;

    private final Table paramTable;

    public AccessorPane(JList<String> list, TypePane parent, Map<String, MappingClass> map, String group, double memberSplit)
    {
        super(JSplitPane.HORIZONTAL_SPLIT);
        setBorder(null);
        setDividerSize(3);
        setResizeWeight(0);
        setContinuousLayout(true);
        setMinimumSize(new Dimension(179, 80));
        setPreferredSize(new Dimension(179, 80));
        (accessorList = list).addListSelectionListener(this);
        accessorMap = map;
        parentPane = parent;
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

        members = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        members.setBorder(null);
        members.setDividerSize(3);
        members.setResizeWeight(memberSplit);
        members.setContinuousLayout(true);
        members.setMinimumSize(new Dimension(200, 80));
        members.setPreferredSize(new Dimension(200, 80));

        methodList = new JList<>(methods);
        methodList.setCellRenderer(new ListMethodRenderer());
        methodList.addListSelectionListener(this::methodValueChanged);
        methodPane = createList("Functions", methodList);

        fieldList = new JList<>(fields);
        fieldList.setCellRenderer(new ListFieldRenderer());
        fieldList.addListSelectionListener(this::fieldValueChanged);
        fieldPane = createList("Variables", fieldList);

        members.setTopComponent(methodPane);
        members.setBottomComponent(fieldPane);

        setLeftComponent(EMPTY);
        setRightComponent(EMPTY);
    }

    private void gotoAccessor(String link)
    {
        String group = MappingViewer.input.getGroup(link);
        AccessorView view = parentPane.accessors.get(group);

        if (view != null)
        {
            gotoAccessor(view.windowPane, link);
            parentPane.setDividerSize(3);
            parentPane.setLeftComponent(view.listPane);
            parentPane.setRightComponent(view.windowPane);
        }
    }

    private static void gotoAccessor(AccessorPane pane, String link)
    {
        for (int i = 0; i < pane.accessorList.getModel().getSize(); ++i)
        {
            String s = pane.accessorList.getModel().getElementAt(i);

            if (s.equals(link))
            {
                pane.accessorList.clearSelection();
                pane.accessorList.addSelectionInterval(i, i);
                pane.paramTable.clear();
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
                MappingClass cl = accessorMap.get(accessorList.getSelectedValue());
                fieldList.clearSelection();
                infobox.clear();

                if (m.deprecated)
                {
                    infobox.addText("Deprecated\n", "bold-it");
                    infobox.addText("Deprecated functions are marked as unstable. They are kept in for the sake of back-compatibility, but may be removed in future releases.\n\n", "italic");
                }

                if (m.parent != cl)
                {
                    String name = MappingViewer.input.accessorKeys.get(m.parent);
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

    private void fieldValueChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            MappingField f = fieldList.getSelectedValue();

            if (f != null)
            {
                methodList.clearSelection();
                infobox.clear();

                if (f.deprecated)
                {
                    infobox.addText("Deprecated\n", "bold-it");
                    infobox.addText("Deprecated fields are marked as unstable. They are kept in for the sake of back-compatibility, but may be removed in future releases.\n\n", "italic");
                }

                infobox.addText(f.name, f.deprecated ? "header-depr" : "header");

                if (f.assignable)
                {
                    infobox.addText("\nAssignable", "regular");
                }

                if (f.desc != null)
                {
                    infobox.addText("\n\n" + f.desc, "regular");
                }

                infobox.addText("\n", "regular");

                if (f.type != null)
                {
                    infobox.addText("\nType: ", "italic");

                    if (MappingViewer.input.accessors.containsKey(f.type))
                    {
                        infobox.addLink(f.type, () -> gotoAccessor(f.type), true);
                    }
                    else
                    {
                        infobox.addText(primitiveName(f.type), "bold");
                    }
                }

                if (f.defVal != null)
                {
                    infobox.addText("\nDefault Value: ", "italic");
                    String s = f.defVal;

                    if (f.defVal.startsWith("0x"))
                    {
                        infobox.addColor(Integer.decode(f.defVal));
                        s = " " + s;
                    }

                    infobox.addText(s, "bold");
                }

                paramTable.clear();
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            String sel = accessorList.getSelectedValue();
            MappingClass cl = accessorMap.get(sel);

            setLeftComponent(members);
            setRightComponent(window);
            paramTable.clear();
            infobox.clear();
            methods.clear();
            fields.clear();

            if (cl != null)
            {
                cl.getAllMethods().forEach(methods::addElement);
                cl.fields.forEach(fields::addElement);
            }
        }
    }

    private static class ListMethodRenderer extends DefaultListCellRenderer
    { 
        private static final long serialVersionUID = 1L;

        private static final Color INHERITED = new Color(0x666666);
        private static final Color INHERITED_SEL = new Color(0xCCCCCC);

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
                c.setForeground(isSelected ? INHERITED_SEL : INHERITED);
            }
            else
            {
                c.setFont(FONT);
            }

            return c;
        }
    }

    private static class ListFieldRenderer extends DefaultListCellRenderer
    { 
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            MappingField f = (MappingField) value;
            setText(f.name);

            if (f.deprecated)
            {
                c.setFont(DEPR);
                c.setForeground(Color.LIGHT_GRAY);
            }
            else
            {
                c.setFont(FONT);
            }

            return c;
        }
    }
}
