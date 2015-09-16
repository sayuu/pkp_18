package ast;

import java.util.ArrayList;
import java.util.List;

import keywordProgramming.KeywordProgramming;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;

/**
 * 型情報を
 * 現在編集中の
 * エディター、パッケージ、プロジェクトなどから
 * インポートするためのメソッド群
 * 
 * @author sayuu
 *
 */
public class Import {

	   

	//インポート文に存在する有効な型を入れるリスト
	public static List<String> classesInImports = new ArrayList<String>();
	//インポート文に存在する有効な関数を入れるリスト
	public static List<String> functionsInImports = new ArrayList<String>();
	
	//現在エディタで編集中のコードから取得する型と関数
	public static List<String> classesInLocalCode = new ArrayList<String>();
	public static List<String> functionsInLocalCode = new ArrayList<String>();
	
	//現在エディタで編集中のクラスが所属するパッケージから取得する型と関数
	public static List<String> classesInPackage = new ArrayList<String>();
	public static List<String> functionsInPackage = new ArrayList<String>();

	//現在エディタで編集中のクラスが所属するプロジェクトから取得する型と関数
	public static List<String> classesInProject = new ArrayList<String>();
	public static List<String> functionsInProject = new ArrayList<String>();
		
	public static void clearImportDeclaration(){
		classesInImports.clear();
		functionsInImports.clear();
	}
	
	/*
	 * インポート文のみ、
	 * 現在選択しているファイルが変化した際に取得する。
	 */
//	private static IWorkbenchPart currentSourcepart = null;//現在選択しているソース
	private static String  currentSourcepartTitle = null;//現在選択しているソースのタイトル
	private static String  currentPackageName = null;//現在選択しているソースのパッケージ名
	
	public static ISelectionListener listener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
        	
        	if (selection instanceof ITextSelection) {
        		String title = sourcepart.getTitle();
        		//nullまたは現在選択しているソースのタイトルが変化した
            	if(currentSourcepartTitle == null || !currentSourcepartTitle.equals(title)){
            		currentSourcepartTitle = title;
            		
            		//インポート文を取得する。
            		Import.clearImportDeclaration();
            		Import.getImportDeclaration(null);		
            		
            		
            		CompilationUnit cu = Import.getCompilationUnitOfActiveEditor();
            		if(cu == null)
            			return;
            		String packageName = cu.getPackage().getName().getFullyQualifiedName();
            		//パッケージ名が変化した.
            		if(currentPackageName == null || !currentPackageName.equals(packageName)){
            			currentPackageName = cu.getPackage().getName().getFullyQualifiedName();
            		}
            		
            	}
    		}
        	
        
        	
        }
    };
 
	/*
	 * インポート文の読み込み
	 * 
	 * 自分が所属しているパッケージ(プロジェクト)のクラスも取る。
	 */
	public static void getImportDeclaration(CompilationUnit cu){
//		long start = System.currentTimeMillis();
		
		if(cu == null){
			cu = getCompilationUnitOfActiveEditor();
			if(cu == null)
				return;
		}
		
		getImportDeclarationFromCompilationUnit(cu);
		
	
		AstUtil.compactTypeDBstring(classesInImports);
		AstUtil.compactFunctionDBstring(functionsInImports);
		
		getImportDeclarationFromOtherPlaces(cu);
		
		/*
		 * imported_typesとimported_fucntionsを
		 * クリアしてから、新しく追加する。
		 * 同時に、types とfunctionsにも追加される。
		 */
		
//		//typesとfunctionsをクリアする。
//		KeywordProgramming.clearStaticTypeAndFunctionFields();
//		//imported_typesとimported_fucntionsをクリアする。
//		KeywordProgramming.clearImportStaticFields();
//		
//		//original分を追加する。
//		KeywordProgramming.putAllOriginalStaticFields();
//		
//		//import分を imported_typesとimported_fucntionsに追加する。
//		//typesとfunctionsにも同時に追加される。
//		KeywordProgramming.setImportedClassAndFunction(classesInImports, functionsInImports);
		
//		long stop = System.currentTimeMillis();
//		System.out.println("  実行にかかった時間= " + (stop-start) + " ミリ秒。AstLocalCode.getImportDeclaration");
	}

	/**
	 * cuのImportDeclarationから情報を取得する。
	 * @param cu
	 */
	public static void getImportDeclarationFromCompilationUnit(CompilationUnit cu) {
		List<ImportDeclaration> cu_imports = cu.imports();
		for(ImportDeclaration impd: cu_imports){
			if (impd.isOnDemand() == false){
				//とりあえず、まとめてimportの".*"は抜き。
				Name name = impd.getName();
				ITypeBinding itb = name.resolveTypeBinding();
				if(itb != null){
					//staticのimportはitbがnullになるので、とりあえず無視。
//					AstUtil.getImportDeclarationSuperTypes(impd, classesInImports, functionsInImports);
					String className = itb.getBinaryName();
					AstUtil.createFunctionDbString(itb, className, functionsInImports, true, false);
					
					/*
					 * classNameは同じで、
					 * その関数だけを
					 * スーパータイプのものを取得する。
					 */
					for(ITypeBinding super_itb: AstUtil.getSuperTypes(itb)){
						AstUtil.createFunctionDbString(super_itb, className, functionsInImports, true, false);
						
					}
					
					
					List<List<String>> pairs = new ArrayList<List<String>>();
					AstUtil.getParentChildPairsLists(itb, pairs);
					AstUtil.getImportDeclarationSuperTypesAndInterfaces(pairs, classesInImports);
				}
			}else{
				//まとめてimportのとき。".*"
				Name name = impd.getName();
				IJavaElement javaElement = cu.getJavaElement();
				ICompilationUnit icu = (ICompilationUnit) javaElement;
				IJavaProject ijp = icu.getJavaProject();
				String packageName = name.getFullyQualifiedName();
				try {
					getAllClassesFromPackage(ijp, packageName);
				} catch (JavaModelException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * エディタで編集している以外の、
	 * 他の場所からのインポート
	 * @param cu
	 */
	public static void getImportDeclarationFromOtherPlaces(CompilationUnit cu) {
		//他の場所からのインポート
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String place_of_import_types = store.getString(PreferenceInitializer.IMPORT_TYPES_FROM);
		
		//自クラスのみからインポートのとき。
		if(place_of_import_types.equals(PreferenceInitializer.IMPORT_TYPES_FROM_MY_CLASS))
			return;
		
		
		IJavaElement javaElement = cu.getJavaElement();
		ICompilationUnit icu = (ICompilationUnit) javaElement;
		IJavaProject ijp = icu.getJavaProject();
		
		/*
		 * 自分が所属しているパッケージ中で定義されているクラス群から取る。
		 */
		if(place_of_import_types.equals(PreferenceInitializer.IMPORT_TYPES_FROM_MY_PACKAGE)){

			try {
				if(icu.getPackageDeclarations() != null && icu.getPackageDeclarations().length > 0){
					IPackageDeclaration ipd = icu.getPackageDeclarations()[0];
					String myPackageName = ipd.getElementName();
					getAllClassesFromPackage(ijp, myPackageName);
				}
			} catch (JavaModelException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
			return;
		}
		
		/*
		 * 自分が所属しているプロジェクト中で定義されているクラス群から取る。
		 */
		try {
			getAllClassesFromProject(ijp);
		} catch (JavaModelException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * 現在エディタ上で編集中のCompilationUnitを取得する
	 * 
	 * @return cu
	 */
	public static CompilationUnit getCompilationUnitOfActiveEditor() {
		CompilationUnit cu;
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		if(page == null)
			return null;
		
		IEditorPart editorPart = page.getActiveEditor();
		IEditorInput editorInput = editorPart.getEditorInput();
		ICompilationUnit icu = (ICompilationUnit) editorInput.getAdapter(IJavaElement.class);
		
		//javaのソースコードでない場合は抜ける。
		if(icu == null)
			return null;
		
		//現在編集中のソースファイル(1つのクラス)のASTを作成する
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(icu);
		parser.setResolveBindings(true);//Bindingsのセット
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		ASTNode rootAstNode = parser.createAST(new NullProgressMonitor());//この rootAstNode は ASTNode.COMPILATION_UNIT である。
		cu = (CompilationUnit)rootAstNode;
		return cu;
	}
	
	/**
	 * 指定 packageName 以下にある
	 * クラス情報を取得する
	 * 
	 * @param ijp
	 * @param packageName
	 * @throws JavaModelException
	 */
	private static void getAllClassesFromPackage(IJavaProject ijp, String packageName) throws JavaModelException{

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // Get all projects in the workspace
        IProject project = root.getProject(ijp.getElementName());
        IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
        for (IPackageFragment mypackage : packages) {
        	if(mypackage.getElementName().equals(packageName))
            if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
            	ICompilationUnit []icu_arr = mypackage.getCompilationUnits();
				for(ICompilationUnit icu2: icu_arr){
					if(icu2.getTypes().length > 0){
						IType type = icu2.getTypes()[0];
						String className = type.getFullyQualifiedParameterizedName();
						classesInImports.add(className);
						AstUtil.createFunctionDbString(type, className, functionsInImports, true, false);
						
						/*
						 * classNameは同じで、
						 * その関数だけを
						 * スーパータイプのものを取得する。
						 */
	//					for(ITypeBinding super_itb: AstUtil.getSuperTypes(itb)){
	//						AstUtil.createDbString(super_itb, className, functionsInImports, true);
	//						
	//					}
						
						
						List<List<String>> pairs = new ArrayList<List<String>>();
						AstUtil.getParentChildPairsLists(type, pairs);
						AstUtil.getImportDeclarationSuperTypesAndInterfaces(pairs, classesInImports);
					}
				}
            }

		}
	}
	
	
	/**
	 * 指定 パッケージ ijp 以下にある
	 * クラス情報を取得する
	 * 
	 * @param ijp
	 * @throws JavaModelException
	 */
	private static void getAllClassesFromProject(IJavaProject ijp) throws JavaModelException{

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // Get all projects in the workspace
        IProject project = root.getProject(ijp.getElementName());
        IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
        for (IPackageFragment mypackage : packages) {
            if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE || mypackage.getKind() == IPackageFragmentRoot.K_BINARY) {
            	ICompilationUnit []icu_arr = mypackage.getCompilationUnits();
				for(ICompilationUnit icu2: icu_arr){
					IType type = icu2.getTypes()[0];
					String className = type.getFullyQualifiedParameterizedName();
					classesInImports.add(className);
					AstUtil.createFunctionDbString(type, className, functionsInImports, true, false);
					
					/*
					 * classNameは同じで、
					 * その関数だけを
					 * スーパータイプのものを取得する。
					 */
//					for(ITypeBinding super_itb: AstUtil.getSuperTypes(itb)){
//						AstUtil.createDbString(super_itb, className, functionsInImports, true);
//						
//					}
					
					
					List<List<String>> pairs = new ArrayList<List<String>>();
					AstUtil.getParentChildPairsLists(type, pairs);
					AstUtil.getImportDeclarationSuperTypesAndInterfaces(pairs, classesInImports);
				}
            }

		}
	}
/*
 * 	ijp.getAllPackageFragmentRoots();
 *
 *	から取ろうとすると、
 * まだエラーがでていて未完成のソースコードを含むパッケージの中身を取ることができない。
 * 
 * ICompilationUnit []icu_arr = ipf.getCompilationUnits();
 * 
 * icu_arrが、空の配列になる。
 * 
 *
 /	
//	private static void getAllClassesFromPackage(IJavaProject ijp, String packageName) throws JavaModelException{
//		IPackageFragmentRoot []roots = ijp.getAllPackageFragmentRoots();
//		for(IPackageFragmentRoot root: roots){
//			if (root.getKind() == IPackageFragmentRoot.K_SOURCE){//srcフォルダを見る。
//				for(IJavaElement je: root.getChildren()){//srcフォルダの中のパッケージを見る。
//					IPackageFragment ipf = (IPackageFragment)je;
//					if(ipf.getElementName().equals(packageName)){//指定したパッケージのみ追加。
//						ICompilationUnit []icu_arr = ipf.getCompilationUnits();
//						
////						IJavaElement j_c[] = ipf.getChildren();
////                    	Object o[] = ipf.getNonJavaResources();
////                        for(IJavaElement e: j_c){
////                        	System.out.println(e);
////                        }
////                            
////                        for(Object b:o){
////                        	System.out.println(b);
////                        }
//                        
//						for(ICompilationUnit icu2: icu_arr){
//							IType type = icu2.getTypes()[0];
//							String className = type.getFullyQualifiedParameterizedName();
//							classesInImports.add(className);
//							AstUtil.createFunctionDbString(type, className, functionsInImports, true);
//							
//
//							/*
//							 * classNameは同じで、
//							 * その関数だけを
//							 * スーパータイプのものを取得する。
//							 */
////							for(ITypeBinding super_itb: AstUtil.getSuperTypes(itb)){
////								AstUtil.createDbString(super_itb, className, functionsInImports, true);
////								
////							}
//							
//							
//							List<List<String>> pairs = new ArrayList<List<String>>();
//							AstUtil.getParentChildPairsLists(type, pairs);
//							AstUtil.getImportDeclarationSuperTypesAndInterfaces(pairs, classesInImports);
//						}
//						IClassFile[] classes = ipf.getClassFiles();
//						for(IClassFile cf: classes){
//							
//						}
//						return;
//					}
//				}
//			}
//		}
//	}
//	
//	private static void getAllClassesFromProject(IJavaProject ijp) throws JavaModelException{
//		IPackageFragmentRoot []roots = ijp.getAllPackageFragmentRoots();
//		for(IPackageFragmentRoot root: roots){
//			if (root.getKind() == IPackageFragmentRoot.K_SOURCE){//srcフォルダを見る。
//				for(IJavaElement je: root.getChildren()){//srcフォルダの中のパッケージを見る。
//					IPackageFragment ipf = (IPackageFragment)je;
//					
//					ICompilationUnit []icu_arr = ipf.getCompilationUnits();
//					for(ICompilationUnit icu2: icu_arr){
//						
//						IType type = icu2.getTypes()[0];
//						String className = type.getFullyQualifiedParameterizedName();
//						classesInImports.add(className);
//						AstUtil.createFunctionDbString(type, className, functionsInImports, true);
//
//						List<List<String>> pairs = new ArrayList<List<String>>();
//						AstUtil.getParentChildPairsLists(type, pairs);
//						AstUtil.getImportDeclarationSuperTypesAndInterfaces(pairs, classesInImports);
//					}
//				}
//			}
//		}
//	}
	
//	private static void getAllClassesFromProject(IJavaProject ijp) throws JavaModelException{
//		IPackageFragmentRoot []roots = ijp.getAllPackageFragmentRoots();
//		for(IPackageFragmentRoot root: roots){
//			if (root.getKind() == IPackageFragmentRoot.K_SOURCE){//srcフォルダを見る。
//				for(IJavaElement je: root.getChildren()){//srcフォルダの中のパッケージを見る。
//					IPackageFragment ipf = (IPackageFragment)je;
//					ICompilationUnit []icu_arr = ipf.getCompilationUnits();
//					for(ICompilationUnit icu2: icu_arr){
//						
//						IType type = icu2.getTypes()[0];
//						String className = type.getFullyQualifiedParameterizedName();
//						classesInImports.add(className);
//						AstUtil.createDbString(type, className, functionsInImports, true);
//					}
//				}
//			}
//			
//			//java.lang パッケージの取得。
//			/*
//			if(root.getElementName().equals("rt.jar")){
//				for(IJavaElement je: root.getChildren()){
//					IPackageFragment ipf = (IPackageFragment)je;
//					IClassFile[] classes = ipf.getClassFiles();
//					for(IClassFile cf: classes){
//						IType type = cf.getType();
//						String className = type.getFullyQualifiedParameterizedName();
//						
//						if(className.startsWith("java.lang.")){
//
//							System.out.println(className);
//							List<String> list = new ArrayList<String>();
//							AstUtil.createDbString(type, className, list, true);
//							for(String s :list){
//								System.out.println(s);
//							}
//						}
//					}
//				}
//				
//			}
//			*/
//		}
//	}
	
}
