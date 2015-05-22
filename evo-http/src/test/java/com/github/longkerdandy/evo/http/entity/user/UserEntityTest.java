package com.github.longkerdandy.evo.http.entity.user;

import com.github.longkerdandy.evo.api.util.UuidUtils;
import com.github.longkerdandy.evo.http.exception.ValidateException;
import org.junit.Test;

import static com.googlecode.catchexception.CatchException.verifyException;

/**
 * AbstractResource Test
 */
public class UserEntityTest {

    @Test
    public void idOrMobileValidTest() {
        UserEntity u = new UserEntity();

        verifyException(u, ValidateException.class).validateIdOrMobile("zh");

        u.setId(UuidUtils.shortUuid());
        u.validateIdOrMobile("zh");

        u.setId(null);
        u.setMobile("+86 18616862881");
        u.validateIdOrMobile("zh");
    }

    @Test
    public void mobileValidTest() {
        UserEntity u = new UserEntity();

        u.setMobile("+86 18616862881");
        u.validateMobile("zh");

        u.setMobile("18616862881");
        verifyException(u, ValidateException.class).validateMobile("zh");

        u.setMobile(null);
        verifyException(u, ValidateException.class).validateMobile("zh");

        u.setMobile("");
        verifyException(u, ValidateException.class).validateMobile("zh");
    }

    @Test
    public void emailValidTest() {
        UserEntity u = new UserEntity();

        u.setEmail("longkerdandy@gmail.com");
        u.validateEmail("zh");

        u.setEmail("longkerdandy");
        verifyException(u, ValidateException.class).validateEmail("zh");

        u.setEmail(null);
        verifyException(u, ValidateException.class).validateEmail("zh");

        u.setEmail("");
        verifyException(u, ValidateException.class).validateEmail("zh");
    }

    @Test
    public void aliasValidTest() {
        UserEntity u = new UserEntity();

        u.setAlias("LongkerDandy");
        u.validateAlias("zh");

        u.setAlias("\u6D6A\u5BA2Dandy");
        u.validateAlias("zh");

        u.setAlias("I love LongkerDandy");
        verifyException(u, ValidateException.class).validateAlias("zh");

        u.setAlias(null);
        verifyException(u, ValidateException.class).validateAlias("zh");

        u.setAlias("");
        verifyException(u, ValidateException.class).validateAlias("zh");
    }

    @Test
    public void passwordValidTest() {
        UserEntity u = new UserEntity();

        u.setPassword("Dandy@0516");
        u.validatePassword("zh");

        u.setPassword("1234567890");
        verifyException(u, ValidateException.class).validatePassword("zh");

        u.setPassword(null);
        verifyException(u, ValidateException.class).validatePassword("zh");

        u.setPassword("");
        verifyException(u, ValidateException.class).validatePassword("zh");
    }
}
