/*******************************************************************************
 *     Cloud Foundry
 *     Copyright (c) [2009-2015] Pivotal Software, Inc. All Rights Reserved.
 *
 *     This product is licensed to you under the Apache License, Version 2.0 (the "License").
 *     You may not use this product except in compliance with the License.
 *
 *     This product includes a number of subcomponents with
 *     separate copyright notices and license terms. Your use of these
 *     subcomponents is subject to the terms and conditions of the
 *     subcomponent's license, as noted in the LICENSE file.
 *******************************************************************************/
package org.cloudfoundry.identity.uaa.ldap;

import com.unboundid.scim.data.Manager;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;

import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;

public class LdapIdentityProviderDefinitionTest {

    private LdapIdentityProviderDefinition ldapIdentityProviderDefinition;
    private final String SSL_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n" +
        "MIIBfTCCAScCBgFDfaC2yzANBgkqhkiG9w0BAQUFADBCMQswCQYDVQQGEwJVUzEMMAoGA1UEChMD\n" +
        "QVNGMRIwEAYDVQQLEwlEaXJlY3RvcnkxETAPBgNVBAMTCEFwYWNoZURTMB4XDTE0MDExMDE5Mjg0\n" +
        "MVoXDTE1MDExMDE5Mjg0MVowTDELMAkGA1UEBhMCVVMxDDAKBgNVBAoTA0FTRjESMBAGA1UECxMJ\n" +
        "RGlyZWN0b3J5MRswGQYDVQQDExJmaGFuaWstd29ya3N0YXRpb24wXDANBgkqhkiG9w0BAQEFAANL\n" +
        "ADBIAkEAuA6Nmto6NFCCJ+CwsBnT2cvMxuYgf26iZ3ckIpLhs2V4ZJ4PFinR6JZUsVnRp0RbYoV5\n" +
        "iW6F91XDTVtAMtDTJwIDAQABMA0GCSqGSIb3DQEBBQUAA0EATFGpEIprKYcnc+JuNcSQ8v2P2J7e\n" +
        "lQ23NhTaljASF0g8AZ7SZEItU8JFYqf/KnNJ7FPwo4LbMbr7Zg6BRKBvnQ==\n" +
        "-----END CERTIFICATE-----";

    @Before
    public void setUp() throws Exception {
        ldapIdentityProviderDefinition = new LdapIdentityProviderDefinition("ldap://localhost:389/",
                                                                            SSL_CERTIFICATE,
                                                                            "cn=admin,ou=Users,dc=test,dc=com",
                                                                            "adminsecret",
                                                                            "dc=test,dc=com",
                                                                            "cn={0}",
                                                                            "ou=scopes,dc=test,dc=com",
                                                                            "member={0}");
    }

    @Test
    public void testGetAuthenticationManager() throws Exception {
        ProviderManager authenticationManager = (ProviderManager) ldapIdentityProviderDefinition.getAuthenticationManager();
        List<AuthenticationProvider> providers = authenticationManager.getProviders();
        assertEquals(1, providers.size());
        LdapAuthenticationProvider provider = (LdapAuthenticationProvider) providers.get(0);

    }
}
