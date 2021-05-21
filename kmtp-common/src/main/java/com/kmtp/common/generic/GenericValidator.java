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

@Component
public class GenericValidator {

    final private Validator validator;

    @Autowired
    public GenericValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T api, Class<? extends T> elementClass) {

        Errors errors = new BeanPropertyBindingResult(api, elementClass.getName());
        validator.validate(api, errors);

        if (errors.hasErrors()) {
            ValidationErrorHandler.build(errors);
        }
    }

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
