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
public class DeliveryViewQueryController {

    private final QueryGateway queryGateway;

    private final ReactorQueryGateway reactorQueryGateway;

    public DeliveryViewQueryController(
        QueryGateway queryGateway,
        ReactorQueryGateway reactorQueryGateway
    ) {
        this.queryGateway = queryGateway;
        this.reactorQueryGateway = reactorQueryGateway;
    }

    @GetMapping("/deliveryViews")
    public CompletableFuture findAll(DeliveryViewQuery query) {
        return queryGateway
            .query(query, ResponseTypes.multipleInstancesOf(DeliveryView.class))
            .thenApply(resources -> {
                List modelList = new ArrayList<EntityModel<DeliveryView>>();

                resources
                    .stream()
                    .forEach(resource -> {
                        modelList.add(hateoas(resource));
                    });

                CollectionModel<DeliveryView> model = CollectionModel.of(
                    modelList
                );

                return new ResponseEntity<>(model, HttpStatus.OK);
            });
    }

    @GetMapping("/deliveryViews/{id}")
    public CompletableFuture findById(@PathVariable("id") Long id) {
        DeliveryViewSingleQuery query = new DeliveryViewSingleQuery();
        query.setId(id);

        return queryGateway
            .query(query, ResponseTypes.optionalInstanceOf(DeliveryView.class))
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

    EntityModel<DeliveryView> hateoas(DeliveryView resource) {
        EntityModel<DeliveryView> model = EntityModel.of(resource);

        model.add(Link.of("/deliveryViews/" + resource.getId()).withSelfRel());

        return model;
    }

    @MessageMapping("deliveryViews.all")
    public Flux<DeliveryView> subscribeAll() {
        return reactorQueryGateway.subscriptionQueryMany(
            new DeliveryViewQuery(),
            DeliveryView.class
        );
    }

    @MessageMapping("deliveryViews.{id}.get")
    public Flux<DeliveryView> subscribeSingle(@DestinationVariable Long id) {
        DeliveryViewSingleQuery query = new DeliveryViewSingleQuery();
        query.setId(id);

        return reactorQueryGateway.subscriptionQuery(query, DeliveryView.class);
    }
}
