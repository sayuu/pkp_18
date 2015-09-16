package console_test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 *
 * 同じ編集距離でも、
 * 欲しい関数が存在するコードほど
 * 高い得点になってほしい。
 *
 * 1文字1文字の距離を見るのではなく、
 * 1ラベル1ラベルの距離をLDで見ることにする。
 *
 */
class LabelNameDistance
{
	//ノーマル
  int edit(List<String> px, List<String> py)
  {
    int len1 = px.size(), len2 = py.size();
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
           + ((px.subList(i-1,i).equals(py.subList(j-1,j)))?0:1) , // replace
                      (Integer)(row[i][j-1]) + 1),     // delete
                      (Integer)(row[i-1][j]) + 1);  // insert
      }
    }
    result=(Integer)(row[len1][len2]);

    return result;
  }

  //存在を見るやつを作りたい。
  //存在が無いとき減点！
  //存在があるとき少しだけ減点。
  int edit2(List<String> px, List<String> py)
  {
    int len1 = px.size(), len2 = py.size();
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
           + ((px.subList(i-1,i).equals(py.subList(j-1,j)))?0:1) , // replace
                      (Integer)(row[i][j-1]) + 1),     // delete
                      (Integer)(row[i-1][j]) + 1);  // insert
      }
    }
    result=(Integer)(row[len1][len2]);

    return result;
  }

  public static void main(String[] args)
  {
    LabelNameDistance ld = new LabelNameDistance() ;

    ArrayList<String> px = new ArrayList<String>();
    ArrayList<String> py = new ArrayList<String>();

    px.add("aiueo");
    px.add("kakikukeko");
    px.add("sasisuseso");

    py.add("kakikukeko");
    py.add("aiueo");
    py.add("sasisuseso");

    //System.out.println(ld.edit(px,py));
    String s[] = "((px.subList(i-1,i).equals(py.subList(j-1,j)".split("[\\.\\(\\),]");
    String k[] = new String[s.length];

    for(String ss:s)
    	System.out.println(ss);
    List<String> a = Arrays.asList(s);
    for(String ss: a)
    	System.out.println(ss);

    for(int i = 0; i < s.length; i++)
    	if(!s[i].equals(""))
    		k[i] = s[i];
    List<String> x = Arrays.asList(k);
    for(String ss: x)
    	System.out.println(ss);

  }
}