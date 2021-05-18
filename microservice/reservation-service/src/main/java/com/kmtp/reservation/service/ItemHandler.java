package com.kmtp.reservation.service;

import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.ResponseErrorHandler;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.reservation.endpoint.Charge;
import com.kmtp.reservation.endpoint.Item;
import com.kmtp.reservation.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class ItemHandler {

    private ItemRepository itemRepository;
    private ChargeRepository chargeRepository;
    private GenericValidator genericValidator;

    @Autowired
    public ItemHandler(ItemRepository itemRepository, ChargeRepository chargeRepository, GenericValidator genericValidator) {
        this.itemRepository = itemRepository;
        this.chargeRepository = chargeRepository;
        this.genericValidator = genericValidator;
    }

    public Mono<ServerResponse> list(ServerRequest request) {

        final Mono<Long> masterIdMono = request.queryParam("masterId")
                .map(Long::parseLong)
                .map(Mono::just)
                .orElseGet(Mono::empty);

        final Mono<List<Item>> itemList = masterIdMono
                .switchIfEmpty(GenericError.of(HttpStatus.BAD_REQUEST, "query param master-id is required."))
                .flatMapMany(masterId -> {

                    final Flux<Item> itemFlux = itemRepository.findByMasterId(masterId)
                            .map(ItemMapper.INSTANCE::entityToApi);

                    final Flux<Charge> chargeFlux = chargeRepository.findByMasterId(masterId)
                            .map(ChargeMapper.INSTANCE::entityToApi);

                    return Flux.zip(itemFlux, chargeFlux)
                            .flatMap(tuple2 -> {
                                tuple2.getT1().setCharge(tuple2.getT2());
                                return Mono.just(tuple2.getT1());
                            });
                }).collectList();

        return ResponseHandler.ok(itemList);
    }

    public Mono<ServerResponse> get(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));

        final Mono<Item> itemMono = itemRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id."))
                .map(ItemMapper.INSTANCE::entityToApi);

        final Mono<Charge> chargeMono = chargeRepository.findByItemId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item charge."))
                .map(ChargeMapper.INSTANCE::entityToApi);

        final Mono<Item> responseMono = Mono.zip(itemMono, chargeMono)
                .flatMap(tuple2 -> {
                    tuple2.getT1().setCharge(tuple2.getT2());
                    return Mono.just(tuple2.getT1());
                });

        return ResponseHandler.ok(responseMono);
    }

    public Mono<ServerResponse> post(ServerRequest request) {

        final Mono<ChargeEntity> saveItemMono = request.bodyToMono(Item.class)
                .doOnNext(item -> genericValidator.validate(item, Item.class))
                .doOnNext(item -> genericValidator.validate(item.getCharge(), Charge.class))
                .flatMap(item -> {

                    final Mono<Item> itemMono = Mono.just(item);
                    final Mono<Charge> chargeMono = Mono.just(item.getCharge());

                    return Mono.zip(itemMono, chargeMono);
                })
                .flatMap(tuple2 -> itemRepository.save(ItemMapper.INSTANCE.apiToEntity(tuple2.getT1()))
                        .flatMap(itemEntity -> {

                            tuple2.getT2().setMasterId(itemEntity.getMasterId());
                            tuple2.getT2().setItemId(itemEntity.getId());

                            return chargeRepository.save(ChargeMapper.INSTANCE.apiToEntity(tuple2.getT2()));
                        }));

        return ResponseHandler.created(saveItemMono, URI.create(request.path()));
    }

    public Mono<ServerResponse> put(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));
        final Mono<Item> itemMono = request.bodyToMono(Item.class)
                .doOnNext(item -> genericValidator.validate(item, Item.class))
                .doOnNext(item -> genericValidator.validate(item.getCharge(), Charge.class));

        final Mono<ItemEntity> itemEntityMono = itemRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id"));

        final Mono<ChargeEntity> chargeEntityMono = chargeRepository.findByItemId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item charge."));

        final Mono<?> updateItemMono = Mono.zip(itemMono, itemEntityMono, chargeEntityMono)
                .doOnNext(tuple3 -> tuple3.getT2()
                        .change(itemEntity -> itemEntity.setName(tuple3.getT1().getName())))
                .doOnNext(tuple3 -> tuple3.getT3()
                        .change(chargeEntity -> {
                            chargeEntity.setCharge(tuple3.getT1().getCharge().getCharge());
                            chargeEntity.setName(tuple3.getT1().getCharge().getName());
                        }))
                .flatMap(tuple3 -> itemRepository.save(tuple3.getT2())
                        .then(chargeRepository.save(tuple3.getT3())));

        return ResponseHandler.noContent(updateItemMono);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));

        final Mono<Void> chargeMono = chargeRepository.findByItemId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item charge."))
                .then(chargeRepository.deleteByItemId(id));

        final Mono<Void> itemMono = itemRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id."))
                .then(itemRepository.deleteById(id));

        return ResponseHandler.noContent(Mono.zip(chargeMono, itemMono));
    }
}
