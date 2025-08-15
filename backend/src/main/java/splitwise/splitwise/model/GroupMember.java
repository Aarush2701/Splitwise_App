package splitwise.splitwise.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="groupmembers")
public class GroupMember {


    @EmbeddedId
    private GroupMemberId id;

    private Timestamp joiningdate;

    @ManyToOne
    @MapsId("groupid")
    @JoinColumn(name = "groupid",insertable = false,updatable = false)
    private ExpenseGroup group;

    @ManyToOne
    @MapsId("userid")
    @JoinColumn(name = "userid",insertable = false,updatable = false)
    private User user;
}
