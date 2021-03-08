package com.epam.cdp.caclulator

class GroovyCalculator {
    final static def INCORRECT_EXPRESSION = "Expression is incorrect. Expression: "

    final static def LEFT_ASSOC = 0
    final static def RIGHT_ASSOC = 1

    final static def PLUS = "+"
    final static def MINUS = "-"
    final static def MULTIPLY = "*"
    final static def DIVIDE = "/"
    final static def LEFT_BRACKET = "("
    final static def RIGHT_BRACKET = ")"

    final static def EXPRESSION_SPLIT_REGEX = "((?<=\\*)|(?=\\*)|(?<=/)|(?=/)|(?<=\\+)|(?=\\+)|(?<=-)|(?=-)|(?<=\\()|(?=\\)))"
    final static def CHECK_SYMBOLS_REGEX = "(?!(?:(([+-/*0-9()])+))\$).*"
    final static def CHECK_DOUBLES_REGEX = "([0-9]+(\\.|,){1}[0-9]+)(([+-/*0-9()])+)"
    final static def REGEXES = [CHECK_SYMBOLS_REGEX, CHECK_DOUBLES_REGEX]

    final def OPERATORS = [
            (PLUS)    : new int[]{0, LEFT_ASSOC},
            (MINUS)   : new int[]{0, LEFT_ASSOC},
            (MULTIPLY): new int[]{5, LEFT_ASSOC},
            (DIVIDE)  : new int[]{5, LEFT_ASSOC}
    ]

    boolean isOperator(final String token) {
        token in OPERATORS.keySet()
    }

    boolean isAssociative(final String token, final int type) {
        if (!isOperator(token)) {
            throw new IllegalArgumentException("Invalid token: ${token}")
        }

        if (OPERATORS.get(token)[1] == type) {
            return true
        }

        false
    }

    int comparePrecedence(final String token1, final String token2) {
        if (!isOperator(token1) || !isOperator(token2)) {
            throw new IllegalArgumentException("Invalid tokens: ${token1} and/or ${token2}")
        }

        OPERATORS.get(token1)[0] - OPERATORS.get(token2)[0]
    }

    String[] convertToRPN(final String[] inputTokens) {
        final List<String> out = []
        final Stack<String> stack = []

        inputTokens.each { String token ->
            if (isOperator(token)) {
                while (!stack.empty() && isOperator(stack.peek())) {
                    if ((isAssociative(token, LEFT_ASSOC) && comparePrecedence(token, stack.peek()) <= 0) ||
                            (isAssociative(token, RIGHT_ASSOC) && comparePrecedence(token, stack.peek()) < 0)) {
                        out << stack.pop()
                        continue
                    }
                    break
                }
                stack.push(token)
            } else if (token == LEFT_BRACKET) {
                stack.push(token)
            } else if (token == RIGHT_BRACKET) {
                while (!stack.empty() && stack.peek() != LEFT_BRACKET) {
                    out << stack.pop()
                }
                stack.pop()
            } else {
                out << token
            }
        }

        while (!stack.empty()) {
            out << stack.pop()
        }

        final String[] output = new String[out.size()]

        out.toArray(output)
    }

    double resolveRPN(final String[] tokens) {
        final Stack<String> stack = []

        tokens.each {
            if (!isOperator(it)) {
                stack.push(it)
            } else {
                final Double d2 = stack.pop().toDouble()
                final Double d1 = stack.pop().toDouble()

                switch (it) {
                    case PLUS:
                        stack.push((d1 + d2).toString())
                        break
                    case MINUS:
                        stack.push((d1 - d2).toString())
                        break
                    case MULTIPLY:
                        stack.push((d1 * d2).toString())
                        break
                    case DIVIDE:
                        stack.push((d1 / d2).toString())
                        break
                }
            }
        }

        stack.pop().toDouble()
    }

    void validateExpression(final String expression) {
        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException(INCORRECT_EXPRESSION + expression)
        }

        REGEXES.each {
            if (expression ==~ it) {
                throw new IllegalArgumentException(INCORRECT_EXPRESSION + expression)
            }
        }
    }

    static String mod(final String s1, final String s2) {
        s1.trim() + " " + s2.trim()
    }

    double calculate(final String expression) {
        validateExpression(expression)

        final def splittedExpression = expression.split(EXPRESSION_SPLIT_REGEX)

        final def reversePolishNotation = convertToRPN(splittedExpression)

        final Double result = resolveRPN(reversePolishNotation)

        result
    }
}
