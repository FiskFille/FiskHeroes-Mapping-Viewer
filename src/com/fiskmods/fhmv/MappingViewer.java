package com.fiskmods.fhmv;

import static com.fiskmods.fhmv.MappingGui.*;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class MappingViewer
{
    public static final String NAME = "FiskHeroes Mapping Viewer 1.0";
    public static final String BASE_URL = "https://raw.githubusercontent.com/FiskFille/Superheroes/master/mappings";

    private static JFrame selector;

    public static MappingInput input = new MappingInput();

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Throwable throwable)
        {
        }

        Map<String, List<String>> map = new Gson().fromJson(new InputStreamReader(new URL(BASE_URL + "/index.json").openStream()), Map.class);
        List<String> list = map.get("versions");
        list.sort(Comparator.reverseOrder());
        openSelectionDialog(list);
    }

    public static void openSelectionDialog(List<String> versions)
    {
        selector = new JFrame();
        selector.setTitle(NAME);
        selector.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selector.setSize(300, 150);
        selector.setLocationRelativeTo(null);
        selector.getContentPane().setLayout(new BorderLayout());

        JComboBox<String> box = font(new JComboBox<>(versions.toArray(new String[0])));
        JLabel l = bold(new JLabel("Select Version"));
        l.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 10));

        JButton button = font(new JButton("View Mappings"));
        button.addActionListener(e ->
        {
            downloadMappings((String) box.getSelectedItem());
            EventQueue.invokeLater(() ->
            {
                try
                {
                    selector.dispose();
                    new MappingGui();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            });
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 10));
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        buttons.add(button);
        selector.getContentPane().add(pair(l, wrap(box)), BorderLayout.NORTH);
        selector.getContentPane().add(buttons, BorderLayout.SOUTH);
        selector.setResizable(false);
        selector.setVisible(true);
    }

    public static void downloadMappings(String version)
    {
        try
        {
            input = new Gson().fromJson(new InputStreamReader(new URL(BASE_URL + "/" + version + ".json").openStream()), MappingInput.class);
        }
        catch (JsonSyntaxException | JsonIOException | IOException e)
        {
            e.printStackTrace();
        }
    }

    private static JPanel wrap(Component panel)
    {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        p.add(panel);
        return p;
    }

    private static JPanel pair(Component top, Component bottom)
    {
        JPanel p = new JPanel(new BorderLayout());
        p.add(top, BorderLayout.NORTH);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }
}
