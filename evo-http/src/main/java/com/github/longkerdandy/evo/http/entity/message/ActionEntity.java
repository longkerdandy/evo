package com.github.longkerdandy.evo.http.entity.message;

import com.github.longkerdandy.evo.api.message.Action;
import io.dropwizard.validation.ValidationMethod;
import org.apache.commons.lang3.StringUtils;

/**
 * Action Message related entity
 */
@SuppressWarnings("unused")
public class ActionEntity extends Action {

    @ValidationMethod(message="Action id can not be empty")
    public boolean isValid() {
        return !StringUtils.isBlank(this.actionId);
    }
}
