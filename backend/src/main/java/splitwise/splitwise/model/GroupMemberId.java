package splitwise.splitwise.model;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;


@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberId implements Serializable{

    private Long groupid;
    private Long userid;


}
