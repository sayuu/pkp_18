package plugin.frequency;

import java.util.Comparator;
import java.util.Map;

/** Map ‚Ì value ‚Åƒ\[ƒg‚·‚é‚½‚ß‚Ì”äŠr‚ÌƒNƒ‰ƒX */
class IntegerMapComparator implements Comparator<String> {
    private Map<String, Integer> map;
    public IntegerMapComparator(Map<String, Integer>  map) {
        this.map = map;
    }
    /** key 2‚Â‚ª—^‚¦‚ç‚ê‚½‚Æ‚«‚ÉA‚»‚Ì value ‚Å”äŠr */
    public int compare(String key1, String key2) {
        // value ‚ğæ“¾
        int value1 = map.get(key1);
        int value2 = map.get(key2);
        // value ‚Ì~‡, value‚ª“™‚µ‚¢‚Æ‚«‚Í key ‚Ì«‘‡
        if(value1 == value2)
            return key1.toLowerCase().compareTo(key2.toLowerCase());
        else if(value1 < value2)
            return 1;
        else
            return -1;
    }
}
