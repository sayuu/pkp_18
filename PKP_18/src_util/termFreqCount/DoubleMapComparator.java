package termFreqCount;

import java.util.Comparator;
import java.util.Map;

/** Map �� value �Ń\�[�g���邽�߂̔�r�̃N���X */
class DoubleMapComparator implements Comparator {
    private Map map;
    public DoubleMapComparator(Map map) {
        this.map = map;
    }
    /** key 2���^����ꂽ�Ƃ��ɁA���� value �Ŕ�r */
    public int compare(Object o1, Object o2) {
        String key1 = (String)o1;
        String key2 = (String)o2;
        // value ���擾
        double i1 = ((Double)map.get(o1)).doubleValue();
        double i2 = ((Double)map.get(o2)).doubleValue();
        // value �̍~��, value���������Ƃ��� key �̎�����
        if(i1 == i2)
            return key1.toLowerCase().compareTo(key2.toLowerCase());
        else if(i1 < i2)
            return 1;
        else
            return -1;
    }
}
