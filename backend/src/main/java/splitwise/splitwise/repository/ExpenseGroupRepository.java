package splitwise.splitwise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import splitwise.splitwise.model.ExpenseGroup;
import splitwise.splitwise.model.GroupMember;

import java.util.List;

public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, Long> {

}
