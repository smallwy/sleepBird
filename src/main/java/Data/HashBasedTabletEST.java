package Data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class HashBasedTabletEST {

    public static void main(String[] args) {
        Table<Integer, Integer, Integer> oldTable = HashBasedTable.create();
        oldTable.put(1, 1, 1);
        oldTable.put(1, 2, 1);
        oldTable.put(1, 3, 2);
        oldTable.put(1, 4, 3);
        oldTable.put(1, 5, 4);
        oldTable.put(1, 1, 2);
        oldTable.put(1, 2, 1);
        oldTable.put(1, 3, 2);
        oldTable.put(1, 4, 3);
        oldTable.put(1, 5, 4);

        oldTable.put(2, 3, 2);
        oldTable.put(3, 4, 3);
        oldTable.put(4, 5, 4);
        oldTable.rowKeySet().forEach(e-> System.out.println(e));
        System.out.println(oldTable.get(1,1));
        System.out.println("oldTable的size"+oldTable.size());

    }
}