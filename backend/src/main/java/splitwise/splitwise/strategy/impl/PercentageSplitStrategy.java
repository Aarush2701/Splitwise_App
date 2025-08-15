package splitwise.splitwise.strategy.impl;

import org.springframework.stereotype.Component;
import splitwise.splitwise.exception.ParticipantCountMismatch;
import splitwise.splitwise.exception.PercentageSumNot100;
import splitwise.splitwise.model.Expense;
import splitwise.splitwise.model.ExpenseSplit;
import splitwise.splitwise.strategy.SplitStrategy;

import java.util.ArrayList;
import java.util.List;

public class PercentageSplitStrategy implements SplitStrategy {
    @Override
    public List<ExpenseSplit> calculateSplits(Expense expense, Long paidById , List<Long> participantsIds, List<Double> percentages) {

        if (participantsIds.size() != percentages.size()){
            throw new ParticipantCountMismatch("Participants and percentages count mismatch.");
        }

        double total = percentages.stream().mapToDouble(Double::doubleValue).sum();
        if (Double.compare(total, 100.0) != 0) {
            throw new PercentageSumNot100("Percentages must sum to 100.");
        }

        List<ExpenseSplit> splits = new ArrayList<>();
        for (int i = 0; i<participantsIds.size();i++) {
            if (!participantsIds.get(i).equals(expense.getUserid().getUserid())){
                double share = expense.getAmount()*percentages.get(i)/100.0;
                splits.add(new ExpenseSplit(expense.getExpenseid(),participantsIds.get(i),share));
            }
        }
        return splits;
    }
}
