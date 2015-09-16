package plugin.handlers.saveTestSite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import keywordProgramming.KeywordProgramming;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;

import ast.AstLocalCode;
import ast.AstUtil;
import ast.Import;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
/*
 * 選択したエレメント中のテストサイトを全てタスクとして追加、保存するクラス。
 * 
 */
public class SaveSelectedSiteJob extends Job {
	
	private List<ASTNode> node_list = new ArrayList<ASTNode>();
	
	private ICompilationUnit icu;
	private CompilationUnit cu;
	private String source;
	private ExecutionEvent event;
	private int numOfSites;
	private IStructuredSelection iss;
	private int numOfCompilationUnits;
	private int numOfWorkedCompilationUnits;
	private IProgressMonitor monitor;

	//現在のキーワードプログラミングでは正解を出すことができないタスクを排除して一括追加する。
	
	//匿名クラス定義 除外
	private boolean flg_exculde_anonymous_class_declaration;
	//出力関数木の引数に定数が含まれている　除外 
	private boolean flg_exculde_literals;
	//関数のラベルについて、連続して大文字が続くものが存在するとき。除外
	private boolean flg_exculde_contiguous_upper_cases;
	//ロケーションが型キャスト（ダウンキャスト）のタスクを除外.
	private boolean flg_exculde_type_down_cast;

	private boolean flg_exculde_all_false = false;

	//関数木の最大の高さ
	private int max_hight_function_tree;
	
	public SaveSelectedSiteJob(ExecutionEvent event, IStructuredSelection iss, int numOfCompilationUnits, int heightOfFunctionTree) {
		super("タスクの追加");
		// TODO Auto-generated constructor stub
		this.event = event;
		this.iss = iss;
		this.numOfCompilationUnits = numOfCompilationUnits;
		this.max_hight_function_tree = heightOfFunctionTree;
		
		//プリファレンスで定めた値のセット
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		flg_exculde_anonymous_class_declaration = store.getBoolean(PreferenceInitializer.TASK_IMPORT_EXCLUDE_ANONYMOUS_CLASS_DECLARATION);
		flg_exculde_contiguous_upper_cases = store.getBoolean(PreferenceInitializer.TASK_IMPORT_EXCLUDE_CONTIGUOUS_UPPER_CASES);
		flg_exculde_literals = store.getBoolean(PreferenceInitializer.TASK_IMPORT_EXCLUDE_LITERALS);
		if(!flg_exculde_anonymous_class_declaration && !flg_exculde_contiguous_upper_cases && !flg_exculde_literals)
			flg_exculde_all_false = true;
		
		flg_exculde_type_down_cast = store.getBoolean(PreferenceInitializer.TASK_IMPORT_EXCLUDE_TYPE_DOWN_CAST);
		
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		this.monitor = monitor;
		monitor.beginTask("タスクの追加処理中", numOfCompilationUnits);
		try {
			execute();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//キャンセルされた
		if (monitor.isCanceled()) {
		    return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}
	
	private void execute() throws ExecutionException{
		this.numOfSites = 0;
		Iterator it1 = iss.iterator();
		//タスクの保存
		while(it1.hasNext()){
			//ユーザーが処理をキャンセルした
			if(monitor.isCanceled()){
				return;
			}
			Object selectionObject = it1.next();
			if (!(selectionObject instanceof IJavaElement))
				continue;
			IJavaElement javaElement = (IJavaElement) selectionObject;
			switch(javaElement.getElementType()){
				case IJavaElement.COMPILATION_UNIT:
					icu = (ICompilationUnit) javaElement;
					saveICompilationUnit(icu);
					break;
				case IJavaElement.PACKAGE_FRAGMENT:
					IPackageFragment pf = (IPackageFragment) javaElement;
					saveIPackageFragment(pf);
					break;
				case IJavaElement.PACKAGE_FRAGMENT_ROOT:
					IPackageFragmentRoot pfr = (IPackageFragmentRoot) javaElement;
					saveIPackageFragmentRoot(pfr);
					break;
				case IJavaElement.JAVA_PROJECT:
					IJavaProject p = (IJavaProject) javaElement;
					try {
						IPackageFragmentRoot []root_arr = p.getPackageFragmentRoots();
						for(IPackageFragmentRoot root: root_arr){
							saveIPackageFragmentRoot(root);
						}
					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					break;
			}
		}
		
		return;
	}

	class ASTVisitorImpl extends ASTVisitor {
	    public void preVisit(ASTNode node) {
//	    	System.out.println("Type: " + ASTNode.nodeClassForType(node.getNodeType()));
//	    	System.out.println(node);
	    }
	    
	    /*
	     * メソッドコール
	     */
	    public boolean visit(MethodInvocation node) {
	    	
	    	//Enumは取得しない。処理できない。
	    	if(isChildOfEnumDeclaration(node))
	    		return super.visit(node);
	    	
//	    	System.out.println(node);
//	    	System.out.println(node.getLocationInParent());
	    	
	    	boolean exculde = false;
	    	if(!flg_exculde_all_false)//どれか１つでも除外が存在する。
	    			exculde = checkExclude(node);
//	    	System.out.println(existLiterals);
	    	
	    	if(!exculde && !isInInnerClass(node)){
	    		//saveTestsite(node);
	    		node_list.add(node);
	    	}
	        return super.visit(node);
	    }
	    
	    /*
	     * new でのインスタンス生成
	     */
	    public boolean visit(ClassInstanceCreation node) {
	    	
	    	//Enumは取得しない。処理できない。
	    	if(isChildOfEnumDeclaration(node))
	    		return super.visit(node);
	    	
//	    	System.out.println(node);
//	    	System.out.println(node.getLocationInParent());
	    	boolean exculde = false;
	    	if(!flg_exculde_all_false)//どれか１つでも除外が存在する。
	    			exculde = checkExclude(node);
//	    	System.out.println(existLiterals);
	    	
	    	if(!exculde && !isInInnerClass(node)){
//	    		saveTestsite(node);
	    		node_list.add(node);
	    	}
	        return super.visit(node);
	    }
	}
	
	/*
	 * Enumチェック
	 * 
	 */
	private boolean isChildOfEnumDeclaration(ASTNode node){
		//クラス宣言を取得する。
		while(!(node instanceof TypeDeclaration) && node != null){
			//enumはnullが帰ってきてしまう！！　EnumDeclaration　をするべきか。 だめだ。resolveBinding　がない。
			node = node.getParent();
		}
		
		if(node == null)
			return true;
		else
			return false;
	}
	
	
	public int getNumOfSites(){
		return numOfSites;
	}
	
	private void saveIPackageFragmentRoot(IPackageFragmentRoot pfr) {
		//ユーザーが処理をキャンセルした
		if(monitor.isCanceled()){
			return;
		}
		try {
			if (pfr.getKind() == IPackageFragmentRoot.K_SOURCE){
				for(IJavaElement je: pfr.getChildren()){
					saveIPackageFragment((IPackageFragment)je);
				}
				
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveIPackageFragment(IPackageFragment pf) {
		//ユーザーが処理をキャンセルした
		if(monitor.isCanceled()){
			return;
		}
		try {
			ICompilationUnit []icu_arr = pf.getCompilationUnits();
			for(ICompilationUnit icu: icu_arr){
				this.icu = icu;
				saveICompilationUnit(icu);
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveICompilationUnit(ICompilationUnit icu){
		//ユーザーが処理をキャンセルした
		if(monitor.isCanceled()){
			return;
		}
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
	    parser.setResolveBindings(true);
	    parser.setSource(icu);
	    try {
			source = icu.getSource();
			cu = (CompilationUnit) parser.createAST(new NullProgressMonitor());
			
			//使用前にクリアする。
			Import.clearImportDeclaration();
			Import.getImportDeclaration(cu);
			
		    cu.accept(new ASTVisitorImpl());
		    int count = 1;
		    for(ASTNode node: node_list){
		    	//ユーザーが処理をキャンセルした
				if(monitor.isCanceled()){
					return;
				}
		    	saveTestsite(node);
		    	String className = AstUtil.getParentTypeNameWithPackageName(node);
				monitor.setTaskName(count +"(現在のクラスの終了タスク数)/"+ node_list.size() + "(現在のクラスの全タスク数), " + numOfWorkedCompilationUnits + "(終了クラス数)/" + numOfCompilationUnits + "（全クラス数）, 合計終了タスク数= " + numOfSites + "\nクラス名: " + className);
				count++;
		    }
		    node_list.clear();
		    numOfWorkedCompilationUnits++;
			monitor.worked(numOfWorkedCompilationUnits);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	}
	
	private void saveTestsite(ASTNode node){
		//使用前にクリアする。
//		AstLocalCode.clearImportDeclaration();
//		AstLocalCode.getImportDeclaration(cu);
		
		String selected_text = node.toString();
		int height = checkHeight(node);
		
		//指定高さ以上のタスクを除外
		if(height > max_hight_function_tree){
			return;
		}
		
		//System.out.println(height + ": " + selected_text);
		
		
//		int offset = node.getStartPosition();
//		int selected_length = node.getLength();
		
		int offset = cu.getExtendedStartPosition(node);
		int selected_length = cu.getExtendedLength(node);
		
        int selected_line_start = cu.getLineNumber(offset);
        int selected_line_end = cu.getLineNumber(offset + selected_length);
        
        //望ましい返り値の型
    	String desiredReturnType = null;
    	//ロケーション
    	String location = null;
    	//現在エディタ内に存在する有効なTypeを入れるリスト
    	List<String> classesInActiveEditor = null; //new ArrayList<String>(KeywordProgramming.getImportedTypes());
    	//現在エディタ内に存在する有効な関数を入れるリスト
    	List<String> functionsInActiveEditor = null; //new ArrayList<String>(KeywordProgramming.getImportedFunctions());
    	
        AstLocalCode.getLocalInfomation(source, offset, selected_length, true, icu, cu, node);
		desiredReturnType = AstLocalCode.getDesiredReturnType();
		location = AstLocalCode.getLocation();
		classesInActiveEditor.addAll(AstLocalCode.getClasses());
		functionsInActiveEditor.addAll(AstLocalCode.getFunctions());

		AstLocalCode.clear();//使った後はクリアする。
		
		 //現在選択中のクラス名（パッケージ名も）を取得
		String class_name = null;
        try {
			class_name = icu.getTypes()[0].getFullyQualifiedParameterizedName();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //キャストのタスクを除外する。 exculde=on && 文字列が"型キャスト"に等しい
        if(flg_exculde_type_down_cast && location.equals("型キャスト"))
        	return;
        
        TestSite ts = new TestSite(class_name, offset, offset, selected_line_start, selected_line_end, selected_length, selected_text, desiredReturnType, location, classesInActiveEditor, functionsInActiveEditor, false);
		ts.setASTNode(node);
		
//		for(String m:ts.methodList){
//			System.out.println(m);
//		}
//		if(ts.isSelectedFunctionWithXtimesFrequency(1)){
//			System.out.println(ts.getId());
//			System.out.println("freq>=1:" + ts.isSelectedFunctionWithXtimesFrequency(1));
//			System.out.println("freq>=3:" + ts.isSelectedFunctionWithXtimesFrequency(3));
//			System.out.println("freq>=5:" + ts.isSelectedFunctionWithXtimesFrequency(5));
//			
//			ts.printFunctionList();
//		}
		
//		if(ts.isFunctionWithXtimesFrequency(1)){
//			System.out.println("all freq>=1:" + ts.isFunctionWithXtimesFrequency(1));
//			System.out.println("all freq>=3:" + ts.isFunctionWithXtimesFrequency(3));
//			System.out.println("all freq>=5:" + ts.isFunctionWithXtimesFrequency(5));
//			
//			ts.printSimpleNameList();
//			ts.printFunctionList();
//		}
		//ファイルを作成する。
		ts.createNewFile();
		numOfSites++;
        
	}

	/*
	 * ３つの除外対象タスクに当てはまるかをチェックする。
	 * 1
	 * nodeの引数に定数が含まれているかチェックする。
	 * 引数は再帰的に一番下の子まで見る。
	 * 定数はKPでは生成できないので除外したいときに用いる。
	 * 
	 * 2
	 * 大文字が連続するラベルが存在するかをチェックする。isUpperCaseContiguous を使用。
	 * 
	 * 3
	 * 匿名クラス定義 であるかをチェックする。引数がClassInstanceCreationのときのみ。
	 * 
	 */
	boolean checkExclude(MethodInvocation mi){
		
		//引数
		for(Object e: mi.arguments()){
			int num = checkExcludeInternal(e);
			if(num == 1)
				return true;
		}
		
		//レシーバ
		Expression e = mi.getExpression();
		int num = checkExcludeInternal(e);
		if(num == 1)
			return true;
		else if(num == -1)
			return false;
		
		return false;
	}
	
	boolean checkExclude(InfixExpression ie){
		List<Expression> e_list = new ArrayList<Expression>();
		e_list.add(ie.getLeftOperand());
		e_list.add(ie.getRightOperand());
		
		for(Expression e: e_list){
			int num = checkExcludeInternal(e);
			if(num == 1)
				return true;
		}
		return false;
	}

	boolean checkExclude(PrefixExpression pe){
		int num = checkExcludeInternal(pe.getOperand());
			if(num == 1)
				return true;
		return false;
	}
	
	boolean checkExclude(ClassInstanceCreation cic){
		//匿名クラス定義は扱うことができない。
		if(flg_exculde_anonymous_class_declaration)
		if(cic.getAnonymousClassDeclaration() != null){
//			System.out.println("匿名クラスです。");
			return true;
		}
		
		for(Object e: cic.arguments()){
			int num = checkExcludeInternal(e);
			if(num == 1)
				return true;
		}
		return false;
	}

	/*
	 * checkExcludeの中で使うメソッド。
	 * 返り値
	 * 1 : true
	 * -1: false
	 * 0 : まだ判定できない。（ので次の引数へ進む。）
	 */
	private int checkExcludeInternal(Object e) {
		//1番下までもぐる。
		if(e instanceof MethodInvocation){
			MethodInvocation m = (MethodInvocation)e;
			if(checkExclude(m))
				return 1;
			else
				return -1;
		}else if(e instanceof ClassInstanceCreation){
			ClassInstanceCreation c = (ClassInstanceCreation)e;
			if(checkExclude(c))
				return 1;
			else
				return -1;
		}else if(e instanceof InfixExpression){
			// + とか - とかを付けるのはそもそもできない。
			return 1;
//			InfixExpression ie = (InfixExpression)e;
//			if(checkExclude(ie))
//				return 1;
//			else
//				return -1;
		}else if(e instanceof PrefixExpression){
			return 1;
			// + とか - とかを付けるのはそもそもできない。
//			PrefixExpression pe = (PrefixExpression)e;
//			if(checkExclude(pe))
//				return 1;
//			else
//				return -1;
		}else if(e instanceof ArrayAccess){
			//ArrayAccess aa = (ArrayAccess)e;
			return 1;//現行では配列には対応しない。
		}else if(e instanceof CastExpression){
			//アルゴリズム上キャストは生成できない。
			return 1;
		}else if(e instanceof InstanceofExpression){
			//instanceof表現は生成できない。
			return 1;
		}else if(e instanceof ThisExpression){
			//現在未対応
			return 1;
		}else if(e instanceof ParenthesizedExpression){
			// かっこ付きの式、
			// こんなのとか。　((DoubleData)d).getSampleRate()
			//表現できない。
			return 1;
		}
		
		//MethodInvocationでも、ClassInstanceCreationでも、InfixExpressionでもなく、もぐれない場合。
		if(flg_exculde_literals)
		if(e instanceof NumberLiteral || e instanceof CharacterLiteral || e instanceof NullLiteral || e instanceof BooleanLiteral || e instanceof StringLiteral || e instanceof TypeLiteral)
			return 1;
		
		//大文字が連続するラベルが存在するかをチェックする。
		if(flg_exculde_contiguous_upper_cases)
		if(e instanceof Expression){
			Expression ex = (Expression)e;
			String name = ex.toString();
			if(isUpperCaseContiguous(name))
				return 1;
		}
		return 0;
	}
	
	/*
	 * 大文字が連続して続くとき、
	 * trueを返す。 
	 */
	boolean isUpperCaseContiguous(String name){
		int count = 0;
		for(int i=0; i < name.length(); i++){
			char c = name.charAt(i);
			if(Character.isUpperCase(c)) {
				count++;
				if(count > 1){//2回以上大文字が連続
					return true;
				}
			}else{
				count = 0;
			}
		}
		return false;
	}
	
	/*
	 * 内部クラス宣言の中のノードか。
	 */
	boolean isInInnerClass(ASTNode node){
		//クラス宣言を取得する。
		do{
			if(node == null)
				return false;	//classではなくて、enum宣言の場合。
			node = node.getParent();
			
		}while(!(node instanceof TypeDeclaration));
		
		StructuralPropertyDescriptor spd = node.getLocationInParent();
		String id = spd.getId();
	/*
		普通の宣言
		ChildListProperty[org.eclipse.jdt.core.dom.CompilationUnit,types]
		内部クラスでの宣言
		ChildListProperty[org.eclipse.jdt.core.dom.TypeDeclaration,bodyDeclarations]
	*/
//		System.out.println(id);
		if(id.equals("bodyDeclarations"))
			return true;
		else 
			return false;
	}
	
	int checkHeight(Object o){
		if(o instanceof MethodInvocation){
			return checkHeight((MethodInvocation)o);
		}else if(o instanceof ClassInstanceCreation){
			return checkHeight((ClassInstanceCreation)o);
		}
		return 1;//自分自身
	}
	
	int checkHeight(MethodInvocation mi){
		//高さ 
		int height_me = 1;
		
		int height_left = 0;//レシーバの分
		
		//static の場合　レシーバの高さ0
		IMethodBinding imb = mi.resolveMethodBinding();
		int mod = imb.getModifiers();
		if(Modifier.isStatic(mod)){
			height_left = 0;
		}else{
			height_left = checkHeightL(mi);
		}
		
		
		//引数無いときは、自分自身＋レシーバ（左辺）
		int ret_h = height_me + height_left;
		
		//引数複数あるとき、どの引数の高さがmaxか。
		int max_height_right = 0;
		
		//引数
		for(Object e: mi.arguments()){
			int h = checkHeight(e);
			if(h > max_height_right)
				max_height_right = h;
		}
		
		if(max_height_right > height_left)
			ret_h = height_me + max_height_right;
		
		return ret_h;
	}
	
	int checkHeight(ClassInstanceCreation cic){
		int height = 1;//自分自身
		
		//引数
		for(Object e: cic.arguments()){
			int num = checkHeight(e);
			height += num;
		}
		
		return height;
	}
	
	//レシーバの高さを見る。
	int checkHeightL(MethodInvocation mi){
		int h = 1;
		Expression ex = mi.getExpression();
		
		if(ex == null)
			h = 0;
		if(ex instanceof MethodInvocation){
			h += checkHeightL((MethodInvocation)ex);
		}
		
		return h;
	}
	
}
