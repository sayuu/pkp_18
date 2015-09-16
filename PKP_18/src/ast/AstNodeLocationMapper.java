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
 * �L�����b�g�̈ʒu����t�߂�{@code ASTNode}�����o����B
 * @version $Date$
 * @author Suguru ARAKAWA (Gluegent, Inc.)
 */
public class AstNodeLocationMapper {

    /**
     * �w��̃m�[�h�Ɋ܂܂��v�f�̒�����A�w��̈ʒu�ɑ��݂�����̂����o���ĕԂ��B
     * ���̂悤�Ȓl���i�[�����B
     * <ul>
     * <li>
     *   {@link AstNodeLocationMapper.FindResult#enclosing} -
     *     �w��̃L�����b�g�𒼐ړ����m�[�h�̂����A�œ��̃m�[�h�B
     *     ���̒l�͕K����{@code null}�ł���B
     * </li>
     * <li>
     *   {@link AstNodeLocationMapper.FindResult#previous} -
     *     �w��̃L�����b�g�������A�w��̃L�����b�g�̒��O�ɑ��݂���m�[�h�̂����A
     *     {@link AstNodeLocationMapper.FindResult#enclosing enclosing}��
     *     ���ڂ̎q�v�f�ł�����́B�������A���̗v�f�͗�̈ꕔ�łȂ���΂Ȃ�Ȃ��B
     * �@�@�@�@���Ƃ��΁A���L��{@code // CARET}�̈ʒu�ɃL�����b�g�����݂���ꍇ�A
     *     ���̒l��{@code System.out.println("prev");}�ƂȂ�B
     * <pre><code>
     * public void hoge() {
     *   System.out.println("prev");
     *   // CARET
     *   System.out.println("next");
     * }
     * </code></pre>
     *     ��L�̂悤�ȃm�[�h�����݂��Ȃ��ꍇ�A���̒l��{@code null}�ƂȂ�B
     * </li>
     * </ul>
     * @param root �����Ώۂ̃��[�g�m�[�h
     * @param position �ʒu
     * @return
     *      ���o���ʂ�\��{@link AstNodeLocationMapper.FindResult}�I�u�W�F�N�g�B
     *      {@code root}��{@code position}���܂܂Ȃ��ꍇ��{@code null}
     * @throws NullPointerException ������{@code null}���w�肳�ꂽ�ꍇ
     * @throws IllegalArgumentException
     *          {@code position}��{@code 0}�����̒l���w�肳�ꂽ�ꍇ
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

            // ���������q�v�f�̂���ɓ����̈ʒu���w�肳�ꂽ�ꍇ�A����ɉ��[���܂Ō�������
            if (child != null
                    && isEnclosing(child.getStartPosition(), child.getLength(),
                        position)) {
                current = child;
            }

            // �w��̈ʒu�𒼐ڈ͂ގq�v�f�������ł��Ȃ������ꍇ�A���ʂ�Ԃ��B
            else {
                return new FindResult(current, child);
            }
        }
    }

    /**
     * {@code [start, start+length]}�͈̔͂�
     * {@code position}���܂܂��ꍇ�̂�{@code true}��Ԃ��B
     * �������A{@code start, length}�͂������{@code 0}�ȏ�łȂ���΂Ȃ炸�A
     * �����łȂ��ꍇ�͏��{@code false}��Ԃ��B
     * @param start �J�n�ʒu
     * @param length ����
     * @param position �ʒu
     * @return
     *      {@code [start, start+length]}�͈̔͂�
     *      {@code position}���܂܂��ꍇ�̂�{@code true}
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
     * �̎��s���ʂ�\������I�u�W�F�N�g�B
     * @version $Date$
     * @author Suguru ARAKAWA (Gluegent, Inc.)
     */
    public static class FindResult {

        /**
         * �w��̈ʒu�𒼐ڈ͂ރm�[�h�B
         */
        public final ASTNode enclosing;

        /**
         * �w��̈ʒu�̒��O�ɏo������m�[�h�B
         * ���݂��Ȃ��ꍇ��{@code null}
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
         * �w��ʒu�ɍł��߂����O�̃m�[�h��Ԃ��B
         * @return �w��ʒu�ɍł��߂����O�̃m�[�h
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
     * �C���X�^���X���̋֎~�B
     */
    private AstNodeLocationMapper() {
        super();
    }

}
