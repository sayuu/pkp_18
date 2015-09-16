package ast;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import ast.AstNodeLocationMapper.FindResult;

/**
 * ソースコードの周辺状況を取得する。
 */
public class AstLocalCode {

	//現在エディタ内に存在する有効な関数を入れるリスト
	private static List<String> classesInActiveEditor = new ArrayList<String>();
	//現在エディタ内に存在する有効な関数を入れるリスト
	private static List<String> functionsInActiveEditor = new ArrayList<String>();
	//ユーザーの入力キーワード
	private static String keywords;
	//望ましい返り値の型
	private static String desiredReturnType;
	//キーワードの開始offset
	private static int keyword_head_offset;
	//置換される文字列長
	private static int replacement_length;
	//現在選択中のクラス名（パッケージ名も）
    private static String class_name;
	private static int original_offset;	//元のoffset

    //ロケーション
  	/*
  	 * Return文 の中
	 * If文　の中
	 * 宣言文
	 * 代入文
	 * 地の文
	 * メソッドの引数の中
	 * など。
  	 */
  	private static String location;
  
    /*
     * エディタで開かれているクラス名（パッケージ名も）を取得する。
     */
	public static String getClassName() {
		return class_name;
	}
	
	public static List<String> getClasses(){
		return classesInActiveEditor;
	}
	
	public static List<String> getFunctions(){
		return functionsInActiveEditor;
	}
	
	public static String getKeywords(){
		return keywords;
	}
	
	public static String getDesiredReturnType(){
		return desiredReturnType;
	}
	
	public static int getKeywordHeadOffset(){
		return keyword_head_offset;
	}
	
	public static int getReplacementLength(){
		return replacement_length;
	}
	
	public static String getLocation(){
		return location;
	}
	
	public static int getOriginalOffset(){
		return original_offset;
	}
	
	//変数を全てクリアする。
	public static void clear(){
		classesInActiveEditor.clear();
		functionsInActiveEditor.clear();
		keywords = null;
		desiredReturnType = null;
		keyword_head_offset = -1;
		replacement_length = -1;
	}

    
	
	/**
	 * ソースコードの周辺状況を取得する。
	 * 具体的には、
	 * offset位置から、入力キーワードと返り値の型と、周辺の型(Type)と、関数(Function)を取得するメソッド 
	 * 
	 * @param source 1クラス分のソースコード
	 * @param offset 現在のキャレット位置
	 * @param isCompleteCode 完全なコードか、不完全（キーワード入力）か。
	 * @param cu	1クラス分のCompilationUnit (nullでもOK)
	 * @param nearestAstNode	現在のキャレット位置のAstNode (nullでもOK)
	 */
	public static void getLocalInfomation(String source, int offset, int selected_length, boolean isCompleteCode, ICompilationUnit icu, CompilationUnit cu, ASTNode nearestAstNode) {
		boolean flg_exist_cu = false;
		boolean flg_exist_astnode = false;
		if(cu != null){
			flg_exist_cu = true;
		}
		if(nearestAstNode != null){
			flg_exist_astnode = true;
		}
		String subS = source.substring(0, offset);
		int offsetLineStart = subS.lastIndexOf('\n') + 1;		//現在キャレットが存在する行の頭のoffset
		String line_head_to_caret = subS.substring(offsetLineStart, offset);//行頭からキャレット位置まで切り取った文字列
		
		/*
		 *  現在のキャレット位置にあるnearestAstNodeを取得する前に、前処理が必要。
		 *  ここでキーワードをソースから切り取っておく。
		 *
		 *  特に変数宣言などキーワード部分の前に"="がある場合は、
		 *  nearestAstNodeの内容がおかしくることが多い。
		 *
		 */
		original_offset = offset;//元のoffsetの保持
		//キーワード行に含まれる文字によって場合分け。
		if( isCompleteCode == false ){	//&& line_head_to_caret.indexOf("=") != -1 && line_head_to_caret.indexOf("!=") == -1){
			//"="を含むならば代入文であるとみなす。
			/*
			 * 例えば
			 *  int output = new integer input|
			 * という行があったときに、キャレット位置より前の
			 * "new integer input"をキーワードとみなして、消し、
			 * int output = が残る。
			 *
			 * また、キーワードを一つだけ残しておかないと
			 * nearestAstNodeに対して、
			 * getLastVariableDeclarationStatement()
			 * を使用した際に、代入文としてのASTNodeを取得できないらしい。
			 *
			 * 加えて、そのkeywordが"new"などの予約語の場合にも、
			 * 代入文としてのASTNodeを取得できないので、
			 *
			 * 以前は元のキーワードから一つだけ残して・・・とやっていたが、
			 * それも止めて、適当な文字列"ABC"を入れておいて
			 * 後から削除することにする。
			 *
			 * この後でICompilationUnitのcopy (getWorkingCopy)
			 * を使うことによって、
			 * 元のsourceには手をつけずに済むようにした。
			 * したがって後から元sourceの修正の必要は無くなった。
			 *
			 */
			int idx_equal = line_head_to_caret.lastIndexOf("=");
			/*
			 * キャレット位置(offset)から"="の位置(idx_equal)まで戻る。
			 * substringは+1しないと最後の文字含んでくれない。
			 *
			 * これで、sourceの初めから"="まで取れて、
			 * それをoffset以降のsourceとくっ付ける。
			 *
			 */
			String first_keyword = "ABC";
			// ["="までのsource] + "ABC" + [キャレット位置(offset)以降のsource]
			source = source.substring(0, offsetLineStart + idx_equal + 1) + first_keyword + source.substring(offset);
			//sourceの編集に合わせて、offsetも移動しなければならない。
			//削除したkeywordの分を削除して、"ABC"の分を追加する。
			offset = offset - (line_head_to_caret.length() - idx_equal) + 3;
		}

		//エディタ上のソースコード情報の取得

		// 現在エディタで編集中のソースファイルを取得する。
		/*
		 *  ASTは
		 *  ICompilationUnitからも、String からも
		 *  どちらからでも生成可能だが、
		 *  String から生成すると、
		 *  setResolveBindings(true) が機能しないので、
		 *  Bindingsが欲しい時は、ICompilationUnitから生成する必要がある。
		 *  詳しくは、setResolveBindings()のJavaDocを参照。
		 *
		 *　さらに、
		 *  じかに現在編集中のICompilationUnitを
		 *　編集せずに、getWorkingCopy して、
		 *　そのコピーに対して、
		 *　上で編集したsourceをセットして、
		 *  ASTを生成している。
		 *　
		 */
		if(icu == null){
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			IEditorPart editorPart = page.getActiveEditor();
			IEditorInput editorInput = editorPart.getEditorInput();
			icu = (ICompilationUnit) editorInput.getAdapter(IJavaElement.class);
		}
		//現在選択中のクラス名（パッケージ名も）の取得
		try {
			class_name = icu.getTypes()[0].getFullyQualifiedParameterizedName();
		} catch (JavaModelException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		ICompilationUnit icu_copy = null;
		IBuffer buffer = null;

		try {
			icu_copy = icu.getWorkingCopy(new NullProgressMonitor());
			buffer = icu_copy.getBuffer();
		} catch (JavaModelException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		buffer.setContents(source);

		//現在編集中のソースファイル(1つのクラス)のASTを作成する
		if(isCompleteCode == false || cu == null){
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(icu_copy);
			parser.setResolveBindings(true);//Bindingsのセット
			parser.setBindingsRecovery(true);
			parser.setStatementsRecovery(true);
			ASTNode rootAstNode = parser.createAST(new NullProgressMonitor());//この rootAstNode は ASTNode.COMPILATION_UNIT である。
			cu = (CompilationUnit)rootAstNode;
		}
		
		//インポート文関係
		//Import.getImportDeclarationで 先に行っておくことになった。
		//classesInActiveEditor.addAll(Import.classesInImports);
		//functionsInActiveEditor.addAll(Import.functionsInImports);
		
		//変数関係
		List<ITypeBinding> classesInLocalVariables = new ArrayList<ITypeBinding>();

		//現在のキャレット位置にあるastNodeを取得する
		if(isCompleteCode == false){
			FindResult findResult = AstNodeLocationMapper.findNode(cu, offset);
			nearestAstNode = findResult.getNearestNode();
		}else if(isCompleteCode == true && nearestAstNode == null){
			FindResult findResult = AstNodeLocationMapper.findNode(cu, offset + selected_length);
			nearestAstNode = findResult.getNearestNode();
		}
		
		String parentTypeNameOfNearestAstNode = AstUtil.getParentTypeNameWithPackageName(nearestAstNode);
		
		//現在編集中のクラスの情報(TypeとFunction)を取得する
		List<AbstractTypeDeclaration> cu_types = cu.types();
//
		for(int i = 0; i < cu_types.size(); i++){
			AbstractTypeDeclaration atd = cu_types.get(i);
			ITypeBinding itb = atd.resolveBinding();
			boolean type_contains_nearestAstNode = false;	//nearestAstNodeが所属しているクラスかどうか
			if(parentTypeNameOfNearestAstNode.equals(itb.getQualifiedName()))
				type_contains_nearestAstNode = true;
			
			AstUtil.createFunctionDbString(itb, itb.getQualifiedName(), functionsInActiveEditor, false, true);
//			if(type_contains_nearestAstNode){
//				//クラスが無いと分かりにくいので消す。
//				//AstUtil.createFunctionDbString(itb, "", functionsInActiveEditor, false);
//				AstUtil.createFunctionDbString(itb, "this", functionsInActiveEditor, false);
//			}
			/*
			 * classNameは同じで、
			 * その関数だけスーパータイプで定義されているものを取得する。
			 */
			for(ITypeBinding super_itb: AstUtil.getSuperTypes(itb)){
				AstUtil.createFunctionDbString(super_itb, itb.getQualifiedName(), functionsInActiveEditor, false, true);
//				if(type_contains_nearestAstNode){
//					//クラスが無いと分かりにくいので消す。
//					//AstUtil.createFunctionDbString(super_itb, "", functionsInActiveEditor, false);
//					AstUtil.createFunctionDbString(super_itb, "this", functionsInActiveEditor, false);
//				}
			}
			
			List<List<String>> pairs = new ArrayList<List<String>>();
			AstUtil.getParentChildPairsLists(itb, pairs);
			AstUtil.getImportDeclarationSuperTypesAndInterfaces(pairs, classesInActiveEditor);
			
			
			//フィールドのtypeを取得する(genericsのため)
			for(IVariableBinding ivb: itb.getDeclaredFields()){
				if(type_contains_nearestAstNode || Modifier.isPublic(ivb.getModifiers()))
					classesInLocalVariables.add(ivb.getType());
			}
		}
//
	
		//現在位置のメソッド定義を取得する。(現在位置がメソッド定義内でのみ動作)
		MethodDeclaration methodDec = AstUtil.getParentMethodDeclaration(nearestAstNode);

		if(methodDec != null){
			//メソッドのパラメータの取得
			List<SingleVariableDeclaration> params = methodDec.parameters();
			for(SingleVariableDeclaration s: params){
				IVariableBinding ivb =  s.resolveBinding();
				functionsInActiveEditor.addAll(AstUtil.iVariableBindingToDbString(ivb, "", false, true));
				classesInLocalVariables.add(ivb.getType());
			}
		}
		//メソッドのブロック内部で宣言されているローカル変数の取得

		List<ASTNode> vdsList = AstUtil.getUpperAllVariableDeclarationStatements(nearestAstNode);
		if(vdsList != null)
		for(ASTNode n: vdsList){
			if(n.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT){
				VariableDeclarationStatement vds = (VariableDeclarationStatement)n;
				List<VariableDeclarationFragment> vdfList = vds.fragments();//これが複数ある状態というものが良く解らん
				VariableDeclarationFragment vdf = vdfList.get(0);//1個しか無いはずなのでこれを取る。
				IVariableBinding ivb =  vdf.resolveBinding();
				functionsInActiveEditor.addAll(AstUtil.iVariableBindingToDbString(ivb, "", false, true));
				classesInLocalVariables.add(ivb.getType());
				
			}else if(n.getNodeType() == ASTNode.SINGLE_VARIABLE_DECLARATION){
				SingleVariableDeclaration svd = (SingleVariableDeclaration)n;
				IVariableBinding ivb = svd.resolveBinding();
				functionsInActiveEditor.addAll(AstUtil.iVariableBindingToDbString(ivb, "", false, true));
				classesInLocalVariables.add(ivb.getType());
			}else if(n.getNodeType() == ASTNode.VARIABLE_DECLARATION_EXPRESSION){//for文の中のカウンタiなどの取得
				VariableDeclarationExpression vde = (VariableDeclarationExpression)n;
				List<VariableDeclarationFragment> vdfList = vde.fragments();//これが複数ある状態というものが良く解らん
				VariableDeclarationFragment vdf = vdfList.get(0);//1個しか無いはずなのでこれを取る。
				IVariableBinding ivb =  vdf.resolveBinding();
				functionsInActiveEditor.addAll(AstUtil.iVariableBindingToDbString(ivb, "", false, true));
				classesInLocalVariables.add(ivb.getType());
			}
		}

		//新しく得られたTypeのうち、Genericsのものについて、そのTypeに所属するFunctionを新しく生成する。
		for(ITypeBinding t: classesInLocalVariables){
			classesInActiveEditor.add(AstUtil.getITypeBindingName(t, false));//Typeとして追加する。
			if(t.isParameterizedType()){
				// Object<Something> の形。 具体的なクラスが入っている。
				//でも、interface Collections<? extends E> はこっちにきてしまう。まあいいか。
				AstUtil.createFunctionDbString(t, t.getQualifiedName(), functionsInActiveEditor, false, false);
			}
		}

		// 代入文の場合のみ 
		//if(line_head_to_caret.indexOf("=") != -1 && line_head_to_caret.indexOf("!=") == -1){
			offset =  original_offset;//上で変更した元のoffsetに戻す。
		//}
		
//キーワードはスペース区切りのString
		keywords = line_head_to_caret.trim();//前方と後方の空白文字を削除
		String line_head_to_caret_deleted_forward_spaces = line_head_to_caret.replaceAll("^\\s+", "");//前方の空白文字のみを削除
		keyword_head_offset = offset - line_head_to_caret_deleted_forward_spaces.length();//キーワードの開始offset
//返り値の型の宣言、デフォルト値の設定
		desiredReturnType = "java.lang.Object";
		
		AstUtil.keywords = keywords;
		AstUtil.keyword_head_offset = keyword_head_offset;
		AstUtil.desiredReturnType = desiredReturnType;
		AstUtil.japanese_label = "その他";
//返り値の型の処理
		if(isCompleteCode == false){
//			int nodeType = AstUtil.inspectNodeType(nearestAstNode);
//			System.out.println(AstUtil.nodeTypeToString(nodeType));
			
			getKeywordAndDesiredReturnType(nearestAstNode, methodDec);
			
//			AstUtil.inspectDesiredReturnTypeAndKeywords(methodDec, nearestAstNode, nodeType);
			
			//どうもASTだけで処理するのはやっぱしダメだ。
//			AstUtil.inspectDesiredReturnTypeAndLocation2(methodDec, nearestAstNode, false);
			
		}else{
	//		StructuralPropertyDescriptor spd =  nearestAstNode.getLocationInParent();
	//		System.out.println("node: " + nearestAstNode);
	//		System.out.println("node type: " + ASTNode.nodeClassForType(nearestAstNode.getNodeType()));
	//		System.out.println("node type parent: " + ASTNode.nodeClassForType(nearestAstNode.getParent().getNodeType()));
	//		System.out.println("toString: " + spd.toString());
	//		System.out.println("getClass: " + spd.getClass());
	//		System.out.println("getNodeClass: " + spd.getNodeClass().getName());
	//		System.out.println("getNodeClass: " + spd.getNodeClass().getSimpleName());
	//		System.out.println("getNodeClass: " + spd.getNodeClass().getCanonicalName());
	//		System.out.println("id: " + spd.getId());
	//		System.out.println("is child list: " + spd.isChildListProperty());
	//		System.out.println("is child: " + spd.isChildProperty());
	//		System.out.println("is simple: " + spd.isSimpleProperty());
			
//			AstUtil.inspectDesiredReturnTypeAndLocation(methodDec, nearestAstNode, flg_exist_astnode);
			
			
			if(flg_exist_cu == false){
				int nodeType = AstUtil.inspectNodeType(nearestAstNode);
				AstUtil.inspectDesiredReturnTypeAndKeywords(methodDec, nearestAstNode, nodeType);
			}else{
				AstUtil.inspectDesiredReturnTypeAndLocation(methodDec, nearestAstNode, flg_exist_astnode);
			}
			
			keywords = AstUtil.keywords;
			keyword_head_offset = AstUtil.keyword_head_offset;
			desiredReturnType = AstUtil.desiredReturnType;
			location = AstUtil.japanese_label;
	//		location = AstUtil.nodeTypeToJapanese(nodeType);
		}


		desiredReturnType = AstUtil.formatTypeName(desiredReturnType);
//返り値の型の処理 ここまで

		replacement_length = keywords.length();
		//trimする。
		keywords = keywords.trim();
		//キーワードの処理
		keywords = AstUtil.formatKeywords(keywords);
	}

	private static void getKeywordAndDesiredReturnType(ASTNode nearestAstNode,
			MethodDeclaration methodDec) {
		//キーワード行に含まれる文字によって場合分け。
		if(keywords.indexOf("!=") != -1){
			//数値の比較
			int id = keywords.indexOf("!=");
			keyword_head_offset += id + 2;	// 置換開始位置は"!="の次の文字から。
			keywords = keywords.substring(id + 1);
			desiredReturnType = "java.lang.Integer";
		}else if(keywords.indexOf("=") != -1){//"="と"return"は"="の方の優先度を高くしなければtask05に対応できない！
			//"="を含む文字列ならば宣言文もしくは代入文であるとみなす。
			//"="の右辺がキーワード。左辺がdesiredTypeと認識。
			int id = keywords.indexOf("=");
			keyword_head_offset += id + 1;// 置換開始位置は"="の次の文字から。
			keywords = keywords.substring( id + 1 );//"="の次の位置から切り取る
			//変数宣言のノードを取得する
			VariableDeclarationStatement v = AstUtil.getLastVariableDeclarationStatement(nearestAstNode);
			if(v != null){
				Type t =  v.getType();
				desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
			}else{
				//v=nullならば代入文のはず。
				Assignment assign = AstUtil.getLastAssignmentNode(nearestAstNode);
				if(assign != null){
					desiredReturnType = AstUtil.getITypeBindingName(assign.resolveTypeBinding(), false);
				}
			}
		}else if(keywords.startsWith("return")){
			//先頭の文字が"return"ならば、その関数の返り値が、desiredType
			keyword_head_offset += 7;// 6("return"の長さ) + 1(スペースの分) だけキーワード開始位置がずれる。
			if(keywords.length() > 7)
				keywords = keywords.substring(7);//"return"の次の位置から切り取る
			else
				keywords = "";
			desiredReturnType = AstUtil.getITypeBindingName(methodDec.resolveBinding().getReturnType(), false);
		}else if(keywords.indexOf("(") != -1){
			//"("を含む文字列ならば、"("の手前の関数の引数がdesiredType
			//複数カッコがある場合どうするんだろう？
			//x(y(z(keywords)))という場合、lineIncludeKeywordsは"x(y(z(keywords"となる。
			//「zが求める引数の型」ということになる。
			//zの引数が1つなら簡単だなあ。2個以上だとどうなるんだろう。
			//"x(y(z(val1, val2, keywords"　こんな場合。
			//「zの3番目の引数の型に一致」ということになる。
			//複数引数は後回しにするべき。
			int id = keywords.lastIndexOf("(");//最後の"("
			keyword_head_offset += id + 2;// 置換開始位置は"("の次の次の文字から。
			keywords = keywords.substring(id + 1);// "("の次の文字からがキーワードである。
			MethodInvocation mi = AstUtil.getParentMethodInvocation(nearestAstNode);
//			if(isCompleteCode)
//				mi = AstUtil.getParentMethodInvocation(mi);//完成されたコードのときは、もう１つ上をたどる。
			if(nearestAstNode.getNodeType() == ASTNode.SIMPLE_NAME)
				mi = AstUtil.getParentMethodInvocation(mi);	//SimpleNameならば、もう１つ上をたどる。
			ITypeBinding[] itbs = mi.resolveMethodBinding().getParameterTypes();
			desiredReturnType = AstUtil.getITypeBindingName(itbs[0], false);//複数引数は後回し
		}
	}

}
