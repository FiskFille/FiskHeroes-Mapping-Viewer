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

    private Map<MappingClass, String> accessorKeys;

    public Map<String, MappingClass> accessors = new HashMap<>();
    public Map<String, Object> mappings = new HashMap<>();

    private Map<String, String[]> headers = new HashMap<>();

    public List<?> list(String key)
    {
        return (List<?>) mappings.computeIfAbsent(key, k -> new ArrayList<>());
    }

    public Map<?, ?> map(String key)
    {
        return (Map<?, ?>) mappings.computeIfAbsent(key, k -> new HashMap<>());
    }

    public String getAccessorName(MappingClass cl)
    {
        if (accessorKeys == null)
        {
            accessorKeys = new HashMap<>();
            accessors.forEach((k, v) -> accessorKeys.put(v, k));
        }

        return accessorKeys.get(cl);
    }

    public String[] getHeaders(String key, String... names)
    {
        return headers.getOrDefault(key, names);
    }

    public static class MappingClass
    {
        private List<MappingMethod> methods = new ArrayList<>();
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

        public MappingMethod(MappingMethod m, MappingClass cl, boolean b)
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

    public static class MappingParameter
    {
        public String type;
        public String name;
        public String desc;
    }
}
