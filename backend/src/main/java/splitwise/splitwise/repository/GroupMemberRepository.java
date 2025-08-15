package splitwise.splitwise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import splitwise.splitwise.model.ExpenseGroup;
import splitwise.splitwise.model.GroupMember;
import splitwise.splitwise.model.GroupMemberId;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

    List<GroupMember> findById_Groupid(Long groupid);

    List<GroupMember> findById_Userid(Long userid);

    Optional<GroupMember> findById_GroupidAndId_Userid(Long groupid,Long userid);

    boolean existsById_GroupidAndId_Userid(Long groupid,Long userid);

    boolean existsById(GroupMemberId groupMemberId);

    void deleteById_Userid(Long userid);

    List<ExpenseGroup> findByUser_Userid(Long userId);
}
