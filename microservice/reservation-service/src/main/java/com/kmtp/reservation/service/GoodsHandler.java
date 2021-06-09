package com.kmtp.reservation.service;

import com.kmtp.common.api.Discount;
import com.kmtp.common.api.Goods;
import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.reservation.persistence.DiscountEntity;
import com.kmtp.reservation.persistence.DiscountRepository;
import com.kmtp.reservation.persistence.GoodsEntity;
import com.kmtp.reservation.persistence.GoodsRepository;
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
public class GoodsHandler {

    private GoodsRepository goodsRepository;
    private DiscountRepository discountRepository;
    private GenericValidator genericValidator;

    @Autowired
    public GoodsHandler(GoodsRepository goodsRepository
            , DiscountRepository discountRepository, GenericValidator genericValidator) {

        this.goodsRepository = goodsRepository;
        this.discountRepository = discountRepository;
        this.genericValidator = genericValidator;
    }

    /**
     * 상품, 할인율 정보를 조회합니다.
     * <p></p>
     * (1) master-id를 query parameter 에서 조회하고 {@link Long} 타입으로 변환합니다.<br>
     * master-id를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (2) 상품 정보를 조회하고 {@link Goods}로 변환합니다.<br>
     * (3) 할인율 정보를 조회하고 {@link Discount}로 변환합니다.<br>
     * (4) 상품 정보와 할인율 정보를 {@link Flux#zip}해서 상품정보에 할인율 정보를 설정합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link List}<{@link Goods}>></{@link>
     */
    public Mono<ServerResponse> list(ServerRequest request) {

        // (1)
        final Long masterId = request.queryParam("masterId")
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param master-id is required."));

        // (2)
        final Flux<Goods> goodsFlux = goodsRepository.findByMasterId(masterId)
                .map(GoodsMapper.INSTANCE::entityToApi);

        // (3)
        final Flux<Discount> discountFlux = discountRepository.findByMasterId(masterId)
                .map(DiscountMapper.INSTANCE::entityToApi);

        // (4)
        final Mono<List<Goods>> listMono = Flux.zip(goodsFlux, discountFlux)
                .flatMap(tuple2 -> {
                    tuple2.getT1().setDiscount(tuple2.getT2());
                    return Mono.just(tuple2.getT1());
                }).collectList();

        return ResponseHandler.ok(listMono);
    }

    /**
     * 상품, 할인율 정보를 조회합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) 상품 정보를 조회하고 {@link Goods}로 변환합니다.<br>
     * 상품 정보를 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (3) 할인율 정보를 조회하고 {@link Discount}로 변환합니다.<br>
     * 할인율 정보를 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (4) 상품정보와 할인율 정보를 {@link Mono#zip}해서 상품정보에 할인율 정보를 설정합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link Goods}></{@link>
     */
    public Mono<ServerResponse> get(ServerRequest request) {

        // (1)
        final Long id = Long.parseLong(request.pathVariable("id"));

        // (2)
        final Mono<Goods> goodsMono = goodsRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods."))
                .map(GoodsMapper.INSTANCE::entityToApi);

        // (3)
        final Mono<Discount> discountMono = discountRepository.findByGoodsId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods discount."))
                .map(DiscountMapper.INSTANCE::entityToApi);

        // (4)
        final Mono<Goods> responseMono = Mono.zip(goodsMono, discountMono)
                .flatMap(tuple2 -> {
                    tuple2.getT1().setDiscount(tuple2.getT2());
                    return Mono.just(tuple2.getT1());
                });

        return ResponseHandler.ok(responseMono);
    }

    /**
     * 상품, 할인율 정보를 등록 합니다.
     * <p></p>
     * (1) request body 정보를 변환한뒤 {@link Goods},{@link Discount} 유효성 체크를 진행합니다.<br>
     * 상품, 할인율 정보를 분리하고 {@link Mono#zip}하여 {@link Tuple2}객체를 생성합니다.<br>
     * 상품 정보를 Entity 형태로 변한한뒤 DB에 저장하고 저장된 상품정보의 master-id, goods-id 정보를
     * 할인율 정보에 설정한뒤 Entity 형태로 변환하고 DB에 저장합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#CREATED}
     */
    public Mono<ServerResponse> post(ServerRequest request) {

        // (1)
        final Mono<DiscountEntity> goodsEntityMono = request.bodyToMono(Goods.class)
                .doOnNext(goods -> genericValidator.validate(goods, Goods.class))
                .doOnNext(goods -> genericValidator.validate(goods.getDiscount(), Discount.class))
                .flatMap(goods -> {

                    final Mono<Goods> goodsMono = Mono.just(goods);
                    final Mono<Discount> discountMono = Mono.just(goods.getDiscount());

                    return Mono.zip(goodsMono, discountMono);
                })
                .flatMap(tuple2 -> goodsRepository.save(GoodsMapper.INSTANCE.apiToEntity(tuple2.getT1()))
                        .flatMap(goodsEntity -> {

                            tuple2.getT2().setMasterId(goodsEntity.getMasterId());
                            tuple2.getT2().setGoodsId(goodsEntity.getId());

                            return discountRepository.save(DiscountMapper.INSTANCE.apiToEntity(tuple2.getT2()));
                        }));

        return ResponseHandler.created(goodsEntityMono, URI.create(request.path()));
    }

    /**
     * 상품, 할인율 정보를 수정합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) request body 정보를 변환한뒤 {@link Goods},{@link Discount} 유효성 체크를 진행합니다.<br>
     * (3) 상품 정보를 조회합니다. 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (4) 할인율 정보를 조회합니다. 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (5) request body정보, 상품정보, 할인율정보를 {@link Mono#zip}해서 {@link Tuple3}객체를 생성합니다.<br>
     * 수정해야할 상품명, 할인율, 할인율명을 설정하고 상품, 할인율 정보를 DB에 저장합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#NO_CONTENT}
     */
    public Mono<ServerResponse> put(ServerRequest request) {

        // (1)
        final Long id = Long.parseLong(request.pathVariable("id"));

        // (2)
        final Mono<Goods> goodsMono = request.bodyToMono(Goods.class)
                .doOnNext(goods -> genericValidator.validate(goods, Goods.class))
                .doOnNext(goods -> genericValidator.validate(goods.getDiscount(), Discount.class));

        // (3)
        final Mono<GoodsEntity> goodsEntityMono = goodsRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods."));

        // (4)
        final Mono<DiscountEntity> discountEntityMono = discountRepository.findByGoodsId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods discount."));

        // (5)
        final Mono<DiscountEntity> updateGoodsMono = Mono.zip(goodsMono, goodsEntityMono, discountEntityMono)
                .doOnNext(tuple3 -> tuple3.getT2()
                        .change(goodsEntity -> goodsEntity.setName(tuple3.getT1().getName())))
                .doOnNext(tuple3 -> tuple3.getT3()
                        .change(discountEntity -> {
                            discountEntity.setDiscount(tuple3.getT1().getDiscount().getDiscount());
                            discountEntity.setName(tuple3.getT1().getDiscount().getName());
                        }))
                .flatMap(tuple3 -> goodsRepository.save(tuple3.getT2())
                        .then(discountRepository.save(tuple3.getT3())));

        return ResponseHandler.noContent(updateGoodsMono);
    }

    /**
     * 상품, 할인율 정보를 삭제합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) 할인율 정보를 조회합니다. 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br> 할인율 정보를 삭제합니다.<br>
     * (3) 상품 정보를 조회합니다. 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br> 상품 정보를 삭제합니다.<br>
     * (4) {@link Mono#zip}해서 응답정보를 생성합니다. (Subscribe를 하기위해 응답정보를 생성 합니다.)
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#NO_CONTENT}
     */
    public Mono<ServerResponse> delete(ServerRequest request) {

        // (1)
        final Long id = Long.parseLong(request.pathVariable("id"));

        // (2)
        final Mono<Void> discountMono = discountRepository.findByGoodsId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods discount."))
                .then(discountRepository.deleteByGoodsId(id));

        // (3)
        final Mono<Void> goodsMono = goodsRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods."))
                .then(goodsRepository.deleteById(id));

        // (4)
        return ResponseHandler.noContent(Mono.zip(discountMono, goodsMono));
    }
}
