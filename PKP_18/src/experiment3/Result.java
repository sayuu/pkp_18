package experiment3;

public class Result implements Comparable<Result>{
	
	public String fSelectedString;
	public int fAnswerOrder;
	public String fTestSiteId;
	
	int fNumOfKeywords;
	int fNumOfLocalFunctions;
	int fNumOfLocalTypes;
	
	public Result(String testSiteId, String selectedString, int answerOrder, int numOfKeywords,int numOfLocalTypes, int numOfLocalFunctions){
		fTestSiteId = testSiteId;
		fSelectedString = selectedString;
		fAnswerOrder = answerOrder;
		fNumOfKeywords = numOfKeywords;
		fNumOfLocalTypes = numOfLocalTypes;
		fNumOfLocalFunctions = numOfLocalFunctions;
	}

	@Override
	public int compareTo(Result arg0) {
		// TODO 自動生成されたメソッド・スタブ
		int num = this.fSelectedString.compareTo(arg0.fSelectedString);
		if(num != 0)
			return num;
		else{
			int diff = this.fAnswerOrder - arg0.fAnswerOrder;
			return diff;
		}
	}
}
