package shopping.mall.event;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DeliveryCancelledEvent {

    private String id;
    private String orderId;
    private String customerId;
    private String address;
    private String status;
}
