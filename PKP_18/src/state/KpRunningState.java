package state;

/*
 * キーワードプログラミングのアルゴリズムを実行して出力候補群を生成する時の、
 * プラグインの状態を表すフラグを管理するクラス
 * 
 * コード・コンプリーション実行
 * キーワードプログラミング実行（ビューからタスク選択により）
 * ローカルサーチ実行
 * グリッドサーチ実行
 */
public class KpRunningState {
	public static final String CODE_COMPLETION = "STATE_CODE_COMPLETION";
	public static final String STATE_KP_BATCH = "STATE_KP_BATCH";
	public static final String LOCAL_SEARCH_ONLINE = "STATE_LOCAL_SEARCH_ONLINE";
	public static final String LOCAL_SEARCH_BATCH = "STATE_LOCAL_SEARCH_BATCH";
	public static final String GRID_SEARCH = "STATE_GRID_SEARCH";
}
