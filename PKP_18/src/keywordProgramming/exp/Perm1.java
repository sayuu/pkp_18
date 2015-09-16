package keywordProgramming.exp;

import java.util.ArrayList;
import java.util.List;


public class Perm1 {
	public List<List<Integer>> permlist = new ArrayList<List<Integer>>(); 
	
	private int num;
	
	public Perm1(int num){	
		this.num = num;
	}
	
  private void print_perm(int[] perm){
    for(int x: perm){
      System.out.print(x + " ");
    }
    System.out.println();
  }
  
  public void printPerm(){
	  for(List<Integer> list:permlist){
	    for(int x: list){
	      System.out.print(x + " ");
	    }
	    System.out.println();
	  }
  }
  
  private List<Integer> set_perm(int[] perm){
	  List<Integer> list = new ArrayList<Integer>(); 

	    for(int x: perm){
	    	list.add(x);
	    }
	    
	    return list;
  }
  	public void makePerm(){
  		make_perm(0, new int [this.num], new boolean [this.num + 1]);
  	}
  	
	 private void make_perm(int n, int[] perm, boolean[] flag){
	    if(n == perm.length){
	      //print_perm(perm);
	    	permlist.add(set_perm(perm));
	    } else {
	      for(int i = 1; i <= perm.length; i++){
	        if(flag[i]) continue;
	        perm[n] = i;
	        flag[i] = true;
	        make_perm(n + 1, perm, flag);
	        flag[i] = false;
	      }
	    }
	  }

  public static void main(String[] args){
	  for(int i = 1; i < 10; i++){
		  Perm1 p = new Perm1(i);
		  long stert1 = System.currentTimeMillis();
			
		  p.makePerm();
		  long stert2 = System.currentTimeMillis();
		  
		  System.out.println(i +" : "+ (stert2 -stert1));
	  }
	  //p.printPerm();
  }
}
