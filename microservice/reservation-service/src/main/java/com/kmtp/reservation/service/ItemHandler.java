package com.kmtp.reservation.service;

import com.kmtp.common.api.Discount;
import com.kmtp.common.api.Goods;
import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.common.api.Charge;
import com.kmtp.common.api.Item;
import com.kmtp.reservation.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.net.URI;
import java.util.List;

@Component
public class ItemHandler {

    private ItemRepository itemRepository;
    private ChargeRepository chargeRepository;
    private GenericValidator genericValidator;

    @Autowired
    public ItemHandler(ItemRepository itemRepository
            , ChargeRepository chargeRepository, GenericValidator genericValidator) {

        this.itemRepository = itemRepository;
        this.chargeRepository = chargeRepository;
        this.genericValidator = genericValidator;
    }

    /**
     * 아이템, 금액 정보를 조회합니다.
     * <p></p>
     * (1) master-id를 query parameter 에서 조회하고 {@link Long} 타입으로 변환합니다.<br>
     * master-id를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (2) 아이템 정보를 조회하고 {@link Item}로 변환합니다.<br>
     * (3) 금액 정보를 조회하고 {@link Charge}로 변환합니다.<br>
     * (4) 아이템 정보와 금액 정보를 {@link Flux#zip}해서 아이템 정보에 금액 정보를 설정합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link List}<{@link Item}>></{@link>
     */
    public Mono<ServerResponse> list(ServerRequest request) {

        // (1)
        final Long masterId = request.queryParam("masterId")
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param master-id is required."));

        // (2)
        final Flux<Item> itemFlux = itemRepository.findByMasterId(masterId)
                .map(ItemMapper.INSTANCE::entityToApi);

        // (3)
        final Flux<Charge> chargeFlux = chargeRepository.findByMasterId(masterId)
                .map(ChargeMapper.INSTANCE::entityToApi);

        // (4)
        final Mono<List<Item>> listMono = Flux.zip(itemFlux, chargeFlux)
                .flatMap(tuple2 -> {
                    tuple2.getT1().setCharge(tuple2.getT2());
                    return Mono.just(tuple2.getT1());
                }).collectList();

        return ResponseHandler.ok(listMono);
    }

    /**
     * 아이템, 금액 정보를 조회합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) 아이템 정보를 조회하고 {@link Item}로 변환합니다.<br>
     * 아이템 정보를 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (3) 금액 정보를 조회하고 {@link Charge}로 변환합니다.<br>
     * 금액 정보를 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (4) 아이템정보와 금액 정보를 {@link Mono#zip}해서 아이템정보에 금액 정보를 설정합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link Item}></{@link>
     */
    public Mono<ServerResponse> get(ServerRequest request) {

        // (1)
        final Long id = Long.parseLong(request.pathVariable("id"));

        // (2)
        final Mono<Item> itemMono = itemRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id."))
                .map(ItemMapper.INSTANCE::entityToApi);

        // (3)
        final Mono<Charge> chargeMono = chargeRepository.findByItemId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item charge."))
                .map(ChargeMapper.INSTANCE::entityToApi);

        // (4)
        final Mono<Item> responseMono = Mono.zip(itemMono, chargeMono)
                .flatMap(tuple2 -> {
                    tuple2.getT1().setCharge(tuple2.getT2());
                    return Mono.just(tuple2.getT1());
                });

        return ResponseHandler.ok(responseMono);
    }

    /**
     * 아이템, 금액 정보를 등록 합니다.
     * <p></p>
     * (1) request body 정보를 변환한뒤 {@link Item},{@link Charge} 유효성 체크를 진행합니다.<br>
     * 아이템, 금액 정보를 분리하고 {@link Mono#zip}하여 {@link Tuple2}객체를 생성합니다.<br>
     * 아이템 정보를 Entity 형태로 변한한뒤 DB에 저장하고 저장된 아이템정보의 master-id, item-id 정보를
     * 금액 정보에 설정한뒤 Entity 형태로 변환하고 DB에 저장합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#CREATED}
     */
    public Mono<ServerResponse> post(ServerRequest request) {

        // (1)
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

    /**
     * 아이템, 금액 정보를 수정합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) request body 정보를 변환한뒤 {@link Item},{@link Charge} 유효성 체크를 진행합니다.<br>
     * (3) 아이템 정보를 조회합니다. 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (4) 금액 정보를 조회합니다. 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (5) request body정보, 아이템정보, 금액정보를 {@link Mono#zip}해서 {@link Tuple3}객체를 생성합니다.<br>
     * 수정해야할 아이템명, 금액, 금액명을 설정하고 상품, 할인율 정보를 DB에 저장합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#NO_CONTENT}
     */
    public Mono<ServerResponse> put(ServerRequest request) {

        // (1)
        final Long id = Long.parseLong(request.pathVariable("id"));

        // (2)
        final Mono<Item> itemMono = request.bodyToMono(Item.class)
                .doOnNext(item -> genericValidator.validate(item, Item.class))
                .doOnNext(item -> genericValidator.validate(item.getCharge(), Charge.class));

        // (3)
        final Mono<ItemEntity> itemEntityMono = itemRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id"));

        // (4)
        final Mono<ChargeEntity> chargeEntityMono = chargeRepository.findByItemId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item charge."));

        // (5)
        final Mono<ChargeEntity> updateItemMono = Mono.zip(itemMono, itemEntityMono, chargeEntityMono)
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

    /**
     * 아이템, 금액 정보를 삭제합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) 아이템 정보를 조회합니다. 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br> 아이템 정보를 삭제합니다.<br>
     * (3) 금액 정보를 조회합니다. 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br> 금액 정보를 삭제합니다.<br>
     * (4) {@link Mono#zip}해서 응답정보를 생성합니다. (Subscribe를 하기위해 응답정보를 생성 합니다.)
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#NO_CONTENT}
     */
    public Mono<ServerResponse> delete(ServerRequest request) {

        // (1)
        final Long id = Long.parseLong(request.pathVariable("id"));

        // (2)
        final Mono<Void> chargeMono = chargeRepository.findByItemId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item charge."))
                .then(chargeRepository.deleteByItemId(id));

        // (3)
        final Mono<Void> itemMono = itemRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id."))
                .then(itemRepository.deleteById(id));

        // (4)
        return ResponseHandler.noContent(Mono.zip(chargeMono, itemMono));
    }
}
