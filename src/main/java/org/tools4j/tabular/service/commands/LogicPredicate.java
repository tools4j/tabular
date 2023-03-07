package org.tools4j.tabular.service.commands;

import org.tools4j.tabular.service.Row;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.tools4j.tabular.service.commands.CommandXmlHandler.getMandatoryDoubleAttr;
import static org.tools4j.tabular.service.commands.CommandXmlHandler.getMandatoryStringAttr;

public interface LogicPredicate extends Predicate<Row> {
    interface LogicParent extends LogicPredicate {
        void addChild(LogicPredicate logicPredicate);
        LogicPredicate getChild(int index);
    }
    
    class Or implements LogicParent {
        private final List<LogicPredicate> children = new ArrayList<>();
        
        @Override
        public boolean test(Row row) {
            for (LogicPredicate child : children) {
                if(child.test(row)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public void addChild(LogicPredicate logicPredicate) {
            children.add(logicPredicate);
        }

        @Override
        public LogicPredicate getChild(int index) {
            return children.get(index);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Or or = (Or) o;
            return Objects.equals(children, or.children);
        }

        @Override
        public int hashCode() {
            return Objects.hash(children);
        }

        @Override
        public String toString() {
            return "Or{" +
                    "children=" + children +
                    '}';
        }
    }
    
    class And implements LogicParent {
        private final List<LogicPredicate> children = new ArrayList<>();
        
        @Override
        public boolean test(Row row) {
            if(children.isEmpty()){
                return false;
            }
            for (LogicPredicate child : children) {
                if(!child.test(row)){
                    return false;
                }
            }
            return true;
        }

        @Override
        public void addChild(LogicPredicate logicPredicate) {
            children.add(logicPredicate);
        }

        @Override
        public LogicPredicate getChild(int index) {
            return children.get(index);
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            And and = (And) o;
            return Objects.equals(children, and.children);
        }

        @Override
        public int hashCode() {
            return Objects.hash(children);
        }

        @Override
        public String toString() {
            return "And{" +
                    "children=" + children +
                    '}';
        }
    }
    
    class Equals implements LogicPredicate {
        private final String colName;
        private final String colValue;

        public Equals(String qName, Attributes attr) {
            this(getMandatoryStringAttr(qName, attr, "col_name"),
                    getMandatoryStringAttr(qName, attr, "value"));
        }

        public Equals(String colName, String colValue) {
            this.colName = colName;
            this.colValue = colValue;
        }

        @Override
        public boolean test(Row row) {
            String s = row.get(colName);
            if(s == null) return false;
            else return s.equalsIgnoreCase(colValue);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Equals equals = (Equals) o;
            return Objects.equals(colName, equals.colName) && Objects.equals(colValue, equals.colValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(colName, colValue);
        }

        @Override
        public String toString() {
            return "Equals{" +
                    "colName='" + colName + '\'' +
                    ", colValue='" + colValue + '\'' +
                    '}';
        }
    }
    
    class Matches implements LogicPredicate {
        private final String colName;
        private final Predicate<String> regex;
        private final String regexStr;
        
        public Matches(String qName, Attributes attr) {
            this(getMandatoryStringAttr(qName, attr, "col_name"),
                    getMandatoryStringAttr(qName, attr, "regex"));
        }
        
        public Matches(String colName, String regex) {
            this.colName = colName;
            this.regex = Pattern.compile(regex).asPredicate();
            this.regexStr = regex;
        }

        @Override
        public boolean test(Row row) {
            String s = row.get(colName);
            if(s == null) return false;
            else return regex.test(s);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Matches matches = (Matches) o;
            return Objects.equals(colName, matches.colName) && Objects.equals(regex, matches.regex);
        }

        @Override
        public int hashCode() {
            return Objects.hash(colName, regex);
        }

        @Override
        public String toString() {
            return "Matches{" +
                    "colName='" + colName + '\'' +
                    ", regex='" + regexStr + '\'' +
                    '}';
        }
    }
    
    abstract class NumericPredicate implements LogicPredicate {
        protected final String colName;
        protected final DoublePredicate doublePredicate;

        protected NumericPredicate(String colName, DoublePredicate doublePredicate) {
            this.colName = colName;
            this.doublePredicate = doublePredicate;
        }

        @Override
        public boolean test(Row row) {
            return testDouble(colName, row, doublePredicate);
        }
    }
    
    class GreaterThanValue extends NumericPredicate {
        private final double value;
        
        public GreaterThanValue(String qName, Attributes attr) {
            this(getMandatoryStringAttr(qName, attr, "col_name"),
                    getMandatoryDoubleAttr(qName, attr, "value"));
        }

        public GreaterThanValue(String colName, double value) {
            super(colName, colValue -> colValue > value);
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GreaterThanValue that = (GreaterThanValue) o;
            return Double.compare(that.value, value) == 0 && colName.equals(that.colName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(colName, value);
        }

        @Override
        public String toString() {
            return "GreaterThanValue{" +
                    "colName='" + colName + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
    
    
    class GreaterThanOrEqualToValue extends NumericPredicate {
        private final double value;
        
        public GreaterThanOrEqualToValue(String qName, Attributes attr) {
            this(getMandatoryStringAttr(qName, attr, "col_name"),
                    getMandatoryDoubleAttr(qName, attr, "value"));
        }

        public GreaterThanOrEqualToValue(String colName, double value) {
            super(colName, colValue -> colValue >= value);
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GreaterThanOrEqualToValue that = (GreaterThanOrEqualToValue) o;
            return Double.compare(that.value, value) == 0 && colName.equals(that.colName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(colName, value);
        }

        @Override
        public String toString() {
            return "GreaterThanOrEqualToValue{" +
                    "colName='" + colName + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
    
    class LessThanValue extends NumericPredicate {
        private final double value;
        
        public LessThanValue(String qName, Attributes attr) {
            this(getMandatoryStringAttr(qName, attr, "col_name"),
                    getMandatoryDoubleAttr(qName, attr, "value"));
        }

        public LessThanValue(String colName, double value) {
            super(colName, colValue -> colValue < value);
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LessThanValue that = (LessThanValue) o;
            return Double.compare(that.value, value) == 0 && colName.equals(that.colName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(colName, value);
        }

        @Override
        public String toString() {
            return "LessThanValue{" +
                    "colName='" + colName + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
    
    class LessThanOrEqualToValue extends NumericPredicate {
        private final double value;
        
        public LessThanOrEqualToValue(String qName, Attributes attr) {
            this(getMandatoryStringAttr(qName, attr, "col_name"),
                    getMandatoryDoubleAttr(qName, attr, "value"));
        }

        public LessThanOrEqualToValue(String colName, double value) {
            super(colName, colValue -> colValue <= value);
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LessThanOrEqualToValue that = (LessThanOrEqualToValue) o;
            return Double.compare(that.value, value) == 0 && colName.equals(that.colName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(colName, value);
        }

        @Override
        public String toString() {
            return "LessThanOrEqualToValue{" +
                    "colName='" + colName + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
    
    class NumericEquals extends NumericPredicate {
        private final double value;
        
        public NumericEquals(String qName, Attributes attr) {
            this(getMandatoryStringAttr(qName, attr, "col_name"),
                    getMandatoryDoubleAttr(qName, attr, "value"));
        }

        public NumericEquals(String colName, double value) {
            super(colName, colValue -> colValue == value);
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NumericEquals that = (NumericEquals) o;
            return Double.compare(that.value, value) == 0 && colName.equals(that.colName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(colName, value);
        }

        @Override
        public String toString() {
            return "NumericEquals{" +
                    "colName='" + colName + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
    
    class Not implements LogicParent {
        private LogicPredicate child = null;
        
        @Override
        public boolean test(Row row) {
            if(child == null){
                throw new IllegalStateException("Not predicate does not have a child.");
            }
            return !child.test(row);
        }

        @Override
        public void addChild(LogicPredicate logicPredicate) {
            if(child != null){
                throw new IllegalStateException("'Not' predicate can only have one child, existing child [" + this.child + "], child attempting to add [" + logicPredicate + "]");
            }
            child = logicPredicate;
        }

        @Override
        public LogicPredicate getChild(int index) {
            if(index == 0) return child;
            else throw new IllegalStateException("'Not' predicate cannot have more than one child.");
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Not not = (Not) o;
            return Objects.equals(child, not.child);
        }

        @Override
        public int hashCode() {
            return Objects.hash(child);
        }

        @Override
        public String toString() {
            return "Not{" +
                    "child=" + child +
                    '}';
        }
    }
    
    static boolean testDouble(String colName, Row row, DoublePredicate doublePredicate) {
        String s = row.get(colName);
        if(s == null) return false;
        try {
            double v = Double.parseDouble(s);
            return doublePredicate.test(v);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

