package com.epam.cdp.caclulator

import spock.lang.Specification

class GroovyCalculatorTest extends Specification {
    final static GroovyCalculator testingInstance = new GroovyCalculator()

    def "calculate expression with '+' operation"() {
        given:
            def expression = "4+15"
        when:
            def result = testingInstance.calculate(expression)
        then:
            result == 19D
    }

    def "calculate expressions with '+' operator"(final String expression, final double expectedResult) {
        expect:
            testingInstance.calculate(expression) == expectedResult
        where:
            expression      | expectedResult
            "0+15"          | 15D
            "20+55+12"      | 87D
            "100+15+10+900" | 1025D
    }

    def "calculate expression with '-' operation"() {
        given:
            def expression = "20-15"
        when:
            def result = testingInstance.calculate(expression)
        then:
            result == 5D
    }

    def "calculate expressions with '-' operator"(final String expression, final double expectedResult) {
        expect:
            testingInstance.calculate(expression) == expectedResult
        where:
            expression     | expectedResult
            "0-15"         | -15D
            "20-55-12"     | -47D
            "100-15-10-30" | 45D
    }

    def "calculate expression with '*' operation"() {
        given:
            def expression = "3*12"
        when:
            def result = testingInstance.calculate(expression)
        then:
            result == 36D
    }

    def "calculate expressions with '*' operator"(final String expression, final double expectedResult) {
        expect:
            testingInstance.calculate(expression) == expectedResult
        where:
            expression  | expectedResult
            "2*12"      | 24D
            "100*40*37" | 148000D
            "2*5*1*0"   | 0D
    }

    def "calculate expression with '/' operation"() {
        given:
            def expression = "4/2"
        when:
            def result = testingInstance.calculate(expression)
        then:
            result == 2D
    }

    def "calculate expression with '/' operation when divide by 0"() {
        given:
            def expression = "4/0"
        when:
            testingInstance.calculate(expression)
        then:
            def thrownException = thrown(ArithmeticException)
            thrownException.message == "Division by zero"
    }

    def "calculate expressions with '/' operator"(final String expression, final double expectedResult) {
        expect:
            testingInstance.calculate(expression) == expectedResult
        where:
            expression | expectedResult
            "0/2"      | 0D
            "100/2/5"  | 10D
            "36/2/2/3" | 3D
    }

    def "calculate expressions with different operators"(final String expression, final double expectedResult) {
        expect:
            testingInstance.calculate(expression) == expectedResult
        where:
            expression          | expectedResult
            "2+2*2"             | 6D
            "(10-2)/4"          | 2D
            "10-2+9/3"          | 11D
    }

    def "calculate null expression"() {
        given:
        def expression = null
        when:
        testingInstance.calculate(expression)
        then:
        def thrownException = thrown(IllegalArgumentException)
        thrownException.message == testingInstance.INCORRECT_EXPRESSION + expression
    }

    def "calculate empty expression"() {
        given:
            def expression = ""
        when:
            testingInstance.calculate(expression)
        then:
            def thrownException = thrown(IllegalArgumentException)
            thrownException.message == testingInstance.INCORRECT_EXPRESSION + expression
    }

    def "calculate incorrect expression with letters"() {
        given:
            def expression = "1+a-b"
        when:
            testingInstance.calculate(expression)
        then:
            def thrownException = thrown(IllegalArgumentException)
            thrownException.message == testingInstance.INCORRECT_EXPRESSION + expression
    }

    def "calculate incorrect expression with spaces"() {
        given:
            def expression = "1 + 2"
        when:
            testingInstance.calculate(expression)
        then:
            def thrownException = thrown(IllegalArgumentException)
            thrownException.message == testingInstance.INCORRECT_EXPRESSION + expression
    }

    def "calculate incorrect expression with specific symbols"() {
        given:
            def expression = "10%2^36"
        when:
            testingInstance.calculate(expression)
        then:
            def thrownException = thrown(IllegalArgumentException)
            thrownException.message == testingInstance.INCORRECT_EXPRESSION + expression
    }

    def "calculate incorrect expression with double numbers"() {
        given:
            def expression = "10.15+36,1"
        when:
            testingInstance.calculate(expression)
        then:
            def thrownException = thrown(IllegalArgumentException)
            thrownException.message == testingInstance.INCORRECT_EXPRESSION + expression
    }

    def "test mod operator overloading"(final String message1, final String message2, final String expectedResult) {
        expect:
            use(GroovyCalculator) {
                message1 % message2 == expectedResult
            }
        where:
            message1   | message2    | expectedResult
            "  a  "    | " b "       | "a b"
            " first  " | "  Second " | "first Second"
    }
}
