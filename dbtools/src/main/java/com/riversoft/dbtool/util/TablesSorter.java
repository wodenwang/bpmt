package com.riversoft.dbtool.util;

import org.jumpmind.db.model.ForeignKey;
import org.jumpmind.db.model.Table;

import java.util.*;

/**
 * Created by exizhai on 11/12/2014.
 */
public class TablesSorter {

    public static List<Table> sort(List<Table> tables) {
        final List<Table> sortedTables = new ArrayList<>();
        final Map<Table, Integer> distance = new HashMap<>();
        final Map<String, Set<Table>> references = new HashMap<>();

        sortedTables.addAll(tables);
        for (final Table table : sortedTables) {

            distance.put(table, 0);

            if (table.getForeignKeyCount() > 0) {
                final ForeignKey[] foreignKeys = table.getForeignKeys();
                Set<Table> foreignTables = new HashSet<>();
                for (ForeignKey foreignKey : foreignKeys) {
                    foreignTables.add(foreignKey.getForeignTable());
                }
                references.put(table.getName(), foreignTables);
            }
        }


        for (final Table table : sortedTables) {
            push(distance, references, table, -1);
        }

        Collections.sort(sortedTables, new Comparator<Table>() {
            @Override
            public int compare(final Table o1, final Table o2) {
                return distance.get(o1) - distance.get(o2);
            }
        });
        return sortedTables;

    }

    public static void push(final Map<Table, Integer> distance, final Map<String, Set<Table>> references, final Table table,
                            final int previous) {
        int atual = distance.get(table);
        atual = Math.max(previous, atual - 1) + 1;
        distance.put(table, atual);
        if(references.containsKey(table.getName())) {
            for (final Table ref : references.get(table.getName())) {
                if (!table.equals(ref) && distance.containsKey(ref)) {
                    push(distance, references, ref, atual);
                }
            }
        }
    }


}
