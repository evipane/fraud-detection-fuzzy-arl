/* Copyright (C) 2005, University of Massachusetts, Multi-Agent Systems Lab
 * See LICENSE for license information
 */

/***************************************************************************************
 * Expression.java
 ***************************************************************************************/
package org.processmining.plugins.compliance.rules.select.constraints;

import java.text.ParseException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class allows you to evaluate simple, fully parenthesized
 * expressions, using references from a given hashtable.  Here's
 * some examples:
 * <PRE>
 *   (a == b)
 *   ((a != b) || (b == c))
 *   ((a >= 0) && (a < 10))
 *   ((a != null) && (a == "some string"))
 *   (((!(a == b)) && (c < 10)) || (d == "cheese"))
 *   ((a + 5) == b)
 *   ((3 / 2) == 1)
 *   (((a + b) < c) && ((c / 2) > 5))
 *   ((a % 2) == 0)
 *   ((a % 2) == 1)
 * </PRE>
 * In the above, numbers and quoted strings represent their
 * given values.  Bare words (e.g. a, b, c, and d above) represent
 * references to variables, which will be dereferenced from a 
 * hashtable when the expression is evaluated.  For instance, 
 * in the third example above, when the expression is evaluated
 * the key "a" will be used to query a hashtable (passed into the
 * evaluation function).  Let's say in the hashtable there is a
 * field "5" stored under the key "a".  So this expression will
 * then actually evaluate to be <TT>((5 >= 0) && (5 < 10))</TT>,
 * which will be true.  Data in the hashtable, and in quoted strings,
 * will be converted to a number if possible, permitting typical
 * numeric comparisons.  In general the expressions parsed by this
 * class should be pretty intuitive.
 * <P>
 * The keyword "null" can be used to check for the absence of
 * a key in the hashtable, as in the fourth example above.
 * <P>
 * Operators permitted: ==, !=, <, >, <=, >=, !, &&, ||, +, -, *, /, %.
 */
public class Expression {
    Object operand1 = null, operand2 = null, operator = null;

    /**
     * Pass your string representation of the expression into this function
     * and it will parse it into a hierarchy of Expression objects which 
     * can be evaluated.
     * @param s The string version of the expression
     */
    public Expression(String s) throws ParseException {
        Vector v = new Vector();

        StringTokenizer e = new StringTokenizer(s, " \t\r\n\"()&!|=<>-+*/%", true);
        while (e.hasMoreElements()) {
            String t = e.nextToken();
            v.addElement(t);
        }

        try {
            parseExpression(v);
        } catch (ParseException ex) {
            throw new ParseException(ex.getMessage() + " \"" + s + "\"", ex.getErrorOffset()); 
        }
    }

    /**
     * This constructor isn't for public consumption.
     */
    protected Expression(Vector v) throws ParseException {
        parseExpression(v);
    }
    
    public Expression(Expression operand1, String operator, Expression operand2)
    {
    	this.operand1=operand1;
    	this.operand2=operand2;
    	this.operator=operator;
    }

    /**
     * Accessors
     */
    public Object getOperand1() { return operand1; }
    public Object getOperand2() { return operand2; }
    public Object getOperator() { return operator; }
    
    /**
     * Use this function to check if the expression evaluates
     * to be true or not.  The hashtable is a set of key/value
     * pairs where any bare word in the expression is used as
     * a key.  When the expression is evaluated, it uses these
     * keys to find the appropriate value (if any) in the hashtable
     * and then perform the check with that value.
     * <P>
     * This function uses the evaluate method to calculate the
     * value of the expression. 
     * @param h A hashtable of references
     * @return True if the expression evaluates to true
     */
    public boolean isTrue(Hashtable h) {
        Object o = evaluate(h);

        if (o == null)
            return false;

        if (o instanceof Boolean)
            return ((Boolean)o).booleanValue();
        else {
            System.err.println("Warning: Top level expression returned non-boolean value");
            return false;
        }
    }

    /**
     * Just like isTrue, except not.
     * @param h A hashtable of references
     * @return true, if the expression is false
     * @see #isTrue
     */
    public boolean isFalse(Hashtable h) { return (! isTrue(h)); }

    /**
     * This method performs the evaluation of the expression,
     * implementing binary and unary operator functions, and
     * recursively calling itself for sub-expressions.
     * <P>
     * This short ciruits ||s and &&s.
     * @param h A hashtable of references
     * @return True if the expression evaluates to true
     */
    public Object evaluate(Hashtable h) {
        Object ao = null, bo = null;

        // Convert operand 1
        ao = convertOperand(operand1, h);
        
        // Check ORs and ANDs
        if (operator == null) {
            return ao;

        } else if (operator.equals("||")) {
            // Short?
            if ((ao instanceof Boolean) && ((Boolean)ao).booleanValue())
                return new Boolean(true);
            else {
                // Nope
                bo = convertOperand(operand2, h);

                if ((bo instanceof Boolean) && ((Boolean)bo).booleanValue()) {
                    return new Boolean(true);
                } 

                if ((ao != null) && (!(ao instanceof Boolean))) {
                    System.err.println("Error, " + operator + " has incorrect operand types");
                } else if ((bo != null) && (!(bo instanceof Boolean))) {
                    System.err.println("Error, " + operator + " has incorrect operand types");
                } else
                    return new Boolean(false);
            }

        } else if (operator.equals("&&")) {
            // Short?
            if ((ao instanceof Boolean) && (!((Boolean)ao).booleanValue()))
                return new Boolean(false);
            else {
                // Nope
                bo = convertOperand(operand2, h);

                if ((ao instanceof Boolean) &&
                    (bo instanceof Boolean)) {
                    return new Boolean(((Boolean)ao).booleanValue() && ((Boolean)bo).booleanValue());
                }

                if ((ao != null) && (!(ao instanceof Boolean))) {
                    System.err.println("Error, " + operator + " has incorrect operand types");
                } else if ((bo != null) && (!(bo instanceof Boolean))) {
                    System.err.println("Error, " + operator + " has incorrect operand types");
                } else
                    return new Boolean(false);
            }
        } 

        // Get operand 2 if we haven't already
        if (bo == null) {
            bo = convertOperand(operand2, h);
        }

        // Little hack to support numerical comparisons with strings
        if ((ao instanceof Number) && (bo instanceof String)) {
            bo = new Double(bo.hashCode());
        }
        if ((bo instanceof Number) && (ao instanceof String)) {
            ao = new Double(ao.hashCode());
        }

        // eq, noteq
        if (operator.equals("==")) {
            if ((ao == null) || (bo == null)) {
                return new Boolean(ao == bo);
            } else if ((ao instanceof Number) &&
                (bo instanceof Number)) {
                double ad = ((Number)ao).doubleValue();
                double bd = ((Number)bo).doubleValue();
                return new Boolean(ad == bd);
            } else {
                return new Boolean(ao.toString().equals(bo.toString()));
            }

        } else if (operator.equals("!=")) {
            if ((ao == null) || (bo == null)) {
                return new Boolean(ao != bo);
            } else if ((ao instanceof Number) &&
                (bo instanceof Number)) {
                double ad = ((Number)ao).doubleValue();
                double bd = ((Number)bo).doubleValue();
                return new Boolean(ad != bd);
            } else {
                return new Boolean(! ao.toString().equals(bo.toString()));
            }
        }
            
        // The rest
        else if (bo == null) {
            //System.err.println("Warning: Unexpected null operand op2 in (" + a + " " + op + " " + b + ")");

        } else if (operator.equals("!")) {
            if (bo instanceof Boolean) {
                return new Boolean(! ((Boolean)bo).booleanValue());
            } else {
                System.err.println("Error, " + operator + " has incorrect operand types");
            }
            
        } else if (ao == null) {
            //System.err.println("Warning: Unexpected null operand op1 in (" + a + " " + op + " " + b + ")");

        } else if (operator.equals(">")) {
            if ((ao instanceof Number) &&
                (bo instanceof Number)) {
                double ad = ((Number)ao).doubleValue();
                double bd = ((Number)bo).doubleValue();
                return new Boolean(ad > bd);
            } else {
                System.err.println("Error, " + operator + " has incorrect operand types");
            }

        } else if (operator.equals("<")) {
            if ((ao instanceof Number) &&
                (bo instanceof Number)) {
                double ad = ((Number)ao).doubleValue();
                double bd = ((Number)bo).doubleValue();
                return new Boolean(ad < bd);
            } else {
                System.err.println("Error, " + operator + " has incorrect operand types");
            }

        } else if (operator.equals(">=")) {
            if ((ao instanceof Number) &&
                (bo instanceof Number)) {
                double ad = ((Number)ao).doubleValue();
                double bd = ((Number)bo).doubleValue();
                return new Boolean(ad >= bd);
            } else {
                System.err.println("Error, " + operator + " has incorrect operand types");
            }

        } else if (operator.equals("<=")) {
            if ((ao instanceof Number) &&
                (bo instanceof Number)) {
                double ad = ((Number)ao).doubleValue();
                double bd = ((Number)bo).doubleValue();
                return new Boolean(ad <= bd);
            } else {
                System.err.println("Error, " + operator + " has incorrect operand types");
            }

        } else if (operator.equals("+")) {
            if ((ao instanceof Number) &&
                (bo instanceof Number)) {
                double ad = ((Number)ao).doubleValue();
                double bd = ((Number)bo).doubleValue();
                return new Double(ad + bd);
            } else {
                System.err.println("Error, " + operator + " has incorrect operand types");
            }

        } else if (operator.equals("*")) {
            if ((ao instanceof Number) &&
                (bo instanceof Number)) {
                double ad = ((Number)ao).doubleValue();
                double bd = ((Number)bo).doubleValue();
                return new Double(ad * bd);
            } else {
                System.err.println("Error, " + operator + " has incorrect operand types");
            }

        } else if (operator.equals("-")) {
            if ((ao instanceof Number) &&
                (bo instanceof Number)) {
                double ad = ((Number)ao).doubleValue();
                double bd = ((Number)bo).doubleValue();
                return new Double(ad - bd);
            } else {
                System.err.println("Error, " + operator + " has incorrect operand types");
            }

        } else if (operator.equals("/")) {
            if ((ao instanceof Number) &&
                (bo instanceof Number)) {
                double ad = ((Number)ao).doubleValue();
                double bd = ((Number)bo).doubleValue();
                return new Double(ad / bd);
            } else {
                System.err.println("Error, " + operator + " has incorrect operand types");
            }

        } else if (operator.equals("%")) {
            if ((ao instanceof Number) &&
                (bo instanceof Number)) {
                double ad = ((Number)ao).doubleValue();
                double bd = ((Number)bo).doubleValue();
                return new Double(ad % bd);
            } else {
                System.err.println("Error, " + operator + " has incorrect operand types");
            }
        }

        return null;
    }

    /**
     * Converts an object to its Object type for evaluation
     * @param o The operand to convert
     * @param h The hashtable of data to reference
     */
    protected Object convertOperand(Object o, Hashtable h) {
        Object r;

        if (o instanceof Expression) {
            r = ((Expression)o).evaluate(h);

        } else if (o instanceof String) {
            String s = (String)o;
            if (s.charAt(0) == '"') {
                r = s.substring(1, s.length() - 1);
            } else if (s.equalsIgnoreCase("null")) {
                r = null;
            } else {
                r = h.get(s);
            }

            if (r instanceof String) {
                try {
                    Double d = Double.valueOf((String)r);
                    r = d;
                } catch (Exception e) { }
            }

        } else {
            r = o;
        }

        return r;
    }

    /**
     * Parses the vector of tokens into a hierarchy of expressions.
     * @param v A vector of tokens
     */
    protected void parseExpression(Vector v) throws ParseException {
        String t;
        boolean neg = false;

        // Get a
        t = nextToken(v);
        if (t == null) {
            throw new ParseException("Unexpected null token", index);
        }
        if (t.equals("(")) {
            // new expression
            operand1 = new Expression(v);
            t = nextToken(v);
            if (t == null) {
                throw new ParseException("Unexpected null token", index);
            }
            if (!t.equals(")")) {
                throw new ParseException("Token " + t + " is out of place", index);
            }
        } else if (OperatorTest.isSign(t)) {
            // +/- something
            v.insertElementAt(t, 0);
            v.insertElementAt("0", 0);
            operand1 = new Expression(v);
        } else if (t.charAt(0) == '"') {
            // string
            operand1 = t;
        } else if (Character.isDigit(t.charAt(0))) {
            // number
            operand1 = Double.valueOf(t);
        } else if (Character.isLetter(t.charAt(0)) ||
                   (t.charAt(0) == '_')) {
            // variable
            operand1 = t;
        } else if (t.charAt(0) == '!') {
            // ! (not)
            operator = t;
        } else {
            throw new ParseException("Token " + t + " is out of place", index);
        }

        // Check
        if (v.isEmpty()) {
            return;
        }

        // Get op
        if (operator == null) {
            t = nextToken(v);
            if (t == null) {
                throw new ParseException("Unexpected null token", index);
            }
            if (t.equals(")")) {
                return;
            } else if (OperatorTest.isOperator(t)) {
                operator = t;
            } else {
                throw new ParseException("Token " + t + " is out of place", index);
            }
        }

        // Get b
        t = nextToken(v);
        if (t == null) {
            throw new ParseException("Unexpected null token", index);
        }
        if (t.equals("(")) {
            // new expression
            operand2 = new Expression(v);
            t = nextToken(v);
            if (t == null) {
                throw new ParseException("Unexpected null token", index);
            }
            if (!t.equals(")")) {
                throw new ParseException("Token " + t + " is out of place", index);
            }
        } else if (OperatorTest.isSign(t)) {
            // +/- something
            v.insertElementAt(t, 0);
            v.insertElementAt("0", 0);
            operand2 = new Expression(v);
        } else if (t.charAt(0) == '"') {
            // string
            operand2 = t;
        } else if (Character.isDigit(t.charAt(0))) {
            // number
            operand2 = Double.valueOf(t);
        } else if (Character.isLetter(t.charAt(0)) ||
                   (t.charAt(0) == '_')) {
            // variable
            operand2 = t;
        } else {
            throw new ParseException("Token " + t + " is out of place", index);
        }
    }

    /**
     * Gets the next token in the Vector, erasing whitespace (where appropriate)
     * merging operator tokens and generating strings
     * @param v A vector of raw tokens
     */
    private int index = 0;
    protected String nextToken(Vector v) {
        String s, t = null;

        while (!v.isEmpty()) {
            s = (String)v.firstElement();
            v.removeElementAt(0);
            index += s.length();

            if (s.equals(" ")) { continue; }
            if (s.equals("\t")) { continue; }
            if (s.equals("\n")) { continue; }
            if (s.equals("\r")) { continue; }

            if (s.equals("\"")) {
                t = s;
                while (!v.isEmpty()) {
                    s = (String)v.firstElement();
                    v.removeElementAt(0);
                    index += s.length();
                    t += s;
                    if (s.equals("\"")) {
                        break;
                    }
                }

            } else if (s.equals("=")) {
                t = s;
                s = (String)v.firstElement();
                if (s.equals("=")) {
                    v.removeElementAt(0);
                    index += s.length();
                    t += s;
                } else {
                    System.err.println("Error: Unknown token \"" + t + "\"");
                }

            } else if (s.equals("|")) {
                t = s;
                s = (String)v.firstElement();
                if (s.equals("|")) {
                    v.removeElementAt(0);
                    index += s.length();
                    t += s;
                } else {
                    System.err.println("Error: Unknown token \"" + t + "\"");
                }

            } else if (s.equals("&")) {
                t = s;
                s = (String)v.firstElement();
                if (s.equals("&")) {
                    v.removeElementAt(0);
                    index += s.length();
                    t += s;
                } else {
                    System.err.println("Error: Unknown token \"" + t + "\"");
                }

            } else if (s.equals("!")) {
                t = s;
                s = (String)v.firstElement();
                if (s.equals("=")) {
                    v.removeElementAt(0);
                    index += s.length();
                    t += s;
                }

            } else if (s.equals(">")) {
                t = s;
                s = (String)v.firstElement();
                if (s.equals("=")) {
                    v.removeElementAt(0);
                    index += s.length();
                    t += s;
                }

            } else if (s.equals("<")) {
                t = s;
                s = (String)v.firstElement();
                if (s.equals("=")) {
                    v.removeElementAt(0);
                    index += s.length();
                    t += s;
                }

            } else if (s.endsWith("E") && Character.isDigit(s.charAt(0))) {
                t = s;
                s = (String)v.firstElement();
                if (s.equals("-") || s.equals("+")) {
                    v.removeElementAt(0);
                    index += s.length();
                    t += s;
                    
                    s = (String)v.firstElement();
                    if (Character.isDigit(s.charAt(0))) {
                        v.removeElementAt(0);
                        index += s.length();
                        t += s;
                    } else {
                        System.err.println("Error: Unknown token \"" + t + "\"");
                    }
                    
                } else {
                    System.err.println("Error: Unknown token \"" + t + "\"");
                }

            } else {                
                t = s;
            }

            break;
        }

        return t;
    }

    /**
     * Converts the expression to a more normal polynomial form,
     * consisting of a sum of a number of variable/coefficient
     * pairs.  This data is returned in a hashtable, where each
     * key is a variable name, and the data stored in that key
     * will be a Double representing the coefficient.  The special
     * key CONSTANT will contain the constant values, and
     * the key OPERATOR will contain the operator string.
     * <P>
     * All the items in the expression will be moved over
     * to the LHS, so the RHS is effectively zero.
     * <P>
     * In this function, strings are considered constants, and
     * will be converted to some Double form with reasonable
     * uniqueness within the space.
     * <P>
     * If the expression cannot be reduced to this form, because
     * of embedded boolean expressions or nonlinear expressions
     * null will be returned.  It will also return null for 
     * linear multiplications, i.e. (a * b) cannot be represented
     * this way.
     * @return A hashtable as described above.
     */
    public Hashtable generateLinearForm() {
        Hashtable atable, btable;

        // Checks
        if (isBooleanExpression()) return null;
        if ((operand1 == null) || (operand2 == null)) return null;

        // Op 1
        if (operand1 instanceof Expression) {
            atable = ((Expression)operand1).generateLinearForm();
            if (atable == null) return null;
        } else {
            Object ao = simplifyOperand(operand1);
            if (ao == null) return null;
            atable = new Hashtable();
            if (ao instanceof String) {
                atable.put(ao, new Double(1));
            } else if (ao instanceof Double) {
                atable.put("CONSTANT", ao);
            } else
                return null;
        }

        // Op 2
        if (operand2 instanceof Expression) {
            btable = ((Expression)operand2).generateLinearForm();
            if (btable == null) return null;
        } else {
            Object bo = simplifyOperand(operand2);
            if (bo == null) return null;
            btable = new Hashtable();
            if (bo instanceof String) {
                btable.put(bo, new Double(1));
            } else if (bo instanceof Double) {
                btable.put("CONSTANT", bo);
            } else
                return null;
        }

        // Apply operator
        if (operator.equals("+")) {
            Enumeration e = btable.keys();
            while (e.hasMoreElements()) {
                String key = (String)e.nextElement();
                if (atable.containsKey(key)) {
                    Double ad = (Double)atable.get(key);
                    Double bd = (Double)btable.get(key);
                    Double rd = new Double(ad.doubleValue() + bd.doubleValue());
                    atable.put(key, rd);
                } else {
                    atable.put(key, btable.get(key));
                }
            }

        } else if (operator.equals("-") || isComparisonExpression()) {
            Enumeration e = btable.keys();
            while (e.hasMoreElements()) {
                String key = (String)e.nextElement();
                if (atable.containsKey(key)) {
                    Double ad = (Double)atable.get(key);
                    Double bd = (Double)btable.get(key);
                    Double rd = new Double(ad.doubleValue() - bd.doubleValue());
                    atable.put(key, rd);
                } else {
                    Double bd = (Double)btable.get(key);
                    Double rd = new Double(-1 * bd.doubleValue());
                    atable.put(key, rd);
                }
            }

        } else if (operator.equals("*") || operator.equals("/")) {
            if ((btable.size() == 1) && btable.containsKey("CONSTANT")) {
                Double bd = (Double)btable.get("CONSTANT");
                Enumeration e = new SafeEnumeration(atable.keys());
                while (e.hasMoreElements()) {
                    String key = (String)e.nextElement();
                    Double ad = (Double)atable.get(key);
                    Double rd;
                    if (operator.equals("*"))
                        rd = new Double(ad.doubleValue() * bd.doubleValue());
                    else
                        rd = new Double(ad.doubleValue() / bd.doubleValue());
                    atable.put(key, rd);
                }
            } else if ((atable.size() == 1) && atable.containsKey("CONSTANT")) {
                Double ad = (Double)atable.get("CONSTANT");
                Enumeration e = new SafeEnumeration(btable.keys());
                while (e.hasMoreElements()) {
                    String key = (String)e.nextElement();
                    Double bd = (Double)btable.get(key);
                    Double rd;
                    if (operator.equals("*"))
                        rd = new Double(ad.doubleValue() * bd.doubleValue());
                    else
                        rd = new Double(ad.doubleValue() / bd.doubleValue());
                    btable.put(key, rd);
                }
                atable = btable;
            } else
                return null;
            
        } else if (operator.equals("%")) {
            return null;
        }

        // Finally...
        if (isComparisonExpression()) {
            atable.put("OPERATOR", operator);
        }

        return atable;
    }
    
    public boolean equals(Object other)
    {
    	if (!(other instanceof Expression))
    		return false;
    	if (operator==null)
    	{
    		if (((Expression)other).operator==null)
    		{
    			if (operand1!=null)
    				return equals(operand1.equals((((Expression)other).operand1)));
    			else
    				return equals(operand2.equals((((Expression)other).operand2)));
    		}
    		else
    			return false;
    	}
    	return operand1.equals(((Expression)other).operand1) && operand2.equals(((Expression)other).operand2) 
    			&& operator.equals(((Expression)other).operator);
    }
    
    /**
     * Converts an object to either a Double (string or numeric
     * constants) or a String (variable reference).
     * @param o The operand to convert
     * @return The converted operand, or null if error
     */
    protected Object simplifyOperand(Object o) {
    	Object r = null;
    	boolean var=false;
    	if (o instanceof String) {
    		String s = (String)o;
    		if (s.charAt(0) == '"') {
    			r = s.substring(1, s.length() - 1);
    		} else if (s.equalsIgnoreCase("null")) {
    			r = "null";
    		} else {
    			r = s;
    			var = true;
    		}

    		if (r == null)
    			r = "null";

    		if (var==false)
    			r=org.processmining.models.guards.NumericValueConversion.toNumericValue(r);


    	} else if (o instanceof Number) {
    		r = new Double(((Number)o).doubleValue());
    	}

    	return r;
    }

    /**
     * Determines if the expression is satisfiable - that there
     * is some possible assignment of variables such that the
     * expression will as a whole be true.
     * @return true if the expression is satisfiable
     */
    public boolean isSatisfiable() {
        Enumeration e;

        try {
            Expression exp = new Expression(this.toString());
            Hashtable comparisons = new Hashtable();

            // First convert it to boolean
            System.err.println(exp);
            exp.replaceComparisons(comparisons);
            System.err.println(exp);

            e = comparisons.keys();
            while (e.hasMoreElements()) {
                String k = (String)e.nextElement();
                Expression cexp = (Expression)comparisons.get(k);
                System.err.println("\t" + k + " -> " + cexp);
                System.err.println("\t\t" + cexp.generateLinearForm());
            }

            // Then get the variable names
            Vector variables = new Vector();
            e = exp.findVariables();
            while (e.hasMoreElements())
                variables.addElement(e.nextElement());

            // Next look at all permutations and pick the winners
            Vector satisfies = new Vector();
            boolean []vals = new boolean[variables.size()];
            int []mods = new int[variables.size()];
            int num = (int)Math.pow(2, variables.size());
            for (int j = 0; j < variables.size(); j++) {
                vals[j] = false;
                mods[j] = (int)Math.pow(2, j);
            }
            for (int i = 0; i < num; i++) {
                Hashtable hash = new Hashtable();
                for (int j = 0; j < variables.size(); j++) {
                    if (i % mods[j] == 0)
                        vals[j] = ! vals[j];
                    hash.put(variables.elementAt(j), new Boolean(vals[j]));
                }
                if (exp.isTrue(hash))
                    satisfies.addElement(hash);
            }
            e = satisfies.elements();
            while (e.hasMoreElements())
                System.err.println(e.nextElement());

        } catch (ParseException ex) {
            System.err.println("Error parsing expression: " + ex.toString());
        }

        return false;
    }

    /**
     * Recursively replaces comparison subexpressions with arbitrary
     * variables ment to represent their actual outcome.  In a nutshell,
     * this will convert all comparison subexpressions to simple boolean
     * variables, so that the expression as a whole will then be just
     * a simple boolean expression.  The replacement mapping is stored
     * in the provided hashtable.
     * @param h Send in an empty hashtable, it will contain the boolean
     *   variable to replaced expression mapping on completion.
     */
    public void replaceComparisons(Hashtable h) {
        int variable = 0;

        // Test a
        if (OperandTest.isExpression(operand1)) {
            if (((Expression)operand1).isComparisonExpression()) {
                while (h.containsKey("__" + variable))
                    variable++;
                h.put("__" + variable, operand1);
                operand1 = "__" + variable;
            } else {
                ((Expression)operand1).replaceComparisons(h);
            }
        }

        // Test b
        if (OperandTest.isExpression(operand2)) {
            if (((Expression)operand2).isComparisonExpression()) {
                while (h.containsKey("__" + variable))
                    variable++;
                h.put("__" + variable, operand2);
                operand2 = "__" + variable;
            } else {
                ((Expression)operand2).replaceComparisons(h);
            }
        }
    }

    /**
     * Returns a list of the variables present in the expression.
     * Each variable is a String.
     * @return An enumeration of Strings
     */
    public Enumeration findVariables() {
        // Store vars in hashtable as keys to only keep uniques
        Hashtable h = new Hashtable();
        
        // Test a
        if (OperandTest.isVariable(operand1))
            h.put(operand1, new Boolean(true));
        else if (OperandTest.isExpression(operand1)) {
            Enumeration e = ((Expression)operand1).findVariables();
            while (e.hasMoreElements())
                h.put(e.nextElement(), new Boolean(true));
        }

        // Test b
        if (OperandTest.isVariable(operand2))
            h.put(operand2, new Boolean(true));
        else if (OperandTest.isExpression(operand2)) {
            Enumeration e = ((Expression)operand2).findVariables();
            while (e.hasMoreElements())
                h.put(e.nextElement(), new Boolean(true));
        }

        return h.keys();
    }

    /**
     * Determines if the expression is a boolean one (i.e. &&, ||, or !,
     * or just a simple Boolean object).
     * @return true if the expression is a boolean one
     */
    public boolean isBooleanExpression() {

        if (operator == null) {
            if (convertOperand(operand1, new Hashtable()) instanceof Boolean)
                return true;
        }

        return OperatorTest.isBoolean(operator);
    }

    /**
     * Determines if the expression is a comparison one (i.e. <, >, ==, !=,
     * >=, <=).
     * @return true if the expression is a comparison one
     */
    public boolean isComparisonExpression() {

        return OperatorTest.isComparison(operator);
    }


    /**
     * Determines if the expression is a mathematics one (i.e. +, -, /, %, *)
     * @return true if the expression is a mathematical one
     */
    public boolean isMathematicalExpression() {

        return OperatorTest.isMathematical(operator);
    }

    /**
     * Stringifys it
     */
    public String toString() {

        if (operator == null)
            return operand1.toString();
        else if (operand1 == null)
            return "(" +operator + " " + operand2 + ")";
        else
            return "(" + operand1 + " " + operator + " " + operand2 +")";
    }

}

class OperatorTest {

    public static boolean isOperator(Object o) {
        return (OperatorTest.isBoolean(o) ||
                OperatorTest.isComparison(o) ||
                OperatorTest.isMathematical(o));
    }
    public static boolean isComparison(Object o) {
        if ((o == null) || (!(o instanceof String))) return false;
        String s = (String)o;
        if (s.equals("==") ||
            s.equals("!=") ||
            s.equals(">=") ||
            s.equals("<=") ||
            s.equals(">") ||
            s.equals("<")) {
            return true;
        }
        return false;
    }
    public static boolean isMathematical(Object o) {
        if ((o == null) || (!(o instanceof String))) return false;
        String s = (String)o;
        if (s.equals("+") ||
            s.equals("-") ||
            s.equals("/") ||
            s.equals("*") ||
            s.equals("%")) {
            return true;
        }
        return false;
    }
    public static boolean isBoolean(Object o) {
        if ((o == null) || (!(o instanceof String))) return false;
        String s = (String)o;
        if (s.equals("&&") ||
            s.equals("||") ||
            s.equals("!")) {
            return true;
        }
        return false;
    }
    public static boolean isSign(Object o) {
        if ((o == null) || (!(o instanceof String))) return false;
        String s = (String)o;
        if (s.equals("+") ||
            s.equals("-")) {
            return true;
        }
        return false;
    }
}

class OperandTest {

    public static boolean isNumber(Object o) {
        if (o == null) return false;
        if (o instanceof Number)
            return true;
        if (o instanceof String) {
            try {
                Double d = Double.valueOf((String)o);
                return false;
            } catch (Exception e) { }
        }
        return false;
    }
    public static boolean isString(Object o) {
        if (o == null) return false;
        if (o instanceof String) {
            return ((String)o).charAt(0) == '"';
        }
        return false;
    }
    public static boolean isExpression(Object o) {
        if (o == null) return false;
        if (o instanceof Expression) {
            return true;
        }
        return false;
    }
    public static boolean isConstant(Object o) {
        if (o == null) return true;
        if (o instanceof String) {
            String s = (String)o;
            if (s.equals("null"))
                return true;
        }
        return false;
    }
    public static boolean isVariable(Object o) {
        if (o == null) return false;
        if (o instanceof String) {
            if ((((String)o).charAt(0) != '"') &&
                (!isNumber(o)) &&
                (!isConstant(o))) {
                return true;
            }
        }
        return false;
    }

}
