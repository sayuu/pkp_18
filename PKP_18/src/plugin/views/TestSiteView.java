package plugin.views;

import experiment1.LocalSearch2;
import experiment1.LocalSearch2Job;
import experiment1.Result;
import experiment2.Experiment2;
import experiment2.Experiment2Job;
import experiment3.Experiment3;
import experiment3.Experiment3Job;
import gridSearch.GridSearchJob;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import keywordProgramming.ExplanationVector;
import keywordProgramming.KeywordProgramming;
import keywordProgramming.KeywordProgrammingJob;
import keywordProgramming.Params;

import localSearch.LocalSearch;
import localSearch.LocalSearchJob;
import logging.LogControl;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ast.AstLocalCode;
import ast.Import;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;
import plugin.testSite.TestSiteComparator;
import plugin.testSite.exp.TsSync;
import plugin.testSite.exp.TsThread;

public class TestSiteView extends org.eclipse.ui.part.ViewPart{

	private CheckboxTableViewer viewer; 
	private Composite fParent;
	
	private IAction importReloadAction;//インポート文をリロードするアクション
	
  	private IAction refreshAction;//画面をリフレッシュするアクション
  	private IAction deleteAction;//項目を削除するアクション
  	private IAction localSearchAction;//ローカルサーチをするアクション
  	private IAction gridSearchAction;//グリッドサーチをするアクション
  	
  	private IAction settingAction;//特徴の重みの初期値の設定アクション
  	
  	private IAction selectAllAction;//全てのタスクを選択するアクション
  	private IAction unSelectAllAction;//全てのタスクを選択解除するアクション
  	
  	private IAction countTaskAction;//プロパティ。(タスク数をカウントする)
  	
  	private IAction selectedTaskPropetyAction;//タスクのプロパティを表示する
  	
  	private IAction kpAction;//キーワードプログラミングの実行アクション

  	private IAction arrangeTaskAction1;//タスクを整理するアクション
  	private IAction arrangeTaskAction2;//タスクを整理するアクション
  	
  	private IAction experiment1Action;//実験のためのアクション
  	
  	private IAction experiment1Action_exp;//実験のためのアクション
  	
  	private IAction experiment1_renzoku_Action;//実験のためのアクション
  	
  	private IAction experiment1_renzoku_Action2;//実験のためのアクション
  	
  	private IAction experiment1_renzoku_exp_Action;//実験のためのアクション
  	
  	private IAction experiment1_renzoku_exp_short_Action;//実験のためのアクション
  	
  	private IAction experiment1ByClassAction;//実験のためのアクション クラス一括
  	
  	private IAction experiment2Action;//実験のためのアクション
  	private IAction experiment2ByKeywordNumAction;//実験のためのアクション 　指定キーワード数のタスクのみ実行。
  	
  	private IAction experiment3ByKeywordNumAction;//実験3 実験２の頻度昇順
  	
  	private IAction experiment4Action;	//実験4 全組み合わせ平均。
  	
  	private IAction experiment234Action;	//実験234 同時にやる。
  	
  	private IAction taskViewAction;	//タスク一覧アクション
  	
  	private IAction taskRandomSelectAction;	//タスクを間引く

  	private IAction verifyAction;//タスクの正当性検証アクション
  	
  	private IAction idFilterAction;//タスクのidによるフィルタリング
  	private IAction classNameFilterAction;//タスクのidによるフィルタリング
  	 
  	private String id_filtering = "";//フィルタリングするid
  	private String className_filtering = "";//フィルタリングするclassName
  	
  	private IAction openFileAction;//タスクのテキストファイルを開く
  	
	@Override
	public void createPartControl(Composite parent) {
		// TODO 自動生成されたメソッド・スタブ
		//インポート文読み込み。リスナの登録
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		window.getSelectionService().addSelectionListener(Import.listener);
		
		//インポート文読み込み。あらかじめエディタ上で開いてあるファイルに対応する
//		AstLocalCode.clearImportDeclaration();
//		AstLocalCode.getImportDeclaration();
		
		fParent = parent;
		
//		viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER);
//		Table table = viewer.getTable();
		
		Table table = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		
		viewer = new CheckboxTableViewer(table);
		
		viewer.setContentProvider(new ArrayContentProvider());
		
		viewer.addDoubleClickListener(new ViewerDoubleClickListener(viewer));
	
		IdFilter idFilter = new IdFilter();
		viewer.addFilter(idFilter);
		
		ClassNameFilter classNameFilter = new ClassNameFilter();
		viewer.addFilter(classNameFilter);
		
//		TableViewerColumn viewerColumn_check = new TableViewerColumn(viewer, SWT.LEFT);
//		//viewerColumn_check.setLabelProvider(new ClassNameLabelProvider());
//		TableColumn tableColumn_check = viewerColumn_check.getColumn();
//		tableColumn_check.setText("選択");
//		tableColumn_check.setWidth(100); // もしくは pack()
//		viewerColumn_check.setEditingSupport(new CheckboxEditingSupport(viewer));
		
		TableViewerColumn viewerColumn_id = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_id.setLabelProvider(new IdLabelProvider());
		TableColumn tableColumn_id = viewerColumn_id.getColumn();
		tableColumn_id.setText("ID");
		tableColumn_id.setWidth(150); // もしくは pack()
		
		TableViewerColumn viewerColumn_className = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_className.setLabelProvider(new ClassNameLabelProvider());
		TableColumn tableColumn_className = viewerColumn_className.getColumn();
		tableColumn_className.setText("所属パッケージとクラス");
		tableColumn_className.setWidth(220); // もしくは pack()
//		tableColumn_className.pack();
		
		/*
		TableViewerColumn viewerColumn_offset = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_offset.setLabelProvider(new OffsetLabelProvider());
		TableColumn tableColumn_offset = viewerColumn_offset.getColumn();
		tableColumn_offset.setText("オフセット");
//		tableColumn_offset.setWidth(50); // もしくは pack()
		tableColumn_offset.pack();
//		viewerColumn.setEditingSupport(new MyEditingSupport(viewer));
		*/
		
		TableViewerColumn viewerColumn_startLine = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_startLine.setLabelProvider(new StartLineLabelProvider());
		TableColumn tableColumn_startLine = viewerColumn_startLine.getColumn();
		tableColumn_startLine.setText("開始行");
//		tableColumn_startLine.setWidth(50); // もしくは pack()
		tableColumn_startLine.pack();
//		viewerColumn.setEditingSupport(new MyEditingSupport(viewer));
		
		TableViewerColumn viewerColumn_location = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_location.setLabelProvider(new LocationLabelProvider());
		TableColumn tableColumn_location = viewerColumn_location.getColumn();
		tableColumn_location.setText("ロケーション");
		tableColumn_location.setWidth(150); // もしくは pack()
//		tableColumn_text.pack();
		
		TableViewerColumn viewerColumn_retType = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_retType.setLabelProvider(new ReturnTypeLabelProvider());
		TableColumn tableColumn_retType = viewerColumn_retType.getColumn();
		tableColumn_retType.setText("望ましい返り値");
		tableColumn_retType.setWidth(320); // もしくは pack()
//		tableColumn_retType.pack();
//		viewerColumn.setEditingSupport(new MyEditingSupport(viewer));
		
		TableViewerColumn viewerColumn_text = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_text.setLabelProvider(new TextLabelProvider());
		TableColumn tableColumn_text = viewerColumn_text.getColumn();
		tableColumn_text.setText("生成したい出力(選択したテキスト)");
		tableColumn_text.setWidth(320); // もしくは pack()
//		tableColumn_text.pack();
		viewerColumn_text.setEditingSupport(new TextCellEditingSupport(viewer));
		
		TableViewerColumn viewerColumn_keyword = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_keyword.setLabelProvider(new KeywordLabelProvider());
		TableColumn tableColumn_keyword = viewerColumn_keyword.getColumn();
		tableColumn_keyword.setText("入力キーワード");
		tableColumn_keyword.setWidth(320); // もしくは pack()
//		tableColumn_text.pack();
		//セルをエディット可能にする。
		viewerColumn_keyword.setEditingSupport(new KeywordCellEditingSupport(viewer));

		TableViewerColumn viewerColumn_saveTime = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_saveTime.setLabelProvider(new SaveTimeLabelProvider());
		TableColumn tableColumn_saveTime = viewerColumn_saveTime.getColumn();
		tableColumn_saveTime.setText("保存日時");
		tableColumn_saveTime.setWidth(190); // もしくは pack()
		
		viewer.setInput(getItems());
//		viewer.setAllChecked(true);
		
		//リフレッシュ・アクション追加。
		registerAction();
	}

	@Override
	public void setFocus() {
		// TODO 自動生成されたメソッド・スタブ
		viewer.getControl().setFocus();
	}

	private List<TestSite> getItems(){
		List<TestSite> list = new ArrayList<TestSite>();
		
		String path = TestSite.TestSiteFolder;
		
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return null;
		}

	    File[] classFolders = dir.listFiles();
	    for (File folder: classFolders) {
	        File[] files = folder.listFiles();
	        for(File file: files){
	        	TestSite t = new TestSite(file, false);
	        	list.add(t);
	        }
	    }
	    
	  //時間の降順にソートする。
	    TestSiteComparator comp = new TestSiteComparator();
	    Collections.sort(list, comp);
	    
		return list;
	}
	
	private void registerAction(){
		//リフレッシュアクションを作成
		importReloadAction = new Action(){
			public void run(){
				Import.clearImportDeclaration();
				Import.getImportDeclaration(null);
			}
		};
		importReloadAction.setText("import関数のリロード");
				
		//リフレッシュアクションを作成
		refreshAction = new Action(){
			public void run(){
				viewer.setInput(getItems());
			}
		};
		refreshAction.setText("リフレッシュ");
//		refreshAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_REFRESH));
//		refreshAction.setImageDescriptor(Activator.getImageDescriptor(Activator.IMG_REFRESH, "refresh.gif"));
		refreshAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui.browser", "$nl$/icons/elcl16/nav_refresh.gif"));

		//全選択アクションを作成
		selectAllAction = new Action(){
			public void run(){
				viewer.setAllChecked(true);
			}
		};
		selectAllAction.setText("全て選択");
		selectAllAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "$nl$/icons/full/elcl16/step_done.gif"));

		//全選択解除アクションを作成
		unSelectAllAction = new Action(){
			public void run(){
				viewer.setAllChecked(false);
			}
		};
		unSelectAllAction.setText("全て選択解除");
		unSelectAllAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.debug.ui", "$nl$/icons/full/dlcl16/disabled_co.gif"));

				
		//削除アクションを作成
		deleteAction = new Action(){
			public void run(){
				//選択されたアイテムを削除する。
				TestSite[] list = getCheckedItems();
				
				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "タスクの削除", "削除するタスクをチェックボックスで選択してください。");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"タスクの削除",
						"選択された "+ list.length + " 個の学習タスクを削除します。");
			
				//"No"なら何もしない。
				if(yn == false){
					return;
				}
				//選択されたファイルを全て削除する。
				for(TestSite site:list){
					site.deleteFile();
				}
				//リフレッシュ
				viewer.setInput(getItems());
			}
		};
		deleteAction.setText("タスク削除");
//		deleteAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_DELETE));
		deleteAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "$nl$/icons/full/obj16/delete_obj.gif"));
		
		//タスクのプロパティアクション
		selectedTaskPropetyAction = new Action(){
			public void run(){
				//選択されたアイテムを削除する。
				TestSite[] list = getCheckedItems();
				
				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "タスクのプロパティ", "タスクをチェックボックスで選択してください。");
					return;
				}
				String line = "";
				
				for(TestSite site:list){
					line += site.toString();
				}
				
				MessageDialog.openInformation(fParent.getShell(), "キーワードプログラミング", line);
			}
		};
		selectedTaskPropetyAction.setText("タスクのプロパティ");

		//キーワードプログラミングのアクションを作成
		kpAction = new Action(){
			public void run(){
				TestSite[] list = getCheckedItems();
				

				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "キーワードプログラミング", "タスクをチェックボックスで選択してください。");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"キーワードプログラミング",
						"選択された "+ list.length + " 個の学習タスクに対してキーワードプログラミングを行います。");
			
				//"No"なら何もしない。
				if(yn == false){
					return;
				}
					
				//コンソール画面表示
				Activator.showConsoleView();
				
				//長い処理なので、ジョブを使う
				KeywordProgrammingJob job = new KeywordProgrammingJob("キーワードプログラミングの実行", list);
				job.setUser(true);//ユーザーにポップアップを表示する。
				job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
			}
		};
		kpAction.setText("キーワードプログラミング実行");
//		localSearchAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_RUN));
		kpAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui.cheatsheets", "$nl$/icons/elcl16/skip_task.gif"));

		
		//ローカルサーチアクションを作成
		localSearchAction = new Action(){
			public void run(){
				//各ファイルについて、
				//キーワードプログラミングをしてからその結果を用いてローカルサーチを行う。
				TestSite[] list = getCheckedItems();
				

				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "ローカルサーチ", "タスクをチェックボックスで選択してください。");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"ローカルサーチ",
						"選択された "+ list.length + " 個の学習タスクに対してローカルサーチを行います。");
			
				//"No"なら何もしない。
				if(yn == false){
					return;
				}
				
				LocalSearch ls = new LocalSearch(list, false);
				
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				//ステップ数を指定
				int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
				//収束まで行うか否か。
				boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
				if(isConvergence == true)
					numOfSteps = -1;
				
				//コンソール画面表示
				Activator.showConsoleView();
				
				//長い処理なので、ジョブを使う
				LocalSearchJob job = new LocalSearchJob("ローカルサーチの実行", ls, numOfSteps);
				job.setUser(true);//ユーザーにポップアップを表示する。
				job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
			}
		};
		localSearchAction.setText("ローカルサーチ実行");
//		localSearchAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_RUN));
		localSearchAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui.cheatsheets", "$nl$/icons/elcl16/start_task.gif"));

		
		//グリッドサーチアクションを作成
		gridSearchAction = new Action(){
			public void run(){
				//各ファイルについて、
				//キーワードプログラミングをしてからその結果を用いてグリッドサーチを行う。
				TestSite[] list = getCheckedItems();
				

				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "グリッドサーチ", "タスクをチェックボックスで選択してください。");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"グリッドサーチ",
						"選択された "+ list.length + " 個の学習タスクに対してグリッドサーチを行います。");
			
				//"No"なら何もしない。
				if(yn == false){
					return;
				}
				
				//コンソール画面表示
				Activator.showConsoleView();
				
				//長い処理なので、ジョブを使う
				GridSearchJob job = new GridSearchJob("グリッドサーチの実行", list);
				job.setUser(true);//ユーザーにポップアップを表示する。
				job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
			}
		};
		gridSearchAction.setText("グリッドサーチ実行");
//		gridSearchAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_RUN));
		gridSearchAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.debug.ui", "$nl$/icons/full/obj16/lrun_obj.gif"));

		
		//設定アクションを作成
		settingAction = new Action(){
			public void run(){
				//プリファレンスを取得して、ダイアログに表示する。
				PreferenceManager pm = PlatformUI.getWorkbench( ).getPreferenceManager();
		        IPreferenceNode kp_node = pm.find("jp.ac.hokudai.eng.complex.kussharo.sayuu.kp.preference.page.kp");
		        PreferenceManager pm2 = new PreferenceManager();
		        pm2.addToRoot(kp_node);
		        PreferenceDialog dialog1 = new PreferenceDialog(fParent.getShell(), pm2);
		        dialog1.open();
			}
		};
		
		settingAction.setText("設定");
//		settingAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_SETTINGS));
		settingAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.debug.ui", "$nl$/icons/full/elcl16/prop_ps.gif"));

		//プロパティを表示する。
		countTaskAction = new Action(){
			public void run(){
				
				Object o = viewer.getInput();
				if(o instanceof ArrayList){
					//ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
					List<TestSite> ts_arr = (List<TestSite>) Arrays.asList(getCheckedItems());
					int sum = ts_arr.size();
//					HashMap<String, Integer> map = new HashMap<String, Integer>();
//					for(TestSite ts: ts_arr){
//						String name = ts.getPackageName();
//						if(map.containsKey(name)){
//							int count = map.get(name);
//							map.put(name, count+1);
//						}else{
//							map.put(name, 1);
//						}
//					}
//					int package_sum = map.size();
					
					HashSet<String> set = new HashSet<String>();
					for(TestSite ts: ts_arr){
						String name = ts.getPackageName();
						set.add(name);
					}
					int package_sum = set.size();
					set.clear();
					for(TestSite ts: ts_arr){
						String name = ts.getFullyQualifiedClassName();
						set.add(name);
					}
					int class_sum = set.size();
					
					HashMap<String, Integer> map = new HashMap<String, Integer>();
					for(TestSite ts: ts_arr){
						String name = ts.getLocation();
						if(map.containsKey(name)){
							int count = map.get(name);
							map.put(name, count+1);
						}else{
							map.put(name, 1);
						}
					}
					Iterator<String> it = map.keySet().iterator();
					String s = "";
			        while (it.hasNext()) {
			            String name = it.next();
			            s += name + " = " + map.get(name) + "\n";
			        }
					System.out.println(map.toString());
					String countTasksByClassName = countTasksByClassName(ts_arr);
					
					MessageDialog.openInformation(fParent.getShell(), "ビューのプロパティ", "ビューに表示されている\n全タスク数= " + sum
							+ "\n全パッケージ数= " + package_sum
							+ "\n全クラス数= " + class_sum + "\n"
							+ "クラス名, タスク数\n"
							+ countTasksByClassName);
				}
				
				

			}
			
			private String countTasksByClassName(List<TestSite> all_task_list) {
				//クラス名とタスクのリストのマップ
				TreeMap<String, ArrayList<TestSite>> class_task_map = new TreeMap<String, ArrayList<TestSite>>();
				for(TestSite task: all_task_list){
					String className = task.getFullyQualifiedClassName();
					//マップにリストが存在しなければ追加
					if(!class_task_map.containsKey(className)){
						class_task_map.put(className, new ArrayList<TestSite>());
					}
					//リストを取り出して、タスクを追加する。
					ArrayList<TestSite> task_list = class_task_map.get(className);
					task_list.add(task);
				}
				
				Iterator<String> iter = class_task_map.keySet().iterator();
				String ret = "";
				while(iter.hasNext()){
					String className = iter.next();
					ArrayList<TestSite> task_list = class_task_map.get(className);
					ret += className + ", " + task_list.size() + "\n";
				}
				
				return ret;
			}
		};
		
		countTaskAction.setText("プロパティ");
//				countTaskAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_SETTINGS));
		countTaskAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "$nl$/icons/full/obj16/generic_elements.gif"));


		//タスクをコンソールに表示する。
		taskViewAction = new Action(){
			public void run(){
				Object o = viewer.getInput();
				if(o instanceof ArrayList){
					ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
					for(TestSite ts: ts_arr){
						//System.out.println(ts.getId() + ", "+ ts.getSelectedString());
						System.out.println(ts.getSelectedString());
						for(String s: ts.getLocalFunctionNameKeywords()){
							System.out.print(s + ", ");
						}
						System.out.println();
						System.out.println();
					}
				}
			}
			
		};
		
		taskViewAction.setText("タスク一覧");
		
		//タスクを間引く
		taskRandomSelectAction = new Action(){
			public void run(){
				InputDialog dialog =
		                new InputDialog(
		                		fParent.getShell(),
		                        "タスク",
		                        "タスクを間引く間隔を入力してください。",
		                        "3",
		                        null);
		        int ret = dialog.open();
		        if (ret != Window.OK){
		        	return;
		        }
		        
		        int number = Integer.valueOf(dialog.getValue());
				Object o = viewer.getInput();
				if(o instanceof ArrayList){
					ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
					for(int i=0; i< ts_arr.size(); i++){
						if(i % number == 0){
							System.out.println(ts_arr.get(i).getId());
						}else{
							ts_arr.get(i).deleteFile();
						}
					}
				}
			}
			
		};
		
		taskRandomSelectAction.setText("タスク間引く");
		
		//タスク整理アクション
		arrangeTaskAction1 = new Action(){
			public void run(){
				Object o = viewer.getInput();
				if(o instanceof ArrayList){
					ArrayList<TestSite> all_task_list = (ArrayList<TestSite>)o;
					arrangeTasksByClassName(all_task_list);
			        refresh();
				}
			}

			private void arrangeTasksByClassName(ArrayList<TestSite> all_task_list) {
				//クラス名とタスクのリストのマップ
				HashMap<String, ArrayList<TestSite>> class_task_map = new HashMap<String, ArrayList<TestSite>>();
				for(TestSite task: all_task_list){
					String className = task.getFullyQualifiedClassName();
					//マップにリストが存在しなければ追加
					if(!class_task_map.containsKey(className)){
						class_task_map.put(className, new ArrayList<TestSite>());
					}
					//リストを取り出して、タスクを追加する。
					ArrayList<TestSite> task_list = class_task_map.get(className);
					task_list.add(task);
				}
				
				Iterator<String> iter = class_task_map.keySet().iterator();
				while(iter.hasNext()){
					String className = iter.next();
					ArrayList<TestSite> task_list = class_task_map.get(className);
					System.out.println("開始：" + className + ", " + task_list.size());
					arrangeTasks(task_list);
				}
			}
			
			/*
			 * TestSiteのリストから、
			 * 重複文字列を削除する。
			 */
			private void arrangeTasks(ArrayList<TestSite> ts_arr) {
				List<Result> result_list = new ArrayList<Result>(); 
				
				for(TestSite ts: ts_arr){
					String str = ts.getSelectedString();
					int odr = -1;//順位は不明。
					int numKey = ts.getNumOfKeywords();
					int numLT = ts.getNumOfLocalTypes();
					int numLF = ts.getNumOfLocalFunctions();
					result_list.add(new Result(ts.getId(), str, odr, numKey, numLT, numLF));
				}
				System.out.println("==============重複文字列を削除した=================================");
				
				TreeSet<Result> result_set = new TreeSet<Result>();
				List<Result> deleted_list = new ArrayList<Result>(); 
				
				for(Result r : result_list){
					//重複を削除
					if(!result_set.contains(r)){
						result_set.add(r);
						System.out.println(r.fTestSiteId + "\t" + r.fSelectedString);
					}else{
						deleted_list.add(r);
					}
				}
				
				System.out.println("=================削除されたもの一覧==============================");
				
				for(Result r: deleted_list){
				    System.out.println(r.fTestSiteId + "\t" + r.fSelectedString);
				    for(TestSite ts: ts_arr){
				    	if(r.fTestSiteId.equals(ts.getId())){
				    		ts.deleteFile();
				    	}
				    }
				}
			}
		};
		arrangeTaskAction1.setText("タスク整理(重複文字列削除)");


		//タスク整理アクション
		arrangeTaskAction2 = new Action(){
			public void run(){
				Object o = viewer.getInput();
				if(o instanceof ArrayList){
					ArrayList<TestSite> all_task_list = (ArrayList<TestSite>)o;
					arrangeTasksByClassName(all_task_list);
			        refresh();
				}
			}

			private void arrangeTasksByClassName(ArrayList<TestSite> all_task_list) {
				//クラス名とタスクのリストのマップ
				HashMap<String, ArrayList<TestSite>> class_task_map = new HashMap<String, ArrayList<TestSite>>();
				for(TestSite task: all_task_list){
					String className = task.getFullyQualifiedClassName();
					//マップにリストが存在しなければ追加
					if(!class_task_map.containsKey(className)){
						class_task_map.put(className, new ArrayList<TestSite>());
					}
					//リストを取り出して、タスクを追加する。
					ArrayList<TestSite> task_list = class_task_map.get(className);
					task_list.add(task);
				}
				
				Iterator<String> iter = class_task_map.keySet().iterator();
				while(iter.hasNext()){
					String className = iter.next();
					ArrayList<TestSite> task_list = class_task_map.get(className);
					System.out.println("開始：" + className + ", " + task_list.size());
					arrangeTasks(task_list);
				}
			}
			
			/*
			 * TestSiteのリストから、
			 * genericsを削除する。
			 */
			private void arrangeTasks(ArrayList<TestSite> ts_arr) {
				List<Result> result_list = new ArrayList<Result>(); 
				
				for(TestSite ts: ts_arr){
					String str = ts.getSelectedString();
					int odr = -1;//順位は不明。
					int numKey = ts.getNumOfKeywords();
					int numLT = ts.getNumOfLocalTypes();
					int numLF = ts.getNumOfLocalFunctions();
					result_list.add(new Result(ts.getId(), str, odr, numKey, numLT, numLF));
				}
				System.out.println("==============genericsを削除した=================================");
				
				TreeSet<Result> result_set = new TreeSet<Result>();
				List<Result> deleted_list = new ArrayList<Result>(); 
				
				for(Result r : result_list){
					//genericsを削除
					if(!r.fSelectedString.contains("<")){
						result_set.add(r);
						System.out.println(r.fTestSiteId + "\t" + r.fSelectedString);
					}else{
						deleted_list.add(r);
					}
				}
				
				System.out.println("=================削除されたもの一覧==============================");
				
				for(Result r: deleted_list){
				    System.out.println(r.fTestSiteId + "\t" + r.fSelectedString);
				    for(TestSite ts: ts_arr){
				    	if(r.fTestSiteId.equals(ts.getId())){
				    		ts.deleteFile();
				    	}
				    }
				}
			}
		};
		arrangeTaskAction2.setText("タスク整理(generics削除)");

		//実験1
		experiment1Action = new Action(){
			public void run(){

				System.out.println("事前にＫＰすること！");
				
				//各ファイルについて、
				//キーワードプログラミングをしてからその結果を用いてローカルサーチを行う。
				TestSite[] list = getCheckedItems();
				

				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "実験1", "タスクをチェックボックスで選択してください。");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"実験1",
						"選択された "+ list.length + " 個の学習タスクに対して実験1を行います。");
			
				//"No"なら何もしない。
				if(yn == false){
					return;
				}
				
				LocalSearch2 ls = new LocalSearch2(list, false);
				
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				//ステップ数を指定
				int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
				//収束まで行うか否か。
				boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
				if(isConvergence == true)
					numOfSteps = -1;
				
				//コンソール画面表示
				Activator.showConsoleView();
				
				//長い処理なので、ジョブを使う
				LocalSearch2Job job = new LocalSearch2Job("実験1の実行", ls, numOfSteps);
				job.setUser(true);//ユーザーにポップアップを表示する。
				job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
			}
		};
		experiment1Action.setText("実験１");
		
		//実験1exp
		experiment1Action_exp = new Action(){
			public void run(){
				
				//各ファイルについて、
				//キーワードプログラミングをしてからその結果を用いてローカルサーチを行う。
				TestSite[] list = getCheckedItems();
				

				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "実験1", "タスクをチェックボックスで選択してください。");
					return;
				}
				
				String projectName = "";
				
				InputDialog dialog =
		                new InputDialog(
		                		fParent.getShell(),
		                        "プロジェクト名の入力",
		                        "プロジェクト名を入力してください。",
		                        "",
		                        null);
		        int ret = dialog.open();
		        if (ret != Window.OK){
		        	return;
		        }
		        
		        projectName = dialog.getValue();
		        
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"実験1",
						"プロジェクト名" + projectName + ". 選択された "+ list.length + " 個の学習タスクに対して実験1を行います。");
			
				//"No"なら何もしない。
				if(yn == false){
					return;
				}
				
				
				keywordProgramming.exp.Params para = new keywordProgramming.exp.Params();
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				keywordProgramming.exp.KeywordProgramming.BEST_R = store.getInt(PreferenceInitializer.LOCAL_BEST_R);
				para.const_freq = store.getDouble(PreferenceInitializer.CONST_FREQ);
				para.w_arr.add(0, -store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_0));//負
				para.w_arr.add(1, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_1));
				para.w_arr.add(2, -store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_2));//負
				para.w_arr.add(3, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_3));
				para.w_arr.add(4, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_4));
				
				para.separate_keywords = String.valueOf(store.getBoolean(PreferenceInitializer.SEPARATE_KEYWORDS));
				para.common_subsequence = store.getString(PreferenceInitializer.COMMON_SUBSEQUENCE);
				para.ld_delete = store.getInt(PreferenceInitializer.LD_DELETE);
				para.ld_replace = store.getInt(PreferenceInitializer.LD_REPLACE);
				para.ld_add = store.getInt(PreferenceInitializer.LD_ADD);
				
				para.shortened_input_keywords = store.getString(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS);
				
				long start = System.currentTimeMillis();
				
				runTasksUsingMultiThread(list, para, projectName, start);
				
			    long stop = System.currentTimeMillis();
			    //System.out.println("  実行にかかった時間= " + (stop - start) + " ミリ秒");
				
			}

			
		};
		experiment1Action_exp.setText("実験１exp");
		
		//実験1
		experiment1_renzoku_exp_short_Action = new Action(){
			public void run(){
				
				//各ファイルについて、
				//キーワードプログラミングをしてからその結果を用いてローカルサーチを行う。
				TestSite[] list = getCheckedItems();
				

				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "実験1", "タスクをチェックボックスで選択してください。");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"実験1",
						"選択された "+ list.length + " 個の学習タスクに対して実験1を行います。");
			
				//"No"なら何もしない。
				if(yn == false){
					return;
				}
			
				keywordProgramming.exp.Params para = new keywordProgramming.exp.Params();
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				//重みは固定
				keywordProgramming.exp.KeywordProgramming.BEST_R = store.getInt(PreferenceInitializer.LOCAL_BEST_R);
				para.const_freq = store.getDouble(PreferenceInitializer.CONST_FREQ);
				para.w_arr.add(0, -store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_0));//負
				para.w_arr.add(1, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_1));
				para.w_arr.add(2, -store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_2));//負
				para.w_arr.add(3, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_3));
				para.w_arr.add(4, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_4));
				
				//キーワード改変(5種類)
				for(int i = 0; i< 5; i++){
					para.shortened_input_keywords = store.getString(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS);
					
					//分割入力(2種類)
					for(int j = 0; j< 2; j++){
						para.separate_keywords = String.valueOf(store.getBoolean(PreferenceInitializer.SEPARATE_KEYWORDS));
						
						//類似度(3種類 ldは変数を動かす)
						for(int k = 0; k< 3; k++){
							para.common_subsequence = store.getString(PreferenceInitializer.COMMON_SUBSEQUENCE);
							
							if(k == 2){
								int k_list[] = {0, 1, 2, 4, 8};
								for(int m = 0; m < k_list.length; m++){
									para.ld_delete = 1;
									para.ld_replace = 1 + k_list[m];
									para.ld_add = 1 + 2 * k_list[m];
									//runTasksUsingMultiThread(list, para);			
								}
							}else{
								//runTasksUsingMultiThread(list, para);
							}
						}
					}
				}
			}
		};
		experiment1_renzoku_exp_short_Action.setText("実験１連続exp short");
		
		//実験1
		experiment1_renzoku_Action = new Action(){
			public void run(){
//				System.out.println("事前にＫＰすること！");
				//各ファイルについて、
				//キーワードプログラミングをしてからその結果を用いてローカルサーチを行う。
				TestSite[] list = getCheckedItems();
				

				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "実験1", "タスクをチェックボックスで選択してください。");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"実験1",
						"選択された "+ list.length + " 個の学習タスクに対して実験1を行います。");
			
				//"No"なら何もしない。
				if(yn == false){
					return;
				}
				
				double a_start = 0.1;
				double a_end = 0.5;
				double a_step = 0.1;
				double a_current = a_start;
				
				while(a_current < a_end){
					ExplanationVector.setConstFreq(a_current);
					
				
					double start = 0.0001;
					double end = 1.0;
					double step = 10;
					double current = start;
					
					//for(int i = 0; i< 2; i++){
					while(current < end){
						System.out.println("a= " + a_current + ", t5= " + current);
						
						ExplanationVector.setWeight(current, 4);
						
						
						LocalSearch2 ls = new LocalSearch2(list, false);
						
						IPreferenceStore store = Activator.getDefault().getPreferenceStore();
						
						//ステップ数を指定
						int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
						//収束まで行うか否か。
						boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
						if(isConvergence == true)
							numOfSteps = -1;
						
						//コンソール画面表示
						//Activator.showConsoleView();
						
						//長い処理なので、ジョブを使う
						LocalSearch2Job job = new LocalSearch2Job("実験1の実行", ls, numOfSteps);
						job.setUser(true);//ユーザーにポップアップを表示する。
						job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
						try {
							job.join();
						} catch (InterruptedException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}
						current *= step;
					}
					a_current += a_step;
				}				
			}
		};
		experiment1_renzoku_Action.setText("実験１連続");
		
		
		//最新。
		//実験1
		experiment1_renzoku_Action2 = new Action(){
			public void run(){
				//各ファイルについて、
				//キーワードプログラミングをしてからその結果を用いてローカルサーチを行う。
				TestSite[] list = getCheckedItems();
				

				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "実験1", "タスクをチェックボックスで選択してください。");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"実験1",
						"選択された "+ list.length + " 個の学習タスクに対して実験1を行います。");
			
				//"No"なら何もしない。
				if(yn == false){
					return;
				}
							
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				keywordProgramming.Params para = new Params();
				
				//キーワード改変(5種類)
				//なし
				//母音削除, 先頭３文字、１文字置換、１文字挿入
				for(int i = 0; i< 5; i++){
					
					switch(i){
					case 0:
						para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_OFF;
						break;
					case 1:
						para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_BOIN_DEL;
						break;
					case 2:
						para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_FIRST_3;
						break;
					case 3:
						para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_REPLACE;
						break;
					case 4:
						para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_ADD;
						break;
					}
					
					store.setValue(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS, para.shortened_input_keywords);//キーワード短縮化

				
					//分割入力(2種類)
					//off, on
					for(int j = 0; j< 2; j++){
						
						switch(j){
						case 0:
							para.separate_keywords = "false";
							break;
						case 1:
							para.separate_keywords = "true";
							break;
						}
						store.setValue(PreferenceInitializer.SEPARATE_KEYWORDS, para.separate_keywords);//キーワード短縮化

						if((i == 0 && j == 0) || (i == 0 && j == 1)){
							continue;
						}
						//類似度(6種類 ldは変数を動かす)
						// なし。
						// LCS1,2,3,4
						// LD (削除、追加、置換)コストを動かす。(k=0 1 2 4 8)なら5通り
						// 計、10種類
						for(int k = 0; k< 6; k++){
													
							switch(k){
							case 0:
								para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_OFF;
								break;
							case 1:
								para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS1;
								break;
							case 2:
								para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS2;
								break;
							case 3:
								para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS3;
								break;
							case 4:
								para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS4;
								break;
							case 5:
								para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LD;
								break;
							}
							
							store.setValue(PreferenceInitializer.COMMON_SUBSEQUENCE, para.common_subsequence);

							if(k == 5){
								//とりあえずLDやらない。
								
//								int k_list[] = {0, 1, 2, 4, 8};
//								for(int m = 0; m < k_list.length; m++){
//									para.ld_delete = 1;
//									para.ld_replace = 1 + k_list[m];
//									para.ld_add = 1 + 2 * k_list[m];
//									long start = System.currentTimeMillis();
//									runKP(list, store);
//								}
							}else{
								//リセット　見栄えのため。
								para.ld_delete = 0;
								para.ld_replace = 0;
								para.ld_add = 0;
								long start = System.currentTimeMillis();
								runKP(list, store);
							}
						}
					}
				}		
			}

			private void runKP(TestSite[] list, IPreferenceStore store) {
				LocalSearch2 ls = new LocalSearch2(list, false);
			//ステップ数を指定
				int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
				
				//長い処理なので、ジョブを使う
				LocalSearch2Job job = new LocalSearch2Job("実験1の実行", ls, numOfSteps);
				job.setUser(true);//ユーザーにポップアップを表示する。
				job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
				try {
					job.join();
//						Thread.sleep(3 * 1000);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			
		};
		experiment1_renzoku_Action2.setText("実験１連続2");
				
		//実験1
		experiment1_renzoku_exp_Action = new Action(){
			public void run(){
				
				//各ファイルについて、
				//キーワードプログラミングをしてからその結果を用いてローカルサーチを行う。
				TestSite[] list = getCheckedItems();
				

				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "実験1", "タスクをチェックボックスで選択してください。");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"実験1",
						"選択された "+ list.length + " 個の学習タスクに対して実験1を行います。");
			
				//"No"なら何もしない。
				if(yn == false){
					return;
				}
				
				// 特徴５の頻度の重み0のとき。
				keywordProgramming.exp.Params para = new keywordProgramming.exp.Params();
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				keywordProgramming.exp.KeywordProgramming.BEST_R = store.getInt(PreferenceInitializer.LOCAL_BEST_R);
				para.const_freq = 0;
				para.w_arr.add(0, -store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_0));//負
				para.w_arr.add(1, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_1));
				para.w_arr.add(2, -store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_2));//負
				para.w_arr.add(3, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_3));
				para.w_arr.add(4, 0.0);
				
				//runTasksUsingMultiThread(list, para);
				
				double a_start = 0.1;
				double a_end = 0.5;
				double a_step = 0.1;
				double a_current = a_start;
				
				while(a_current < a_end){

					double start = 0.0001;
					double end = 1.0;
					double step = 10;
					double current = start;
					
					//for(int i = 0; i< 2; i++){
					while(current < end){
						keywordProgramming.exp.Params para1 = new keywordProgramming.exp.Params();
						IPreferenceStore store1 = Activator.getDefault().getPreferenceStore();
						keywordProgramming.exp.KeywordProgramming.BEST_R = store1.getInt(PreferenceInitializer.LOCAL_BEST_R);
						para1.const_freq = a_current;
						para1.w_arr.add(0, -store1.getDouble(PreferenceInitializer.INITIAL_WEIGHT_0));//負
						para1.w_arr.add(1, store1.getDouble(PreferenceInitializer.INITIAL_WEIGHT_1));
						para1.w_arr.add(2, -store1.getDouble(PreferenceInitializer.INITIAL_WEIGHT_2));//負
						para1.w_arr.add(3, store1.getDouble(PreferenceInitializer.INITIAL_WEIGHT_3));
						
						
						para1.w_arr.add(4, current);
						
						//runTasksUsingMultiThread(list, para1);
						
						
						current *= step;
					}
					a_current += a_step;
				}				
			}
		};
		experiment1_renzoku_exp_Action.setText("実験１連続exp para");
		
		//実験1(クラスごと)
		experiment1ByClassAction = new Action(){
			public void run(){
				
				Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "入力したクラスごとに実験１を実行します", resultContainer); 
            	md.open();
            	String text = resultContainer.getText();
           	 	List<String> classNames = Arrays.asList(text.split("[, 　\t\n\r]", -1));
	
				Object o = viewer.getInput();
				if(o instanceof ArrayList){
					ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
					for(String className: classNames){
						if(!className.equals("")){
							ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
							for(TestSite ts: ts_arr){
								if(ts.getClassSingleName().equals(className) || ts.getFullyQualifiedClassName().equals(className)){
									ts_arr_by_class.add(ts);
								}
							}
							
							LocalSearch2 ls = new LocalSearch2(ts_arr_by_class.toArray(new TestSite[0]), false);
							
							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
							
							//ステップ数を指定
							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
							//収束まで行うか否か。
							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
							if(isConvergence == true)
								numOfSteps = -1;
							
							//コンソール画面表示
							Activator.showConsoleView();
							
							//長い処理なので、ジョブを使う
							LocalSearch2Job job = new LocalSearch2Job("実験1の実行", ls, numOfSteps);
							job.setUser(true);//ユーザーにポップアップを表示する。
							job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
							try {
								job.join();
							} catch (InterruptedException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
						}
					}
				}
				
			}
		};
		experiment1ByClassAction.setText("実験１(クラスごと)");
		
		
		//実験2
		experiment2Action = new Action(){
			public void run(){
				//各ファイルについて、
				//キーワードプログラミングをしてからその結果を用いてローカルサーチを行う。
				TestSite[] list = getCheckedItems();
				

				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "実験2", "タスクをチェックボックスで選択してください。");
					return;
				}
				
				Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "出現頻度が低い順にｘ個（入力した数）のキーワードをクエリとします。", resultContainer); 
            	md.open();
            	String text = resultContainer.getText();
            	int num_of_keywords = Integer.parseInt(text);
            	
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"実験2",
						"選択された "+ list.length + " 個の学習タスクに対して実験2を行います。");
			
				//"No"なら何もしない。
				if(yn == false){
					return;
				}
				
				experiment2.Experiment2 ls = new experiment2.Experiment2(list, num_of_keywords, false, true);
				
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				//ステップ数を指定
				int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
				//収束まで行うか否か。
				boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
				if(isConvergence == true)
					numOfSteps = -1;
				
				//コンソール画面表示
				Activator.showConsoleView();
				
				//長い処理なので、ジョブを使う
				experiment2.Experiment2Job job = new experiment2.Experiment2Job("実験2の実行", ls, numOfSteps);
				job.setUser(true);//ユーザーにポップアップを表示する。
				job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
			}
		};
		experiment2Action.setText("実験2");
		

		//実験2(正解に含まれる単語数を指定)
		/*
		 * 正解に含まれる単語数xを指定すると、
		 * 正解に含まれる単語数がxのタスク全てについて実験2を行う。
		 * 
		 * 実験2は単語の出現頻度が多い順にクエリーから切り捨てる方法で、
		 * クエリー中の単語数0~xまでを行う。
		 */
		experiment2ByKeywordNumAction = new Action(){
			public void run(){
				
//				Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
//            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "入力したキーワード数のタスクについて実験２を実行します", resultContainer); 
//            	md.open();
//            	String text = resultContainer.getText();
//            	int num_of_keywords_in_label = Integer.parseInt(text);

//				Label resultContainer2 = new Label(fParent.getShell(), SWT.NONE);
//            	MultiTextInputDialog md2 = new MultiTextInputDialog(fParent.getShell(), "出現頻度が低い順にｘ個（入力した数）のキーワードをクエリとします。", resultContainer2); 
//            	md2.open();
//            	String text2 = resultContainer2.getText();
//            	int num_of_keywords_in_query = Integer.parseInt(text2);
//            	
            	for( int num_of_keywords_in_label = 1; num_of_keywords_in_label <= 9; num_of_keywords_in_label++){

	            	System.out.println("in　ラベル, " + num_of_keywords_in_label);
	            	
					Object o = viewer.getInput();
					if(o instanceof ArrayList){
						ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
						ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
						
						for(TestSite ts: ts_arr){
							//指定キーワード数と一致していたら追加する。
							if(ts.getNumOfKeywords() == num_of_keywords_in_label){
								ts_arr_by_class.add(ts);
							}
						}
						
						for(int num_of_keywords_in_query = 0; num_of_keywords_in_query <= num_of_keywords_in_label; num_of_keywords_in_query++){
							//if(!className.equals("")){
							System.out.println("in　クエリー, " + num_of_keywords_in_query);
								
							Experiment2 ls = new Experiment2(ts_arr_by_class.toArray(new TestSite[0]), num_of_keywords_in_query, false, true);
							
							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
							
							//ステップ数を指定
							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
							//収束まで行うか否か。
							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
							if(isConvergence == true)
								numOfSteps = -1;
							
							//コンソール画面表示
							Activator.showConsoleView();
							
							//長い処理なので、ジョブを使う
							Experiment2Job job = new Experiment2Job("実験2の実行", ls, numOfSteps);
							job.setUser(true);//ユーザーにポップアップを表示する。
							job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
							try {
								job.join();
							} catch (InterruptedException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
						}
					}
            	}
			}
		};
		experiment2ByKeywordNumAction.setText("実験2(頻度大の単語から切り捨て)");
		

		//実験3(正解に含まれる単語数を指定)
		/*
		 * 正解に含まれる単語数xを指定すると、
		 * 正解に含まれる単語数がxのタスク全てについて実験3を行う。
		 * 
		 * 実験3は単語の出現頻度が少ない順にクエリーから切り捨てる方法で、
		 * クエリー中の単語数0~xまでを行う。
		 */
		experiment3ByKeywordNumAction = new Action(){
			public void run(){
				
//				Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
//            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "入力したキーワード数のタスクについて実験3を実行します", resultContainer); 
//            	md.open();
//            	String text = resultContainer.getText();
//            	int num_of_keywords_in_label = Integer.parseInt(text);

//				Label resultContainer2 = new Label(fParent.getShell(), SWT.NONE);
//            	MultiTextInputDialog md2 = new MultiTextInputDialog(fParent.getShell(), "出現頻度が低い順にｘ個（入力した数）のキーワードをクエリとします。", resultContainer2); 
//            	md2.open();
//            	String text2 = resultContainer2.getText();
//            	int num_of_keywords_in_query = Integer.parseInt(text2);
            	
            	for( int num_of_keywords_in_label = 1; num_of_keywords_in_label <= 9; num_of_keywords_in_label++){

	            	System.out.println("in　ラベル, " + num_of_keywords_in_label);
	            	
					Object o = viewer.getInput();
					if(o instanceof ArrayList){
						ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
						ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
						
						for(TestSite ts: ts_arr){
							//指定キーワード数と一致していたら追加する。
							if(ts.getNumOfKeywords() == num_of_keywords_in_label){
								ts_arr_by_class.add(ts);
							}
						}
						
						for(int num_of_keywords_in_query = 0; num_of_keywords_in_query <= num_of_keywords_in_label; num_of_keywords_in_query++){
							//if(!className.equals("")){
							System.out.println("in　クエリー, " + num_of_keywords_in_query);
								
							Experiment2 ls = new Experiment2(ts_arr_by_class.toArray(new TestSite[0]), num_of_keywords_in_query, false, false);
							
							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
							
							//ステップ数を指定
							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
							//収束まで行うか否か。
							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
							if(isConvergence == true)
								numOfSteps = -1;
							
							//コンソール画面表示
							Activator.showConsoleView();
							
							//長い処理なので、ジョブを使う
							Experiment2Job job = new Experiment2Job("実験3の実行", ls, numOfSteps);
							job.setUser(true);//ユーザーにポップアップを表示する。
							job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
							try {
								job.join();
							} catch (InterruptedException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
						}
					}
            	}
			}
		};
		experiment3ByKeywordNumAction.setText("実験3(頻度小の単語から切り捨て)");
		

		//実験4(正解に含まれる単語数を指定)
		/*
		 * 正解に含まれる単語数xを指定すると、
		 * 正解に含まれる単語数がxのタスク全てについて実験3を行う。
		 * 
		 * 実験4は単語の切り捨ての組み合わせすべての平均値を求める。
		 * 
		 * クエリー中の単語数0~xまでを行う。
		 */
		experiment4Action = new Action(){
			public void run(){
				
//				Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
//            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "入力したキーワード数のタスクについて実験4を実行します", resultContainer); 
//            	md.open();
//            	String text = resultContainer.getText();
//            	
            	//int num_of_keywords_in_label = Integer.parseInt(text);
            	
            	for( int num_of_keywords_in_label = 1; num_of_keywords_in_label <= 9; num_of_keywords_in_label++){

	            	System.out.println("in　ラベル, " + num_of_keywords_in_label);
	            	
					Object o = viewer.getInput();
					if(o instanceof ArrayList){
						ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
						ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
						
						for(TestSite ts: ts_arr){
							//指定キーワード数と一致していたら追加する。
							if(ts.getNumOfKeywords() == num_of_keywords_in_label){
								ts_arr_by_class.add(ts);
							}
						}
						
						for(int num_of_keywords_in_query = 1; num_of_keywords_in_query < num_of_keywords_in_label; num_of_keywords_in_query++){
							//キーワード数0 とキーワード数=ラベル含有キーワード数の場合は、
							//他の結果と同じになる。『切り捨てが無いので。』
							//不要なので、飛ばす。
							
							
							//if(!className.equals("")){
							System.out.println("in　クエリー, " + num_of_keywords_in_query);
								
							Experiment3 ls = new Experiment3(ts_arr_by_class.toArray(new TestSite[0]), num_of_keywords_in_query,num_of_keywords_in_label, false, false);
							
							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
							
							//ステップ数を指定
							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
							//収束まで行うか否か。
							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
							if(isConvergence == true)
								numOfSteps = -1;
							
							//コンソール画面表示
							Activator.showConsoleView();
							
							//長い処理なので、ジョブを使う
							Experiment3Job job = new Experiment3Job("実験4の実行", ls, numOfSteps);
							job.setUser(true);//ユーザーにポップアップを表示する。
							job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
							try {
								job.join();
							} catch (InterruptedException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
						}
					}
            	}
			}
		};
		experiment4Action.setText("実験4(全ての単語切り捨ての組み合わせの平均値)");
		
		
		
		experiment234Action = new Action(){
			public void run(){

//				System.out.println(" ========== 実験2(頻度大の単語から切り捨て)開始 ===================================== ");
//
//            	for( int num_of_keywords_in_label = 5; num_of_keywords_in_label <= 9; num_of_keywords_in_label++){
//
//	            	System.out.println("in　ラベル, " + num_of_keywords_in_label);
//	            	
//					Object o = viewer.getInput();
//					if(o instanceof ArrayList){
//						ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
//						ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
//						
//						for(TestSite ts: ts_arr){
//							//指定キーワード数と一致していたら追加する。
//							if(ts.getNumOfKeywords() == num_of_keywords_in_label){
//								ts_arr_by_class.add(ts);
//							}
//						}
//						
//						for(int num_of_keywords_in_query = 0; num_of_keywords_in_query <= num_of_keywords_in_label; num_of_keywords_in_query++){
//							//if(!className.equals("")){
//							System.out.println("in　クエリー, " + num_of_keywords_in_query);
//								
//							Experiment2 ls = new Experiment2(ts_arr_by_class.toArray(new TestSite[0]), num_of_keywords_in_query, false, true);
//							
//							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//							
//							//ステップ数を指定
//							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
//							//収束まで行うか否か。
//							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
//							if(isConvergence == true)
//								numOfSteps = -1;
//							
//							//コンソール画面表示
//							Activator.showConsoleView();
//							
//							//長い処理なので、ジョブを使う
//							Experiment2Job job = new Experiment2Job("実験2の実行", ls, numOfSteps);
//							job.setUser(true);//ユーザーにポップアップを表示する。
//							job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
//							try {
//								job.join();
//							} catch (InterruptedException e) {
//								// TODO 自動生成された catch ブロック
//								e.printStackTrace();
//							}
//						}
//					}
//            	}
            	
				System.out.println(" ========== 実験3(頻度小の単語から切り捨て)開始 ===================================== ");

            	for( int num_of_keywords_in_label = 6; num_of_keywords_in_label <= 9; num_of_keywords_in_label++){

	            	System.out.println("in　ラベル, " + num_of_keywords_in_label);
	            	
					Object o = viewer.getInput();
					if(o instanceof ArrayList){
						ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
						ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
						
						for(TestSite ts: ts_arr){
							//指定キーワード数と一致していたら追加する。
							if(ts.getNumOfKeywords() == num_of_keywords_in_label){
								ts_arr_by_class.add(ts);
							}
						}
						
						for(int num_of_keywords_in_query = 0; num_of_keywords_in_query <= num_of_keywords_in_label; num_of_keywords_in_query++){
							//if(!className.equals("")){
							System.out.println("in　クエリー, " + num_of_keywords_in_query);
								
							Experiment2 ls = new Experiment2(ts_arr_by_class.toArray(new TestSite[0]), num_of_keywords_in_query, false, false);
							
							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
							
							//ステップ数を指定
							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
							//収束まで行うか否か。
							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
							if(isConvergence == true)
								numOfSteps = -1;
							
							//コンソール画面表示
							Activator.showConsoleView();
							
							//長い処理なので、ジョブを使う
							Experiment2Job job = new Experiment2Job("実験3の実行", ls, numOfSteps);
							job.setUser(true);//ユーザーにポップアップを表示する。
							job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
							try {
								job.join();
							} catch (InterruptedException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
						}
					}
            	}
            	
            	System.out.println(" ========== 実験4開始 ===================================== ");
	
            	for( int num_of_keywords_in_label = 1; num_of_keywords_in_label <= 9; num_of_keywords_in_label++){

	            	System.out.println("in　ラベル, " + num_of_keywords_in_label);
	            	
					Object o = viewer.getInput();
					if(o instanceof ArrayList){
						ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
						ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
						
						for(TestSite ts: ts_arr){
							//指定キーワード数と一致していたら追加する。
							if(ts.getNumOfKeywords() == num_of_keywords_in_label){
								ts_arr_by_class.add(ts);
							}
						}
						
						for(int num_of_keywords_in_query = 1; num_of_keywords_in_query < num_of_keywords_in_label; num_of_keywords_in_query++){
							//キーワード数0 とキーワード数=ラベル含有キーワード数の場合は、
							//他の結果と同じになる。『切り捨てが無いので。』
							//不要なので、飛ばす。
							
							
							//if(!className.equals("")){
							System.out.println("in　クエリー, " + num_of_keywords_in_query);
								
							Experiment3 ls = new Experiment3(ts_arr_by_class.toArray(new TestSite[0]), num_of_keywords_in_query,num_of_keywords_in_label, false, false);
							
							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
							
							//ステップ数を指定
							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
							//収束まで行うか否か。
							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
							if(isConvergence == true)
								numOfSteps = -1;
							
							//コンソール画面表示
							Activator.showConsoleView();
							
							//長い処理なので、ジョブを使う
							Experiment3Job job = new Experiment3Job("実験4の実行", ls, numOfSteps);
							job.setUser(true);//ユーザーにポップアップを表示する。
							job.schedule();//ジョブをEclipseに登録しておきます。あとはEclipseが適切にrunを実行し、処理してくれる
							try {
								job.join();
							} catch (InterruptedException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
						}
					}
            	}
			}
		};
		experiment234Action.setText("実験234一括実行.");
		
		
        //正当性検証
        verifyAction = new Action(){
            public void run(){
                TestSite[] list = getCheckedItems();

                //タスクが選択されていない。
                if(list.length == 0){
                    MessageDialog.openWarning(fParent.getShell(), "正当性検証", "タスクをチェックボックスで選択してください。");
                    return;
                }

                boolean yn = MessageDialog.openQuestion(fParent.getShell(),
                        "正当性検証",
                        "選択された "+ list.length + " 個の学習タスクに対して正当性検証を行います。");

                //"No"なら何もしない。
                if(yn == false){
                    return;
                }
                int countOk = 0;
                int count_static = 0;
                int count_static_ = 0;
                int count_false = 0;
                for(TestSite site: list){
//                    if(site.verifyLabel2()){
//                        //System.out.println(site.getId() + ", "+ site.getFullyQualifiedClassName() + ", OK");
//                        countOk++;
//                    }else{
//                        //System.out.println(site.getId() + ", "+ site.getFullyQualifiedClassName() + ", NG");
//                    	System.out.println(site.getId() + ", "+ site.getFullyQualifiedClassName() + ", " + site.getSelectedString());
//                    }
                    String flg = site.verifyLabel2();
                    if(flg.equals("true")){
                    	countOk++;
                    }else if(flg.equals("static")){
                        count_static++;
//                        System.out.println("static, "+site.getId() + ", "+ site.getFullyQualifiedClassName() + ", " + site.getSelectedString());
//                        System.out.println(site.getId());
                    }else if(flg.equals("static?")){
                        count_static_++;
//                        System.out.println("static?, "+site.getId() + ", "+ site.getFullyQualifiedClassName() + ", " + site.getSelectedString());
//                        System.out.println(site.getId());
                    }else{
                    	//System.out.println("NG, "+site.getId() + ", "+ site.getFullyQualifiedClassName() + ", " + site.getSelectedString());
                    	System.out.println(site.getId());
                    	count_false++;
                    	site.deleteFile();
                    }
                    
//                    try{
//                    	site.verifyFunctions();
//                    }catch(Exception e){
//                    	e.printStackTrace();
//                    }
                    
                }
                System.out.println("OK= "+ countOk );
                System.out.println("static= "+ count_static);
                System.out.println("static?= "+ count_static_);
                System.out.println("false= "+ count_false);
                
            }
        };
        verifyAction.setText("正当性検証");
        
		
        //タスクをidでフィルタリング
        idFilterAction = new Action(){
            public void run(){
            	Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "入力したIDでタスクをフィルタリングします", resultContainer); 
            	md.open();
            	id_filtering = resultContainer.getText();
            	refresh();
            }
        };
        idFilterAction.setText("IDでフィルタリング");
        

        //タスクをクラス名でフィルタリング
        classNameFilterAction = new Action(){
            public void run(){
            	Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "入力したクラス名でタスクをフィルタリングします", resultContainer); 
            	md.open();
            	className_filtering = resultContainer.getText();
            	refresh();
            }
        };
        classNameFilterAction.setText("クラス名でフィルタリング");
        
        //テキストファイルを開く
  		openFileAction = new Action(){
  			public void run(){
  				TestSite[] list = getCheckedItems();

                //タスクが選択されていない。
                if(list.length == 0){
                    MessageDialog.openWarning(fParent.getShell(), "正当性検証", "タスクをチェックボックスで選択してください。");
                    return;
                }
                for(TestSite site: list){
                	site.openTextFile();
                }
  			}
  		};
      	openFileAction.setText("テキストファイルを開く");
        
		//アクションをツールバーとプルダウンメニューに組み込む。
		IActionBars bars = getViewSite().getActionBars();
		
		bars.getToolBarManager().add(refreshAction);
		bars.getToolBarManager().add(deleteAction);
		bars.getToolBarManager().add(localSearchAction);
		bars.getToolBarManager().add(gridSearchAction);
		bars.getToolBarManager().add(kpAction);
		bars.getToolBarManager().add(settingAction);
		bars.getToolBarManager().add(selectAllAction);
		bars.getToolBarManager().add(unSelectAllAction);
		bars.getToolBarManager().add(countTaskAction);
		
		bars.getMenuManager().add(refreshAction);
		bars.getMenuManager().add(deleteAction);
		bars.getMenuManager().add(localSearchAction);
		bars.getMenuManager().add(gridSearchAction);
		bars.getMenuManager().add(kpAction);
		bars.getMenuManager().add(settingAction);
		bars.getMenuManager().add(selectAllAction);
		bars.getMenuManager().add(unSelectAllAction);
		bars.getMenuManager().add(countTaskAction);
		
		//アクションをツールバーとポップアップメニューに組み込む。
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				// TODO Auto-generated method stub
				manager.add(importReloadAction);
				
				manager.add(refreshAction);
				manager.add(deleteAction);
				manager.add(localSearchAction);
				manager.add(gridSearchAction);
				manager.add(kpAction);
				manager.add(settingAction);
				manager.add(selectAllAction);
				manager.add(unSelectAllAction);
				
				manager.add(countTaskAction);
				manager.add(selectedTaskPropetyAction);
				
				manager.add(taskViewAction);
				
				manager.add(openFileAction);
				
				
//				manager.add(taskRandomSelectAction);
				
//				manager.add(experiment1Action);
				
				//manager.add(experiment1Action_exp);
				
//				manager.add(experiment1_renzoku_Action);
//				manager.add(experiment1_renzoku_Action2); 
				
				//manager.add(experiment1_renzoku_exp_Action);

				
				
				
				//manager.add(experiment1ByClassAction);

				//manager.add(experiment2Action);
				//manager.add(experiment2ByKeywordNumAction);
				
				//manager.add(experiment3ByKeywordNumAction);
				
				//manager.add(experiment4Action);
				
				//manager.add(experiment234Action);
				
//				manager.add(verifyAction);
//				manager.add(idFilterAction);
//				manager.add(classNameFilterAction);
//				manager.add(arrangeTaskAction1);
//				manager.add(arrangeTaskAction2);
				
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	
	public void runTasksUsingMultiThread(TestSite[] list,
			keywordProgramming.exp.Params para, String projectName, long startTime) {
		int max_thread_num = 3;
		if(list.length < max_thread_num){
			max_thread_num = list.length;
		}
	    TsThread thread[];
	    TsSync sync;
	    
	    // 同期オブジェクトの作成
	    sync = new TsSync();

	    // 子スレッド情報配列の作成
	    thread = new TsThread[max_thread_num];

	    // スレッドの起動
	    for (int i = 0; i < list.length; ) {
	    	
	    	for(int j = 0; j < max_thread_num; j++){
	    		if(i >= list.length){
	    			break;
	    		}
		    	thread[j] = new TsThread(sync, para, new plugin.testSite.exp.TestSite(list[i]), projectName, startTime);
			    thread[j].start();
			    			    
			    i++;
	    	}
		      
		    try {
			 Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
			 e.printStackTrace();
			}
		      
		    // 子の終了を待つ
		    sync.waitSync();
		    
	    }
	    
	    System.out.println(sync.output);
	    
		  String name = "";
			name += projectName;
			name += ",";
			name += startTime;//開始時間　
			name += ",";
			name += para.shortened_input_keywords;
			name += ",";
			name += para.separate_keywords;
			name += ",";
			name += para.common_subsequence;
			name += ",";
			name += para.ld_delete;
			name += ",";
			name += para.ld_replace;
			name += ",";
			name += para.ld_add;
			
		//出力テキストファイルの作成
    	String savefilename = PreferenceInitializer.extFileFolder + name + ".txt";
    	
    	//ファイルが無ければ作成する
    	File txtFile = new File(savefilename);
    	if (!txtFile.exists()){
			try {
				txtFile.createNewFile();
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
    	}
    	
		//フォルダがなければ作成する。
		File dir = txtFile.getParentFile();
		if (!dir.exists()) {  
		    dir.mkdirs();
		}
		
		try{
			PrintWriter export_pw = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
			//書き込み
			export_pw.print(sync.output);
			//終了処理
			export_pw.flush();
			export_pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	    
	}
	/*
	 * 画面をリフレッシュし、新たに登録されたものを表示する。
	 */
	public void refresh(){
		refreshAction.run();
	}
	
	/*
	 * チェックしたもののみ取得する。
	 * 
	 * 
	 */
	public TestSite[] getCheckedItems(){
		/*
		Object[]からTestSite[]のキャストはできない。
		TestSite[] site = (TestSite[])viewer.getCheckedElements();
		
		参考：http://d.hatena.ne.jp/fumokmm/20080902/1220372739
		*/
		Object[] o = viewer.getCheckedElements();
		List<Object> site = Arrays.asList(o);
		return site.toArray(new TestSite[site.size()]);
	}
	
	
	 class IdFilter extends ViewerFilter{
		 /*
		  * selectの3番目の引き数 e が、テーブルのレコードに対応するオブジェクトになっており、このメソッドでtrueを返したレコードのみが表示されるという仕組みです。
		  * (非 Javadoc)
		  * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		  */
         public boolean select(Viewer viewer, Object parent, Object e) {
        	 if(id_filtering.equals(""))
        		 return true;
        	 TestSite site = (TestSite)e;
        	 //カンマ、空白文字(半角、全角、タブ)、改行を区切り文字とする。
        	 List<String> ids = Arrays.asList(id_filtering.split("[, 　\t\n\r]", -1));
        	 return ids.contains(site.getId());
         }
	 }
		
	 class ClassNameFilter extends ViewerFilter{
		 /*
		  * selectの3番目の引き数 e が、テーブルのレコードに対応するオブジェクトになっており、このメソッドでtrueを返したレコードのみが表示されるという仕組みです。
		  * (非 Javadoc)
		  * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		  */
         public boolean select(Viewer viewer, Object parent, Object e) {
        	 if(className_filtering.equals(""))
        		 return true;
        	 TestSite site = (TestSite)e;
        	 //カンマ、空白文字(半角、全角、タブ)、改行を区切り文字とする。
        	 List<String> classNames = Arrays.asList(className_filtering.split("[, 　\t\n\r]", -1));
        	 return (classNames.contains(site.getClassSingleName()) || classNames.contains(site.getFullyQualifiedClassName())); 
         }
	 }
	  
}
