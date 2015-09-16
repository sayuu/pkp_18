package plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class GeneralPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public GeneralPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		//addField(new BooleanFieldEditor(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS, "入力キーワードの改変（短縮化）", getFieldEditorParent()));
	
		addField(new BooleanFieldEditor(PreferenceInitializer.SEPARATE_KEYWORDS, "キーワード分割", getFieldEditorParent()));
		
		//addField(new BooleanFieldEditor(PreferenceInitializer.COMMON_SUBSEQUENCE, "キーワード部分一致", getFieldEditorParent()));
		RadioGroupFieldEditor editor1 = new RadioGroupFieldEditor(PreferenceInitializer.COMMON_SUBSEQUENCE,
				"キーワード部分一致機能",4,
                new String[][] {
                { "OFF", PreferenceInitializer.COMMON_SUBSEQUENCE_OFF},
                { "LCS1", PreferenceInitializer.COMMON_SUBSEQUENCE_LCS1},
                { "LCS2", PreferenceInitializer.COMMON_SUBSEQUENCE_LCS2},
                { "LCS3", PreferenceInitializer.COMMON_SUBSEQUENCE_LCS3},
                { "LCS4", PreferenceInitializer.COMMON_SUBSEQUENCE_LCS4},
                { "LD",  PreferenceInitializer.COMMON_SUBSEQUENCE_LD}
                },getFieldEditorParent());
		
		addField(editor1);
		
		addField(new IntegerFieldEditor(PreferenceInitializer.LD_DELETE, "LD 削除コスト", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceInitializer.LD_REPLACE, "LD 置換コスト", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceInitializer.LD_ADD, "LD 追加コスト", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.LD_CONST, "LD 定数C", getFieldEditorParent()));
		
		//addField(new BooleanFieldEditor(PreferenceInitializer.GROUPING_FUNCTIONS, "0点関数のグルーピング", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.INCLUDE_FREQUENCY, "頻度も計算する。", getFieldEditorParent()));
		
//		addField(new BooleanFieldEditor(PreferenceInitializer.STATIC_LABEL_WITHOUT_CLASS_NAME, "static関数はラベルにクラス名含めないものも追加", getFieldEditorParent()));
//		addField(new BooleanFieldEditor(PreferenceInitializer.CONSTRUCTOR_LABEL_WITH_NEW, "コンストラクタはラベルにnewを含める", getFieldEditorParent()));
		
		addField(new IntegerFieldEditor(PreferenceInitializer.BEST_R, "BEST_Rの値（動的計画法の表の各交点に保持する木の個数の最大値）", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceInitializer.HEIGHT, "HEIGHTの値（動的計画法の表の高さの最大値）", getFieldEditorParent()));
		
//		RadioGroupFieldEditor editor1 = new RadioGroupFieldEditor(PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER,
//				"関数の引数決定の探索方法",3,
//                new String[][] {
//                { "先行研究と同じ(貪欲法)", PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER_ORIGINAL},
//                { "(引数の組み合わせ数が閾値以下の場合に限り)全探索", PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER_EXHAUSTIVE},
//                { "最良優先探索(閾値で打ち切り)",  PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER_BEST_FIRST}
//                },getFieldEditorParent());
//		
//		addField(editor1);
//
//		addField(new IntegerFieldEditor(PreferenceInitializer.COMBINATION_SIZE, "全探索を行う組み合わせの最大値", getFieldEditorParent()));
//		addField(new IntegerFieldEditor(PreferenceInitializer.MAX_ARGUMETNT_SIZE, "全探索を行う最大引数数", getFieldEditorParent()));
//		
//		addField(new IntegerFieldEditor(PreferenceInitializer.BEST_FIRST_SIZE, "最良優先探索のとき、探索実行回数", getFieldEditorParent()));
		
		
		
		RadioGroupFieldEditor editor11 = new RadioGroupFieldEditor(PreferenceInitializer.IMPORT_TYPES_FROM,
				"内部データベース(java.langパッケージ)以外の型と関数情報をインポートする場所を指定.",3,
                new String[][] {
                { "現在編集中のクラスのみ", PreferenceInitializer.IMPORT_TYPES_FROM_MY_CLASS},
                { "現在編集中のクラスが所属するパッケージから", PreferenceInitializer.IMPORT_TYPES_FROM_MY_PACKAGE},
                { "現在編集中のクラスが所属するプロジェクト全体から",  PreferenceInitializer.IMPORT_TYPES_FROM_MY_PROJECT}
                },getFieldEditorParent());
		
		addField(editor11);
		
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
