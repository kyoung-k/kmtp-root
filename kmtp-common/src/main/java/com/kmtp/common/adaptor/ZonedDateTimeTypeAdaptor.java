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
package com.kmtp.common.adaptor;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>{@link Gson}을 사용하여 JSON Response를 파싱할때 {@link ZonedDateTime}을 캐스팅 할때 사용하는 어탭터 입니다.</p>
 *
 * <pre>
 *     {@code
 *     example :
 *     new GsonBuilder()
 *          .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdaptor())
 *          .create()
 *          .fromJson(jsonBody, TypeToken.getParameterized(HttpInfo.class, clazz).getType())
 *     }
 * </pre>
 *
 * @author KYoung
 */
public class ZonedDateTimeTypeAdaptor implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        try {
            final String dateTimeString = json.getAsString();
            return ZonedDateTime.parse(dateTimeString);
        } catch (Exception e) {
            throw new JsonParseException("Failed to new instance", e);
        }
    }

    @Override
    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(DateTimeFormatter.ISO_INSTANT));
    }
}
