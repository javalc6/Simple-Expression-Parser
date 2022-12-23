/* very simple math expression parser in less than 200 lines of Java code

Expressions may contain brackets ( ), operands *,-,/,+ and numbers with optional decimal point.

<expression> ::= <term> { ("+"|"-") <term> }*
<term> ::= <factor> { ("*"|"/") <factor> }*
<factor> ::= "(" <expression> ")" | "-" <factor> | <number>
<number> ::= { <digit> }+ [ "." { <digit> }* ]
<digit> ::= "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"

23-12-2022: initial release: storing expression as binary tree
*/

public class ExpressionParser {

	final static int MaxNumOfDigit = 38; // max number of digits for numbers

	public static void main(String[] args) {
		ExpressionParser ep = new ExpressionParser();
		try {
			Node p = ep.parseExpression("-4.123+5*-6/3-2.*-(2-1)");
			System.out.println("Result: "+ep.evaluate(p));//expected result: -12.123
			ep.visit(p);			
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.getMessage());
		}
	}

//public method that evaluates an expression stored in binary tree 'p'
	public double evaluate(Node p) {
		if (p != null) {
			switch(p.type) {
				case add:
					return evaluate(p.left) + evaluate(p.right);
				case subtract:
					return evaluate(p.left) - evaluate(p.right);
				case multiply:
					return evaluate(p.left) * evaluate(p.right);
				case divide:
					return evaluate(p.left) / evaluate(p.right);
				case minus:
					return -evaluate(p.left);
				case number:
					return p.num;
				default://will never happen
					throw new RuntimeException("unexpected type: " + p.type);
			}
		}
		return 0;
	}

//public method that visits expression stored in binary tree 'p'
	public void visit(Node p) {
		if (p != null) {
			switch(p.type) {
				case add:
				case subtract:
				case multiply:
				case divide:
					System.out.print('(');
					visit(p.left);
					System.out.print(" " + p.type + " ");
					visit(p.right);
					System.out.print(')');
					break;
				case minus:
					System.out.print('-');
					visit(p.left);
					break;
				case number:
					System.out.print(p.num);
					break;
				default://will never happen
					throw new RuntimeException("unexpected type: " + p.type);
			}
		}
	}

//public method that returns the parsed expression as a binary tree
	public Node parseExpression(String expr) throws Exception {
		StringHolder sh = new StringHolder(expr);
		Node result = expression(sh);
		if (sh.chars_available())
			throw new Exception("unexpected character: " + expr.charAt(sh.pointer));
		return result;
	}

	private Node expression(StringHolder sh) throws Exception {
		Node pt = term(sh);
		Character ch;
		while ((ch = sh.nextChar("+-")) != null) {
			Node p = new Node(ch == '+' ? Type.add : Type.subtract);
			p.left = pt;
			pt = p;
			p.right = term(sh);
		}
		return pt;
	}

	private Node term(StringHolder sh) throws Exception {
		Node pf = factor(sh);
		Character ch;
		while ((ch = sh.nextChar("*/")) != null) {
			Node p = new Node(ch == '*' ? Type.multiply : Type.divide);
			p.left = pf;
			pf = p;
			p.right = factor(sh);
		}
		return pf;
	}

	private Node factor(StringHolder sh) throws Exception {
		Character ch = sh.nextChar("(-0123456789");
		if (ch == null)
			if (sh.chars_available())
				throw new Exception("unexpected character: " + sh.expr.charAt(sh.pointer));
			else throw new Exception("unexpected end of expression");
		if (ch == '(') {
			Node n1 = expression(sh);
			ch = sh.nextChar(")");
			if (ch == null) {
				throw new Exception("missing ) bracket");
			}
			return n1;
		} else if (ch == '-') {
			Node n2 = new Node(Type.minus);
			n2.left = factor(sh);
			return n2;
		} else if (ch >= '0' && ch <= '9') {
			double num = 0; int nDigits = 0;
			while (ch != null) {
				if (nDigits < MaxNumOfDigit) {
					num = num * 10 + ch - '0';
					nDigits++;
				} else num = num * 10;
				ch = sh.nextChar("0123456789");
			}
			if (sh.nextChar(".") != null) {//ch != null --> ch == '.'
				double m = 1;
				while ((ch = sh.nextChar("0123456789")) != null) {
					m = m * 0.1;
					num = num + (ch - '0') * m;
				}
			}
			return new Node(num);
		} else return null;
	}

	enum Type {//type of Node
		number, minus, add, subtract, multiply, divide
	}

	class Node {//Node element of the binary tree
		private final Type type;
		private Node left;
		private Node right;
		private double num;

		Node(Type type) {//generic node constructor
			this.type = type;
			left = null;
			right = null;
		}

		Node(double num) {//node constructor of type number
			type = Type.number;
			left = null;
			right = null;
			this.num = num;
		}
	}//end of class Node

	class StringHolder {//helper for parsing expressions
		private final String expr;
		private int pointer;

		StringHolder(String expr) {
			this.expr = expr;
			pointer = 0;
		}

		private Character nextChar(String charset) {//returns next char only if it is in charset
			Character ch = null;
			while ((pointer < expr.length()) && (ch = expr.charAt(pointer++)) == ' ')
				;
			if (ch != null) {
				if (charset.indexOf(ch) != -1)
					return ch;
				pointer--;//retract pointer if ch is not in charset and returns null
			}
			return null;
		}

		private boolean chars_available() {
			return pointer < expr.length();
		}

	}//end of class StringHolder
}