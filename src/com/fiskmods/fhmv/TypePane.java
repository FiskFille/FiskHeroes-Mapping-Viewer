package com.fiskmods.fhmv;

import static com.fiskmods.fhmv.MappingGui.*;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.fiskmods.fhmv.MappingInput.MappingClass;

public class TypePane extends JSplitPane implements ListSelectionListener
{
    private static final long serialVersionUID = 1L;

    private final JList<String> typeList;
    public final Map<String, AccessorView> accessors = new HashMap<>();

    public TypePane(JList<String> list)
    {
        super(JSplitPane.HORIZONTAL_SPLIT);
        setBorder(null);
        setDividerSize(0);
        setResizeWeight(0);
        setContinuousLayout(true);
        setMinimumSize(new Dimension(179, 80));
        setPreferredSize(new Dimension(179, 80));
        (typeList = list).addListSelectionListener(this);
        
        for (Map.Entry<String, Map<String, MappingClass>> e : MappingViewer.input.accessorGroups.entrySet())
        {
            String[] array = e.getValue().keySet().stream().sorted().toArray(String[]::new);
            accessors.put(e.getKey(), new AccessorView(this, array, e.getValue(), e.getKey(), e.getKey().equals("JS Accessors") ? 1 : 0.5));
        }

        setLeftComponent(EMPTY);
        setRightComponent(EMPTY);
    }

    @SuppressWarnings("unchecked")
    private JScrollPane generateWindow(String sel)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Object obj = MappingViewer.input.mappings.get(sel);

        if (obj instanceof List)
        {
            JTextPane t = large(new JTextPane());
            t.setText(((List<?>) obj).stream().sorted().map(s -> s + "\n").collect(Collectors.joining()));
            t.setEditable(false);
            t.setBackground(null);
            t.setBorder(null);
            panel.add(t);
        }
        else if (obj instanceof Map)
        {
            Map<String, ?> map = (Map<String, ?>) obj;

            // Map of maps
            if (map.values().stream().anyMatch(t -> Map.class.isAssignableFrom(t.getClass())))
            {
                TableMap table = new TableMap();
                JSplitPane pane = createTable(table, false, MappingViewer.input.getHeaders(sel, "Key", "Values"));
                map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(t -> table.addRow(t.getKey(), ((Map<String, String>) t.getValue())));
                panel.add(pane);
            }
            // Map of lists
            else if (map.values().stream().anyMatch(t -> List.class.isAssignableFrom(t.getClass())))
            {
                Table table = new Table();
                JSplitPane pane = createTable(table, false, MappingViewer.input.getHeaders(sel, "Key", "Values"));
                map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(t -> table.addRow(t.getKey(), ((List<String>) t.getValue()).stream().collect(Collectors.joining("\n"))));
                panel.add(pane);
            }
            else // Map of objects
            {
                Table table = new Table();
                JSplitPane pane = createTable(table, false, MappingViewer.input.getHeaders(sel, "Key", "Value"));
                map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(t -> table.addRow(t.getKey(), String.valueOf(t.getValue())));
                panel.add(pane);
            }
        }

        panel.updateUI();
        return withScroll(panel, false);
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            String sel = typeList.getSelectedValue();
            AccessorView view = accessors.get(sel);

            if (view != null)
            {
                setDividerSize(3);
                setLeftComponent(view.listPane);
                setRightComponent(view.windowPane);
            }
            else
            {
                setDividerSize(0);
                setLeftComponent(generateWindow(sel));
                setRightComponent(EMPTY);
            }
        }
    }
    
    public static class AccessorView
    {
        public final JList<String> list;
        public final AccessorPane windowPane;
        public final JSplitPane listPane;
        
        public AccessorView(TypePane parent, String[] array, Map<String, MappingClass> map, String group, double memberSplit)
        {
            listPane = createList("Types", list = new JList<>(array));
            windowPane = new AccessorPane(list, parent, map, group, memberSplit);
        }
    }
}
