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

        return ResponseHandler.ok(itemList);
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        return ok().contentType(MediaType.APPLICATION_JSON)
                .body(itemRepository.findById(1l), ItemEntity.class);
    }

    public Mono<ServerResponse> post(ServerRequest request) {

        return request.bodyToMono(Item.class)
                .doOnNext(item -> genericValidator.validate(item, Item.class))
                .map(ItemMapper.INSTANCE::apiToEntity)
                .flatMap(itemRepository::save)
                .flatMap(itemEntity -> ServerResponse
                        .created(URI.create(request.path()))
                        .build());
    }
}
