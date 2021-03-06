package plugin.frequency;

import java.util.Comparator;
import java.util.Map;

/** Map の value でソートするための比較のクラス */
class IntegerMapComparator implements Comparator<String> {
    private Map<String, Integer> map;
    public IntegerMapComparator(Map<String, Integer>  map) {
        this.map = map;
    }
    /** key 2つが与えられたときに、その value で比較 */
    public int compare(String key1, String key2) {
        // value を取得
        int value1 = map.get(key1);
        int value2 = map.get(key2);
        // value の降順, valueが等しいときは key の辞書順
        if(value1 == value2)
            return key1.toLowerCase().compareTo(key2.toLowerCase());
        else if(value1 < value2)
            return 1;
        else
            return -1;
    }
}
