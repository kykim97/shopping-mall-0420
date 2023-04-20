package shopping.mall.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import shopping.mall.query.*;

@RestController
public class OrderViewQueryController {

    private final QueryGateway queryGateway;

    private final ReactorQueryGateway reactorQueryGateway;

    public OrderViewQueryController(
        QueryGateway queryGateway,
        ReactorQueryGateway reactorQueryGateway
    ) {
        this.queryGateway = queryGateway;
        this.reactorQueryGateway = reactorQueryGateway;
    }

    @GetMapping("/orderViews")
    public CompletableFuture findAll(OrderViewQuery query) {
        return queryGateway
            .query(query, ResponseTypes.multipleInstancesOf(OrderView.class))
            .thenApply(resources -> {
                List modelList = new ArrayList<EntityModel<OrderView>>();

                resources
                    .stream()
                    .forEach(resource -> {
                        modelList.add(hateoas(resource));
                    });

                CollectionModel<OrderView> model = CollectionModel.of(
                    modelList
                );

                return new ResponseEntity<>(model, HttpStatus.OK);
            });
    }

    @GetMapping("/orderViews/{id}")
    public CompletableFuture findById(@PathVariable("id") Long id) {
        OrderViewSingleQuery query = new OrderViewSingleQuery();
        query.setId(id);

        return queryGateway
            .query(query, ResponseTypes.optionalInstanceOf(OrderView.class))
            .thenApply(resource -> {
                if (!resource.isPresent()) {
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(
                    hateoas(resource.get()),
                    HttpStatus.OK
                );
            })
            .exceptionally(ex -> {
                throw new RuntimeException(ex);
            });
    }

    EntityModel<OrderView> hateoas(OrderView resource) {
        EntityModel<OrderView> model = EntityModel.of(resource);

        model.add(Link.of("/orderViews/" + resource.getId()).withSelfRel());

        return model;
    }

    @MessageMapping("orderViews.all")
    public Flux<OrderView> subscribeAll() {
        return reactorQueryGateway.subscriptionQueryMany(
            new OrderViewQuery(),
            OrderView.class
        );
    }

    @MessageMapping("orderViews.{id}.get")
    public Flux<OrderView> subscribeSingle(@DestinationVariable Long id) {
        OrderViewSingleQuery query = new OrderViewSingleQuery();
        query.setId(id);

        return reactorQueryGateway.subscriptionQuery(query, OrderView.class);
    }
}
