package console_test;

import java.io.UnsupportedEncodingException;
/*
 * wikipedia 参考　動的計画法
 */
public class LevenshteinDistance
{
	public int w_delete = 1;//削除
	
	public int w_replace = 1;
	public int w_insert = 1;
	
	public LevenshteinDistance(int d, int r, int i){
		this.w_delete = d;
		this.w_replace = r;
		this.w_insert = i;
	}
	
  public int edit(String px, String py)
  {
    int len1=px.length(),len2=py.length();
    int[][] row = new int[len1+1][len2+1];
    int i,j;
    int result;

    for(i=0;i<len1+1;i++) row[i][0] = i;
    for(i=0;i<len2+1;i++) row[0][i] = i;
    for(i=1;i<=len1;++i)
    {
      for(j=1;j<=len2;++j)
      {
    	  row[i][j] = Math.min(Math.min(
		           (Integer)(row[i-1][j-1])
		           + ((px.substring(i-1,i).equals(py.substring(j-1,j)))?0:this.w_replace) , // replace
		                      (Integer)(row[i][j-1]) + this.w_delete),     // delete
		                      (Integer)(row[i-1][j]) + this.w_insert);  // insert
//    	  char x = py.charAt(j-1);
//    	  if(j != 1 && (x == 'a' ||x == 'i' ||x == 'u' ||x == 'e' ||x == 'o')){
//    		  
//	        row[i][j] = Math.min(Math.min(
//	           (Integer)(row[i-1][j-1])
//	           + ((px.substring(i-1,i).equals(py.substring(j-1,j)))?0:this.w_replace) , // replace
//	                      (Integer)(row[i][j-1]) + 0),     // delete
//	                      (Integer)(row[i-1][j]) + this.w_insert);  // insert
//    	  }else{
//    		  row[i][j] = Math.min(Math.min(
//    		           (Integer)(row[i-1][j-1])
//    		           + ((px.substring(i-1,i).equals(py.substring(j-1,j)))?0:this.w_replace) , // replace
//    		                      (Integer)(row[i][j-1]) + this.w_delete),     // delete
//    		                      (Integer)(row[i-1][j]) + this.w_insert);  // insert
//    		    	  
//    	  }  
      }
    }
    result=(Integer)(row[len1][len2]);

    return result;
  }
  public static void main(String[] args) throws UnsupportedEncodingException
  {
    LevenshteinDistance ld = new LevenshteinDistance(1,1,1) ;
    System.out.println(ld.edit("mtch","match"));
    System.out.println(ld.edit("match","mtch"));
//    System.out.println(ld.edit("Andrd","Android"));
//    System.out.println(ld.edit("Dstnc","Distance"));
//    System.out.println(ld.edit("abcdcd","abcd"));
//    System.out.println(ld.edit("abdd","abcd"));
  }
}