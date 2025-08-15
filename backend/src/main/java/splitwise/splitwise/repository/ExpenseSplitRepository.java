package splitwise.splitwise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import splitwise.splitwise.model.Expense;
import splitwise.splitwise.model.ExpenseSplit;
import splitwise.splitwise.model.ExpenseSplitId;

import java.util.List;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, ExpenseSplitId> {
    List<ExpenseSplit> findByExpenseid(Long expenseid);

    void deleteByUserid(Long userId);
    void deleteByExpenseid(Long expenseid);


}
