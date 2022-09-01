package com.fiskmods.fhmv;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class MappingInput
{
    public int format = 0;

    public Map<String, MappingClass> accessors = new HashMap<>();
    public Map<String, Object> mappings = new HashMap<>();
    private Map<String, String[]> headers = new HashMap<>();

    public Map<MappingClass, String> accessorKeys = new HashMap<>();
    public Map<String, Map<String, MappingClass>> accessorGroups = new HashMap<>();
    private Map<String, String> accessorGroupLookup = new HashMap<>();

    public void init()
    {
        Map<String, MappingClass> map = new HashMap<>();
        
        for (Map.Entry<String, MappingClass> e : accessors.entrySet())
        {
            String s = e.getKey();
            String group = "JS Accessors";

            if (s.contains("|"))
            {
                String[] astring = s.split("\\|");
                group = astring[0];
                s = astring[1];
            }

            map.put(s, e.getValue());
            accessorGroupLookup.put(s, group);
            accessorGroups.computeIfAbsent(group, k -> new HashMap<>()).put(s, e.getValue());
        }
        
        accessors = map;
        accessors.forEach((k, v) -> accessorKeys.put(v, k));
    }

    public List<?> list(String key)
    {
        return (List<?>) mappings.computeIfAbsent(key, k -> new ArrayList<>());
    }

    public Map<?, ?> map(String key)
    {
        return (Map<?, ?>) mappings.computeIfAbsent(key, k -> new HashMap<>());
    }

    public String[] getHeaders(String key, String... names)
    {
        return headers.getOrDefault(key, names);
    }
    
    public String getGroup(String key)
    {
        return accessorGroupLookup.getOrDefault(key, "JS Accessors");
    }

    public static class MappingClass
    {
        private List<MappingMethod> methods = new ArrayList<>();
        public List<MappingField> fields = new ArrayList<>();
        private String parent;

        private List<MappingMethod> allMethods;

        public List<MappingMethod> getAllMethods()
        {
            if (allMethods != null)
            {
                return allMethods;
            }

            addMethods(allMethods = new ArrayList<>(), false);
            allMethods.sort(Comparator.comparing(MappingMethod::getFullName));
            return allMethods;
        }

        private void addMethods(List<MappingMethod> list, boolean b)
        {
            methods.forEach(t -> list.add(new MappingMethod(t, this, b)));

            if (parent != null)
            {
                MappingClass cl = MappingViewer.input.accessors.get(parent);

                if (cl != null)
                {
                    cl.addMethods(list, true);
                }
            }
        }
    }

    public static abstract class MappingMember
    {

    }

    public static class MappingMethod
    {
        public MappingClass parent;
        public List<MappingParameter> params;

        public String name;
        public String returns;
        public String desc;

        public boolean deprecated;
        public boolean inherited;

        private String fullName;

        private MappingMethod(MappingMethod m, MappingClass cl, boolean b)
        {
            parent = cl;
            params = m.params;
            name = m.name;
            returns = m.returns;
            desc = m.desc;
            deprecated = m.deprecated;
            inherited = b;
        }

        public String getFullName()
        {
            if (fullName != null)
            {
                return fullName;
            }

            StringJoiner j = new StringJoiner(", ");

            if (params != null)
            {
                params.forEach(t -> j.add(t.name));
            }

            return fullName = name + "(" + j + ")";
        }
    }

    public class MappingField
    {
        public String name;
        public String type;
        public String desc;

        public boolean assignable;
        public boolean deprecated;

        public String defVal;
    }

    public static class MappingParameter
    {
        public String type;
        public String name;
        public String desc;
    }
}
