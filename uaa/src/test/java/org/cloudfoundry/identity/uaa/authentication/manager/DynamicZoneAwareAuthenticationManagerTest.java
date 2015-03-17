package org.cloudfoundry.identity.uaa.authentication.manager;

import org.cloudfoundry.identity.uaa.authentication.Origin;
import org.cloudfoundry.identity.uaa.authentication.UaaPrincipal;
import org.cloudfoundry.identity.uaa.ldap.LdapIdentityProviderDefinition;
import org.cloudfoundry.identity.uaa.util.JsonUtils;
import org.cloudfoundry.identity.uaa.zone.IdentityProvider;
import org.cloudfoundry.identity.uaa.zone.IdentityProviderProvisioning;
import org.cloudfoundry.identity.uaa.zone.IdentityZoneHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DynamicZoneAwareAuthenticationManagerTest {

    DynamicZoneAwareAuthenticationManager manager;
    IdentityProviderProvisioning providerProvisioning = mock(IdentityProviderProvisioning.class);
    LdapIdentityProviderDefinition ldapIdentityProviderDefinition = LdapIdentityProviderDefinition.searchAndBindMapGroupToScopes(
        "ldap://localhost:389/",
        "cn=admin,ou=Users,dc=test,dc=com",
        "adminsecret",
        "dc=test,dc=com",
        "cn={0}",
        "ou=scopes,dc=test,dc=com",
        "member={0}",
        "mail",
        null,
        false,
        true,
        true,
        100);


    @Before
    @After
    public void clearIdentityZoneHolder() throws Exception {
        manager = new DynamicZoneAwareAuthenticationManager(providerProvisioning);
        IdentityZoneHolder.clear();
    }

    @Test
    public void testAuthenticate() throws Exception {
        IdentityProvider provider = new IdentityProvider();
        provider.setOriginKey(Origin.LDAP);
        provider.setActive(true);
        provider.setConfig(JsonUtils.writeValueAsString(ldapIdentityProviderDefinition));
        provider.setName(Origin.LDAP);
        provider.setIdentityZoneId(IdentityZoneHolder.get().getId());
        provider.setId("ldap");

        when(providerProvisioning.retrieveByOrigin(Origin.LDAP, IdentityZoneHolder.get().getId())).thenReturn(provider);
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("marissa");
        when(auth.getCredentials()).thenReturn("koala");
        Authentication result = manager.authenticate(auth);
        assertNotNull(result);
        assertTrue(result.getPrincipal() instanceof UaaPrincipal);
        assertEquals("marissa", ((UaaPrincipal)result.getPrincipal()).getName());
    }



}