package shopping.mall.query;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Entity
@Table(name = "DeliveryView_table")
@Data
@Relation(collectionRelation = "deliveryViews")
public class DeliveryView {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
}
