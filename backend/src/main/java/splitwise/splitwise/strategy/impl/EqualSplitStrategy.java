package splitwise.splitwise.strategy.impl;

import org.springframework.stereotype.Component;
import splitwise.splitwise.model.Expense;
import splitwise.splitwise.model.ExpenseSplit;
import splitwise.splitwise.strategy.SplitStrategy;

import java.util.ArrayList;
import java.util.List;

public class EqualSplitStrategy implements SplitStrategy {

    @Override
    public List<ExpenseSplit> calculateSplits(Expense expense, Long paidById , List<Long> participantsIds, List<Double> values){

        List<ExpenseSplit> splits = new ArrayList<>();
        double share = expense.getAmount()/participantsIds.size();

        for (Long userId : participantsIds) {
            if (!userId.equals(paidById)){
                splits.add(new ExpenseSplit(expense.getExpenseid(),userId,share));
            }
        }
        return splits;
    }
}


