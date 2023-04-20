package shopping.mall.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shopping.mall.event.*;

@Service
@ProcessingGroup("orderView")
public class OrderViewCQRSHandler {

    @Autowired
    private OrderViewRepository orderViewRepository;

    @QueryHandler
    public List<OrderView> handle(OrderViewQuery query) {
        return orderViewRepository.findAll();
    }
}
