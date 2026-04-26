package com.riversoft.platform.stat;

/**
 * Created by exizhai on 3/20/2014.
 */
public class SingleSelectSQLStatement implements Comparable {

    private String table;
    private String conditions;
    private int time;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SingleSelectSQLStatement statement = (SingleSelectSQLStatement) o;

        if (conditions != null ? !conditions.equals(statement.conditions) : statement.conditions != null) return false;
        if (!table.equals(statement.table)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = table.hashCode();
        result = 31 * result + (conditions != null ? conditions.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public String toString() {
        return "SingleSelectSQLStatement{" +
                "table='" + table + '\'' +
                ", conditions='" + conditions + '\'' +
                ", time=" + time +
                '}';
    }
}
