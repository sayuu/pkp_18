package plugin.frequency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import keywordProgramming.KeywordProgramming;
import keywordProgramming.Type;

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
public class GetFrequencyJob extends Job {
	
	private static HashMap<String, Integer> function_frequency = new HashMap<String, Integer>();
	private ICompilationUnit icu;
	private CompilationUnit cu;
	private String source;
	private ExecutionEvent event;
	private IStructuredSelection iss;
	private int numOfCompilationUnits;
	private int numOfWorkedCompilationUnits;
	private IProgressMonitor monitor;

	//現在のキーワードプログラミングでは正解を出すことができないタスクを排除して一括追加する。
	
	public GetFrequencyJob(ExecutionEvent event, IStructuredSelection iss, int numOfCompilationUnits, int heightOfFunctionTree) {
		super("タスクの追加");
		// TODO Auto-generated constructor stub
		this.event = event;
		this.iss = iss;
		this.numOfCompilationUnits = numOfCompilationUnits;
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
		
		function_frequency.clear();
		
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
		
		//結果表示
		printFrequency();
		
		return;
	}

	class ASTVisitorImpl extends ASTVisitor {

	    /*
	     * メソッドコール
	     */
	    public boolean visit(MethodInvocation node) {
	    	//DBString にして、hash(DBString, count)に入れる。 	
	     	
	    	String parentClass = node.resolveMethodBinding().getDeclaringClass().getBinaryName();
 			List<String> list =  AstUtil.iMethodBindingToDbStringStatic1(node.resolveMethodBinding(), parentClass, false, false, false);
 			for(String dbString:list){
 				if(function_frequency.containsKey(dbString)){
 					int freq = function_frequency.get(dbString);
 					function_frequency.put(dbString, ++freq);
 				}else{
 					function_frequency.put(dbString, 1);
 				}
 			}
	        return super.visit(node);
	    }
	    
	    /*
	     * new でのインスタンス生成
	     */
	    public boolean visit(ClassInstanceCreation node) {
	    	//DBString にして、hash(DBString, count)に入れる。 	
 	
	    	String parentClass = node.resolveConstructorBinding().getDeclaringClass().getBinaryName();
 			List<String> list =  AstUtil.iMethodBindingToDbStringStatic1(node.resolveConstructorBinding(), parentClass, false, false, false);
 			for(String dbString:list){
 				if(function_frequency.containsKey(dbString)){
 					int freq = function_frequency.get(dbString);
 					function_frequency.put(dbString, ++freq);
 				}else{
 					function_frequency.put(dbString, 1);
 				}
 			}
	        return super.visit(node);
	    }
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
			
			//CompilationUnit の中身を調べる。
			getFrequencyFromCompilationUnit();
			
		    cu.accept(new ASTVisitorImpl());
		    numOfWorkedCompilationUnits++;
			monitor.worked(numOfWorkedCompilationUnits);
			
			monitor.setTaskName(numOfWorkedCompilationUnits + "(終了クラス数)/" + numOfCompilationUnits + "（全クラス数）");
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	}

	private void printFrequency() {
		//TreeMapの先頭から順番に画面表示する
		
		//比較のためのクラスを指定して TreeMap を生成
		TreeMap<String, Integer> treeMap =
		     new TreeMap<String, Integer>(new IntegerMapComparator(function_frequency));
		// TreeMap に全部の組をコピー(このときにソートされる)
		treeMap.putAll(function_frequency);
		// TreeMap の表示
		Set<String> set = treeMap.keySet();  // ソートされている
		Iterator<String> it = set.iterator();
		
		//System.out.println("=== start ===");
		while (it.hasNext()) {
		    String key = it.next();
		    int value = treeMap.get(key);
		    System.out.println(value + "," + key);
		}
		//System.out.println("=== end ===");
	}
	
	private void getFrequencyFromCompilationUnit(){

//		cu.get
//		int offset = cu.getExtendedStartPosition(node);
//		int selected_length = cu.getExtendedLength(node);
//		
//        int selected_line_start = cu.getLineNumber(offset);
//        int selected_line_end = cu.getLineNumber(offset + selected_length);
//        
		 //現在選択中のクラス名（パッケージ名も）を取得
		String class_name = null;
        try {
        	if(icu.getTypes().length > 0)
        		class_name = icu.getTypes()[0].getFullyQualifiedParameterizedName();
//        	else
//        		System.out.println("");
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
    	List<String> classesInActiveEditor = null ; //new ArrayList<String>(KeywordProgramming.getImportedTypes());
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
      
        TestSite ts = new TestSite(class_name, offset, offset, selected_line_start, selected_line_end, selected_length, selected_text, desiredReturnType, location, classesInActiveEditor, functionsInActiveEditor, false);
		ts.setASTNode(node);
		
		if(ts.isSelectedFunctionWithXtimesFrequency(1)){
			System.out.println(ts.getId());
			System.out.println("freq>=1:" + ts.isSelectedFunctionWithXtimesFrequency(1));
			System.out.println("freq>=3:" + ts.isSelectedFunctionWithXtimesFrequency(3));
			System.out.println("freq>=5:" + ts.isSelectedFunctionWithXtimesFrequency(5));
			
			ts.printFunctionList();
		}
		
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
        
	}

}
