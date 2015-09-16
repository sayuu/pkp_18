/*
 * Copyright 2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */



package ast;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

/**
 * キャレットの位置から付近の{@code ASTNode}を検出する。
 * @version $Date$
 * @author Suguru ARAKAWA (Gluegent, Inc.)
 */
public class AstNodeLocationMapper {

    /**
     * 指定のノードに含まれる要素の中から、指定の位置に存在するものを検出して返す。
     * 次のような値が格納される。
     * <ul>
     * <li>
     *   {@link AstNodeLocationMapper.FindResult#enclosing} -
     *     指定のキャレットを直接内包するノードのうち、最内のノード。
     *     この値は必ず非{@code null}である。
     * </li>
     * <li>
     *   {@link AstNodeLocationMapper.FindResult#previous} -
     *     指定のキャレットを内包せず、指定のキャレットの直前に存在するノードのうち、
     *     {@link AstNodeLocationMapper.FindResult#enclosing enclosing}の
     *     直接の子要素であるもの。ただし、その要素は列の一部でなければならない。
     * 　　　　たとえば、下記の{@code // CARET}の位置にキャレットが存在する場合、
     *     この値は{@code System.out.println("prev");}となる。
     * <pre><code>
     * public void hoge() {
     *   System.out.println("prev");
     *   // CARET
     *   System.out.println("next");
     * }
     * </code></pre>
     *     上記のようなノードが存在しない場合、この値は{@code null}となる。
     * </li>
     * </ul>
     * @param root 検索対象のルートノード
     * @param position 位置
     * @return
     *      検出結果を表す{@link AstNodeLocationMapper.FindResult}オブジェクト。
     *      {@code root}が{@code position}を含まない場合は{@code null}
     * @throws NullPointerException 引数に{@code null}が指定された場合
     * @throws IllegalArgumentException
     *          {@code position}に{@code 0}未満の値が指定された場合
     */
    public static FindResult findNode(ASTNode root, int position) {
        if (root == null) {
            throw new NullPointerException("root"); //$NON-NLS-1$
        }
        if (position < 0) {
            throw new IllegalArgumentException(MessageFormat.format(
                "position = {0} (< 0)", //$NON-NLS-1$
                position));
        }
        ASTNode current = root;
        if (!isEnclosing(root.getStartPosition(), root.getLength(), position)) {
            return null;
        }

        while (true) {
            ASTNode child = findNearestPreviousChild(current, position);

            // 発見した子要素のさらに内部の位置が指定された場合、さらに奥深くまで検索する
            if (child != null
                    && isEnclosing(child.getStartPosition(), child.getLength(),
                        position)) {
                current = child;
            }

            // 指定の位置を直接囲む子要素が発見できなかった場合、結果を返す。
            else {
                return new FindResult(current, child);
            }
        }
    }

    /**
     * {@code [start, start+length]}の範囲に
     * {@code position}が含まれる場合のみ{@code true}を返す。
     * ただし、{@code start, length}はいずれも{@code 0}以上でなければならず、
     * そうでない場合は常に{@code false}を返す。
     * @param start 開始位置
     * @param length 長さ
     * @param position 位置
     * @return
     *      {@code [start, start+length]}の範囲に
     *      {@code position}が含まれる場合のみ{@code true}
     */
    public static boolean isEnclosing(int start, int length, int position) {
        if (start < 0 || length < 0) {
            return false;
        }
        return (start <= position && position <= start + length);
    }

    private static ASTNode findNearestPreviousChild(ASTNode node, int position) {
        int childAt = -1;
        ASTNode childNode = null;
        for (Object o : node.structuralPropertiesForType()) {
            if (o instanceof ChildPropertyDescriptor) {
                ASTNode n = (ASTNode) node
                    .getStructuralProperty((StructuralPropertyDescriptor) o);
                if (n != null) {
                    int start = n.getStartPosition();
                    if (start >= 0 && childAt < start && start <= position) {
                        childAt = start;
                        childNode = n;
                    }
                }
            }
            else if (o instanceof ChildListPropertyDescriptor) {
                List<?> list = (List<?>) node
                    .getStructuralProperty((StructuralPropertyDescriptor) o);
                for (Object e : list) {
                    ASTNode n = (ASTNode) e;
                    assert n != null;
                    int start = n.getStartPosition();
                    if (start >= 0 && childAt < start && start <= position) {
                        childAt = start;
                        childNode = n;
                    }
                }
            }
        }
        return childNode;
    }

    /**
     * {@link AstNodeLocationMapper#findNode(ASTNode, int)}
     * の実行結果を表現するオブジェクト。
     * @version $Date$
     * @author Suguru ARAKAWA (Gluegent, Inc.)
     */
    public static class FindResult {

        /**
         * 指定の位置を直接囲むノード。
         */
        public final ASTNode enclosing;

        /**
         * 指定の位置の直前に出現するノード。
         * 存在しない場合は{@code null}
         */
        public final ASTNode previous;

        FindResult(ASTNode enclosing, ASTNode previous) {
            super();
            if (enclosing == null) {
                throw new NullPointerException("enclosing"); //$NON-NLS-1$
            }
            this.enclosing = enclosing;
            this.previous = previous;
        }

        /**
         * 指定位置に最も近い直前のノードを返す。
         * @return 指定位置に最も近い直前のノード
         */
        public ASTNode getNearestNode() {
            if (previous == null) {
                return enclosing;
            }
            else {
                return previous;
            }
        }

        @Override
        public String toString() {
            return MessageFormat.format(
                "'{'enclosing=[{0}], previous=[{1}]'}'", enclosing, previous);
        }
    }

    /**
     * インスタンス化の禁止。
     */
    private AstNodeLocationMapper() {
        super();
    }

}
