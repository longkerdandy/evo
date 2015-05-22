package com.github.longkerdandy.evo.http.entity.message;

import com.github.longkerdandy.evo.api.message.Action;
import com.github.longkerdandy.evo.http.entity.ErrorCode;
import com.github.longkerdandy.evo.http.entity.ErrorEntity;
import com.github.longkerdandy.evo.http.exception.ValidateException;
import org.apache.commons.lang3.StringUtils;

/**
 * Action Message related entity
 */
public class ActionEntity extends Action {

    public void validateActionId(String lang) {
        if (StringUtils.isBlank(this.actionId))
            throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
    }
}
