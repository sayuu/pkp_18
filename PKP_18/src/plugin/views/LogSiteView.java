package plugin.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ast.AstLocalCode;
import ast.Import;

import plugin.activator.Activator;
import plugin.testSite.TestSite;
import plugin.testSite.TestSiteComparator;

public class LogSiteView extends org.eclipse.ui.part.ViewPart{

	private CheckboxTableViewer viewer; 
	private Composite fParent;
	
  	private IAction refreshAction;//画面をリフレッシュするアクション
  	private IAction moveTaskAction;//履歴をタスクビューに追加するアクション
  	private IAction settingAction;//特徴の重みの初期値の設定アクション
  	
  	private IWorkbenchWindow window;
  	
  	
	@Override
	public void createPartControl(Composite parent) {
		// TODO 自動生成されたメソッド・スタブ
		//インポート文読み込み。リスナの登録
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
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

		
		//チェックボックス状態監視のためのリスナ
//		viewer.addCheckStateListener(new ICheckStateListener() {  
//		    public void checkStateChanged(CheckStateChangedEvent event) {  
//		    	TestSite site = (TestSite)event.getElement();
//		    	System.out.println(site.getSelectedString());
//		    
//		    }  
//		});  
		
//		TableViewerColumn viewerColumn_check = new TableViewerColumn(viewer, SWT.LEFT);
//		//viewerColumn_check.setLabelProvider(new ClassNameLabelProvider());
//		TableColumn tableColumn_check = viewerColumn_check.getColumn();
//		tableColumn_check.setText("選択");
//		tableColumn_check.setWidth(100); // もしくは pack()
//		viewerColumn_check.setEditingSupport(new CheckboxEditingSupport(viewer));
		
		TableViewerColumn viewerColumn_saveTime = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_saveTime.setLabelProvider(new SaveTimeLabelProvider());
		TableColumn tableColumn_saveTime = viewerColumn_saveTime.getColumn();
		tableColumn_saveTime.setText("保存日時");
		tableColumn_saveTime.setWidth(190); // もしくは pack()
		
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
		

		TableViewerColumn viewerColumn_text = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_text.setLabelProvider(new TextLabelProvider());
		TableColumn tableColumn_text = viewerColumn_text.getColumn();
		tableColumn_text.setText("生成された出力");
		tableColumn_text.setWidth(320); // もしくは pack()
//		tableColumn_text.pack();
//		viewerColumn.setEditingSupport(new MyEditingSupport(viewer));
		

		TableViewerColumn viewerColumn_keyword = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_keyword.setLabelProvider(new KeywordLabelProvider());
		TableColumn tableColumn_keyword = viewerColumn_keyword.getColumn();
		tableColumn_keyword.setText("入力キーワード");
		tableColumn_keyword.setWidth(320); // もしくは pack()
//		tableColumn_text.pack();
		//セルをエディット可能にする。
//		viewerColumn_keyword.setEditingSupport(new KeywordCellEditingSupport(viewer));
		

		TableViewerColumn viewerColumn_retType = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_retType.setLabelProvider(new ReturnTypeLabelProvider());
		TableColumn tableColumn_retType = viewerColumn_retType.getColumn();
		tableColumn_retType.setText("望ましい返り値");
		tableColumn_retType.setWidth(320); // もしくは pack()
//		tableColumn_retType.pack();
//		viewerColumn.setEditingSupport(new MyEditingSupport(viewer));
		
		
		viewer.setInput(getItems());
		
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
		
		String path = TestSite.LogSiteFolder;//ログを読み取る
		
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return null;
		}

	    File[] classFolders = dir.listFiles();
	    for (File folder: classFolders) {
	        File[] files = folder.listFiles();
	        for(File file: files){
	        	//出力文字列ログファイルは除く。
	        	if(file.getName().matches(".*out\\.txt$") == false){
		        	TestSite t = new TestSite(file, true);
		        	list.add(t);
	        	}
	        }
	    }
	    //時間の降順にソートする。
	    Collections.sort(list, new TestSiteComparator());
		return list;
	}
	
	private void registerAction(){
		//リフレッシュアクションを作成
		refreshAction = new Action(){
			public void run(){
				viewer.setInput(getItems());
			}
		};
		refreshAction.setText("リフレッシュ");
//		refreshAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_REFRESH));
		refreshAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui.browser", "$nl$/icons/elcl16/nav_refresh.gif"));

		//履歴を学習タスクビューに移動するアクションを作成
		moveTaskAction = new Action(){
			public void run(){
				//選択されたアイテムを得る
				TestSite[] list = getCheckedItems();
				
				//タスクが選択されていない。
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "学習タスクビューへの移動", "移動するタスクをチェックボックスで選択してください。");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"学習タスクとして登録する",
						"選択された "+ list.length + " 個の履歴を学習タスクとして追加します。");
			
				//"No"なら何もしない。
				if(yn == false){
					return;
				}
				
				//学習タスクの場所に再保存
				for(int i = 0; i < list.length; i++){
					list[i].copyAndMoveFileToTestSiteFolder();
				}
				//ビューのリフレッシュ
				TestSiteView view = (TestSiteView) window.getActivePage().findView("jp.ac.hokudai.eng.complex.kussharo.sayuu.kp.testSiteView");
				view.refresh();
			}
		};
		moveTaskAction.setText("履歴を学習タスクビューに移動する");
//		moveTaskAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_EXPORT));
		moveTaskAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "$nl$/icons/full/etool16/export_wiz.gif"));


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

		//アクションをツールバーとプルダウンメニューに組み込む。
		IActionBars bars = getViewSite().getActionBars();
		
		bars.getToolBarManager().add(refreshAction);
		bars.getToolBarManager().add(settingAction);
		bars.getToolBarManager().add(moveTaskAction);
		
		bars.getMenuManager().add(refreshAction);
		bars.getMenuManager().add(settingAction);
		bars.getMenuManager().add(moveTaskAction);
		
		
		//アクションをツールバーとポップアップメニューに組み込む。
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				// TODO Auto-generated method stub
				manager.add(refreshAction);
				manager.add(settingAction);
				manager.add(moveTaskAction);
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
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
}
