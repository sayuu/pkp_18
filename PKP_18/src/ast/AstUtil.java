package ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Assignment.Operator;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;

public class AstUtil {


	/*
	 * 引数ASTNodeが含まれるメソッド定義のASTNode(MethodDeclaration)を得る。
	 */
	public static MethodDeclaration getParentMethodDeclaration(ASTNode current) {
		while(true){
			if(current == null){
				//見つからなかった
				return null;
			}
			if(current.getNodeType() == ASTNode.METHOD_DECLARATION){
				return (MethodDeclaration)current;
			}
					
			current = current.getParent();
		}
	}

	/*
	 * 現在位置よりも1つ前にある変数宣言部分を１つだけ取得する
	 *
	 *  自分が居るBlockの中のstatementのうち、
	    自分よりも上に記述されているものだけを"後ろから"調べ、
		それが、VARIABLE_DECLARATION_STATEMENTならば、リストに格納する
	 *
	 */
	public static VariableDeclarationStatement getLastVariableDeclarationStatement(ASTNode current) {
		int currentPos = current.getStartPosition();

		while(true){
			if(current.getNodeType() == ASTNode.METHOD_DECLARATION){
				//自分が居るメソッド定義まで到達したら終了。
				return null;
			}else if(current.getNodeType() == ASTNode.BLOCK){
				//自分が居るBlockの中のstatementのうち、
				//自分よりも上に記述されているものだけを"後ろから"調べ、
				//それが、VARIABLE_DECLARATION_STATEMENTならば、リストに格納する
				Block currentBlock = (Block)current;
				List<Statement> statements = currentBlock.statements();
				for(int i = statements.size() - 2; i >= 0; i--){
					if(statements.get(i).getStartPosition() < currentPos){
						if(statements.get(i).getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT && current.toString().contains("=")){
							return (VariableDeclarationStatement)statements.get(i);
						}
					}
				}
			}else if(current.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT && current.toString().contains("=")){
				// currentが文字列に"="を含む変数宣言。
				//"="を含むものも、含まないものも、同じフラグのようなので仕方なくこのやり方をしている。
				return (VariableDeclarationStatement)current;
			}
			//1つ上の階層へ進む
			current = current.getParent();
		}
	}

	/*
	 * 現在位置よりも1つ前にあるフィールド宣言部分を１つだけ取得する
	 */
	public static FieldDeclaration getLastFieldDeclarationStatement(ASTNode current) {

		while(true){
			if(current.getNodeType() == ASTNode.TYPE_DECLARATION){
				//自分が居るクラス定義まで到達したら終了。
				return null;
			}else if(current.getNodeType() == ASTNode.FIELD_DECLARATION){
				return (FieldDeclaration)current;
			}
			//1つ上の階層へ進む
			current = current.getParent();
		}
	}


	/*
	 * 現在位置よりも1つ前にある代入文を表すノードを１つだけ取得する
	 *
	 */
	public static Assignment getLastAssignmentNode(ASTNode current) {
		int currentPos = current.getStartPosition();

		while(true){
			if(current.getNodeType() == ASTNode.METHOD_DECLARATION){
				//自分が居るメソッド定義まで到達したら終了。
				return null;
			}else if(current.getNodeType() == ASTNode.BLOCK){
				//自分が居るBlockの中のstatementのうち、
				//自分よりも上に記述されているものだけを"後ろから"調べ、
				//それが、VARIABLE_DECLARATION_STATEMENTならば、リストに格納する
				Block currentBlock = (Block)current;
				List<Statement> statements = currentBlock.statements();
				for(int i = statements.size() - 2; i >= 0; i--){
					if(statements.get(i).getStartPosition() < currentPos){
						if(statements.get(i).getNodeType() == ASTNode.EXPRESSION_STATEMENT && current.toString().contains("=")){
							ExpressionStatement e = (ExpressionStatement)(statements.get(i));
							return (Assignment)e.getExpression();
						}
					}
				}
			}else if(current.getNodeType() == ASTNode.ASSIGNMENT && current.toString().contains("=")){
				// currentが文字列に"="を含む変数宣言。
				//"="を含むものも、含まないものも、同じフラグのようなので仕方なくこのやり方をしている。
				return (Assignment)current;
			}
			//1つ上の階層へ進む
			current = current.getParent();
		}
	}
	
	/*
	 * 引数ASTNodeが含まれるVariableDeclarationStatementを得る。
	 */
	public static MethodInvocation getParentMethodInvocation(ASTNode current) {
		current = current.getParent();//自分よりも１つ上以上を探す。
		while(true){
			if(current.getNodeType() == ASTNode.METHOD_INVOCATION){
				return (MethodInvocation)current;
			}
			if(current.getParent() != null)
				current = current.getParent();
			else{
//				System.out.println(current);
				throw new Error();
			}
		}
	}

	/*
	 * 現在位置よりも上方にある変数宣言部分を"すべて"取得する
	 *
	 * 普通の変数宣言は、
	 * VariableDeclarationStatement
	 * For文の中の変数宣言は、
	 * initializersの中にあるVariableDeclarationStatement。
	 * EnhancedForStatement文は、
	 * getParameterの中にある、
	 * SingleVariableDeclaration。
	 *
	 */
	public static List<ASTNode> getUpperAllVariableDeclarationStatements(ASTNode current) {
		int currentPos = current.getStartPosition();

		List<ASTNode> list = new ArrayList<ASTNode>();
		while(true){
			if(current == null){
				//見つからなかった
				return null;
			}
			if(current.getNodeType() == ASTNode.METHOD_DECLARATION){
				//自分が居るメソッド定義まで到達したら終了。
				return list;
			}else if(current.getNodeType() == ASTNode.BLOCK){
				//自分が居るBlockの中のstatementのうち、
				//自分よりも上に記述されているものだけを調べ、
				//それが、VARIABLE_DECLARATION_STATEMENTならば、リストに格納する
				Block currentBlock = (Block)current;
				List<Statement> statements = currentBlock.statements();
				for(Statement statement: statements){
					if(statement.getStartPosition() < currentPos){
						if(statement.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT){
							//statementの型は VariableDeclarationStatement
							list.add(statement);
						}else if(statement.getNodeType() == ASTNode.FOR_STATEMENT){
							//for文の中の宣言は、VariableDeclarationExpression が取れる。
							ForStatement fors = (ForStatement)statement;
							if(fors.initializers().size() > 0){	//変数宣言がない場合もある。
								Object o = fors.initializers().get(0);
								if(o instanceof VariableDeclarationExpression){
									VariableDeclarationExpression e = (VariableDeclarationExpression)o;
									list.add(e);
								}
							}
						}else if(statement.getNodeType() == ASTNode.ENHANCED_FOR_STATEMENT){
							//for文の中の宣言は、SingleVariableDeclaration が取れる。
							EnhancedForStatement efors = (EnhancedForStatement)statement;
							SingleVariableDeclaration s = (SingleVariableDeclaration)efors.getParameter();
							list.add(s);
						}
					}
				}
			}else if(current.getNodeType() == ASTNode.CATCH_CLAUSE){
				CatchClause catchClause = (CatchClause)current;
				SingleVariableDeclaration e = catchClause.getException();
				list.add(e);
			}
			
			
			//1つ上の階層へ進む
			current = current.getParent();
		}
	}

	
	/**
	 * function のDB用のString を作成する。
	 * 引数 ITypeBinding
	 * @param itb
	 * @param className
	 * @param functions
	 * @param isImport
	 * @param isThisClass 現在編集中のクラスか？
	 */
	public static void createFunctionDbString(ITypeBinding itb, String className, List<String> functions, boolean isImport, boolean inThisClass){

		for(IVariableBinding ivb: itb.getDeclaredFields()){
			if(className.equals("this") || Modifier.isPublic(ivb.getModifiers()) || (inThisClass == true))
				functions.addAll(iVariableBindingToDbString(ivb, className, isImport, inThisClass));
		}
		for(IMethodBinding imb: itb.getDeclaredMethods()){
			if(className.equals("this") || Modifier.isPublic(imb.getModifiers()) || (inThisClass == true))
				functions.addAll(iMethodBindingToDbString(imb, className, isImport, inThisClass));
		}
	}


	/**
	 * function のDB用のString を作成する。
	 * 引数 IType
	 * @param it
	 * @param className
	 * @param functions
	 * @param isImport
	 * @throws JavaModelException
	 */
	public static void createFunctionDbString(IType it, String className, List<String> functions, boolean isImport, boolean inThisClass) throws JavaModelException{

		for(IField ifd: it.getFields()){
			if(className.equals("this") || Flags.isPublic(ifd.getFlags()))
				functions.addAll(iFieldToDbString(ifd, className, isImport, inThisClass));
		}
		for(IMethod im: it.getMethods()){
			if(className.equals("this") || Flags.isPublic(im.getFlags()))
				functions.addAll(iMethodToDbString(im, className, isImport, inThisClass));
		}
	}

	public static List<String> iVariableBindingToDbString(IVariableBinding ivb, String parentClass, boolean isImport, boolean inThisClass){
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean flg_static = store.getBoolean(PreferenceInitializer.STATIC_LABEL_WITHOUT_CLASS_NAME);
		if(flg_static == true)
			//static で2個関数を生成する。
			return iVariableBindingToDbStringStatic2(ivb, parentClass, isImport, inThisClass);
		else
			//static で1個だけ関数を生成する。
			return iVariableBindingToDbStringStatic1(ivb, parentClass, isImport, inThisClass);
	}
	/**
	 *	IVariableBindingの内容をDBStringにして返す
	 * @param ivb
	 * @param parentClass	親クラス
	 * @return
	 */
	public static List<String> iVariableBindingToDbStringStatic2(IVariableBinding ivb, String parentClass, boolean isImport, boolean inThisClass){
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> このような時、,を|にする。
		int mod = ivb.getModifiers();
		int loop = 1;

		String isFinal = "nonfinal";
		if(Modifier.isFinal(mod)){
			isFinal = "final";
		}
		String isStatic = "nonstatic";
		if(Modifier.isStatic(mod)){
			isStatic = "static";
			loop = 2;
		}

		List<String> ret = new ArrayList<String>();

		//クラス名の書き出し(staticは2回書く)
		for(int i = 0; i < loop; i++){
			String name = ivb.getName();
			String return_type = getITypeBindingName(ivb.getType(), isImport);
			String type = "localvariable";
			String label = name;

			if(ivb.isField()){
				type = "field";
				if(i == 0){
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+",";
					ret.add(a);
				}else{
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(getSimpleClassName(parentClass), ";") + ";" + splitName(label, ";")+",";
					ret.add(a);
				}
			}else{
				//localtype
				//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+",";
				ret.add(a);
				//charはintとしても認識させるためもう１つ関数を作る。
				if(return_type.equals("char")){
					return_type = "int";
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					String a2 = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+",";
					ret.add(a2);
				}
			}
		}
		return ret;
	}

	/**
	 *	IVariableBindingの内容をDBStringにして返す
	 *
	 * static で1個だけ関数を生成する。
	 
	 * @param ivb
	 * @param parentClass	親クラス
	 * @return
	 */
	public static List<String> iVariableBindingToDbStringStatic1(IVariableBinding ivb, String parentClass, boolean isImport, boolean inThisClass){
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> このような時、,を|にする。
		int mod = ivb.getModifiers();
		int loop = 1;

		String isFinal = "nonfinal";
		if(Modifier.isFinal(mod)){
			isFinal = "final";
		}
		String isStatic = "nonstatic";
		if(Modifier.isStatic(mod)){
			isStatic = "static";
			loop = 1;
		}

		List<String> ret = new ArrayList<String>();

		//クラス名の書き出し(staticは2回書く)
		for(int i = 0; i < 1; i++){
			String name = ivb.getName();
			String return_type = getITypeBindingName(ivb.getType(), isImport);
			String type = "localvariable";
			String label = name;

			if(ivb.isField()){
				type = "field";
				if(loop == 1){
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+",";
					ret.add(a);
				}else{
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(getSimpleClassName(parentClass), ";") + ";" + splitName(label, ";")+",";
					ret.add(a);
				}
			}else{
				//localtype
				//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+",";
				ret.add(a);
				//charはintとしても認識させるためもう１つ関数を作る。
				if(return_type.equals("char")){
					return_type = "int";
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					String a2 = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+",";
					ret.add(a2);
				}
			}
		}
		return ret;
	}

	public static List<String> iFieldToDbString(IField ifd, String parentClass, boolean isImport, boolean inThisClass) throws JavaModelException{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean flg_static = store.getBoolean(PreferenceInitializer.STATIC_LABEL_WITHOUT_CLASS_NAME);
		if(flg_static == true)
			//static で2個関数を生成する。
			return iFieldToDbStringStatic2(ifd, parentClass, isImport, inThisClass);
		else
			//staticの時に引数1個
			return iFieldToDbStringStatic1(ifd, parentClass, isImport, inThisClass);
	}
	
	/**
	 *	IFieldの内容をDBStringにして返す
	 * @param ifd
	 * @param parentClass	親クラス
	 * @return
	 * @throws JavaModelException 
	 */
	public static List<String> iFieldToDbStringStatic2(IField ifd, String parentClass, boolean isImport, boolean inThisClass) throws JavaModelException{
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> このような時、,を|にする。
		int mod = ifd.getFlags();
		
		int loop = 1;

		String isFinal = "nonfinal";
		if(Flags.isFinal(mod)){
			isFinal = "final";
		}
		String isStatic = "nonstatic";
		if(Flags.isStatic(mod)){
			isStatic = "static";
			loop = 2;
		}

		List<String> ret = new ArrayList<String>();

		//クラス名の書き出し(staticは2回書く)
		for(int i = 0; i < loop; i++){
			String name = ifd.getElementName();
			String return_type = Signature.toString(ifd.getTypeSignature()); //getITypeBindingName(ifd.getTypeSignature(), isImport);
			String type = "field";
			String params = "";
			String label = name;
			
			if(i == 0){
				params = parentClass;
				//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+","+params;
				ret.add(a);
			}else{
				//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(getSimpleClassName(parentClass), ";") + ";" + splitName(label, ";")+","+params;
				ret.add(a);
			}
			
		}
		return ret;
	}
	
	/**
	 *	IFieldの内容をDBStringにして返す
	 *
	 * staticの時に引数1個
	 
	 * @param ifd
	 * @param parentClass	親クラス
	 * @return
	 * @throws JavaModelException 
	 */
	public static List<String> iFieldToDbStringStatic1(IField ifd, String parentClass, boolean isImport, boolean inThisClass) throws JavaModelException{
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> このような時、,を|にする。
		int mod = ifd.getFlags();
		
		int loop = 1;

		String isFinal = "nonfinal";
		if(Flags.isFinal(mod)){
			isFinal = "final";
		}
		String isStatic = "nonstatic";
		if(Flags.isStatic(mod)){
			isStatic = "static";
			loop = 2;
		}

		List<String> ret = new ArrayList<String>();

		//クラス名の書き出し(staticは2回書く)
		for(int i = 0; i < 1; i++){
			String name = ifd.getElementName();
			String return_type = Signature.toString(ifd.getTypeSignature()); //getITypeBindingName(ifd.getTypeSignature(), isImport);
			String type = "field";
			String params = "";
			String label = name;
			
			if(loop == 1){
				params = parentClass;
				//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+","+params;
				ret.add(a);
			}else{
				//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(getSimpleClassName(parentClass), ";") + ";" + splitName(label, ";")+","+params;
				ret.add(a);
			}
			
		}
		return ret;
	}
	
	public static List<String> iMethodBindingToDbString(IMethodBinding imb, String parentClass, boolean isImport, boolean inThisClass){
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean flg_static = store.getBoolean(PreferenceInitializer.STATIC_LABEL_WITHOUT_CLASS_NAME);
		boolean flg_isConstructorWithNew = store.getBoolean(PreferenceInitializer.CONSTRUCTOR_LABEL_WITH_NEW);
		if(flg_static == true)
			//static で2個関数を生成する。
			return iMethodBindingToDbStringStatic2(imb, parentClass, isImport, inThisClass, flg_isConstructorWithNew);
		else
			//static の時　１つだけ。
			return iMethodBindingToDbStringStatic1(imb, parentClass, isImport, inThisClass, flg_isConstructorWithNew);
	}
	
	/**
	 *	IMethodBindingの内容をDBStringにして返す
	 * @param imb
	 * @param parentClass	親クラス
	 * @return
	 */
	public static List<String> iMethodBindingToDbStringStatic2(IMethodBinding imb, String parentClass, boolean isImport, boolean inThisClass, boolean isConstructorWithNew){
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> このような時、,を|にする。

		int mod = imb.getModifiers();
        int loop = 1;
		String isFinal = "nonfinal";
		if(Modifier.isFinal(mod)){
			isFinal = "final";
		}
		String isStatic = "nonstatic";
		if(Modifier.isStatic(mod)){
			isStatic = "static";
			loop = 2;
		}

		List<String> ret = new ArrayList<String>();

		//クラス名の書き出し(staticは ラベルを2種類書く)
		for(int i = 0; i < loop; i++){
			String name = imb.getName();
			String return_type = getITypeBindingName(imb.getReturnType(), isImport);
			String type = "method";
			String params = "";
			String label = name;

			if(imb.isConstructor() || imb.isDefaultConstructor()){
				type = "constructor";
				name = imb.getName();
				return_type = parentClass;
				//パラメーターを追加
				for(ITypeBinding itb: imb.getParameterTypes()){
					params += getITypeBindingName(itb, isImport) + ",";
				}
				String a = "";
				if(isConstructorWithNew)
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + "new;" + splitName(label, ";") + "," + params;
				else
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}else{//methodの場合。
				if(i == 0){
					if(Modifier.isStatic(mod) == false){//インスタンスメソッド
						params = parentClass;//レシーバが第一パラメータとなる。
						//パラメーターを追加
						for(ITypeBinding itb: imb.getParameterTypes()){
							params += "," + getITypeBindingName(itb, isImport);
						}
						//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
						String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
						ret.add(a);
						//下のようにやっても、型の順番が崩れるからうまくいかない。
//						if(parentClass.equals("this")){
//							//パラメータなし（レシーバなしで駆動）VERも入れる。
//							//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
//							String aa = parentClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + ",";
//							ret.add(aa);
//						}
						return ret;
					}else{//static メソッド
						//パラメーターを追加
						for(int j = 0; j < imb.getParameterTypes().length; j++){
							params += getITypeBindingName(imb.getParameterTypes()[j], isImport);
							if(j < imb.getParameterTypes().length -1){
								params += ",";
							}
						}
					}
				}else{
					//static メソッド2回目 (ラベルにクラス名を含む）
					if(parentClass.equals("this"))
						label += "This";//あとでlabelをsplitNameで処理するため、先頭を大文字にする。
					else
						label += getSimpleClassName(parentClass); //ラベルにparentのsimpleNameを追加する。
					//パラメーターを追加
					for(int j = 0; j < imb.getParameterTypes().length; j++){
						params += getITypeBindingName(imb.getParameterTypes()[j], isImport);
						if(j < imb.getParameterTypes().length -1){
							params += ",";
						}
					}
					
				}
				
				//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}
		}
		return ret;
	}
	
	/**
	 *	IMethodBindingの内容をDBStringにして返す
	 * @param imb
	 * @param parentClass	親クラス
	 * @return
	 */
	public static List<String> iMethodBindingToDbStringStatic1(IMethodBinding imb, String parentClass, boolean isImport, boolean inThisClass, boolean isConstructorWithNew){
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> このような時、,を|にする。

		int mod = imb.getModifiers();
        int loop = 1;
		String isFinal = "nonfinal";
		if(Modifier.isFinal(mod)){
			isFinal = "final";
		}
		String isStatic = "nonstatic";
		if(Modifier.isStatic(mod)){
			isStatic = "static";
			loop = 1;
		}

		List<String> ret = new ArrayList<String>();

		//クラス名の書き出し(staticは ラベルを2種類書く)
		for(int i = 0; i < 1; i++){
			String name = imb.getName();
			String return_type = getITypeBindingName(imb.getReturnType(), isImport);
			String type = "method";
			String params = "";
			String label = name;

			if(imb.isConstructor() || imb.isDefaultConstructor()){
				type = "constructor";
				name = imb.getName();
				return_type = parentClass;
				//パラメーターを追加
				for(ITypeBinding itb: imb.getParameterTypes()){
					params += getITypeBindingName(itb, isImport) + ",";
				}
				String a = "";
				if(isConstructorWithNew)
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + "new;" + splitName(label, ";") + "," + params;
				else
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}else{//methodの場合。
				if(loop == 1){
					if(Modifier.isStatic(mod) == false){//インスタンスメソッド
						params = parentClass;//レシーバが第一パラメータとなる。
						//パラメーターを追加
						for(ITypeBinding itb: imb.getParameterTypes()){
							params += "," + getITypeBindingName(itb, isImport);
						}
						//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
						String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
						ret.add(a);
						//下のようにやっても、型の順番が崩れるからうまくいかない。
//						if(parentClass.equals("this")){
//							//パラメータなし（レシーバなしで駆動）VERも入れる。
//							//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
//							String aa = parentClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + ",";
//							ret.add(aa);
//						}
						return ret;
					}else{//static メソッド
						//パラメーターを追加
						for(int j = 0; j < imb.getParameterTypes().length; j++){
							params += getITypeBindingName(imb.getParameterTypes()[j], isImport);
							if(j < imb.getParameterTypes().length -1){
								params += ",";
							}
						}
					}
				}else{
					//static メソッド2回目 (ラベルにクラス名を含む）
					if(parentClass.equals("this"))
						label += "This";//あとでlabelをsplitNameで処理するため、先頭を大文字にする。
					else
						label += getSimpleClassName(parentClass); //ラベルにparentのsimpleNameを追加する。
					//パラメーターを追加
					for(int j = 0; j < imb.getParameterTypes().length; j++){
						params += getITypeBindingName(imb.getParameterTypes()[j], isImport);
						if(j < imb.getParameterTypes().length -1){
							params += ",";
						}
					}
					
				}
				
				//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}
		}
		return ret;
	}

	public static List<String> iMethodToDbString(IMethod im, String parentClass, boolean isImport, boolean inThisClass) throws JavaModelException{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean flg_static = store.getBoolean(PreferenceInitializer.STATIC_LABEL_WITHOUT_CLASS_NAME);
		boolean flg_isConstructorWithNew = store.getBoolean(PreferenceInitializer.CONSTRUCTOR_LABEL_WITH_NEW);
		if(flg_static == true)
			//static で2個関数を生成する。
			return iMethodToDbStringStatic2(im, parentClass, isImport, inThisClass, flg_isConstructorWithNew);
		else
			//static の時1個
		return iMethodToDbStringStatic1(im, parentClass, isImport, inThisClass, flg_isConstructorWithNew);
	}
	/**
	 *	IMethodの内容をDBStringにして返す
	 * @param im
	 * @param parentClass	親クラス
	 * @return
	 * @throws JavaModelException 
	 */
	public static List<String> iMethodToDbStringStatic2(IMethod im, String parentClass, boolean isImport, boolean inThisClass, boolean isConstructorWithNew) throws JavaModelException{
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> このような時、,を|にする。

		int mod = im.getFlags();
        int loop = 1;
		String isFinal = "nonfinal";
		if(Flags.isFinal(mod)){
			isFinal = "final";
		}
		String isStatic = "nonstatic";
		if(Flags.isStatic(mod)){
			isStatic = "static";
			loop = 2;
		}

		List<String> ret = new ArrayList<String>();

		//クラス名の書き出し(staticは ラベルを2種類書く)
		for(int i = 0; i < loop; i++){
			String name = im.getElementName();
			String return_type = Signature.toString(im.getReturnType());	//getITypeBindingName(im.getReturnType(), isImport);
			String type = "method";
			String params = "";
			String label = name;
			
			if(im.isConstructor()){
				type = "constructor";
				name = im.getElementName();
				return_type = parentClass;
				//パラメーターを追加 
//				for(ITypeBinding itb: im.getParameterTypes()){
//					params += getITypeBindingName(itb, isImport) + ",";
//				}
				for(String s: im.getParameterTypes()){
					params += Signature.toString(s) + ",";
				}
				String a = "";
				if(isConstructorWithNew)
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + "new;" + splitName(label, ";") + "," + params;
				else
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
					
				ret.add(a);

			}else{//methodの場合。
				if(i == 0){
					if(Modifier.isStatic(mod) == false){//インスタンスメソッド
						params = parentClass;//レシーバが第一パラメータとなる。
						//パラメーターを追加
//						for(ITypeBinding itb: im.getParameterTypes()){
//							params += "," + getITypeBindingName(itb, isImport);
//						}
						for(String s: im.getParameterTypes()){
							params += "," + Signature.toString(s);
						}
					}else{//static メソッド
						//パラメーターを追加
//						for(int j = 0; j < im.getParameterTypes().length; j++){
//							params += getITypeBindingName(im.getParameterTypes()[j], isImport);
//							if(j < im.getParameterTypes().length -1){
//								params += ",";
//							}
//						}
						for(int j = 0; j < im.getParameterTypes().length; j++){
							params += Signature.toString(im.getParameterTypes()[j]);
							if(j < im.getParameterTypes().length -1){
								params += ",";
							}
						}
					}
				}else{
					//static メソッド (ラベルにクラス名を含む）
					if(parentClass.equals("this"))
						label += "This";//あとでlabelをsplitNameで処理するため、先頭を大文字にする。
					else
						label += getSimpleClassName(parentClass); //ラベルにparentのsimpleNameを追加する。
					//パラメーターを追加
//					for(int j = 0; j < im.getParameterTypes().length; j++){
//						params += getITypeBindingName(im.getParameterTypes()[j], isImport);
//						if(j < im.getParameterTypes().length -1){
//							params += ",";
//						}
//					}
					for(int j = 0; j < im.getParameterTypes().length; j++){
						params += Signature.toString(im.getParameterTypes()[j]);
						if(j < im.getParameterTypes().length -1){
							params += ",";
						}
					}
					
				}
				
				//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}
		}
		return ret;
	}

	/**
	 *	IMethodの内容をDBStringにして返す
	 * @param im
	 * @param parentClass	親クラス
	 * @return
	 * @throws JavaModelException 
	 */
	public static List<String> iMethodToDbStringStatic1(IMethod im, String parentClass, boolean isImport, boolean inThisClass, boolean isConstructorWithNew) throws JavaModelException{
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> このような時、,を|にする。

		int mod = im.getFlags();
        int loop = 1;
		String isFinal = "nonfinal";
		if(Flags.isFinal(mod)){
			isFinal = "final";
		}
		String isStatic = "nonstatic";
		if(Flags.isStatic(mod)){
			isStatic = "static";
			loop = 2;
		}

		List<String> ret = new ArrayList<String>();

		//クラス名の書き出し(staticは ラベルを2種類書く)
		for(int i = 0; i < 1; i++){
			String name = im.getElementName();
			String return_type = Signature.toString(im.getReturnType());	//getITypeBindingName(im.getReturnType(), isImport);
			String type = "method";
			String params = "";
			String label = name;
			
			if(im.isConstructor()){
				type = "constructor";
				name = im.getElementName();
				return_type = parentClass;
				//パラメーターを追加 
//				for(ITypeBinding itb: im.getParameterTypes()){
//					params += getITypeBindingName(itb, isImport) + ",";
//				}
				for(String s: im.getParameterTypes()){
					params += Signature.toString(s) + ",";
				}
				String a = "";
				if(isConstructorWithNew)
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + "new;" + splitName(label, ";") + "," + params;
				else
					//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}else{//methodの場合。
				if(loop == 2){
					if(Modifier.isStatic(mod) == false){//インスタンスメソッド
						params = parentClass;//レシーバが第一パラメータとなる。
						//パラメーターを追加
//						for(ITypeBinding itb: im.getParameterTypes()){
//							params += "," + getITypeBindingName(itb, isImport);
//						}
						for(String s: im.getParameterTypes()){
							params += "," + Signature.toString(s);
						}
					}else{//static メソッド
						//パラメーターを追加
//						for(int j = 0; j < im.getParameterTypes().length; j++){
//							params += getITypeBindingName(im.getParameterTypes()[j], isImport);
//							if(j < im.getParameterTypes().length -1){
//								params += ",";
//							}
//						}
						for(int j = 0; j < im.getParameterTypes().length; j++){
							params += Signature.toString(im.getParameterTypes()[j]);
							if(j < im.getParameterTypes().length -1){
								params += ",";
							}
						}
					}
				}else{
					//static メソッド (ラベルにクラス名を含む）
					if(parentClass.equals("this"))
						label += "This";//あとでlabelをsplitNameで処理するため、先頭を大文字にする。
					else
						label += getSimpleClassName(parentClass); //ラベルにparentのsimpleNameを追加する。
					//パラメーターを追加
//					for(int j = 0; j < im.getParameterTypes().length; j++){
//						params += getITypeBindingName(im.getParameterTypes()[j], isImport);
//						if(j < im.getParameterTypes().length -1){
//							params += ",";
//						}
//					}
					for(int j = 0; j < im.getParameterTypes().length; j++){
						params += Signature.toString(im.getParameterTypes()[j]);
						if(j < im.getParameterTypes().length -1){
							params += ",";
						}
					}
					
				}
				
				//クラス名(parentClass)//修飾子(isStatic, isFinal, type)//返り値の型(return_type)//Function名(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}
		}
		return ret;
	}
	
	//名前の大文字の部分の前に文字列withを挿入し、大文字を小文字に変換する
	//最後の1文字が大文字の場合は切らない。
	//大文字が連続している場合は、切らない。
	// "_" で 切る。
	public static String splitName(String name,  String with){
		String ret = name;
		// "_" で区切り。
		if(name.contains("_")){
			ret = splitName_splitByUnderBar(name, with);
		}else if(isAllUpperCase(name)){
			//Void.TYPE のような全て大文字の時
			ret = name.toLowerCase().trim();
		}else{
			// 大文字で区切り。
			ret = splitName_splitByUpperCase(name, with);
		}
		return ret;
	}

	public static String splitName_splitByUnderBar(String name,  String with){
		name = name.replaceAll("_", with);
		return name.toLowerCase().trim();
	}
	
	//文字列が全て大文字かを確かめる
	public static boolean isAllUpperCase(String name){
		/*
		 * 一文字でも小文字があれば、false
		 */
		StringBuffer sb = new StringBuffer(name);
		for(int i = 0; i < sb.length(); i++){
			//大文字（位置i）を見つける
			if(Character.isLowerCase(sb.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	public static String splitName_splitByUpperCase(String name,  String with){
		StringBuffer sb = new StringBuffer(name);
		for(int i = 0; i < sb.length() -1; i++){
			//大文字（位置i）を見つける
			if(Character.isUpperCase(sb.charAt(i))){
				
				//次も連続して大文字なら、続ける。
				if(Character.isUpperCase(sb.charAt(i+1))){
					continue;
				}
				//1つ前の文字が大文字なら、続ける。
				if(i > 0 && Character.isUpperCase(sb.charAt(i-1))){
					continue;
				}
				//その大文字の前に文字列withを挿入
				if(i >= 1){
					sb.insert(i, with);
					//文字列withの長さ分だけiを進める
					i += with.length();
				}
			}
		}
		return sb.toString().toLowerCase().trim();
	}
	
public static void main(String[] args){
	String s = splitName_splitByUpperCase("JFrame", ";");
	System.out.println(s);
}

	/**
	 * クラスのジェネリクスを考慮した型の名前を返す
	 *
	 * インポート文の場合、
	 * クラスのジェネリクスを考慮しない
	 *
	 * @param itb
	 * @return
	 */
	public static String getITypeBindingName(ITypeBinding itb, boolean isImport){
		//何もせずにgetQualifiedNameだと、ジェネリクスが入ってしまう
		if(isImport)
			itb = itb.getErasure();//インポート文の場合、ジェネリクスを排除する。
		String ret = itb.getQualifiedName().replace(",", "|");	//DBの区切り文字","と重複するのを避ける。
		return formatTypeName(ret);
		//return itb.getQualifiedName().replace(",", "|");	//DBの区切り文字","と重複するのを避ける。
		
	}

	/**
	 * 1行のキーワード文字列を整形する。
	 * 小文字化して、trimする。
	 * @return
	 */
	public static String formatKeywords(String keywords){
		//文字列を小文字化して、trimする。
		return keywords.toLowerCase().trim();
	}

	// 基本データ型は全てオブジェクト型に変換する。
	public static String formatTypeName(String name){

		// 基本データ型は全てオブジェクト型に変換する。
			//		  B            byte
		name = name.replaceFirst("byte$", "java.lang.Byte");
			//		  C            char
		name = name.replaceFirst("char$", "java.lang.Character");
			//		  D            double
		name = name.replaceFirst("double$", "java.lang.Double");
			//		  F            float
		name = name.replaceFirst("float$", "java.lang.Float");
			//		  I            int
		name = name.replaceFirst("int$", "java.lang.Integer");
			//		  J            long
		name = name.replaceFirst("long$", "java.lang.Long");
			//		  S            short
		name = name.replaceFirst("short$", "java.lang.Short");
			//		  Z            boolean
		name = name.replaceFirst("boolean$", "java.lang.Boolean");
			//		  V            void
		name = name.replaceFirst("void$", "java.lang.Void");

			return name;
	}
	
	/*
	 * 親クラスの名前を返す
	 */
	public static String getParentTypeNameWithPackageName(ASTNode node){
		//クラス宣言を取得する。
		while(!(node instanceof TypeDeclaration) && node != null){
			//enumはnullが帰ってきてしまう！！　EnumDeclaration　をするべきか。 だめだ。resolveBinding　がない。
			node = node.getParent();
		}
		
		TypeDeclaration td = (TypeDeclaration)node;
		ITypeBinding itb = td.resolveBinding();
		return itb.getQualifiedName();
	}
	
	//修飾が付かないクラス名を返す.(java.lang.StringXyzならば、StringXyzを返す。)
	public static String getSimpleClassName(String name){
		//末尾から探して、一番手前にある.を見つける
		int last = name.lastIndexOf(".");
		if(last == -1)
			return name;
		return name.substring(last + 1);//番号lastの次から始まる部分文字列を返す
	}



	/*
	 * オンデマンド型のインポート文を処理する。
	 */
	public static void getOnDemandImportDeclaration(String packageName) throws JavaModelException{
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editorPart = page.getActiveEditor();
		IEditorInput editorInput = editorPart.getEditorInput();
		ICompilationUnit icu = (ICompilationUnit) editorInput.getAdapter(IJavaElement.class);
		IJavaProject javaProject = icu.getJavaProject();
		IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
		//IPackageFragmentRoot とは srcフォルダなどの事。
		for (IPackageFragmentRoot root: roots) {
			for (IJavaElement el : root.getChildren()) {//パッケージが取れる.
				if(el.getElementType() == IJavaElement.PACKAGE_FRAGMENT && packageName.equals(el.getElementName())){
					IPackageFragment packagef = (IPackageFragment)el;
//					packagef.createCompilationUnit(name, contents, force, new NullProgressMonitor());
//					ICompilationUnit []cu_arr = packagef.getCompilationUnits();
//					for(ICompilationUnit c: cu_arr){
//						System.out.println(c.getElementName());
//					}
//					for (IJavaElement el2 : packagef.getChildren()) {
//						System.out.println(el2);
//						System.out.println();
//					}

					IClassFile []class_arr = packagef.getClassFiles();
					for (IClassFile cl : class_arr) {
//						System.out.println(cl);
						IType type = cl.getType();
					}
				}

			}
		}

//
//		try {
//			IType baseType = javaProject.findType(className);
//			ITypeHierarchy hierarchy = baseType.newTypeHierarchy(javaProject, null);
//			// プロジェクト内を対象にしたサブタイプ群の取得(クラスもインタフェースも)
//			IType[] subclasses = hierarchy.getSubtypes(baseType);
//			//consoleStream.print(className);//親クラス名
////			System.out.print(className);
////			printWriter_Sub.print(className);
//			for(IType t: subclasses){
//				String subclassName = t.getFullyQualifiedName();
//				//パターンマッチ：指定パッケージの中にあるクラスのみを取得する。
//				for(String p: packages){
//					//パターンマッチ：クラス名に'$'が含まれていれば除外
//					if(subclassName.indexOf('$') > -1){
//						continue;
//					}
//					//先頭文字が指定パッケージ名で始まり、パッケージ名の次が大文字であるかを見る
//					Pattern pattern1 = Pattern.compile("^"+ p + "\\.[A-Z].*");
//					Matcher matcher1 = pattern1.matcher(subclassName);
//					//パターンマッチ：クラス名に数字が含まれていれば除外
//					Pattern pattern2 = Pattern.compile("[^0-9]+");
//					Matcher matcher2 = pattern2.matcher(subclassName);
//					//パターンマッチ：クラス名がImplで終わる
//					Pattern pattern3 = Pattern.compile(".*Impl$");
//					Matcher matcher3 = pattern3.matcher(subclassName);
//
//					if(matcher1.matches() && matcher2.matches() &&  ! matcher3.matches()){
//						//consoleStream.print("," + subclassName);
////						System.out.print("," + subclassName);
////						printWriter_Sub.print("," + subclassName);
//					}
//				}
//			}
//			//consoleStream.println();
//			System.out.println();
//			printWriter_Sub.println();
//
//		} catch (JavaModelException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
	}
	
	/*
	 * impdのスーパータイプを含めて
	 * インポート文情報を処理する関数.
	 */
	public static void getImportDeclarationSuperTypes(ImportDeclaration impd, List<String> classesInImports, List<String> functionsInImports){
		
		Name name = impd.getName();
		ITypeBinding itb = name.resolveTypeBinding();
		List<String> types = new ArrayList<String>();//自分、親、祖父、曽祖父・・・の順に入る.
		
		if(itb == null){//staticなimport
			types.add(name.getFullyQualifiedName());
			return;
		}
		
		/*
		 * java.lang.Objectに到達したら抜ける
		 */
		while(!itb.getBinaryName().equals("java.lang.Object")){
			String className = itb.getBinaryName();
			types.add(className);
			createFunctionDbString(itb, className, functionsInImports, true, false);
			itb = itb.getSuperclass();
			if(itb == null)//interfaceなどクラス以外のときはnullになる
				break;
		}
	
		/*
		 * classesInImports
		 * 
		 * 要素NO, 内容 
		 * 1, 自分
		 * 2, 親, 自分
		 * 3, 祖父, 親, 自分
		 * 4, 曽祖父, 祖父, 親, 自分
		 */
		String s = "";
		for(int i = 0; i < types.size(); i++){
			s = types.get(i) + "," + s;	//前に前に追加していく.
			classesInImports.add(s);
		}
		
		ITypeBinding start = name.resolveTypeBinding();
		String myName = start.getBinaryName();
		//インターフェースも自分の親になる.
		for(ITypeBinding i: start.getInterfaces()){
			String interfaceName = i.getBinaryName();
			createFunctionDbString(i, interfaceName, functionsInImports, true, false);
			classesInImports.add(interfaceName + "," + myName);
		}
	}
	
	
	/*
	 * ITypeBinding itb のスーパークラスとインターフェースを取得する関数
	 */
	public static void getImportDeclarationSuperTypesAndInterfaces(List<List<String>> pairs, List<String> classesInImports){
		
		List<List<String>> descendantsList = new ArrayList<List<String>>();//自分とその子孫
		List<List<String>> descendantsList2 = new ArrayList<List<String>>();//自分とその子孫
		
		//要素のコピー
		for(List<String> pair: pairs){
			descendantsList.add(pair);
			descendantsList2.add(pair);
		}
		
		boolean isChange = false;
		while(true){
			for(List<String> descendants: descendantsList){
				String parent = descendants.get(descendants.size()-1);//一番若い子孫を親として見る。
				for(List<String> descendants2: descendantsList2){
					String child = descendants2.get(0);//一番上の親を子として見る。
					if(child.equals(parent)){//子と親が一致したら、
						descendants.addAll(descendants2.subList(1, descendants2.size()));//その子孫の全てを追加する。
						isChange = true;
					}
				}
			}
			if(isChange == false){
				//１度も変化しなかったらループを抜ける。
				break;
			}
			isChange = false;//フラグリセット
			
		}
		
		//一番先頭が同じものがあれば、その子孫をまとめる。
		descendantsList2.clear();
		descendantsList2.addAll(descendantsList);
		for(List<String> descendants: descendantsList){
			for(List<String> descendants2: descendantsList2){
				if(descendants.get(0).equals(descendants2.get(0))){
					for(String name: descendants2){
						if(!descendants.contains(name)){
							descendants.add(name);
						}
					}
				}
			}
		}
		
		//classesInImportsに追加する。
		for(List<String> descendants: descendantsList){
			String s = "";
			for(String name: descendants){
				s += name + ",";
			}
			classesInImports.add(s);
		}
		
	}
	
	/*
	 * functionsInImports
	 * の要素の重複をなくす。
	 */
	public static void compactFunctionDBstring(List<String> functionsInImports){
		//重複を防いで、ソートもする。
		TreeSet<String> set = new TreeSet<String>(functionsInImports);
		
		//入れなおす。
		functionsInImports.clear();
		functionsInImports.addAll(set);
	}
	
	
	/*
	 * TypeのDBStringの形式は、
	 * 先頭が自分自身（親）で、
	 * 2番目以降か子どもで、子供の順序は問わない。
	 * という形式である。
	 * 
	 * この関数は
	 * 一番先頭が同じものがあれば、その子孫をまとめる関数。
	 * 
	 * 子孫の順序は問わない。
	 * 
	 */
	public static void compactTypeDBstring(List<String> classesInImports){
		List<String> list1 = new ArrayList<String>(classesInImports);//自分とその子孫
		List<String> list2 = new ArrayList<String>(classesInImports);//自分とその子孫
		
		//キーが親、要素が子
		HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
//		HashMap<String, String> map = new HashMap<String, String>();
		
		//一番先頭が同じものがあれば、その子孫をまとめる。
		for(int i = 0; i < list1.size(); i++){
			String []types1 = list1.get(i).split(",");
			//子だけのリスト
			List<String> types1_children_list = Arrays.asList(types1).subList(1, types1.length);
			HashSet<String> child_set = new HashSet<String>(types1_children_list);
			
			for(int j = i+1; j < list2.size(); j++){
				String []types2 = list2.get(j).split(",");
				//子だけのリスト
				List<String> types2_children_list = Arrays.asList(types2).subList(1, types2.length);
				
				if(types1[0].equals(types2[0])){	//一番先頭(親）が同じ
					child_set.addAll(types2_children_list);//Setなので自動的に重複を防ぐ
				}
			}
			
			HashSet<String> child_set2 = map.get(types1[0]);
			if(child_set2 != null){	//すでにキーが存在する場合
				child_set2.addAll(child_set);
				map.put(types1[0], child_set2);
			}else{
				map.put(types1[0], child_set);//新規追加
			}

		}
		
        //classesInImportsに追加する。
		classesInImports.clear();
		Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String parent = it.next();
            //ソートしておく。
            TreeSet<String> tree = new TreeSet<String>(map.get(parent));
    		String s = parent;
    		Iterator<String> itt = tree.iterator();
            while (itt.hasNext()) {
    			s += "," + itt.next();
            }
    		classesInImports.add(s);
        }
	}
	
	/*
	 * 自分のSuperType（クラス、インターフェース）をListに入れて返す。
	 */
	public static List<ITypeBinding> getSuperTypes(ITypeBinding itb){
		
		if(itb == null){
			return null;
		}
		
		List<ITypeBinding> list = new ArrayList<ITypeBinding>();
		
		//インターフェースの処理。
		ITypeBinding[] itb_interfaces = itb.getInterfaces();
		if(itb_interfaces.length > 0)
		for(ITypeBinding itb_interface: itb_interfaces){
			list.add(itb_interface);
		}
		
		//スーパークラスの処理。
		ITypeBinding itb_superclass = itb.getSuperclass();
		if(itb_superclass != null){
			list.add(itb_superclass);
			list.addAll(getSuperTypes(itb_superclass));//スーパークラスのスーパークラスも取る。
		}
		
		return list;
	}
	
	
	/*
	 * 自分のSuperType（クラス、インターフェース）をListに入れて返す。
	 */
//	public static List<ITypeBinding> getSuperTypes(IType itb){
//		
//		if(itb == null){
//			return null;
//		}
//		
//		List<ITypeBinding> list = new ArrayList<ITypeBinding>();
//		
//		//インターフェースの処理。
//		ITypeBinding[] itb_interfaces = itb.get
//		if(itb_interfaces.length > 0)
//		for(ITypeBinding itb_interface: itb_interfaces){
//			list.add(itb_interface);
//		}
//		
//		//スーパークラスの処理。
//		ITypeBinding itb_superclass = itb.getSuperclass();
//		if(itb_superclass != null){
//			list.add(itb_superclass);
//			list.addAll(getSuperTypes(itb_superclass));//スーパークラスのスーパークラスも取る。
//		}
//		
//		return list;
//	}
	
	
	/*
	 * 親子ペアのリストを作成して、そのリスト群をtypesに格納する。
	 */
	public static void getParentChildPairsLists(ITypeBinding itb, List<List<String>> pairs){
		
		if(itb == null){
			return;
		}
		
		String className = itb.getBinaryName();
		
		//インターフェースの処理。
		ITypeBinding[] itb_interfaces = itb.getInterfaces();
		for(ITypeBinding itb_interface: itb_interfaces){
			List<String> parent_child_pair = new ArrayList<String>();
			parent_child_pair.add(itb_interface.getBinaryName());
			parent_child_pair.add(className);
			pairs.add(parent_child_pair);
			getParentChildPairsLists(itb_interface, pairs);
		}
		
		//スーパークラスの処理。
		ITypeBinding itb_superclass = itb.getSuperclass();
		if(itb_superclass != null){
			List<String> parent_child_pair = new ArrayList<String>();
			parent_child_pair.add(itb_superclass.getBinaryName());
			parent_child_pair.add(className);
			pairs.add(parent_child_pair);
			getParentChildPairsLists(itb_superclass, pairs);
		}
	}
	

	/*
	 * 親子ペアのリストを作成して、そのリスト群をtypesに格納する。
	 */
	public static void getParentChildPairsLists(IType itb, List<List<String>> pairs) throws JavaModelException{
		
		if(itb == null){
			return;
		}
		
		String className = itb.getElementName();
		
		//インターフェースの処理。
		for(String name: itb.getSuperInterfaceNames()){
			List<String> parent_child_pair = new ArrayList<String>();
			parent_child_pair.add(name);
			parent_child_pair.add(className);
			pairs.add(parent_child_pair);
		}
		
		//スーパークラスの処理。
		String itb_superclass = itb.getSuperclassName();
		if(itb_superclass != null){
			List<String> parent_child_pair = new ArrayList<String>();
			parent_child_pair.add(itb_superclass);
			parent_child_pair.add(className);
			pairs.add(parent_child_pair);
		}
	}
	
	
	/*
	 * 引数のnodeがどのような状況で使用されているか調べる。
	 * 
	 * Return文 の中
	 * If文　の中
	 * 宣言文
	 * 代入文
	 * 地の文
	 * メソッドの引数の中
	 */
	public static int inspectNodeType(ASTNode node) {
		ASTNode current = node;
		int count_method_invocation = 0;
		while(true){
			switch(current.getNodeType()){
				case ASTNode.CLASS_INSTANCE_CREATION:	//インスタンス生成
				case ASTNode.METHOD_INVOCATION:	//メソッドコール
					if(count_method_invocation > 0)	//このケース文2回目なら、
						return IN_METHOD_INVOCATION;
					count_method_invocation++;
					current = current.getParent();
					break;
				case ASTNode.INFIX_EXPRESSION:	//条件判定文
					return INFIX_EXPRESSION;
				case ASTNode.IF_STATEMENT:	//If文
					return IF_STATEMENT;
				case ASTNode.WHILE_STATEMENT:	//while文
					return WHILE_STATEMENT;
				case ASTNode.RETURN_STATEMENT:	//return文
					return RETURN_STATEMENT;
				case ASTNode.VARIABLE_DECLARATION_STATEMENT:	//変数宣言文
					return VARIABLE_DECLARATION_STATEMENT;
				case ASTNode.FIELD_DECLARATION:	//フィールド宣言文
					return FIELD_DECLARATION;
				case ASTNode.ASSIGNMENT:	//代入文
					return ASSIGNMENT;
				case ASTNode.BLOCK:	//ブロック
					//ブロックまできたら、メソッドコールまたはメソッドの引数の中の呼び出し。
					if(count_method_invocation > 1)
						return IN_METHOD_INVOCATION;
					else
						return METHOD_INVOCATION;
				default:
					current = current.getParent();
			}
		}
	}

	public static final int INFIX_EXPRESSION = 0;
	public static final int RETURN_STATEMENT = 1;
	public static final int VARIABLE_DECLARATION_STATEMENT = 2;
	public static final int ASSIGNMENT = 3;
	public static final int BLOCK = 4;
	public static final int METHOD_INVOCATION = 5;
	public static final int IN_METHOD_INVOCATION = 6;	//メソッドの引数の中
	public static final int CLASS_INSTANCE_CREATION = 7;
	public static final int IF_STATEMENT = 8;
	public static final int WHILE_STATEMENT = 9;
	public static final int FIELD_DECLARATION = 10;
	
	public static String nodeTypeToString(int nodeType) {
		
		switch(nodeType){
			case METHOD_INVOCATION:	//メソッドコール
				return "METHOD_INVOCATION";
			case INFIX_EXPRESSION:	//条件判定文
				return "INFIX_EXPRESSION";
			case RETURN_STATEMENT:	//return文
				return "RETURN_STATEMENT";
			case VARIABLE_DECLARATION_STATEMENT:	//宣言文
				return "VARIABLE_DECLARATION_STATEMENT";
			case ASSIGNMENT:	//代入文
				return "ASSIGNMENT";
			case BLOCK:	//ブロック
				return "BLOCK";
			case IN_METHOD_INVOCATION:	//メソッドの引数
				return "IN_METHOD_INVOCATION";
			case CLASS_INSTANCE_CREATION:
				return "CLASS_INSTANCE_CREATION";
			case IF_STATEMENT:
				return "IF_STATEMENT";
			case WHILE_STATEMENT:
				return "WHILE_STATEMENT";
			case FIELD_DECLARATION:
				return "FIELD_DECLARATION";
			default:
				return "default";
		}
	}

	public static String nodeTypeToJapanese(int nodeType) {
		
		switch(nodeType){
			case METHOD_INVOCATION:	//メソッドコール
//				return "地の文（メソッド呼び出し）";
				return "地の文";
			case INFIX_EXPRESSION:	//条件判定文
				return "条件判定文";
			case RETURN_STATEMENT:	//return文
				return "Return文";
			case VARIABLE_DECLARATION_STATEMENT:	//宣言文
				return "変数宣言文";
			case ASSIGNMENT:	//代入文
				return "代入文";
			case BLOCK:	//ブロック
				return "ブロックの中";
			case IN_METHOD_INVOCATION:	//メソッドの引数
				return "メソッドの引数内";
			case CLASS_INSTANCE_CREATION:
				return "インスタンス生成";
			case IF_STATEMENT:
				return "If文";
			case WHILE_STATEMENT:
				return "While文";
			case FIELD_DECLARATION:
				return "フィールド宣言文";	//フィールド宣言文
			default:
				return "default";
		}
	}

	public static String keywords;
	public static String desiredReturnType;
	public static int keyword_head_offset;
	public static String japanese_label	= "";
	
	/*
	 * 望ましい返り値の型と
	 * 入力キーワードを調べ、代入する。
	 */
	public static void inspectDesiredReturnTypeAndKeywords(MethodDeclaration methodDec, ASTNode node, int nodeType) {
		switch(nodeType){
			case METHOD_INVOCATION:	//メソッドコール
				//地の文。何もしない。
				break;
			case INFIX_EXPRESSION:	//条件判定文
//				System.out.println(node.getLocationInParent().toString());
				break;
			case RETURN_STATEMENT:	//return文
				//先頭の文字が"return"ならば、その関数の返り値が、desiredType
				keyword_head_offset += 7;// 6("return"の長さ) + 1(スペースの分) だけキーワード開始位置がずれる。
				if(keywords.length() > 7)
					keywords = keywords.substring(7);//"return"の次の位置から切り取る
				else
					keywords = "";
				desiredReturnType = AstUtil.getITypeBindingName(methodDec.resolveBinding().getReturnType(), false);
				break;
			case VARIABLE_DECLARATION_STATEMENT:	//宣言文
				//"="の右辺がキーワード。左辺がdesiredTypeと認識。
				int id = keywords.indexOf("=");
				keyword_head_offset += id + 1;// 置換開始位置は"="の次の文字から。
				keywords = keywords.substring( id + 1 );//"="の次の位置から切り取る
				//変数宣言のノードを取得する
				VariableDeclarationStatement v = AstUtil.getLastVariableDeclarationStatement(node);
				Type t =  v.getType();
				desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
				break;
			case FIELD_DECLARATION:	//フィールド宣言文
				//"="の右辺がキーワード。左辺がdesiredTypeと認識。
				int id1 = keywords.indexOf("=");
				keyword_head_offset += id1 + 1;// 置換開始位置は"="の次の文字から。
				keywords = keywords.substring( id1 + 1 );//"="の次の位置から切り取る
				//変数宣言のノードを取得する
				FieldDeclaration f = AstUtil.getLastFieldDeclarationStatement(node);
				Type t1 =  f.getType();
				desiredReturnType = AstUtil.getITypeBindingName(t1.resolveBinding(), false);
				break;
			case ASSIGNMENT:	//代入文
				//"="の右辺がキーワード。左辺がdesiredTypeと認識。
				int id11 = keywords.indexOf("=");
				keyword_head_offset += id11 + 1;// 置換開始位置は"="の次の文字から。
				keywords = keywords.substring( id11 + 1 );//"="の次の位置から切り取る
				Assignment assign = AstUtil.getLastAssignmentNode(node);
				desiredReturnType = AstUtil.getITypeBindingName(assign.resolveTypeBinding(), false);
				break;
			case BLOCK:	//ブロック
			case IN_METHOD_INVOCATION:	//メソッドの引数
				//"("を含む文字列ならば、"("の手前の関数の引数がdesiredType
				//複数カッコがある場合どうするんだろう？
				//x(y(z(keywords)))という場合、lineIncludeKeywordsは"x(y(z(keywords"となる。
				//「zが求める引数の型」ということになる。
				//zの引数が1つなら簡単だなあ。2個以上だとどうなるんだろう。
				//"x(y(z(val1, val2, keywords"　こんな場合。
				//「zの3番目の引数の型に一致」ということになる。
				//複数引数は後回しにするべき。
				int id111 = keywords.lastIndexOf("(");//最後の"("
				keyword_head_offset += id111 + 2;// 置換開始位置は"("の次の次の文字から。
				keywords = keywords.substring(id111 + 1);// "("の次の文字からがキーワードである。
				MethodInvocation mi = AstUtil.getParentMethodInvocation(node);
				
				if(node.getNodeType() == ASTNode.SIMPLE_NAME)
					mi = AstUtil.getParentMethodInvocation(mi);	//SimpleNameならば、もう１つ上をたどる。
				
				if(node.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION){	//インスタンス生成の場合。
					ClassInstanceCreation cic = (ClassInstanceCreation)node;
					Type t11 = cic.getType();
					desiredReturnType = AstUtil.getITypeBindingName(t11.resolveBinding(), false);
				}else{	//メソッド呼び出しの場合。
					ITypeBinding[] itbs = mi.resolveMethodBinding().getParameterTypes();
					desiredReturnType = AstUtil.getITypeBindingName(itbs[0], false);//複数引数は後回し
				}
				break;
			case CLASS_INSTANCE_CREATION:
				break;
			case IF_STATEMENT:	//if文
				int id1111 = keywords.lastIndexOf("(");//最後の"("
				keyword_head_offset += id1111 + 1;// 置換開始位置は"("の次の文字から。
				keywords = keywords.substring(id1111 + 1);// "("の次の文字からがキーワードである。
				desiredReturnType = "java.lang.Boolean";//条件判定なので。
				break;
			case WHILE_STATEMENT:	//while文
				int id11111 = keywords.lastIndexOf("(");//最後の"("
				keyword_head_offset += id11111 + 2;// 置換開始位置は"("の次の次の文字から。
				keywords = keywords.substring(id11111 + 1);// "("の次の文字からがキーワードである。
				desiredReturnType = "java.lang.Boolean";//条件判定なので。
				break;
			default:
				break;
		}
	}
	
	
	/**
	 * 望ましい返り値の型とnodeのロケーションを調べる。
	 * 一括でタスクを保存するときに使用する。
	 * @param methodDec
	 * @param node
	 */
	public static void inspectDesiredReturnTypeAndLocation(MethodDeclaration methodDec, ASTNode node, boolean is_exist_astnode) {
		int parentNodeType = node.getParent().getNodeType();

//		int parentNodeType = node.getNodeType();
//		if(is_exist_astnode)
//			parentNodeType = node.getParent().getNodeType();
		StructuralPropertyDescriptor spd =  node.getLocationInParent();
		String id = spd.getId();
		
		//親の形で判別
		switch(parentNodeType){
			case ASTNode.VARIABLE_DECLARATION_FRAGMENT:	//変数宣言
				if(id.equals("initializer")){
					//変数宣言のノードを取得する
					int type = node.getParent().getParent().getNodeType();
					if(type == ASTNode.VARIABLE_DECLARATION_STATEMENT){	//変数宣言　ふつうの。
						VariableDeclarationStatement v = AstUtil.getLastVariableDeclarationStatement(node);
						Type t =  v.getType();
						desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
						japanese_label = "変数宣言";
					}else if(type == ASTNode.FIELD_DECLARATION){//フィールド。
						FieldDeclaration f = AstUtil.getLastFieldDeclarationStatement(node);
						Type t1 =  f.getType();
						desiredReturnType = AstUtil.getITypeBindingName(t1.resolveBinding(), false);
						japanese_label = "フィールド宣言";
					}else if(type == ASTNode.VARIABLE_DECLARATION_EXPRESSION){	//for文の中(など)の変数宣言
						VariableDeclarationExpression vde = (VariableDeclarationExpression)node.getParent().getParent();
						Type t = vde.getType();
						desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
						japanese_label = "変数宣言";
					}
				}
				break;
			case ASTNode.ASSIGNMENT:	//代入文
				if(id.equals("rightHandSide")){
					Assignment assign = AstUtil.getLastAssignmentNode(node);
					desiredReturnType = AstUtil.getITypeBindingName(assign.resolveTypeBinding(), false);
					japanese_label = "代入文";
				}
				break;
			case ASTNode.METHOD_INVOCATION:	//メソッドコール
				MethodInvocation mi = AstUtil.getParentMethodInvocation(node);
				if(id.equals("expression")){//親がメソッドコール。自分はレシーバ。
					//親の所属するクラスとなる。
					desiredReturnType = AstUtil.getITypeBindingName(mi.resolveMethodBinding().getDeclaringClass(), false);
					japanese_label = "メソッド呼び出し内(レシーバ)";
				}else if(id.equals("arguments")){//親がメソッドコール。自分は引数。
					//自分自身が何番目の引数なのかわからん。
					ITypeBinding[] itbs = mi.resolveMethodBinding().getParameterTypes();
					if(itbs.length == 1)
						desiredReturnType = AstUtil.getITypeBindingName(itbs[0], false);//複数引数は後回し
					japanese_label = "メソッド呼び出し内(引数)";
				}
				break;
			case ASTNode.CLASS_INSTANCE_CREATION:	//インスタンス生成
				ClassInstanceCreation cic = (ClassInstanceCreation)node.getParent();
				if(id.equals("arguments")){//親がnew文。自分は引数。
					//自分自身が何番目の引数なのかわからん。
					ITypeBinding[] itbs = cic.resolveConstructorBinding().getParameterTypes();
					if(itbs.length == 1)
					desiredReturnType = AstUtil.getITypeBindingName(itbs[0], false);//複数引数は後回し
					japanese_label = "インスタンス生成内(引数)";
				}
				break;
			case ASTNode.EXPRESSION_STATEMENT:	//地の文
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Object";	//何でもあり。
					japanese_label = "地の文";
				}
				break;
			case ASTNode.CAST_EXPRESSION:	//型のキャスト
				if(id.equals("expression")){
					CastExpression ce = (CastExpression)node.getParent();
					Type t = ce.getType();
					desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
					japanese_label = "型キャスト";
				}
				break;
			case ASTNode.IF_STATEMENT:	//If文
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Boolean";//条件判定なので。
					japanese_label = "If文内";
				}
				break;
			case ASTNode.WHILE_STATEMENT:	//while文
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Boolean";//条件判定なので。
					japanese_label = "While文内";
				}
				break;
			case ASTNode.FOR_STATEMENT:	//for文
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Integer";//カウンタ
					japanese_label = "for文内";
				}
				break;
			case ASTNode.PREFIX_EXPRESSION:	//条件判定など記号を使った処理。
			case ASTNode.INFIX_EXPRESSION:	//条件判定など記号を使った処理。
				//もう１つ上を見て、その条件で判定
				//inspectDesiredReturnTypeAndKeywords2(methodDec, node.getParent());
				//対応できないのもある。
				//場合わけが面倒なので、自分自身の返り値の型を返す。
				if(node instanceof MethodInvocation){
					MethodInvocation mi1 = (MethodInvocation)node;
					desiredReturnType = AstUtil.getITypeBindingName(mi1.resolveTypeBinding(), false);
				}else if(node instanceof ClassInstanceCreation){
					ClassInstanceCreation cic1 = (ClassInstanceCreation)node;
					desiredReturnType = AstUtil.getITypeBindingName(cic1.resolveTypeBinding(), false);
				}else{
					//自分自身がわからなければ親をあたる。
					InfixExpression ie = (InfixExpression)node.getParent();
					desiredReturnType = AstUtil.getITypeBindingName(ie.resolveTypeBinding(), false);
				}
				japanese_label = "オペレーターが存在する";
				break;
			case ASTNode.RETURN_STATEMENT:
				ReturnStatement rs = (ReturnStatement)node.getParent();
				desiredReturnType = AstUtil.getITypeBindingName(rs.getExpression().resolveTypeBinding(), false);
//				System.out.println(rs.getExpression().resolveTypeBinding().getQualifiedName());
				japanese_label = "リターン文内";
				break;
			case ASTNode.THROW_STATEMENT:
				ThrowStatement ts = (ThrowStatement)node.getParent();
				desiredReturnType = AstUtil.getITypeBindingName(ts.getExpression().resolveTypeBinding(), false);
//				System.out.println(ts.getExpression().resolveTypeBinding().getQualifiedName());
				japanese_label = "スロー文内";
				break;
			default:
				japanese_label = "その他";
				break;
		}
	}
	

	/**
	 * 望ましい返り値の型とnodeのロケーションを調べる。
	 * 一括でタスクを保存するときに使用する。
	 * @param methodDec
	 * @param node
	 */
	public static void inspectDesiredReturnTypeAndLocation2(MethodDeclaration methodDec, ASTNode node, boolean is_exist_astnode) {
		int nodeType = node.getNodeType();
		int parentNodeType = node.getParent().getNodeType();

//		int parentNodeType = node.getNodeType();
//		if(is_exist_astnode)
//			parentNodeType = node.getParent().getNodeType();
		StructuralPropertyDescriptor spd =  node.getLocationInParent();
		String id = spd.getId();
		
		//親の形で判別
		switch(parentNodeType){
			case ASTNode.VARIABLE_DECLARATION_FRAGMENT:	//変数宣言
				if(id.equals("initializer")){
					//変数宣言のノードを取得する
					int type = node.getParent().getParent().getNodeType();
					if(type == ASTNode.VARIABLE_DECLARATION_STATEMENT){	//変数宣言　ふつうの。
						VariableDeclarationStatement v = AstUtil.getLastVariableDeclarationStatement(node);
						Type t =  v.getType();
						desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
						japanese_label = "変数宣言";
					}else if(type == ASTNode.FIELD_DECLARATION){//フィールド。
						FieldDeclaration f = AstUtil.getLastFieldDeclarationStatement(node);
						Type t1 =  f.getType();
						desiredReturnType = AstUtil.getITypeBindingName(t1.resolveBinding(), false);
						japanese_label = "フィールド宣言";
					}else if(type == ASTNode.VARIABLE_DECLARATION_EXPRESSION){	//for文の中(など)の変数宣言
						VariableDeclarationExpression vde = (VariableDeclarationExpression)node.getParent().getParent();
						Type t = vde.getType();
						desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
						japanese_label = "変数宣言";
					}
				}
				break;
			case ASTNode.ASSIGNMENT:	//代入文
				if(id.equals("rightHandSide")){
					Assignment assign = AstUtil.getLastAssignmentNode(node);
					desiredReturnType = AstUtil.getITypeBindingName(assign.resolveTypeBinding(), false);
					japanese_label = "代入文";
				}
				break;
			case ASTNode.METHOD_INVOCATION:	//メソッドコール
				MethodInvocation mi = AstUtil.getParentMethodInvocation(node);
				if(id.equals("expression")){//親がメソッドコール。自分はレシーバ。
					//親の所属するクラスとなる。
					desiredReturnType = AstUtil.getITypeBindingName(mi.resolveMethodBinding().getDeclaringClass(), false);
					japanese_label = "メソッド呼び出し内(レシーバ)";
				}else if(id.equals("arguments")){//親がメソッドコール。自分は引数。
					//自分自身が何番目の引数なのかわからん。
					ITypeBinding[] itbs = mi.resolveMethodBinding().getParameterTypes();
					desiredReturnType = AstUtil.getITypeBindingName(itbs[0], false);//複数引数は後回し
					japanese_label = "メソッド呼び出し内(引数)";
				}
				break;
			case ASTNode.CLASS_INSTANCE_CREATION:	//インスタンス生成
				ClassInstanceCreation cic = (ClassInstanceCreation)node.getParent();
				if(id.equals("arguments")){//親がnew文。自分は引数。
					//自分自身が何番目の引数なのかわからん。
					ITypeBinding[] itbs = cic.resolveConstructorBinding().getParameterTypes();
					desiredReturnType = AstUtil.getITypeBindingName(itbs[0], false);//複数引数は後回し
					japanese_label = "インスタンス生成内(引数)";
				}
				break;
			case ASTNode.EXPRESSION_STATEMENT:	//地の文
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Object";	//何でもあり。
					japanese_label = "地の文";
				}
				break;
			case ASTNode.CAST_EXPRESSION:	//型のキャスト
				if(id.equals("expression")){
					CastExpression ce = (CastExpression)node.getParent();
					Type t = ce.getType();
					desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
					japanese_label = "型キャスト";
				}
				break;
			case ASTNode.IF_STATEMENT:	//If文
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Boolean";//条件判定なので。
					japanese_label = "If文内";
				}
				break;
			case ASTNode.WHILE_STATEMENT:	//while文
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Boolean";//条件判定なので。
					japanese_label = "While文内";
				}
				break;
			case ASTNode.FOR_STATEMENT:	//for文
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Integer";//カウンタ
					japanese_label = "for文内";
				}
				break;
			case ASTNode.PREFIX_EXPRESSION:	//条件判定など記号を使った処理。
			case ASTNode.INFIX_EXPRESSION:	//条件判定など記号を使った処理。
				//もう１つ上を見て、その条件で判定
				//inspectDesiredReturnTypeAndKeywords2(methodDec, node.getParent());
				//対応できないのもある。
				//場合わけが面倒なので、自分自身の返り値の型を返す。
				if(node instanceof MethodInvocation){
					MethodInvocation mi1 = (MethodInvocation)node;
					desiredReturnType = AstUtil.getITypeBindingName(mi1.resolveTypeBinding(), false);
				}else if(node instanceof ClassInstanceCreation){
					ClassInstanceCreation cic1 = (ClassInstanceCreation)node;
					desiredReturnType = AstUtil.getITypeBindingName(cic1.resolveTypeBinding(), false);
				}else{
					//自分自身がわからなければ親をあたる。
					InfixExpression ie = (InfixExpression)node.getParent();
					desiredReturnType = AstUtil.getITypeBindingName(ie.resolveTypeBinding(), false);
				}
				japanese_label = "オペレーターが存在する";
				break;
			case ASTNode.RETURN_STATEMENT:
				ReturnStatement rs = (ReturnStatement)node.getParent();
				desiredReturnType = AstUtil.getITypeBindingName(rs.getExpression().resolveTypeBinding(), false);
//				System.out.println(rs.getExpression().resolveTypeBinding().getQualifiedName());
				japanese_label = "リターン文内";
				break;
			case ASTNode.THROW_STATEMENT:
				ThrowStatement ts = (ThrowStatement)node.getParent();
				desiredReturnType = AstUtil.getITypeBindingName(ts.getExpression().resolveTypeBinding(), false);
//				System.out.println(ts.getExpression().resolveTypeBinding().getQualifiedName());
				japanese_label = "スロー文内";
				break;
			default:
				japanese_label = "その他";
				inspectDesiredReturnTypeAndLocation2(methodDec, node.getParent(), is_exist_astnode);
				break;
		}
	}
}
