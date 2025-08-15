package splitwise.splitwise.strategy;

import splitwise.splitwise.model.Expense;
import splitwise.splitwise.model.ExpenseSplit;

import java.util.List;

public interface SplitStrategy {

    List<ExpenseSplit> calculateSplits(Expense expense, Long paidById , List<Long> participantsIds, List<Double> values);

}
