package com.fiskmods.fhmv;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MappingGui extends JFrame
{
    private static final long serialVersionUID = -1L;

    public static final Font FONT, LARGE, BOLD, HEADER, ITALIC, DEPR;
    public static final JPanel EMPTY;

    static
    {
        Font f = new JLabel().getFont();
        FONT = new Font(f.getFamily(), f.getStyle(), 14);
        LARGE = new Font(f.getFamily(), f.getStyle(), 16);
        BOLD = new Font(f.getFamily(), f.getStyle() | Font.BOLD, 14);
        HEADER = new Font(f.getFamily(), f.getStyle() | Font.BOLD, 16);
        ITALIC = new Font(f.getFamily(), f.getStyle() | Font.ITALIC, 14);

        Map attributes = FONT.getAttributes();
        attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        DEPR = new Font(attributes);

        EMPTY = new JPanel();
        EMPTY.setVisible(false);
    }

    private JList<String> classList;

    public MappingGui()
    {
        setTitle(MappingViewer.NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(925, 621);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout(0, 0));
        initialize();
        setVisible(true);
    }

    public static JScrollPane withScroll(Component c, boolean always)
    {
        JScrollPane pane = new JScrollPane();
        pane.setVerticalScrollBarPolicy(always ? ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS : ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setViewportView(c);
        return pane;
    }

    public static JPanel center(Component c)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(c);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        return panel;
    }

    public static <T extends Component> T font(T t)
    {
        t.setFont(FONT);
        return t;
    }

    public static <T extends Component> T large(T t)
    {
        t.setFont(LARGE);
        return t;
    }

    public static <T extends Component> T bold(T t)
    {
        t.setFont(BOLD);
        return t;
    }

    public static <T extends Component> T header(T t)
    {
        t.setFont(HEADER);
        return t;
    }

    public static JSplitPane withHeader(Component t, Component header)
    {
        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        pane.setEnabled(false);
        pane.setBorder(null);
        pane.setDividerSize(0);
        pane.setTopComponent(header);
        pane.setBottomComponent(t);
        return pane;
    }

    public static JSplitPane createList(String header, JList<?> list)
    {
        JSplitPane pane = withHeader(withScroll(font(list), false), center(header(new JLabel(header))));
        pane.setDividerSize(5);
        return pane;
    }

    public static JSplitPane createTable(JPanel table, boolean scroll, String... headers)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(table);

        JPanel header = new JPanel(new GridLayout(1, headers.length));

        for (String s : headers)
        {
            header.add(center(bold(new JLabel(s))));
        }

        return withHeader(scroll ? withScroll(panel, false) : panel, header);
    }

    public static String primitiveName(String s)
    {
        switch (s)
        {
        case "String[]":
            return "Array (String)";
        case "int":
            return "Integer";
        case "int[]":
            return "Array (Integer)";
        case "byte":
            return "Byte";
        case "byte[]":
            return "Array (Byte)";
        case "boolean":
            return "Boolean";
        case "double":
            return "Double";
        case "float":
            return "Float";
        case "float[]":
            return "Array (Float)";
        case "long":
            return "Long";
        case "short":
            return "Short";
        default:
            return s;
        }
    }

    private void initialize()
    {
        // setIconImage((new ImageIcon(MappingGui.class.getResource("/bspkrs/mmv/gui/icon/bspkrs32.png"))).getImage());
        // addWindowListener(new WindowAdapter() {
        // public void windowClosing(WindowEvent arg0) {
        // // MappingGui.savePrefs();
        // }
        // });

        JSplitPane project = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        project.setBorder(null);
        project.setDividerSize(3);
        project.setResizeWeight(0);
        project.setContinuousLayout(true);
        project.setMinimumSize(new Dimension(179, 80));
        project.setPreferredSize(new Dimension(179, 80));

        List<String> list = new ArrayList<>();
        list.addAll(MappingViewer.input.mappings.keySet());
        list.addAll(MappingViewer.input.accessorGroups.keySet());
        list.sort(null);

        project.setLeftComponent(createList("Mappings", classList = new JList<>(list.toArray(new String[0]))));
        project.setRightComponent(new TypePane(classList));
        getContentPane().add(project, BorderLayout.CENTER);
    }
}
