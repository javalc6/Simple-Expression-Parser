# Expression Parser
This Java class parses simple math expressions, like 2.5+2*(3-7).
Expressions may contain brackets ( ), operators *,-,/,+ and numbers with optional decimal point.

An improved and extensibile math parser is implemented in [Expression Parser](https://github.com/javalc6/Expression-Parser)

# Methods
The following methods are provided to parse and evaluate math expressions:
```
parseExpression(String expr): parses the given expression and returns a binary tree representing the parse expression;

evaluate(Node p): evaluates the binary tree containing an expression;

visit(Node p): visits the binary tree containing an expression;
```

# Syntax Rules
The parser use the following rules to parse math expressions:
```
<expression> ::= <term> { ("+"|"-") <term> }*
<term> ::= <factor> { ("*"|"/") <factor> }*
<factor> ::= "(" <expression> ")" | "-" <factor> | <number>
<number> ::= { <digit> }+ [ "." { <digit> }* ]
<digit> ::= "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
```

# Example

Parsing the expression 2+3*(4+5) you obtain the following binary tree:

```
  +
 / \
2   *
   / \
  3   +
     / \
    4   5
```
The evaluation of this binary tree returns the value 29, as expected.

