package TEST;

import java.util.ArrayList;
import java.util.Iterator;

public class Test12 {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        Iterator it = list.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            System.out.println(str);
            list.add("d");
        }
    }
}


