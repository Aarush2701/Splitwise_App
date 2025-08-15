package splitwise.splitwise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import splitwise.splitwise.model.Expense;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByGroupid_Groupid(Long groupid);  //expense for group (get)

    List<Expense> findByGroupid_GroupidAndUserid_Userid(Long groupid, Long userid);

    void deleteByUserid_Userid(Long userId);
}
