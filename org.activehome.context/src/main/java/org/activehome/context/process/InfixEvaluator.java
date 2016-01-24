package org.activehome.context.process;

import org.kevoree.log.Log;

import java.util.Collections;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Evaluate infix expression.
 * <p>
 * Compatible with operators:
 * ternary: ? :
 * logical: OR AND
 * equality: == !=
 * relational: &gt; &lt; &gt;= &lt;=
 * additive: + -
 * multiplicative: * /
 */
public class InfixEvaluator {

    /**
     * The infix expression to evaluate.
     */
    private String expression;
    /**
     * The infix as array.
     */
    private String[] infixArray;
    /**
     * The transformed expression as postFix.
     */
    private LinkedList<String> postfix;

    /**
     * @param infix The infix expression to evaluate
     * @return The result (double or boolean) of the expression
     */
    public final Object evalInfix(final String infix) {
        expression = infix;
        return evaluatePostfix(convert2Postfix(infix));
    }

    /**
     * @param infixExpr The infix expression to evaluate
     * @return The equivalent postfix expression
     */
    private LinkedList<String> convert2Postfix(final String infixExpr) {
        String[] temp = infixExpr.split(
                "(?<=([\\(\\)<>+-/\\*]|==|!=|\\|\\||&&|\\?|:)"
                        + "|(?=([\\(\\)<>+-/\\*]|==|!=|\\|\\||&&|\\?|:)))");
        LinkedList<String> infixList = new LinkedList<>();
        Collections.addAll(infixList, temp);

        // concat floating number
        int i = 0;
        while (i < infixList.size()) {
            if (infixList.get(i).compareTo(".") == 0 && i > 0
                    && i < infixList.size() - 1) {
                String decNumber = infixList.get(i - 1) + infixList.get(i)
                        + infixList.get(i + 1);
                infixList.set(i - 1, decNumber);
                infixList.remove(i);
                infixList.remove(i);

            } else {
                i++;
            }
        }

        // concat negative number
        i = 0;
        while (i < infixList.size()) {
            if (infixList.get(i).compareTo("-") == 0
                    && (i < infixList.size() - 1)
                    && isNumber(infixList.get(i + 1))
                    && (i == 0 || !isNumber(infixList.get(i - 1)))) {
                String decNumber = infixList.get(i) + infixList.get(i + 1);
                infixList.set(i, decNumber);
                infixList.remove(i + 1);
            }
            i++;
        }

        infixArray = infixList.toArray(new String[infixList.size()]);
        LinkedList<String> output = new LinkedList<>();
        Stack<String> stack = new Stack<>();
        for (String token : infixArray) {
            if (token.matches("[<>+-/\\*]|==|!=|\\|\\||&&")) {
                while (!stack.isEmpty() && !stack.peek().matches("\\(")) {
                    if (operatorGreaterOrEqual(stack.peek(), token)) {
                        output.addLast(stack.pop());
                    } else {
                        break;
                    }
                }
                stack.push(token);
            } else if (token.matches(":")) {
                stack.push(":?");
            } else if (token.matches("\\(")) {
                stack.push(token);
            } else if (token.matches("\\)")) {
                while (!stack.isEmpty() && !stack.peek().matches("\\(")) {
                    output.addLast(stack.pop());
                }
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (token.matches("^[-+]?[0-9]*\\.?[0-9]+$")) {
                output.addLast(token);
            } else if (token.matches("false")) {
                output.addLast("false");
            } else if (token.matches("true")) {
                output.addLast("true");
            }
        }
        while (!stack.empty()) {
            output.addLast(stack.pop());
        }

        postfix = output;
        return output;
    }

    /**
     * @param str String to evaluate
     * @return true if str is a number
     */
    private boolean isNumber(final String str) {
        try {
            double d = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * @param postfixExpr postfix epression to evaluate
     * @return result of the expression (double or boolean)
     */
    private Object evaluatePostfix(final LinkedList<String> postfixExpr) {
        Stack<Object> stack = new Stack<>();
        for (String token : postfixExpr) {
            if (token.matches("^[-+]?[0-9]*\\.?[0-9]+$")) {
                stack.push(Double.valueOf(token));
            } else if (token.matches("false")) {
                stack.push(Boolean.FALSE);
            } else if (token.matches("true")) {
                stack.push(Boolean.TRUE);
            } else if (token.matches("[<>+-/\\*]|==|!=|\\|\\||&&|:\\?")) {
                try {
                    Object op1 = stack.pop();
                    Object op2 = stack.pop();
                    switch (token) {
                        case "*":
                            stack.push((double) op1 * (double) op2);
                            break;
                        case "/":
                            stack.push((double) op2 / (double) op1);
                            break;
                        case "+":
                            stack.push((double) op1 + (double) op2);
                            break;
                        case "-":
                            stack.push((double) op2 - (double) op1);
                            break;
                        case "<":
                            stack.push((double) op2 < (double) op1);
                            break;
                        case ">":
                            stack.push((double) op2 > (double) op1);
                            break;
                        case "==":
                            try {
                                stack.push((double) op2 == (double) op1);
                            } catch (ClassCastException e) {
                                stack.push((boolean) op2 == (boolean) op1);
                            }
                            break;
                        case "!=":
                            stack.push((double) op2 != (double) op1);
                            break;
                        case "||":
                            stack.push((boolean) op2 || (boolean) op1);
                            break;
                        case "&&":
                            stack.push((boolean) op2 && (boolean) op1);
                            break;
                        case ":?":
                            Object op3 = stack.pop();
                            if ((boolean) op3) {
                                stack.push(op2);
                            } else {
                                stack.push(op1);
                            }
                            break;
                        default:
                    }
                } catch (EmptyStackException e) {
                    diagnostic();
                }
            }
        }

        try {
            return stack.pop();
        } catch (EmptyStackException e) {
            diagnostic();
        }
        return null;
    }

    private void diagnostic() {
        StringBuilder infixStr = new StringBuilder();
        for (String elem : infixArray) {
            infixStr.append(elem).append(" , ");
        }
        Log.error("EmptyStack Exception for " + expression);
        Log.error(" => infix: " + infixStr.toString());
        Log.error(" => postfix: " + postfix);
    }

    /**
     * @param operator The operator to check
     * @return preference's rank of the operator
     */
    private int getPrecedence(final String operator) {
        int ret = 0;
        if (operator.matches("||")) {
            ret = 1;
        } else if (operator.matches("&&")) {
            ret = 2;
        } else if (operator.matches("==") || operator.matches("!=")) {
            ret = 3;
        } else if (operator.matches("[<>]") || operator.matches(">=")
                || operator.matches("<=")) {
            ret = 4;
        } else if (operator.matches("[-+]")) {
            ret = 5;
        } else if (operator.matches("[*/]")) {
            ret = 6;
        }
        return ret;
    }

    /**
     * @param op1 operator to check
     * @param op2 reference operator
     * @return true if op1 greater or equal to opt2
     */
    private boolean operatorGreaterOrEqual(final String op1, final String op2) {
        return getPrecedence(op1) >= getPrecedence(op2);
    }

}
