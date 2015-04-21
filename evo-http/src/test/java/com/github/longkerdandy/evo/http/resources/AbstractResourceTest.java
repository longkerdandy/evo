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
}
