package experiment3;

import java.util.*;

class Perm{
  private static void nxpm(int[] z,int n,int r){
    int i,t,k;
    for(i=r-1; i>=0; i--) if(i<n-1){t=z[i];
      for(k=i+1; k<n; k++) if(z[i]<z[k])break;
      if(k<n){z[i]=z[k]; z[k]=t; return;
      } else {for(k=i; k<n-1; k++){z[k]=z[k+1];} z[k]=t;
      }
    }
    return;
  }
  public static int[][] perm(int n,int r){
    int i,c=1; for(i=n-r+1; i<=n; i++) c*=i;
    int[] z=new int[n];
    int[][] p=new int[c][r];
    for(i=0; i<n; i++){z[i]=i+1; if(i<r) p[0][i]=i+1;}
    for(i=1; i<c; i++){
      nxpm(z,n,r); for(int j=0; j<r; j++) p[i][j]=z[j];
    }
    return p;
  }
  public static void main(String[] argv){
    int[][] p=perm(6,2);
    for(int i=0;i<p.length;i++){
      System.out.print("<"+(i+1)+">=");
      System.out.println(Arrays.toString(p[i]));
    }
  }
}