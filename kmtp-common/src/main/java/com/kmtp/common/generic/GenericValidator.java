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
package com.kmtp.common.generic;

import com.kmtp.common.validation.ValidationErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * Request 요청에 대한 유효성 확인을 처리하는 class 입니다.
 * {@link Validator}를 사용해서 유효성 검사를 처리합니다. WebFlux Functional Endpoint에서 사용 합니다.
 * @see <a href="https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/validation.html">Spring validation</a>
 * @author KYoung
 */
@Component
public class GenericValidator {

    final private Validator validator;

    @Autowired
    public GenericValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * {@link Validator} 인터페이스를 사용해서 유효성을 검사한다.<br>
     * Error가 발생하면 에러처리를 {@link ValidationErrorHandler}에 위임한다.
     * @param api POJO
     * @param elementClass POJO {@link Class}
     * @param <T> POJO
     */
    public <T> void validate(T api, Class<? extends T> elementClass) {

        Errors errors = new BeanPropertyBindingResult(api, elementClass.getName());
        validator.validate(api, errors);

        if (errors.hasErrors()) {
            ValidationErrorHandler.build(errors);
        }
    }

    /**
     * {@link Validator} 인터페이스를 사용해서 유효성을 검사한다.<br>
     * Error가 발생하면 에러처리를 {@link ValidationErrorHandler}에 위임한다.<br>
     * TODO 분석 설계를 통해 목록에 대한 에러처리를 고민해보자.
     * @param list POJO
     * @param elementClass POJO {@link Class}
     * @param <T> POJO
     */
    public <T> void validateList(List<T> list, Class<? extends T> elementClass) {

        // TODO List Validation index 처리
        Errors errors = new BeanPropertyBindingResult(list, elementClass.getName());

        list.forEach(object -> {
            validator.validate(object, errors);
        });

        if (errors.hasErrors()) {
            ValidationErrorHandler.build(errors);
        }
    }
}
