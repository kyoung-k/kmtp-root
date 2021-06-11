/*
 * Copyright (c) 2021-Present KYoung.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kmtp.common.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceInfo {

    // master-service host
    public static String masterHost;
    // master-service port
    public static int masterPort;

    // reservation-service host
    public static String reservationHost;
    // reservation-service port
    public static int reservationPort;

    public ServiceInfo(
            @Value("${app.master-service.host}") String masterHost,
            @Value("${app.master-service.port}") int masterPort,
            @Value("${app.reservation-service.host}") String reservationHost,
            @Value("${app.reservation-service.port}") int reservationPort
    ) {

        this.masterHost = masterHost;
        this.masterPort = masterPort;
        this.reservationHost = reservationHost;
        this.reservationPort = reservationPort;
    }
}
