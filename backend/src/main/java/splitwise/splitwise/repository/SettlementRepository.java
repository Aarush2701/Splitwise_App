package splitwise.splitwise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import splitwise.splitwise.model.Settlement;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findByGroupid_Groupid(Long groupid);

    List<Settlement> findByGroupid_GroupidAndPaidby_Userid(Long groupid, Long paidbyUserid);


    List<Settlement> findByGroupid_GroupidAndPaidto_Userid(Long groupid, Long paidtoUserid);

    void deleteByPaidby_UseridOrPaidto_Userid(Long paidbyUserid,Long paidtoUserid);


}
