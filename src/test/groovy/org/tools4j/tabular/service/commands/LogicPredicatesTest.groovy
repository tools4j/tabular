package org.tools4j.tabular.service.commands

import org.tools4j.tabular.commands.LogicPredicate
import org.tools4j.tabular.datasets.Row
import org.tools4j.tabular.datasets.RowFromMap
import spock.lang.Specification
import spock.lang.Unroll

class LogicPredicatesTest extends Specification {
    static Row row1 = new RowFromMap("id": "1", "Item": "Jacket", "price": "19.95")
    static Row row2 = new RowFromMap("id": "2", "Item": "Skirt", "price": "21.95")
    static Row row3 = new RowFromMap("I": "am", "a": "banana")
    static Row row4 = new RowFromMap("I": "am", "a": "cabbage", "price": "potato")

    def testNot(){
        when:
        LogicPredicate.Not not = new LogicPredicate.Not()
        not.addChild(new LogicPredicate.GreaterThanValue('price', 20.00))

        then:
        assert not.test(row1)
        assert !not.test(row2)
        assert not.test(row3)
        assert not.test(row4)
    }
    
    def testEmptyAnd(){
        when:
        LogicPredicate.And and = new LogicPredicate.And()
        
        then:
        assert !and.test(row1)
        assert !and.test(row2)
        assert !and.test(row3)
        assert !and.test(row4)
    }
    
    def testAnd(){
        when:
        LogicPredicate.And and = new LogicPredicate.And()
        and.addChild(new LogicPredicate.GreaterThanValue('price', 18.05))
        and.addChild(new LogicPredicate.Equals('Item', 'Jacket'))
        
        then:
        assert and.test(row1)
        assert !and.test(row2)
        assert !and.test(row3)
        assert !and.test(row4)
    }
    
    def testEmptyOr(){
        when:
        LogicPredicate.Or or = new LogicPredicate.Or()
        
        then:
        assert !or.test(row1)
        assert !or.test(row2)
        assert !or.test(row3)
        assert !or.test(row4)
    }
    
    def testOr(){
        when:
        LogicPredicate.Or or = new LogicPredicate.Or()
        or.addChild(new LogicPredicate.NumericEquals('price', 19.95))
        or.addChild(new LogicPredicate.Matches('a', '.*?bb.*'))
        
        then:
        assert or.test(row1)
        assert !or.test(row2)
        assert !or.test(row3)
        assert or.test(row4)
    }
    
    def testNumericEquals(){
        when:
        LogicPredicate.NumericEquals equals = new LogicPredicate.NumericEquals('price', 21.95)
        
        then:
        assert !equals.test(row1)
        assert equals.test(row2)
        assert !equals.test(row3)
        assert !equals.test(row4)
    }
    
    def testLessThanOrEqual(){
        when:
        LogicPredicate.LessThanOrEqualToValue lessThanValue = new LogicPredicate.LessThanOrEqualToValue('price', 19.95)
        
        then:
        assert lessThanValue.test(row1)
        assert !lessThanValue.test(row2)
        assert !lessThanValue.test(row3)
        assert !lessThanValue.test(row4)
        
        when:
        lessThanValue = new LogicPredicate.LessThanOrEqualToValue('price', 20.95)
        
        then:
        assert lessThanValue.test(row1)
        assert !lessThanValue.test(row2)
        assert !lessThanValue.test(row3)
        assert !lessThanValue.test(row4)
        
        when:
        lessThanValue = new LogicPredicate.LessThanOrEqualToValue('price', 21.95)
        
        then:
        assert lessThanValue.test(row1)
        assert lessThanValue.test(row2)
        assert !lessThanValue.test(row3)
        assert !lessThanValue.test(row4)
    }
    
    def testLessThan(){
        when:
        LogicPredicate.LessThanOrEqualToValue lessThanValue = new LogicPredicate.LessThanOrEqualToValue('price', 20)
        
        then:
        assert lessThanValue.test(row1)
        assert !lessThanValue.test(row2)
        assert !lessThanValue.test(row3)
        assert !lessThanValue.test(row4)
    }
    
    def testGreaterThan(){
        when:
        LogicPredicate.GreaterThanOrEqualToValue greaterThanValue = new LogicPredicate.GreaterThanOrEqualToValue('price', 20)
        
        then:
        assert !greaterThanValue.test(row1)
        assert greaterThanValue.test(row2)
        assert !greaterThanValue.test(row3)
        assert !greaterThanValue.test(row4)
    }
    
    def testLessGreaterThanOrEqual(){
        when:
        LogicPredicate.GreaterThanOrEqualToValue greaterThanOrEqualTo = new LogicPredicate.GreaterThanOrEqualToValue('price', 19.95)
        
        then:
        assert greaterThanOrEqualTo.test(row1)
        assert greaterThanOrEqualTo.test(row2)
        assert !greaterThanOrEqualTo.test(row3)
        assert !greaterThanOrEqualTo.test(row4)
        
        when:
        greaterThanOrEqualTo = new LogicPredicate.GreaterThanOrEqualToValue('price', 20.95)
        
        then:
        assert !greaterThanOrEqualTo.test(row1)
        assert greaterThanOrEqualTo.test(row2)
        assert !greaterThanOrEqualTo.test(row3)
        assert !greaterThanOrEqualTo.test(row4)
        
        when:
        greaterThanOrEqualTo = new LogicPredicate.GreaterThanOrEqualToValue('price', 21.95)
        
        then:
        assert !greaterThanOrEqualTo.test(row1)
        assert greaterThanOrEqualTo.test(row2)
        assert !greaterThanOrEqualTo.test(row3)
        assert !greaterThanOrEqualTo.test(row4)
    }
    
    @Unroll
    def testMatchesItemName(String regex, boolean mRow1, boolean mRow2, boolean mRow3){
        when:
        LogicPredicate.Matches matches = new LogicPredicate.Matches('Item', regex)
        
        then:
        assert matches.test(row1) == mRow1
        assert matches.test(row2) == mRow2
        assert matches.test(row3) == mRow3
        
        where:
         regex    || mRow1 | mRow2 | mRow3
         '.*'     || true  | true  | false          
         '^.*$'   || true  | true  | false
        '.*?ck.*' || true  | false | false
        '.*k.*'   || true  | true  | false
    }
    
    def testEquals(){
        when:
        LogicPredicate.Equals equals = new LogicPredicate.Equals("Item", "Jacket")
        
        then:
        assert equals.test(row1)
        assert !equals.test(row2)
        assert !equals.test(row3)
    }
}
