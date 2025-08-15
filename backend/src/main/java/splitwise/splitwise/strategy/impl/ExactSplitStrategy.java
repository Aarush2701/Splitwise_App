package splitwise.splitwise.strategy.impl;

import org.springframework.stereotype.Component;
import splitwise.splitwise.exception.ExactAmountSum;
import splitwise.splitwise.exception.ParticipantCountMismatch;
import splitwise.splitwise.model.Expense;
import splitwise.splitwise.model.ExpenseSplit;
import splitwise.splitwise.strategy.SplitStrategy;

import java.util.ArrayList;
import java.util.List;


public class ExactSplitStrategy implements SplitStrategy {

    @Override
    public List<ExpenseSplit> calculateSplits(Expense expense, Long paidById , List<Long> participantsIds, List<Double> exactAmounts) {

        if (participantsIds.size() != exactAmounts.size()){
            throw new ParticipantCountMismatch("Participants and exact amount count mismatch.");
        }

        double total = exactAmounts.stream().mapToDouble(Double::doubleValue).sum();
        if (Double.compare(total, expense.getAmount()) != 0) {
            throw new ExactAmountSum("Exact amounts do not sum to total expense.");
        }

        List<ExpenseSplit> splits = new ArrayList<>();
        for (int i = 0; i< participantsIds.size();i++){
            if (!participantsIds.get(i).equals(expense.getUserid().getUserid())){
                splits.add(new ExpenseSplit(expense.getExpenseid(), participantsIds.get(i),exactAmounts.get(i)));
            }
        }
        return splits;
    }
}
