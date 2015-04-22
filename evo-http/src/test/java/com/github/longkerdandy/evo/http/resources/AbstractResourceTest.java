package com.github.longkerdandy.evo.http.resources;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * AbstractResource Test
 */
public class AbstractResourceTest {

    @Test
    public void mobileValidTest() {
        AbstractResource r = Mockito.mock(AbstractResource.class, Mockito.CALLS_REAL_METHODS);
        assert r.isMobileValid("+86 18616862881");
        assert !r.isMobileValid("18616862881");
        assert !r.isMobileValid(null);
        assert !r.isMobileValid("");
    }

    @Test
    public void emailValidTest() {
        AbstractResource r = Mockito.mock(AbstractResource.class, Mockito.CALLS_REAL_METHODS);
        assert r.isEmailValid("longkerdandy@gmail.com");
        assert !r.isEmailValid("longkerdandy");
        assert !r.isEmailValid(null);
        assert !r.isEmailValid("");
    }

    @Test
    public void aliasValidTest() {
        AbstractResource r = Mockito.mock(AbstractResource.class, Mockito.CALLS_REAL_METHODS);
        assert r.isAliasValid("LongkerDandy");
        assert r.isAliasValid("\u6D6A\u5BA2Dandy");
        assert !r.isAliasValid("I love LongkerDandy");
        assert !r.isAliasValid(null);
        assert !r.isAliasValid("");
    }

    @Test
    public void passwordValidTest() {
        AbstractResource r = Mockito.mock(AbstractResource.class, Mockito.CALLS_REAL_METHODS);
        assert r.isPasswordValid("Dandy@0516");
        assert !r.isPasswordValid("1234567890");
        assert !r.isPasswordValid(null);
        assert !r.isPasswordValid("");
    }
}
