package com.kmtp.reservation.service;

import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.reservation.endpoint.Discount;
import com.kmtp.reservation.endpoint.Goods;
import com.kmtp.reservation.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Mono<ServerResponse> list(ServerRequest request) {

        final Mono<Long> masterIdMono = request.queryParam("masterId")
                .map(Long::parseLong)
                .map(Mono::just)
                .orElseGet(Mono::empty);

        final Mono<List<Goods>> listMono = masterIdMono
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "query param master-id is required."))
                .flatMapMany(masterId -> {

                    final Flux<Goods> goodsFlux = goodsRepository.findByMasterId(masterId)
                            .map(GoodsMapper.INSTANCE::entityToApi);

                    final Flux<Discount> discountFlux = discountRepository.findByMasterId(masterId)
                            .map(DiscountMapper.INSTANCE::entityToApi);

                    return Flux.zip(goodsFlux, discountFlux)
                            .flatMap(tuple2 -> {
                               tuple2.getT1().setDiscount(tuple2.getT2());
                               return Mono.just(tuple2.getT1());
                            });
                }).collectList();

        return ResponseHandler.ok(listMono);
    }

    public Mono<ServerResponse> get(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));

        final Mono<Goods> goodsMono = goodsRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods-id."))
                .map(GoodsMapper.INSTANCE::entityToApi);

        final Mono<Discount> discountMono = discountRepository.findByGoodsId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods discount."))
                .map(DiscountMapper.INSTANCE::entityToApi);

        final Mono<Goods> responseMono = Mono.zip(goodsMono, discountMono)
                .flatMap(tuple2 -> {
                    tuple2.getT1().setDiscount(tuple2.getT2());
                    return Mono.just(tuple2.getT1());
                });

        return ResponseHandler.ok(responseMono);
    }

    public Mono<ServerResponse> post(ServerRequest request) {

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

    public Mono<ServerResponse> put(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));

        final Mono<Goods> goodsMono = request.bodyToMono(Goods.class)
                .doOnNext(goods -> genericValidator.validate(goods, Goods.class))
                .doOnNext(goods -> genericValidator.validate(goods.getDiscount(), Discount.class));

        final Mono<GoodsEntity> goodsEntityMono = goodsRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods-id."));

        final Mono<DiscountEntity> discountEntityMono = discountRepository.findByGoodsId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods discount."));

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

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));

        final Mono<Void> discountMono = discountRepository.findByGoodsId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods discount."))
                .then(discountRepository.deleteByGoodsId(id));

        final Mono<Void> goodsMono = goodsRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods-id."))
                .then(goodsRepository.deleteById(id));

        return ResponseHandler.noContent(Mono.zip(discountMono, goodsMono));
    }
}
