package com.kmtp.reservation.service;

import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.ResponseErrorHandler;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.reservation.endpoint.Item;
import com.kmtp.reservation.persistence.ItemEntity;
import com.kmtp.reservation.persistence.ItemRepository;
import com.kmtp.reservation.persistence.ReservationEntity;
import com.kmtp.reservation.persistence.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class ItemHandler {

    private ItemRepository itemRepository;
    private GenericValidator genericValidator;

    @Autowired
    public ItemHandler(ItemRepository itemRepository, GenericValidator genericValidator) {
        this.itemRepository = itemRepository;
        this.genericValidator = genericValidator;
    }

    public Mono<ServerResponse> list(ServerRequest request) {

        final Mono<Long> masterIdMono = request.queryParam("masterId")
                .map(Long::parseLong)
                .map(Mono::just)
                .orElseGet(Mono::empty);

        Mono<List<Item>> itemList = masterIdMono
                .switchIfEmpty(GenericError.of(HttpStatus.BAD_REQUEST, "query param master-id is required."))
                .flatMapMany(itemRepository::findByMasterId)
                .map(ItemMapper.INSTANCE::entityToApi)
                .collectList();

        return ResponseHandler.ok(itemList);기영씨 알라뷰
    }

    public Mono<ServerResponse> get(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));

        Mono<Item> itemMono = itemRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id."))
                .map(ItemMapper.INSTANCE::entityToApi);

        return ResponseHandler.ok(itemMono);
    }

    public Mono<ServerResponse> post(ServerRequest request) {

        Mono<ItemEntity> saveItemMono = request.bodyToMono(Item.class)
                .doOnNext(item -> genericValidator.validate(item, Item.class))
                .map(ItemMapper.INSTANCE::apiToEntity)
                .flatMap(itemRepository::save);

        return ResponseHandler.created(saveItemMono, URI.create(request.path()));
    }

    public Mono<ServerResponse> put(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));
        final Mono<Item> itemMono = request.bodyToMono(Item.class)
                .doOnNext(item -> genericValidator.validate(item, Item.class));

        final Mono<ItemEntity> itemEntityMono = itemRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id"));

        Mono<ItemEntity> updateItemMono = Mono.zip(itemMono, itemEntityMono)
                .flatMap(tuple2 -> {

                    tuple2.getT2().setName(tuple2.getT1().getName());

                    return Mono.just(tuple2.getT2())
                            .flatMap(itemRepository::save);
                });

        return ResponseHandler.noContent(updateItemMono);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));

        final Mono<Void> itemMono = itemRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id."))
                .then(itemRepository.deleteById(id));

        return ResponseHandler.noContent(itemMono);
    }
}
