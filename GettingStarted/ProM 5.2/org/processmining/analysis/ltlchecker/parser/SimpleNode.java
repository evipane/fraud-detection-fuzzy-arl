/***********************************************************
 *      This software is part of the ProM package          *
 *             http://www.processmining.org/               *
 *                                                         *
 *            Copyright (c) 2003-2006 TU/e Eindhoven       *
 *                and is licensed under the                *
 *            Common Public License, Version 1.0           *
 *        by Eindhoven University of Technology            *
 *           Department of Information Systems             *
 *                 http://is.tm.tue.nl                     *
 *                                                         *
 **********************************************************/

package org.processmining.analysis.ltlchecker.parser;

import java.util.List;

/* Generated By:JJTree: Do not edit this line. SimpleNode.java */

public class SimpleNode implements Node {

	/**
	 * For determining the type of this node, a special enumeration is
	 * introduced. This is lateron used by translating parsetrees to
	 * formulatrees.
	 */

	// formula or static
	public static final int FORMULA = 0;
	public static final int SUBFORMULA = 1;

	// Propositional operators
	public static final int AND = 2;
	public static final int OR = 3;
	public static final int IMPLIES = 4;
	public static final int BIIMPLIES = 5;
	public static final int NOT = 6;

	// LTL operators
	public static final int ALWAYS = 7;
	public static final int EVENTUALLY = 8;
	public static final int NEXTTIME = 9;
	public static final int UNTIL = 10;

	// Quantors
	public static final int FORALL = 11;
	public static final int EXISTS = 12;

	// Comparing operators
	public static final int EQUAL = 13;
	public static final int NOTEQUAL = 14;
	public static final int LESSOREQUAL = 15;
	public static final int BIGGEROREQUAL = 16;
	public static final int LESSER = 17;
	public static final int BIGGER = 18;
	public static final int REGEXPEQUAL = 19;
	public static final int IN = 20;

	// Numerical operators
	public static final int PLUS = 21;
	public static final int MINUS = 22;
	public static final int MULT = 23;
	public static final int DIV = 24;

	// Other
	public static final int UNPROP = 25;
	public static final int BIPROP = 26;
	public static final int EXPR = 27;
	public static final int INT = 28;
	public static final int REAL = 29;
	public static final int STRING = 30;
	public static final int SET = 31;
	public static final int COMPPROP = 32;
	public static final int ATTRIBUTE = 33;
	public static final int USEFORMULA = 34;
	public static final int USESTATICFORMULA = 35;
	public static final int VALLIST = 36;
	public static final int ARGUMENT = 37;
	public static final int PROPOSITION = 38;
	public static final int DUMMY = 39;
	public static final int QUANTOR = 40;
	public static final int UNMINUS = 41;
	public static final int DATESTRING = 42;
	public static final int SETSTRING = 43;
	public static final int STRINGLIST = 44;
	public static final int CONCEPTSET = 45;

    public static final String[] typeNames = {
        "FORMULA",
        "SUBFORMULA",

        // Propositional operators
        "AND",
        "OR",
        "IMPLIES",
        "BIIMPLIES",
        "NOT",

        // LTL operators
        "ALWAYS",
        "EVENTUALLY",
        "NEXTTIME",
        "UNTIL",

        // Quantors
        "FORALL",
        "EXISTS",

        // Comparing operators
        "EQUAL",
        "NOTEQUAL",
        "LESSOREQUAL",
        "BIGGEROREQUAL",
        "LESSER",
        "BIGGER",
        "REGEXPEQUAL",
        "IN",

        // Numerical operators
        "PLUS",
        "MINUS",
        "MULT",
        "DIV",

        // Other
        "UNPROP",
        "BIPROP",
        "EXPR",
        "INT",
        "REAL",
        "STRING",
        "SET",
        "COMPPROP",
        "ATTRIBUTE",
        "USEFORMULA",
        "USESTATICFORMULA",
        "VALLIST",
        "ARGUMENT",
        "PROPOSITION",
        "DUMMY",
        "QUANTOR",
        "UNMINUS",
        "DATESTRING",
        "SETSTRING",
        "STRINGLIST",
        "CONCEPTSET"
    };

	/**
	 * The type of this node.
	 */
	protected int type;

	/**
	 * The name or text of this node.
	 */
	protected String name;

	/**
	 * An attribute associated with the node.
	 */
	protected Attribute attribute;

	protected Node parent;
	protected Node[] children;
	protected int id;
	protected LTLParser parser;

	public SimpleNode(int i) {
		id = i;
	}

	public SimpleNode(LTLParser p, int i) {
		this(i);
		parser = p;
	}

	/**
	 * Sets the name of this node.
	 *
	 * @param s The name of this node.
	 */
	public void setName(String s) {
		this.name = s;
	}

	/**
	 * Sets the name of this node.
	 *
	 * @param tok The name of this node, given an token.
	 */
	public void setName(Token tok) {
		this.name = tok.image;
	}

	/**
	 * Sets the type of this node.
	 *
	 * @param t The type of this node.
	 */
	public void setType(int t) {
		this.type = t;
	}

	/**
	 * Gets the name of this node.
	 *
	 * @return The name of this node.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the type of this node.
	 *
	 * @return The type of this node.
	 */
	public int getType() {
		return this.type;
	}

	public Attribute getAttribute() {
		return this.attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public void jjtOpen() {
	}

	public void jjtClose() {
	}

	public void jjtSetParent(Node n) {
		parent = n;
	}

	public Node jjtGetParent() {
		return parent;
	}

	public void jjtAddChild(Node n, int i) {
		if (children == null) {
			children = new Node[i + 1];
		} else if (i >= children.length) {
			Node c[] = new Node[i + 1];
			System.arraycopy(children, 0, c, 0, children.length);
			children = c;
		}
		children[i] = n;
	}

	public Node jjtGetChild(int i) {
		return children[i];
	}

	public int jjtGetNumChildren() {
		return (children == null) ? 0 : children.length;
	}

	/* You can override these two methods in subclasses of SimpleNode to
	   customize the way the node appears when the tree is dumped.  If
	   your output uses more than one line you should override
	   toString(String), otherwise overriding toString() is probably all
	   you need to do. */

	public String toString(boolean verbose) {
        return toString();
	}

	public String toString() {
		return LTLParserTreeConstants.jjtNodeName[id];
	}

	public String toString(String prefix) {
		return prefix + toString();
	}

	/* Override this method if you want to customize how the node dumps
	   out its children. */

	public void dump(String prefix) {
		System.out.println(toString(prefix));
		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				SimpleNode n = (SimpleNode) children[i];
				if (n != null) {
					n.dump(prefix + " ");
				}
			}
		}
	}
	
	public String asParseableString() {
		assert false: getName();
		return "";
	}
	public String asParseableString(List<FormulaParameter> args, int type) {
		assert(false);
		return "";
	}
}