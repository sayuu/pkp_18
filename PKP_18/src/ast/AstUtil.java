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
	 * ����ASTNode���܂܂�郁�\�b�h��`��ASTNode(MethodDeclaration)�𓾂�B
	 */
	public static MethodDeclaration getParentMethodDeclaration(ASTNode current) {
		while(true){
			if(current == null){
				//������Ȃ�����
				return null;
			}
			if(current.getNodeType() == ASTNode.METHOD_DECLARATION){
				return (MethodDeclaration)current;
			}
					
			current = current.getParent();
		}
	}

	/*
	 * ���݈ʒu����1�O�ɂ���ϐ��錾�������P�����擾����
	 *
	 *  ����������Block�̒���statement�̂����A
	    ����������ɋL�q����Ă�����̂�����"��납��"���ׁA
		���ꂪ�AVARIABLE_DECLARATION_STATEMENT�Ȃ�΁A���X�g�Ɋi�[����
	 *
	 */
	public static VariableDeclarationStatement getLastVariableDeclarationStatement(ASTNode current) {
		int currentPos = current.getStartPosition();

		while(true){
			if(current.getNodeType() == ASTNode.METHOD_DECLARATION){
				//���������郁�\�b�h��`�܂œ��B������I���B
				return null;
			}else if(current.getNodeType() == ASTNode.BLOCK){
				//����������Block�̒���statement�̂����A
				//����������ɋL�q����Ă�����̂�����"��납��"���ׁA
				//���ꂪ�AVARIABLE_DECLARATION_STATEMENT�Ȃ�΁A���X�g�Ɋi�[����
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
				// current���������"="���܂ޕϐ��錾�B
				//"="���܂ނ��̂��A�܂܂Ȃ����̂��A�����t���O�̂悤�Ȃ̂Ŏd���Ȃ����̂��������Ă���B
				return (VariableDeclarationStatement)current;
			}
			//1��̊K�w�֐i��
			current = current.getParent();
		}
	}

	/*
	 * ���݈ʒu����1�O�ɂ���t�B�[���h�錾�������P�����擾����
	 */
	public static FieldDeclaration getLastFieldDeclarationStatement(ASTNode current) {

		while(true){
			if(current.getNodeType() == ASTNode.TYPE_DECLARATION){
				//����������N���X��`�܂œ��B������I���B
				return null;
			}else if(current.getNodeType() == ASTNode.FIELD_DECLARATION){
				return (FieldDeclaration)current;
			}
			//1��̊K�w�֐i��
			current = current.getParent();
		}
	}


	/*
	 * ���݈ʒu����1�O�ɂ���������\���m�[�h���P�����擾����
	 *
	 */
	public static Assignment getLastAssignmentNode(ASTNode current) {
		int currentPos = current.getStartPosition();

		while(true){
			if(current.getNodeType() == ASTNode.METHOD_DECLARATION){
				//���������郁�\�b�h��`�܂œ��B������I���B
				return null;
			}else if(current.getNodeType() == ASTNode.BLOCK){
				//����������Block�̒���statement�̂����A
				//����������ɋL�q����Ă�����̂�����"��납��"���ׁA
				//���ꂪ�AVARIABLE_DECLARATION_STATEMENT�Ȃ�΁A���X�g�Ɋi�[����
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
				// current���������"="���܂ޕϐ��錾�B
				//"="���܂ނ��̂��A�܂܂Ȃ����̂��A�����t���O�̂悤�Ȃ̂Ŏd���Ȃ����̂��������Ă���B
				return (Assignment)current;
			}
			//1��̊K�w�֐i��
			current = current.getParent();
		}
	}
	
	/*
	 * ����ASTNode���܂܂��VariableDeclarationStatement�𓾂�B
	 */
	public static MethodInvocation getParentMethodInvocation(ASTNode current) {
		current = current.getParent();//���������P��ȏ��T���B
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
	 * ���݈ʒu��������ɂ���ϐ��錾������"���ׂ�"�擾����
	 *
	 * ���ʂ̕ϐ��錾�́A
	 * VariableDeclarationStatement
	 * For���̒��̕ϐ��錾�́A
	 * initializers�̒��ɂ���VariableDeclarationStatement�B
	 * EnhancedForStatement���́A
	 * getParameter�̒��ɂ���A
	 * SingleVariableDeclaration�B
	 *
	 */
	public static List<ASTNode> getUpperAllVariableDeclarationStatements(ASTNode current) {
		int currentPos = current.getStartPosition();

		List<ASTNode> list = new ArrayList<ASTNode>();
		while(true){
			if(current == null){
				//������Ȃ�����
				return null;
			}
			if(current.getNodeType() == ASTNode.METHOD_DECLARATION){
				//���������郁�\�b�h��`�܂œ��B������I���B
				return list;
			}else if(current.getNodeType() == ASTNode.BLOCK){
				//����������Block�̒���statement�̂����A
				//����������ɋL�q����Ă�����̂����𒲂ׁA
				//���ꂪ�AVARIABLE_DECLARATION_STATEMENT�Ȃ�΁A���X�g�Ɋi�[����
				Block currentBlock = (Block)current;
				List<Statement> statements = currentBlock.statements();
				for(Statement statement: statements){
					if(statement.getStartPosition() < currentPos){
						if(statement.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT){
							//statement�̌^�� VariableDeclarationStatement
							list.add(statement);
						}else if(statement.getNodeType() == ASTNode.FOR_STATEMENT){
							//for���̒��̐錾�́AVariableDeclarationExpression ������B
							ForStatement fors = (ForStatement)statement;
							if(fors.initializers().size() > 0){	//�ϐ��錾���Ȃ��ꍇ������B
								Object o = fors.initializers().get(0);
								if(o instanceof VariableDeclarationExpression){
									VariableDeclarationExpression e = (VariableDeclarationExpression)o;
									list.add(e);
								}
							}
						}else if(statement.getNodeType() == ASTNode.ENHANCED_FOR_STATEMENT){
							//for���̒��̐錾�́ASingleVariableDeclaration ������B
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
			
			
			//1��̊K�w�֐i��
			current = current.getParent();
		}
	}

	
	/**
	 * function ��DB�p��String ���쐬����B
	 * ���� ITypeBinding
	 * @param itb
	 * @param className
	 * @param functions
	 * @param isImport
	 * @param isThisClass ���ݕҏW���̃N���X���H
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
	 * function ��DB�p��String ���쐬����B
	 * ���� IType
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
			//static ��2�֐��𐶐�����B
			return iVariableBindingToDbStringStatic2(ivb, parentClass, isImport, inThisClass);
		else
			//static ��1�����֐��𐶐�����B
			return iVariableBindingToDbStringStatic1(ivb, parentClass, isImport, inThisClass);
	}
	/**
	 *	IVariableBinding�̓��e��DBString�ɂ��ĕԂ�
	 * @param ivb
	 * @param parentClass	�e�N���X
	 * @return
	 */
	public static List<String> iVariableBindingToDbStringStatic2(IVariableBinding ivb, String parentClass, boolean isImport, boolean inThisClass){
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> ���̂悤�Ȏ��A,��|�ɂ���B
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

		//�N���X���̏����o��(static��2�񏑂�)
		for(int i = 0; i < loop; i++){
			String name = ivb.getName();
			String return_type = getITypeBindingName(ivb.getType(), isImport);
			String type = "localvariable";
			String label = name;

			if(ivb.isField()){
				type = "field";
				if(i == 0){
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+",";
					ret.add(a);
				}else{
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(getSimpleClassName(parentClass), ";") + ";" + splitName(label, ";")+",";
					ret.add(a);
				}
			}else{
				//localtype
				//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+",";
				ret.add(a);
				//char��int�Ƃ��Ă��F�������邽�߂����P�֐������B
				if(return_type.equals("char")){
					return_type = "int";
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					String a2 = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+",";
					ret.add(a2);
				}
			}
		}
		return ret;
	}

	/**
	 *	IVariableBinding�̓��e��DBString�ɂ��ĕԂ�
	 *
	 * static ��1�����֐��𐶐�����B
	 
	 * @param ivb
	 * @param parentClass	�e�N���X
	 * @return
	 */
	public static List<String> iVariableBindingToDbStringStatic1(IVariableBinding ivb, String parentClass, boolean isImport, boolean inThisClass){
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> ���̂悤�Ȏ��A,��|�ɂ���B
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

		//�N���X���̏����o��(static��2�񏑂�)
		for(int i = 0; i < 1; i++){
			String name = ivb.getName();
			String return_type = getITypeBindingName(ivb.getType(), isImport);
			String type = "localvariable";
			String label = name;

			if(ivb.isField()){
				type = "field";
				if(loop == 1){
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+",";
					ret.add(a);
				}else{
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(getSimpleClassName(parentClass), ";") + ";" + splitName(label, ";")+",";
					ret.add(a);
				}
			}else{
				//localtype
				//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+",";
				ret.add(a);
				//char��int�Ƃ��Ă��F�������邽�߂����P�֐������B
				if(return_type.equals("char")){
					return_type = "int";
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
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
			//static ��2�֐��𐶐�����B
			return iFieldToDbStringStatic2(ifd, parentClass, isImport, inThisClass);
		else
			//static�̎��Ɉ���1��
			return iFieldToDbStringStatic1(ifd, parentClass, isImport, inThisClass);
	}
	
	/**
	 *	IField�̓��e��DBString�ɂ��ĕԂ�
	 * @param ifd
	 * @param parentClass	�e�N���X
	 * @return
	 * @throws JavaModelException 
	 */
	public static List<String> iFieldToDbStringStatic2(IField ifd, String parentClass, boolean isImport, boolean inThisClass) throws JavaModelException{
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> ���̂悤�Ȏ��A,��|�ɂ���B
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

		//�N���X���̏����o��(static��2�񏑂�)
		for(int i = 0; i < loop; i++){
			String name = ifd.getElementName();
			String return_type = Signature.toString(ifd.getTypeSignature()); //getITypeBindingName(ifd.getTypeSignature(), isImport);
			String type = "field";
			String params = "";
			String label = name;
			
			if(i == 0){
				params = parentClass;
				//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+","+params;
				ret.add(a);
			}else{
				//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(getSimpleClassName(parentClass), ";") + ";" + splitName(label, ";")+","+params;
				ret.add(a);
			}
			
		}
		return ret;
	}
	
	/**
	 *	IField�̓��e��DBString�ɂ��ĕԂ�
	 *
	 * static�̎��Ɉ���1��
	 
	 * @param ifd
	 * @param parentClass	�e�N���X
	 * @return
	 * @throws JavaModelException 
	 */
	public static List<String> iFieldToDbStringStatic1(IField ifd, String parentClass, boolean isImport, boolean inThisClass) throws JavaModelException{
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> ���̂悤�Ȏ��A,��|�ɂ���B
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

		//�N���X���̏����o��(static��2�񏑂�)
		for(int i = 0; i < 1; i++){
			String name = ifd.getElementName();
			String return_type = Signature.toString(ifd.getTypeSignature()); //getITypeBindingName(ifd.getTypeSignature(), isImport);
			String type = "field";
			String params = "";
			String label = name;
			
			if(loop == 1){
				params = parentClass;
				//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type +"," + formatTypeName(return_type) + "," + name + ","+ splitName(label, ";")+","+params;
				ret.add(a);
			}else{
				//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
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
			//static ��2�֐��𐶐�����B
			return iMethodBindingToDbStringStatic2(imb, parentClass, isImport, inThisClass, flg_isConstructorWithNew);
		else
			//static �̎��@�P�����B
			return iMethodBindingToDbStringStatic1(imb, parentClass, isImport, inThisClass, flg_isConstructorWithNew);
	}
	
	/**
	 *	IMethodBinding�̓��e��DBString�ɂ��ĕԂ�
	 * @param imb
	 * @param parentClass	�e�N���X
	 * @return
	 */
	public static List<String> iMethodBindingToDbStringStatic2(IMethodBinding imb, String parentClass, boolean isImport, boolean inThisClass, boolean isConstructorWithNew){
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> ���̂悤�Ȏ��A,��|�ɂ���B

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

		//�N���X���̏����o��(static�� ���x����2��ޏ���)
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
				//�p�����[�^�[��ǉ�
				for(ITypeBinding itb: imb.getParameterTypes()){
					params += getITypeBindingName(itb, isImport) + ",";
				}
				String a = "";
				if(isConstructorWithNew)
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + "new;" + splitName(label, ";") + "," + params;
				else
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}else{//method�̏ꍇ�B
				if(i == 0){
					if(Modifier.isStatic(mod) == false){//�C���X�^���X���\�b�h
						params = parentClass;//���V�[�o�����p�����[�^�ƂȂ�B
						//�p�����[�^�[��ǉ�
						for(ITypeBinding itb: imb.getParameterTypes()){
							params += "," + getITypeBindingName(itb, isImport);
						}
						//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
						String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
						ret.add(a);
						//���̂悤�ɂ���Ă��A�^�̏��Ԃ�����邩�炤�܂������Ȃ��B
//						if(parentClass.equals("this")){
//							//�p�����[�^�Ȃ��i���V�[�o�Ȃ��ŋ쓮�jVER�������B
//							//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
//							String aa = parentClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + ",";
//							ret.add(aa);
//						}
						return ret;
					}else{//static ���\�b�h
						//�p�����[�^�[��ǉ�
						for(int j = 0; j < imb.getParameterTypes().length; j++){
							params += getITypeBindingName(imb.getParameterTypes()[j], isImport);
							if(j < imb.getParameterTypes().length -1){
								params += ",";
							}
						}
					}
				}else{
					//static ���\�b�h2��� (���x���ɃN���X�����܂ށj
					if(parentClass.equals("this"))
						label += "This";//���Ƃ�label��splitName�ŏ������邽�߁A�擪��啶���ɂ���B
					else
						label += getSimpleClassName(parentClass); //���x����parent��simpleName��ǉ�����B
					//�p�����[�^�[��ǉ�
					for(int j = 0; j < imb.getParameterTypes().length; j++){
						params += getITypeBindingName(imb.getParameterTypes()[j], isImport);
						if(j < imb.getParameterTypes().length -1){
							params += ",";
						}
					}
					
				}
				
				//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}
		}
		return ret;
	}
	
	/**
	 *	IMethodBinding�̓��e��DBString�ɂ��ĕԂ�
	 * @param imb
	 * @param parentClass	�e�N���X
	 * @return
	 */
	public static List<String> iMethodBindingToDbStringStatic1(IMethodBinding imb, String parentClass, boolean isImport, boolean inThisClass, boolean isConstructorWithNew){
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> ���̂悤�Ȏ��A,��|�ɂ���B

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

		//�N���X���̏����o��(static�� ���x����2��ޏ���)
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
				//�p�����[�^�[��ǉ�
				for(ITypeBinding itb: imb.getParameterTypes()){
					params += getITypeBindingName(itb, isImport) + ",";
				}
				String a = "";
				if(isConstructorWithNew)
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + "new;" + splitName(label, ";") + "," + params;
				else
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}else{//method�̏ꍇ�B
				if(loop == 1){
					if(Modifier.isStatic(mod) == false){//�C���X�^���X���\�b�h
						params = parentClass;//���V�[�o�����p�����[�^�ƂȂ�B
						//�p�����[�^�[��ǉ�
						for(ITypeBinding itb: imb.getParameterTypes()){
							params += "," + getITypeBindingName(itb, isImport);
						}
						//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
						String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
						ret.add(a);
						//���̂悤�ɂ���Ă��A�^�̏��Ԃ�����邩�炤�܂������Ȃ��B
//						if(parentClass.equals("this")){
//							//�p�����[�^�Ȃ��i���V�[�o�Ȃ��ŋ쓮�jVER�������B
//							//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
//							String aa = parentClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + ",";
//							ret.add(aa);
//						}
						return ret;
					}else{//static ���\�b�h
						//�p�����[�^�[��ǉ�
						for(int j = 0; j < imb.getParameterTypes().length; j++){
							params += getITypeBindingName(imb.getParameterTypes()[j], isImport);
							if(j < imb.getParameterTypes().length -1){
								params += ",";
							}
						}
					}
				}else{
					//static ���\�b�h2��� (���x���ɃN���X�����܂ށj
					if(parentClass.equals("this"))
						label += "This";//���Ƃ�label��splitName�ŏ������邽�߁A�擪��啶���ɂ���B
					else
						label += getSimpleClassName(parentClass); //���x����parent��simpleName��ǉ�����B
					//�p�����[�^�[��ǉ�
					for(int j = 0; j < imb.getParameterTypes().length; j++){
						params += getITypeBindingName(imb.getParameterTypes()[j], isImport);
						if(j < imb.getParameterTypes().length -1){
							params += ",";
						}
					}
					
				}
				
				//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
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
			//static ��2�֐��𐶐�����B
			return iMethodToDbStringStatic2(im, parentClass, isImport, inThisClass, flg_isConstructorWithNew);
		else
			//static �̎�1��
		return iMethodToDbStringStatic1(im, parentClass, isImport, inThisClass, flg_isConstructorWithNew);
	}
	/**
	 *	IMethod�̓��e��DBString�ɂ��ĕԂ�
	 * @param im
	 * @param parentClass	�e�N���X
	 * @return
	 * @throws JavaModelException 
	 */
	public static List<String> iMethodToDbStringStatic2(IMethod im, String parentClass, boolean isImport, boolean inThisClass, boolean isConstructorWithNew) throws JavaModelException{
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> ���̂悤�Ȏ��A,��|�ɂ���B

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

		//�N���X���̏����o��(static�� ���x����2��ޏ���)
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
				//�p�����[�^�[��ǉ� 
//				for(ITypeBinding itb: im.getParameterTypes()){
//					params += getITypeBindingName(itb, isImport) + ",";
//				}
				for(String s: im.getParameterTypes()){
					params += Signature.toString(s) + ",";
				}
				String a = "";
				if(isConstructorWithNew)
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + "new;" + splitName(label, ";") + "," + params;
				else
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
					
				ret.add(a);

			}else{//method�̏ꍇ�B
				if(i == 0){
					if(Modifier.isStatic(mod) == false){//�C���X�^���X���\�b�h
						params = parentClass;//���V�[�o�����p�����[�^�ƂȂ�B
						//�p�����[�^�[��ǉ�
//						for(ITypeBinding itb: im.getParameterTypes()){
//							params += "," + getITypeBindingName(itb, isImport);
//						}
						for(String s: im.getParameterTypes()){
							params += "," + Signature.toString(s);
						}
					}else{//static ���\�b�h
						//�p�����[�^�[��ǉ�
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
					//static ���\�b�h (���x���ɃN���X�����܂ށj
					if(parentClass.equals("this"))
						label += "This";//���Ƃ�label��splitName�ŏ������邽�߁A�擪��啶���ɂ���B
					else
						label += getSimpleClassName(parentClass); //���x����parent��simpleName��ǉ�����B
					//�p�����[�^�[��ǉ�
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
				
				//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}
		}
		return ret;
	}

	/**
	 *	IMethod�̓��e��DBString�ɂ��ĕԂ�
	 * @param im
	 * @param parentClass	�e�N���X
	 * @return
	 * @throws JavaModelException 
	 */
	public static List<String> iMethodToDbStringStatic1(IMethod im, String parentClass, boolean isImport, boolean inThisClass, boolean isConstructorWithNew) throws JavaModelException{
		parentClass = parentClass.replace(",", "|");// java.util.Map<java.lang.Integer,java.lang.String> ���̂悤�Ȏ��A,��|�ɂ���B

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

		//�N���X���̏����o��(static�� ���x����2��ޏ���)
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
				//�p�����[�^�[��ǉ� 
//				for(ITypeBinding itb: im.getParameterTypes()){
//					params += getITypeBindingName(itb, isImport) + ",";
//				}
				for(String s: im.getParameterTypes()){
					params += Signature.toString(s) + ",";
				}
				String a = "";
				if(isConstructorWithNew)
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + "new;" + splitName(label, ";") + "," + params;
				else
					//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
					a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}else{//method�̏ꍇ�B
				if(loop == 2){
					if(Modifier.isStatic(mod) == false){//�C���X�^���X���\�b�h
						params = parentClass;//���V�[�o�����p�����[�^�ƂȂ�B
						//�p�����[�^�[��ǉ�
//						for(ITypeBinding itb: im.getParameterTypes()){
//							params += "," + getITypeBindingName(itb, isImport);
//						}
						for(String s: im.getParameterTypes()){
							params += "," + Signature.toString(s);
						}
					}else{//static ���\�b�h
						//�p�����[�^�[��ǉ�
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
					//static ���\�b�h (���x���ɃN���X�����܂ށj
					if(parentClass.equals("this"))
						label += "This";//���Ƃ�label��splitName�ŏ������邽�߁A�擪��啶���ɂ���B
					else
						label += getSimpleClassName(parentClass); //���x����parent��simpleName��ǉ�����B
					//�p�����[�^�[��ǉ�
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
				
				//�N���X��(parentClass)//�C���q(isStatic, isFinal, type)//�Ԃ�l�̌^(return_type)//Function��(name)//label//params
				String a = parentClass + "," + inThisClass + "," + isStatic + "," + isFinal + "," + type + "," + formatTypeName(return_type) + "," + name + "," + splitName(label, ";") + "," + params;
				ret.add(a);

			}
		}
		return ret;
	}
	
	//���O�̑啶���̕����̑O�ɕ�����with��}�����A�啶�����������ɕϊ�����
	//�Ō��1�������啶���̏ꍇ�͐؂�Ȃ��B
	//�啶�����A�����Ă���ꍇ�́A�؂�Ȃ��B
	// "_" �� �؂�B
	public static String splitName(String name,  String with){
		String ret = name;
		// "_" �ŋ�؂�B
		if(name.contains("_")){
			ret = splitName_splitByUnderBar(name, with);
		}else if(isAllUpperCase(name)){
			//Void.TYPE �̂悤�ȑS�đ啶���̎�
			ret = name.toLowerCase().trim();
		}else{
			// �啶���ŋ�؂�B
			ret = splitName_splitByUpperCase(name, with);
		}
		return ret;
	}

	public static String splitName_splitByUnderBar(String name,  String with){
		name = name.replaceAll("_", with);
		return name.toLowerCase().trim();
	}
	
	//�����񂪑S�đ啶�������m���߂�
	public static boolean isAllUpperCase(String name){
		/*
		 * �ꕶ���ł�������������΁Afalse
		 */
		StringBuffer sb = new StringBuffer(name);
		for(int i = 0; i < sb.length(); i++){
			//�啶���i�ʒui�j��������
			if(Character.isLowerCase(sb.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	public static String splitName_splitByUpperCase(String name,  String with){
		StringBuffer sb = new StringBuffer(name);
		for(int i = 0; i < sb.length() -1; i++){
			//�啶���i�ʒui�j��������
			if(Character.isUpperCase(sb.charAt(i))){
				
				//�����A�����đ啶���Ȃ�A������B
				if(Character.isUpperCase(sb.charAt(i+1))){
					continue;
				}
				//1�O�̕������啶���Ȃ�A������B
				if(i > 0 && Character.isUpperCase(sb.charAt(i-1))){
					continue;
				}
				//���̑啶���̑O�ɕ�����with��}��
				if(i >= 1){
					sb.insert(i, with);
					//������with�̒���������i��i�߂�
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
	 * �N���X�̃W�F�l���N�X���l�������^�̖��O��Ԃ�
	 *
	 * �C���|�[�g���̏ꍇ�A
	 * �N���X�̃W�F�l���N�X���l�����Ȃ�
	 *
	 * @param itb
	 * @return
	 */
	public static String getITypeBindingName(ITypeBinding itb, boolean isImport){
		//����������getQualifiedName���ƁA�W�F�l���N�X�������Ă��܂�
		if(isImport)
			itb = itb.getErasure();//�C���|�[�g���̏ꍇ�A�W�F�l���N�X��r������B
		String ret = itb.getQualifiedName().replace(",", "|");	//DB�̋�؂蕶��","�Əd������̂������B
		return formatTypeName(ret);
		//return itb.getQualifiedName().replace(",", "|");	//DB�̋�؂蕶��","�Əd������̂������B
		
	}

	/**
	 * 1�s�̃L�[���[�h������𐮌`����B
	 * �����������āAtrim����B
	 * @return
	 */
	public static String formatKeywords(String keywords){
		//������������������āAtrim����B
		return keywords.toLowerCase().trim();
	}

	// ��{�f�[�^�^�͑S�ăI�u�W�F�N�g�^�ɕϊ�����B
	public static String formatTypeName(String name){

		// ��{�f�[�^�^�͑S�ăI�u�W�F�N�g�^�ɕϊ�����B
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
	 * �e�N���X�̖��O��Ԃ�
	 */
	public static String getParentTypeNameWithPackageName(ASTNode node){
		//�N���X�錾���擾����B
		while(!(node instanceof TypeDeclaration) && node != null){
			//enum��null���A���Ă��Ă��܂��I�I�@EnumDeclaration�@������ׂ����B ���߂��BresolveBinding�@���Ȃ��B
			node = node.getParent();
		}
		
		TypeDeclaration td = (TypeDeclaration)node;
		ITypeBinding itb = td.resolveBinding();
		return itb.getQualifiedName();
	}
	
	//�C�����t���Ȃ��N���X����Ԃ�.(java.lang.StringXyz�Ȃ�΁AStringXyz��Ԃ��B)
	public static String getSimpleClassName(String name){
		//��������T���āA��Ԏ�O�ɂ���.��������
		int last = name.lastIndexOf(".");
		if(last == -1)
			return name;
		return name.substring(last + 1);//�ԍ�last�̎�����n�܂镔���������Ԃ�
	}



	/*
	 * �I���f�}���h�^�̃C���|�[�g������������B
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
		//IPackageFragmentRoot �Ƃ� src�t�H���_�Ȃǂ̎��B
		for (IPackageFragmentRoot root: roots) {
			for (IJavaElement el : root.getChildren()) {//�p�b�P�[�W������.
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
//			// �v���W�F�N�g����Ώۂɂ����T�u�^�C�v�Q�̎擾(�N���X���C���^�t�F�[�X��)
//			IType[] subclasses = hierarchy.getSubtypes(baseType);
//			//consoleStream.print(className);//�e�N���X��
////			System.out.print(className);
////			printWriter_Sub.print(className);
//			for(IType t: subclasses){
//				String subclassName = t.getFullyQualifiedName();
//				//�p�^�[���}�b�`�F�w��p�b�P�[�W�̒��ɂ���N���X�݂̂��擾����B
//				for(String p: packages){
//					//�p�^�[���}�b�`�F�N���X����'$'���܂܂�Ă���Ώ��O
//					if(subclassName.indexOf('$') > -1){
//						continue;
//					}
//					//�擪�������w��p�b�P�[�W���Ŏn�܂�A�p�b�P�[�W���̎����啶���ł��邩������
//					Pattern pattern1 = Pattern.compile("^"+ p + "\\.[A-Z].*");
//					Matcher matcher1 = pattern1.matcher(subclassName);
//					//�p�^�[���}�b�`�F�N���X���ɐ������܂܂�Ă���Ώ��O
//					Pattern pattern2 = Pattern.compile("[^0-9]+");
//					Matcher matcher2 = pattern2.matcher(subclassName);
//					//�p�^�[���}�b�`�F�N���X����Impl�ŏI���
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
//			// TODO �����������ꂽ catch �u���b�N
//			e.printStackTrace();
//		}
	}
	
	/*
	 * impd�̃X�[�p�[�^�C�v���܂߂�
	 * �C���|�[�g��������������֐�.
	 */
	public static void getImportDeclarationSuperTypes(ImportDeclaration impd, List<String> classesInImports, List<String> functionsInImports){
		
		Name name = impd.getName();
		ITypeBinding itb = name.resolveTypeBinding();
		List<String> types = new ArrayList<String>();//�����A�e�A�c���A�]�c���E�E�E�̏��ɓ���.
		
		if(itb == null){//static��import
			types.add(name.getFullyQualifiedName());
			return;
		}
		
		/*
		 * java.lang.Object�ɓ��B�����甲����
		 */
		while(!itb.getBinaryName().equals("java.lang.Object")){
			String className = itb.getBinaryName();
			types.add(className);
			createFunctionDbString(itb, className, functionsInImports, true, false);
			itb = itb.getSuperclass();
			if(itb == null)//interface�ȂǃN���X�ȊO�̂Ƃ���null�ɂȂ�
				break;
		}
	
		/*
		 * classesInImports
		 * 
		 * �v�fNO, ���e 
		 * 1, ����
		 * 2, �e, ����
		 * 3, �c��, �e, ����
		 * 4, �]�c��, �c��, �e, ����
		 */
		String s = "";
		for(int i = 0; i < types.size(); i++){
			s = types.get(i) + "," + s;	//�O�ɑO�ɒǉ����Ă���.
			classesInImports.add(s);
		}
		
		ITypeBinding start = name.resolveTypeBinding();
		String myName = start.getBinaryName();
		//�C���^�[�t�F�[�X�������̐e�ɂȂ�.
		for(ITypeBinding i: start.getInterfaces()){
			String interfaceName = i.getBinaryName();
			createFunctionDbString(i, interfaceName, functionsInImports, true, false);
			classesInImports.add(interfaceName + "," + myName);
		}
	}
	
	
	/*
	 * ITypeBinding itb �̃X�[�p�[�N���X�ƃC���^�[�t�F�[�X���擾����֐�
	 */
	public static void getImportDeclarationSuperTypesAndInterfaces(List<List<String>> pairs, List<String> classesInImports){
		
		List<List<String>> descendantsList = new ArrayList<List<String>>();//�����Ƃ��̎q��
		List<List<String>> descendantsList2 = new ArrayList<List<String>>();//�����Ƃ��̎q��
		
		//�v�f�̃R�s�[
		for(List<String> pair: pairs){
			descendantsList.add(pair);
			descendantsList2.add(pair);
		}
		
		boolean isChange = false;
		while(true){
			for(List<String> descendants: descendantsList){
				String parent = descendants.get(descendants.size()-1);//��ԎႢ�q����e�Ƃ��Č���B
				for(List<String> descendants2: descendantsList2){
					String child = descendants2.get(0);//��ԏ�̐e���q�Ƃ��Č���B
					if(child.equals(parent)){//�q�Ɛe����v������A
						descendants.addAll(descendants2.subList(1, descendants2.size()));//���̎q���̑S�Ă�ǉ�����B
						isChange = true;
					}
				}
			}
			if(isChange == false){
				//�P�x���ω����Ȃ������烋�[�v�𔲂���B
				break;
			}
			isChange = false;//�t���O���Z�b�g
			
		}
		
		//��Ԑ擪���������̂�����΁A���̎q�����܂Ƃ߂�B
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
		
		//classesInImports�ɒǉ�����B
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
	 * �̗v�f�̏d�����Ȃ����B
	 */
	public static void compactFunctionDBstring(List<String> functionsInImports){
		//�d����h���ŁA�\�[�g������B
		TreeSet<String> set = new TreeSet<String>(functionsInImports);
		
		//����Ȃ����B
		functionsInImports.clear();
		functionsInImports.addAll(set);
	}
	
	
	/*
	 * Type��DBString�̌`���́A
	 * �擪���������g�i�e�j�ŁA
	 * 2�Ԗڈȍ~���q�ǂ��ŁA�q���̏����͖��Ȃ��B
	 * �Ƃ����`���ł���B
	 * 
	 * ���̊֐���
	 * ��Ԑ擪���������̂�����΁A���̎q�����܂Ƃ߂�֐��B
	 * 
	 * �q���̏����͖��Ȃ��B
	 * 
	 */
	public static void compactTypeDBstring(List<String> classesInImports){
		List<String> list1 = new ArrayList<String>(classesInImports);//�����Ƃ��̎q��
		List<String> list2 = new ArrayList<String>(classesInImports);//�����Ƃ��̎q��
		
		//�L�[���e�A�v�f���q
		HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
//		HashMap<String, String> map = new HashMap<String, String>();
		
		//��Ԑ擪���������̂�����΁A���̎q�����܂Ƃ߂�B
		for(int i = 0; i < list1.size(); i++){
			String []types1 = list1.get(i).split(",");
			//�q�����̃��X�g
			List<String> types1_children_list = Arrays.asList(types1).subList(1, types1.length);
			HashSet<String> child_set = new HashSet<String>(types1_children_list);
			
			for(int j = i+1; j < list2.size(); j++){
				String []types2 = list2.get(j).split(",");
				//�q�����̃��X�g
				List<String> types2_children_list = Arrays.asList(types2).subList(1, types2.length);
				
				if(types1[0].equals(types2[0])){	//��Ԑ擪(�e�j������
					child_set.addAll(types2_children_list);//Set�Ȃ̂Ŏ����I�ɏd����h��
				}
			}
			
			HashSet<String> child_set2 = map.get(types1[0]);
			if(child_set2 != null){	//���łɃL�[�����݂���ꍇ
				child_set2.addAll(child_set);
				map.put(types1[0], child_set2);
			}else{
				map.put(types1[0], child_set);//�V�K�ǉ�
			}

		}
		
        //classesInImports�ɒǉ�����B
		classesInImports.clear();
		Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String parent = it.next();
            //�\�[�g���Ă����B
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
	 * ������SuperType�i�N���X�A�C���^�[�t�F�[�X�j��List�ɓ���ĕԂ��B
	 */
	public static List<ITypeBinding> getSuperTypes(ITypeBinding itb){
		
		if(itb == null){
			return null;
		}
		
		List<ITypeBinding> list = new ArrayList<ITypeBinding>();
		
		//�C���^�[�t�F�[�X�̏����B
		ITypeBinding[] itb_interfaces = itb.getInterfaces();
		if(itb_interfaces.length > 0)
		for(ITypeBinding itb_interface: itb_interfaces){
			list.add(itb_interface);
		}
		
		//�X�[�p�[�N���X�̏����B
		ITypeBinding itb_superclass = itb.getSuperclass();
		if(itb_superclass != null){
			list.add(itb_superclass);
			list.addAll(getSuperTypes(itb_superclass));//�X�[�p�[�N���X�̃X�[�p�[�N���X�����B
		}
		
		return list;
	}
	
	
	/*
	 * ������SuperType�i�N���X�A�C���^�[�t�F�[�X�j��List�ɓ���ĕԂ��B
	 */
//	public static List<ITypeBinding> getSuperTypes(IType itb){
//		
//		if(itb == null){
//			return null;
//		}
//		
//		List<ITypeBinding> list = new ArrayList<ITypeBinding>();
//		
//		//�C���^�[�t�F�[�X�̏����B
//		ITypeBinding[] itb_interfaces = itb.get
//		if(itb_interfaces.length > 0)
//		for(ITypeBinding itb_interface: itb_interfaces){
//			list.add(itb_interface);
//		}
//		
//		//�X�[�p�[�N���X�̏����B
//		ITypeBinding itb_superclass = itb.getSuperclass();
//		if(itb_superclass != null){
//			list.add(itb_superclass);
//			list.addAll(getSuperTypes(itb_superclass));//�X�[�p�[�N���X�̃X�[�p�[�N���X�����B
//		}
//		
//		return list;
//	}
	
	
	/*
	 * �e�q�y�A�̃��X�g���쐬���āA���̃��X�g�Q��types�Ɋi�[����B
	 */
	public static void getParentChildPairsLists(ITypeBinding itb, List<List<String>> pairs){
		
		if(itb == null){
			return;
		}
		
		String className = itb.getBinaryName();
		
		//�C���^�[�t�F�[�X�̏����B
		ITypeBinding[] itb_interfaces = itb.getInterfaces();
		for(ITypeBinding itb_interface: itb_interfaces){
			List<String> parent_child_pair = new ArrayList<String>();
			parent_child_pair.add(itb_interface.getBinaryName());
			parent_child_pair.add(className);
			pairs.add(parent_child_pair);
			getParentChildPairsLists(itb_interface, pairs);
		}
		
		//�X�[�p�[�N���X�̏����B
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
	 * �e�q�y�A�̃��X�g���쐬���āA���̃��X�g�Q��types�Ɋi�[����B
	 */
	public static void getParentChildPairsLists(IType itb, List<List<String>> pairs) throws JavaModelException{
		
		if(itb == null){
			return;
		}
		
		String className = itb.getElementName();
		
		//�C���^�[�t�F�[�X�̏����B
		for(String name: itb.getSuperInterfaceNames()){
			List<String> parent_child_pair = new ArrayList<String>();
			parent_child_pair.add(name);
			parent_child_pair.add(className);
			pairs.add(parent_child_pair);
		}
		
		//�X�[�p�[�N���X�̏����B
		String itb_superclass = itb.getSuperclassName();
		if(itb_superclass != null){
			List<String> parent_child_pair = new ArrayList<String>();
			parent_child_pair.add(itb_superclass);
			parent_child_pair.add(className);
			pairs.add(parent_child_pair);
		}
	}
	
	
	/*
	 * ������node���ǂ̂悤�ȏ󋵂Ŏg�p����Ă��邩���ׂ�B
	 * 
	 * Return�� �̒�
	 * If���@�̒�
	 * �錾��
	 * �����
	 * �n�̕�
	 * ���\�b�h�̈����̒�
	 */
	public static int inspectNodeType(ASTNode node) {
		ASTNode current = node;
		int count_method_invocation = 0;
		while(true){
			switch(current.getNodeType()){
				case ASTNode.CLASS_INSTANCE_CREATION:	//�C���X�^���X����
				case ASTNode.METHOD_INVOCATION:	//���\�b�h�R�[��
					if(count_method_invocation > 0)	//���̃P�[�X��2��ڂȂ�A
						return IN_METHOD_INVOCATION;
					count_method_invocation++;
					current = current.getParent();
					break;
				case ASTNode.INFIX_EXPRESSION:	//�������蕶
					return INFIX_EXPRESSION;
				case ASTNode.IF_STATEMENT:	//If��
					return IF_STATEMENT;
				case ASTNode.WHILE_STATEMENT:	//while��
					return WHILE_STATEMENT;
				case ASTNode.RETURN_STATEMENT:	//return��
					return RETURN_STATEMENT;
				case ASTNode.VARIABLE_DECLARATION_STATEMENT:	//�ϐ��錾��
					return VARIABLE_DECLARATION_STATEMENT;
				case ASTNode.FIELD_DECLARATION:	//�t�B�[���h�錾��
					return FIELD_DECLARATION;
				case ASTNode.ASSIGNMENT:	//�����
					return ASSIGNMENT;
				case ASTNode.BLOCK:	//�u���b�N
					//�u���b�N�܂ł�����A���\�b�h�R�[���܂��̓��\�b�h�̈����̒��̌Ăяo���B
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
	public static final int IN_METHOD_INVOCATION = 6;	//���\�b�h�̈����̒�
	public static final int CLASS_INSTANCE_CREATION = 7;
	public static final int IF_STATEMENT = 8;
	public static final int WHILE_STATEMENT = 9;
	public static final int FIELD_DECLARATION = 10;
	
	public static String nodeTypeToString(int nodeType) {
		
		switch(nodeType){
			case METHOD_INVOCATION:	//���\�b�h�R�[��
				return "METHOD_INVOCATION";
			case INFIX_EXPRESSION:	//�������蕶
				return "INFIX_EXPRESSION";
			case RETURN_STATEMENT:	//return��
				return "RETURN_STATEMENT";
			case VARIABLE_DECLARATION_STATEMENT:	//�錾��
				return "VARIABLE_DECLARATION_STATEMENT";
			case ASSIGNMENT:	//�����
				return "ASSIGNMENT";
			case BLOCK:	//�u���b�N
				return "BLOCK";
			case IN_METHOD_INVOCATION:	//���\�b�h�̈���
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
			case METHOD_INVOCATION:	//���\�b�h�R�[��
//				return "�n�̕��i���\�b�h�Ăяo���j";
				return "�n�̕�";
			case INFIX_EXPRESSION:	//�������蕶
				return "�������蕶";
			case RETURN_STATEMENT:	//return��
				return "Return��";
			case VARIABLE_DECLARATION_STATEMENT:	//�錾��
				return "�ϐ��錾��";
			case ASSIGNMENT:	//�����
				return "�����";
			case BLOCK:	//�u���b�N
				return "�u���b�N�̒�";
			case IN_METHOD_INVOCATION:	//���\�b�h�̈���
				return "���\�b�h�̈�����";
			case CLASS_INSTANCE_CREATION:
				return "�C���X�^���X����";
			case IF_STATEMENT:
				return "If��";
			case WHILE_STATEMENT:
				return "While��";
			case FIELD_DECLARATION:
				return "�t�B�[���h�錾��";	//�t�B�[���h�錾��
			default:
				return "default";
		}
	}

	public static String keywords;
	public static String desiredReturnType;
	public static int keyword_head_offset;
	public static String japanese_label	= "";
	
	/*
	 * �]�܂����Ԃ�l�̌^��
	 * ���̓L�[���[�h�𒲂ׁA�������B
	 */
	public static void inspectDesiredReturnTypeAndKeywords(MethodDeclaration methodDec, ASTNode node, int nodeType) {
		switch(nodeType){
			case METHOD_INVOCATION:	//���\�b�h�R�[��
				//�n�̕��B�������Ȃ��B
				break;
			case INFIX_EXPRESSION:	//�������蕶
//				System.out.println(node.getLocationInParent().toString());
				break;
			case RETURN_STATEMENT:	//return��
				//�擪�̕�����"return"�Ȃ�΁A���̊֐��̕Ԃ�l���AdesiredType
				keyword_head_offset += 7;// 6("return"�̒���) + 1(�X�y�[�X�̕�) �����L�[���[�h�J�n�ʒu�������B
				if(keywords.length() > 7)
					keywords = keywords.substring(7);//"return"�̎��̈ʒu����؂���
				else
					keywords = "";
				desiredReturnType = AstUtil.getITypeBindingName(methodDec.resolveBinding().getReturnType(), false);
				break;
			case VARIABLE_DECLARATION_STATEMENT:	//�錾��
				//"="�̉E�ӂ��L�[���[�h�B���ӂ�desiredType�ƔF���B
				int id = keywords.indexOf("=");
				keyword_head_offset += id + 1;// �u���J�n�ʒu��"="�̎��̕�������B
				keywords = keywords.substring( id + 1 );//"="�̎��̈ʒu����؂���
				//�ϐ��錾�̃m�[�h���擾����
				VariableDeclarationStatement v = AstUtil.getLastVariableDeclarationStatement(node);
				Type t =  v.getType();
				desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
				break;
			case FIELD_DECLARATION:	//�t�B�[���h�錾��
				//"="�̉E�ӂ��L�[���[�h�B���ӂ�desiredType�ƔF���B
				int id1 = keywords.indexOf("=");
				keyword_head_offset += id1 + 1;// �u���J�n�ʒu��"="�̎��̕�������B
				keywords = keywords.substring( id1 + 1 );//"="�̎��̈ʒu����؂���
				//�ϐ��錾�̃m�[�h���擾����
				FieldDeclaration f = AstUtil.getLastFieldDeclarationStatement(node);
				Type t1 =  f.getType();
				desiredReturnType = AstUtil.getITypeBindingName(t1.resolveBinding(), false);
				break;
			case ASSIGNMENT:	//�����
				//"="�̉E�ӂ��L�[���[�h�B���ӂ�desiredType�ƔF���B
				int id11 = keywords.indexOf("=");
				keyword_head_offset += id11 + 1;// �u���J�n�ʒu��"="�̎��̕�������B
				keywords = keywords.substring( id11 + 1 );//"="�̎��̈ʒu����؂���
				Assignment assign = AstUtil.getLastAssignmentNode(node);
				desiredReturnType = AstUtil.getITypeBindingName(assign.resolveTypeBinding(), false);
				break;
			case BLOCK:	//�u���b�N
			case IN_METHOD_INVOCATION:	//���\�b�h�̈���
				//"("���܂ޕ�����Ȃ�΁A"("�̎�O�̊֐��̈�����desiredType
				//�����J�b�R������ꍇ�ǂ�����񂾂낤�H
				//x(y(z(keywords)))�Ƃ����ꍇ�AlineIncludeKeywords��"x(y(z(keywords"�ƂȂ�B
				//�uz�����߂�����̌^�v�Ƃ������ƂɂȂ�B
				//z�̈�����1�Ȃ�ȒP���Ȃ��B2�ȏゾ�Ƃǂ��Ȃ�񂾂낤�B
				//"x(y(z(val1, val2, keywords"�@����ȏꍇ�B
				//�uz��3�Ԗڂ̈����̌^�Ɉ�v�v�Ƃ������ƂɂȂ�B
				//���������͌�񂵂ɂ���ׂ��B
				int id111 = keywords.lastIndexOf("(");//�Ō��"("
				keyword_head_offset += id111 + 2;// �u���J�n�ʒu��"("�̎��̎��̕�������B
				keywords = keywords.substring(id111 + 1);// "("�̎��̕������炪�L�[���[�h�ł���B
				MethodInvocation mi = AstUtil.getParentMethodInvocation(node);
				
				if(node.getNodeType() == ASTNode.SIMPLE_NAME)
					mi = AstUtil.getParentMethodInvocation(mi);	//SimpleName�Ȃ�΁A�����P������ǂ�B
				
				if(node.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION){	//�C���X�^���X�����̏ꍇ�B
					ClassInstanceCreation cic = (ClassInstanceCreation)node;
					Type t11 = cic.getType();
					desiredReturnType = AstUtil.getITypeBindingName(t11.resolveBinding(), false);
				}else{	//���\�b�h�Ăяo���̏ꍇ�B
					ITypeBinding[] itbs = mi.resolveMethodBinding().getParameterTypes();
					desiredReturnType = AstUtil.getITypeBindingName(itbs[0], false);//���������͌��
				}
				break;
			case CLASS_INSTANCE_CREATION:
				break;
			case IF_STATEMENT:	//if��
				int id1111 = keywords.lastIndexOf("(");//�Ō��"("
				keyword_head_offset += id1111 + 1;// �u���J�n�ʒu��"("�̎��̕�������B
				keywords = keywords.substring(id1111 + 1);// "("�̎��̕������炪�L�[���[�h�ł���B
				desiredReturnType = "java.lang.Boolean";//��������Ȃ̂ŁB
				break;
			case WHILE_STATEMENT:	//while��
				int id11111 = keywords.lastIndexOf("(");//�Ō��"("
				keyword_head_offset += id11111 + 2;// �u���J�n�ʒu��"("�̎��̎��̕�������B
				keywords = keywords.substring(id11111 + 1);// "("�̎��̕������炪�L�[���[�h�ł���B
				desiredReturnType = "java.lang.Boolean";//��������Ȃ̂ŁB
				break;
			default:
				break;
		}
	}
	
	
	/**
	 * �]�܂����Ԃ�l�̌^��node�̃��P�[�V�����𒲂ׂ�B
	 * �ꊇ�Ń^�X�N��ۑ�����Ƃ��Ɏg�p����B
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
		
		//�e�̌`�Ŕ���
		switch(parentNodeType){
			case ASTNode.VARIABLE_DECLARATION_FRAGMENT:	//�ϐ��錾
				if(id.equals("initializer")){
					//�ϐ��錾�̃m�[�h���擾����
					int type = node.getParent().getParent().getNodeType();
					if(type == ASTNode.VARIABLE_DECLARATION_STATEMENT){	//�ϐ��錾�@�ӂ��́B
						VariableDeclarationStatement v = AstUtil.getLastVariableDeclarationStatement(node);
						Type t =  v.getType();
						desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
						japanese_label = "�ϐ��錾";
					}else if(type == ASTNode.FIELD_DECLARATION){//�t�B�[���h�B
						FieldDeclaration f = AstUtil.getLastFieldDeclarationStatement(node);
						Type t1 =  f.getType();
						desiredReturnType = AstUtil.getITypeBindingName(t1.resolveBinding(), false);
						japanese_label = "�t�B�[���h�錾";
					}else if(type == ASTNode.VARIABLE_DECLARATION_EXPRESSION){	//for���̒�(�Ȃ�)�̕ϐ��錾
						VariableDeclarationExpression vde = (VariableDeclarationExpression)node.getParent().getParent();
						Type t = vde.getType();
						desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
						japanese_label = "�ϐ��錾";
					}
				}
				break;
			case ASTNode.ASSIGNMENT:	//�����
				if(id.equals("rightHandSide")){
					Assignment assign = AstUtil.getLastAssignmentNode(node);
					desiredReturnType = AstUtil.getITypeBindingName(assign.resolveTypeBinding(), false);
					japanese_label = "�����";
				}
				break;
			case ASTNode.METHOD_INVOCATION:	//���\�b�h�R�[��
				MethodInvocation mi = AstUtil.getParentMethodInvocation(node);
				if(id.equals("expression")){//�e�����\�b�h�R�[���B�����̓��V�[�o�B
					//�e�̏�������N���X�ƂȂ�B
					desiredReturnType = AstUtil.getITypeBindingName(mi.resolveMethodBinding().getDeclaringClass(), false);
					japanese_label = "���\�b�h�Ăяo����(���V�[�o)";
				}else if(id.equals("arguments")){//�e�����\�b�h�R�[���B�����͈����B
					//�������g�����Ԗڂ̈����Ȃ̂��킩���B
					ITypeBinding[] itbs = mi.resolveMethodBinding().getParameterTypes();
					if(itbs.length == 1)
						desiredReturnType = AstUtil.getITypeBindingName(itbs[0], false);//���������͌��
					japanese_label = "���\�b�h�Ăяo����(����)";
				}
				break;
			case ASTNode.CLASS_INSTANCE_CREATION:	//�C���X�^���X����
				ClassInstanceCreation cic = (ClassInstanceCreation)node.getParent();
				if(id.equals("arguments")){//�e��new���B�����͈����B
					//�������g�����Ԗڂ̈����Ȃ̂��킩���B
					ITypeBinding[] itbs = cic.resolveConstructorBinding().getParameterTypes();
					if(itbs.length == 1)
					desiredReturnType = AstUtil.getITypeBindingName(itbs[0], false);//���������͌��
					japanese_label = "�C���X�^���X������(����)";
				}
				break;
			case ASTNode.EXPRESSION_STATEMENT:	//�n�̕�
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Object";	//���ł�����B
					japanese_label = "�n�̕�";
				}
				break;
			case ASTNode.CAST_EXPRESSION:	//�^�̃L���X�g
				if(id.equals("expression")){
					CastExpression ce = (CastExpression)node.getParent();
					Type t = ce.getType();
					desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
					japanese_label = "�^�L���X�g";
				}
				break;
			case ASTNode.IF_STATEMENT:	//If��
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Boolean";//��������Ȃ̂ŁB
					japanese_label = "If����";
				}
				break;
			case ASTNode.WHILE_STATEMENT:	//while��
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Boolean";//��������Ȃ̂ŁB
					japanese_label = "While����";
				}
				break;
			case ASTNode.FOR_STATEMENT:	//for��
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Integer";//�J�E���^
					japanese_label = "for����";
				}
				break;
			case ASTNode.PREFIX_EXPRESSION:	//��������ȂǋL�����g���������B
			case ASTNode.INFIX_EXPRESSION:	//��������ȂǋL�����g���������B
				//�����P������āA���̏����Ŕ���
				//inspectDesiredReturnTypeAndKeywords2(methodDec, node.getParent());
				//�Ή��ł��Ȃ��̂�����B
				//�ꍇ�킯���ʓ|�Ȃ̂ŁA�������g�̕Ԃ�l�̌^��Ԃ��B
				if(node instanceof MethodInvocation){
					MethodInvocation mi1 = (MethodInvocation)node;
					desiredReturnType = AstUtil.getITypeBindingName(mi1.resolveTypeBinding(), false);
				}else if(node instanceof ClassInstanceCreation){
					ClassInstanceCreation cic1 = (ClassInstanceCreation)node;
					desiredReturnType = AstUtil.getITypeBindingName(cic1.resolveTypeBinding(), false);
				}else{
					//�������g���킩��Ȃ���ΐe��������B
					InfixExpression ie = (InfixExpression)node.getParent();
					desiredReturnType = AstUtil.getITypeBindingName(ie.resolveTypeBinding(), false);
				}
				japanese_label = "�I�y���[�^�[�����݂���";
				break;
			case ASTNode.RETURN_STATEMENT:
				ReturnStatement rs = (ReturnStatement)node.getParent();
				desiredReturnType = AstUtil.getITypeBindingName(rs.getExpression().resolveTypeBinding(), false);
//				System.out.println(rs.getExpression().resolveTypeBinding().getQualifiedName());
				japanese_label = "���^�[������";
				break;
			case ASTNode.THROW_STATEMENT:
				ThrowStatement ts = (ThrowStatement)node.getParent();
				desiredReturnType = AstUtil.getITypeBindingName(ts.getExpression().resolveTypeBinding(), false);
//				System.out.println(ts.getExpression().resolveTypeBinding().getQualifiedName());
				japanese_label = "�X���[����";
				break;
			default:
				japanese_label = "���̑�";
				break;
		}
	}
	

	/**
	 * �]�܂����Ԃ�l�̌^��node�̃��P�[�V�����𒲂ׂ�B
	 * �ꊇ�Ń^�X�N��ۑ�����Ƃ��Ɏg�p����B
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
		
		//�e�̌`�Ŕ���
		switch(parentNodeType){
			case ASTNode.VARIABLE_DECLARATION_FRAGMENT:	//�ϐ��錾
				if(id.equals("initializer")){
					//�ϐ��錾�̃m�[�h���擾����
					int type = node.getParent().getParent().getNodeType();
					if(type == ASTNode.VARIABLE_DECLARATION_STATEMENT){	//�ϐ��錾�@�ӂ��́B
						VariableDeclarationStatement v = AstUtil.getLastVariableDeclarationStatement(node);
						Type t =  v.getType();
						desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
						japanese_label = "�ϐ��錾";
					}else if(type == ASTNode.FIELD_DECLARATION){//�t�B�[���h�B
						FieldDeclaration f = AstUtil.getLastFieldDeclarationStatement(node);
						Type t1 =  f.getType();
						desiredReturnType = AstUtil.getITypeBindingName(t1.resolveBinding(), false);
						japanese_label = "�t�B�[���h�錾";
					}else if(type == ASTNode.VARIABLE_DECLARATION_EXPRESSION){	//for���̒�(�Ȃ�)�̕ϐ��錾
						VariableDeclarationExpression vde = (VariableDeclarationExpression)node.getParent().getParent();
						Type t = vde.getType();
						desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
						japanese_label = "�ϐ��錾";
					}
				}
				break;
			case ASTNode.ASSIGNMENT:	//�����
				if(id.equals("rightHandSide")){
					Assignment assign = AstUtil.getLastAssignmentNode(node);
					desiredReturnType = AstUtil.getITypeBindingName(assign.resolveTypeBinding(), false);
					japanese_label = "�����";
				}
				break;
			case ASTNode.METHOD_INVOCATION:	//���\�b�h�R�[��
				MethodInvocation mi = AstUtil.getParentMethodInvocation(node);
				if(id.equals("expression")){//�e�����\�b�h�R�[���B�����̓��V�[�o�B
					//�e�̏�������N���X�ƂȂ�B
					desiredReturnType = AstUtil.getITypeBindingName(mi.resolveMethodBinding().getDeclaringClass(), false);
					japanese_label = "���\�b�h�Ăяo����(���V�[�o)";
				}else if(id.equals("arguments")){//�e�����\�b�h�R�[���B�����͈����B
					//�������g�����Ԗڂ̈����Ȃ̂��킩���B
					ITypeBinding[] itbs = mi.resolveMethodBinding().getParameterTypes();
					desiredReturnType = AstUtil.getITypeBindingName(itbs[0], false);//���������͌��
					japanese_label = "���\�b�h�Ăяo����(����)";
				}
				break;
			case ASTNode.CLASS_INSTANCE_CREATION:	//�C���X�^���X����
				ClassInstanceCreation cic = (ClassInstanceCreation)node.getParent();
				if(id.equals("arguments")){//�e��new���B�����͈����B
					//�������g�����Ԗڂ̈����Ȃ̂��킩���B
					ITypeBinding[] itbs = cic.resolveConstructorBinding().getParameterTypes();
					desiredReturnType = AstUtil.getITypeBindingName(itbs[0], false);//���������͌��
					japanese_label = "�C���X�^���X������(����)";
				}
				break;
			case ASTNode.EXPRESSION_STATEMENT:	//�n�̕�
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Object";	//���ł�����B
					japanese_label = "�n�̕�";
				}
				break;
			case ASTNode.CAST_EXPRESSION:	//�^�̃L���X�g
				if(id.equals("expression")){
					CastExpression ce = (CastExpression)node.getParent();
					Type t = ce.getType();
					desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
					japanese_label = "�^�L���X�g";
				}
				break;
			case ASTNode.IF_STATEMENT:	//If��
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Boolean";//��������Ȃ̂ŁB
					japanese_label = "If����";
				}
				break;
			case ASTNode.WHILE_STATEMENT:	//while��
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Boolean";//��������Ȃ̂ŁB
					japanese_label = "While����";
				}
				break;
			case ASTNode.FOR_STATEMENT:	//for��
				if(id.equals("expression")){
					desiredReturnType = "java.lang.Integer";//�J�E���^
					japanese_label = "for����";
				}
				break;
			case ASTNode.PREFIX_EXPRESSION:	//��������ȂǋL�����g���������B
			case ASTNode.INFIX_EXPRESSION:	//��������ȂǋL�����g���������B
				//�����P������āA���̏����Ŕ���
				//inspectDesiredReturnTypeAndKeywords2(methodDec, node.getParent());
				//�Ή��ł��Ȃ��̂�����B
				//�ꍇ�킯���ʓ|�Ȃ̂ŁA�������g�̕Ԃ�l�̌^��Ԃ��B
				if(node instanceof MethodInvocation){
					MethodInvocation mi1 = (MethodInvocation)node;
					desiredReturnType = AstUtil.getITypeBindingName(mi1.resolveTypeBinding(), false);
				}else if(node instanceof ClassInstanceCreation){
					ClassInstanceCreation cic1 = (ClassInstanceCreation)node;
					desiredReturnType = AstUtil.getITypeBindingName(cic1.resolveTypeBinding(), false);
				}else{
					//�������g���킩��Ȃ���ΐe��������B
					InfixExpression ie = (InfixExpression)node.getParent();
					desiredReturnType = AstUtil.getITypeBindingName(ie.resolveTypeBinding(), false);
				}
				japanese_label = "�I�y���[�^�[�����݂���";
				break;
			case ASTNode.RETURN_STATEMENT:
				ReturnStatement rs = (ReturnStatement)node.getParent();
				desiredReturnType = AstUtil.getITypeBindingName(rs.getExpression().resolveTypeBinding(), false);
//				System.out.println(rs.getExpression().resolveTypeBinding().getQualifiedName());
				japanese_label = "���^�[������";
				break;
			case ASTNode.THROW_STATEMENT:
				ThrowStatement ts = (ThrowStatement)node.getParent();
				desiredReturnType = AstUtil.getITypeBindingName(ts.getExpression().resolveTypeBinding(), false);
//				System.out.println(ts.getExpression().resolveTypeBinding().getQualifiedName());
				japanese_label = "�X���[����";
				break;
			default:
				japanese_label = "���̑�";
				inspectDesiredReturnTypeAndLocation2(methodDec, node.getParent(), is_exist_astnode);
				break;
		}
	}
}
