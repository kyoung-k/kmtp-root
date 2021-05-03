package com.kmtp.reservation.endpoint;

import com.kmtp.reservation.persistence.ItemEntity;
import com.kmtp.reservation.persistence.ItemRepository;
import com.kmtp.reservation.persistence.ReservationEntity;
import com.kmtp.reservation.persistence.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class ItemHandler {

    private ItemRepository itemRepository;

    @Autowired
    public ItemHandler(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Mono<ServerResponse> getItem(ServerRequest request) {
        return ok().contentType(MediaType.APPLICATION_JSON)
                .body(itemRepository.findById(1l), ItemEntity.class);
    }
}
