package search;

import java.util.ArrayList;
import java.util.List;

public class State {


	private int paramSize;
	private List<Integer> numberList;//�e�����ԍ����ɕ��񂾁A�֐��̔ԍ�

	public State(int paramSize, List<Integer> numberList){
		this.paramSize = paramSize;
		numberList = new ArrayList<Integer>(numberList);
	}
	
	/*
	 * �����̌�
	 */
	public int getParamSize(){
		return paramSize;
	}
	
	/*
	 * �e�����ԍ����ɕ��񂾁A�֐��̔ԍ�
	 */
	public List<Integer> getNumberList(){
		return numberList;
	}
	
	
	/*
	 * ���̏�Ԃ��擾����B
	 * 
	 * �e�����ԍ��̊֐��̃��X�g�͕]���l���Ƀ\�[�g����Ă���B
	 * 
	 * �e�����ԍ��̊֐��̃��X�g�̂��ꂼ�ꎟ�̊֐���I��������Ԃ�
	 * ���̏�ԂƂ���B
	 * ����(a,b,c)�ł���΁A(1�Ԗڂ̈����̌��̊֐��̃��X�g��a�ԖځA2�Ԗڂ̃��X�g��b�ԖځA3�Ԗڂ̃��X�g��c�Ԗ�)
	 * (a+1,b,c)
	 * (a,b+1,c)
	 * (a,b,c+1)
	 * �̂����ꂩ
	 */
	public List<State> getNextStates(){
		List<State> next_states = new ArrayList<State>();
		
		for(int i = 0; i < paramSize; i++){
			int number = numberList.get(i);
			List<Integer> newNumList = new ArrayList<Integer>(numberList);
			newNumList.set(i, number +1);//�Ίp�v�f��+1����.
			State s = new State(paramSize, newNumList);
			next_states.add(s);
		}
		return next_states;
	}
}
