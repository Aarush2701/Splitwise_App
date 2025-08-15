package splitwise.splitwise.strategy;

import org.springframework.stereotype.Component;
import splitwise.splitwise.enums.SplitType;
import splitwise.splitwise.exception.InvalidSplitType;
import splitwise.splitwise.strategy.impl.EqualSplitStrategy;
import splitwise.splitwise.strategy.impl.ExactSplitStrategy;
import splitwise.splitwise.strategy.impl.PercentageSplitStrategy;

import java.util.HashMap;
import java.util.Map;

@Component

public class SplitStrategyFactory {

    private final Map<SplitType, SplitStrategy> strategyMap = new HashMap<>();

    public SplitStrategyFactory() {
        strategyMap.put(SplitType.EQUAL, new EqualSplitStrategy());
        strategyMap.put(SplitType.EXACT, new ExactSplitStrategy());
        strategyMap.put(SplitType.PERCENTAGE, new PercentageSplitStrategy());
    }

    public SplitStrategy getStrategy(SplitType splitType) {
        if (!strategyMap.containsKey(splitType)) {
            throw new InvalidSplitType("Invalid split type: " + splitType);
        }
        return strategyMap.get(splitType);
    }
}
