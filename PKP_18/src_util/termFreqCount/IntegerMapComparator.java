package termFreqCount;

import java.util.Comparator;
import java.util.Map;

/** Map ‚Ì value ‚Åƒ\[ƒg‚·‚é‚½‚ß‚Ì”äŠr‚ÌƒNƒ‰ƒX */
class IntegerMapComparator implements Comparator {
    private Map map;
    public IntegerMapComparator(Map map) {
        this.map = map;
    }
    /** key 2‚Â‚ª—^‚¦‚ç‚ê‚½‚Æ‚«‚ÉA‚»‚Ì value ‚Å”äŠr */
    public int compare(Object o1, Object o2) {
        String key1 = (String)o1;
        String key2 = (String)o2;
        // value ‚ğæ“¾
        int i1 = ((Integer)map.get(o1)).intValue();
        int i2 = ((Integer)map.get(o2)).intValue();
        // value ‚Ì~‡, value‚ª“™‚µ‚¢‚Æ‚«‚Í key ‚Ì«‘‡
        if(i1 == i2)
            return key1.toLowerCase().compareTo(key2.toLowerCase());
        else if(i1 < i2)
            return 1;
        else
            return -1;
    }
}
