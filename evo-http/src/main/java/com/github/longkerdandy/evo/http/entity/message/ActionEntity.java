package com.github.longkerdandy.evo.http.entity.message;

import com.github.longkerdandy.evo.api.message.Action;
import com.github.longkerdandy.evo.http.entity.ErrorCode;
import com.github.longkerdandy.evo.http.entity.ErrorEntity;
import com.github.longkerdandy.evo.http.exception.ValidateException;

/**
 * Action Message related entity
 */
public class ActionEntity extends Action {

    /**
     * Is action entity valid
     */
    public void validate(String lang) {
        try {
            validate();
        } catch (IllegalStateException e) {
            throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
        }
    }
}
