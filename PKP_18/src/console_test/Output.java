package console_test;

import java.math.BigDecimal;

public class Output {
	
	String output;
	int total;
	int sum1; 
	int sum3; 
	int sum5; 
	int sum10;
	int sum_all;
	
	Output(String output){
		if(output == null){
			return;
		}
		this.output = output;
		String out[] = this.output.split(",");
		total = Integer.parseInt(out[0]);
		sum1 = Integer.parseInt(out[1]);
		sum3 = Integer.parseInt(out[2]);
		sum5 = Integer.parseInt(out[3]);
		sum10 = Integer.parseInt(out[4]);
		sum_all = Integer.parseInt(out[5]);
	}
	
	public void plus(Output other){
		this.total += other.total;
		this.sum1 += other.sum1;
		this.sum3 += other.sum3;
		this.sum5 += other.sum5;
		this.sum10 += other.sum10;
		this.sum_all += other.sum_all;
	}
	
	public String toString(){
		String ret = total + ","+ sum1 +","+ sum3+","+sum5+","+sum10+","+sum_all;
		return ret;
	}
	
	public String outputPercent(){
		String ret = "";
		ret += total + ",";
		ret += roundOfBigDecimal(percent(sum1, total),1) + ",";
		ret += roundOfBigDecimal(percent(sum3, total),1) + ",";
		ret += roundOfBigDecimal(percent(sum5, total),1) + ",";
		ret += roundOfBigDecimal(percent(sum10, total),1) + ",";
		ret += roundOfBigDecimal(percent(sum_all, total),1);
		return ret;
	}
	
	public String outputPerSum10(){
		String ret = "";
		ret += roundOfBigDecimal(per(sum10, total),3);
		return ret;
	}
	
	public String outputPercentPer100(){
		String ret = "";
		ret += total + ",";
		ret += roundOfBigDecimal(per(sum1, total),3) + ",";
		ret += roundOfBigDecimal(per(sum3, total),3) + ",";
		ret += roundOfBigDecimal(per(sum5, total),3) + ",";
		ret += roundOfBigDecimal(per(sum10, total),3) + ",";
		ret += roundOfBigDecimal(per(sum_all, total),3);
		return ret;
	}
	
	/**
	 * 小数第2位を四捨五入。（BigDecimalクラスを使用） 1
	 */
	public static double roundOfBigDecimal(double num, int a) {
		return new BigDecimal(num).setScale(a, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static double percent(double num, double total) {
		if(total == 0)
			return 0;
		return num / total*100;
	}
	
	public static double per(double num, double total) {
		if(total == 0)
			return 0;
		return num / total;
	}
}
