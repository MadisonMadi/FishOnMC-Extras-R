package dannypx.foe.helper;

import dannypx.foe.type.search.FloatValue;
import dannypx.foe.type.search.Operator;
import dannypx.foe.type.search.SearchFilter;

public class MathHelper {
    public static boolean checkOperation(SearchFilter searchFilter, FloatValue floatValue, float fetchedValue) {
        return checkOperation(searchFilter.operator, fetchedValue, floatValue.value());
    }

    public static boolean checkOperation(Operator operator, float leftValue, float rightValue) {
        return switch (operator) {
            case GREATER -> leftValue > rightValue;
            case LESS -> leftValue < rightValue;
            case EQUAL, SHORT_EQUAL -> leftValue == rightValue;
            case NOT_EQUAL -> leftValue != rightValue;
            case GREATER_EQUAL -> leftValue >= rightValue;
            case LESS_EQUAL -> leftValue <= rightValue;
            default -> false;
        };
    }
}
