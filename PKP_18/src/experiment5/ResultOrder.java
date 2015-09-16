package experiment5;

public class ResultOrder {
	
	int sum_all_tasks;	//タスクの総数
	int sum_ans_all;	//全範囲での答えの数。
	int sum_within_one;	//上位1件
	int sum_within_three; //上位3件
	int sum_within_five; //上位5件
	int sum_within_ten; //上位10件
	
	public ResultOrder(int sum_all_tasks, int sum_ans_all,int sum_within_one,	int sum_within_tree, int sum_within_five, int sum_within_ten){
		this.sum_all_tasks = sum_all_tasks;
		this.sum_ans_all = sum_ans_all;
		this.sum_within_one = sum_within_one;
		this.sum_within_three = sum_within_tree;
		this.sum_within_five = sum_within_five;
		this.sum_within_ten = sum_within_ten;
	}

}
