package plugin.frequency;

import java.util.Comparator;
import java.util.Map;

/** Map �� value �Ń\�[�g���邽�߂̔�r�̃N���X */
class IntegerMapComparator implements Comparator<String> {
    private Map<String, Integer> map;
    public IntegerMapComparator(Map<String, Integer>  map) {
        this.map = map;
    }
    /** key 2���^����ꂽ�Ƃ��ɁA���� value �Ŕ�r */
    public int compare(String key1, String key2) {
        // value ���擾
        int value1 = map.get(key1);
        int value2 = map.get(key2);
        // value �̍~��, value���������Ƃ��� key �̎�����
        if(value1 == value2)
            return key1.toLowerCase().compareTo(key2.toLowerCase());
        else if(value1 < value2)
            return 1;
        else
            return -1;
    }
}
