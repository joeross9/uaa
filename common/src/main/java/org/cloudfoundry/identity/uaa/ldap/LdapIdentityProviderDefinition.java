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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.security.authentication.AuthenticationManager;

public class LdapIdentityProviderDefinition {

    private final String baseUrl;
    private final String sslCertificate;
    private final String bindUserDn;
    private final String bindPassword;
    private final String userSearchBase;
    private final String userSearchFilter;
    private final String groupSearchBase;
    private final String groupSearchFilter;

    @JsonCreator
    public LdapIdentityProviderDefinition(@JsonProperty("baseUrl") String baseUrl,
                                          @JsonProperty("sslCertificate") String sslCertificate,
                                          @JsonProperty("bindUserDn") String bindUserDn,
                                          @JsonProperty("bindPassword") String bindPassword,
                                          @JsonProperty("userSearchBase") String userSearchBase,
                                          @JsonProperty("userSearchFilter") String userSearchFilter,
                                          @JsonProperty("groupSearchBase") String groupSearchBase,
                                          @JsonProperty("groupSearchFilter") String groupSearchFilter) {

        this.baseUrl = baseUrl;
        this.sslCertificate = sslCertificate;
        this.bindUserDn = bindUserDn;
        this.bindPassword = bindPassword;
        this.userSearchBase = userSearchBase;
        this.userSearchFilter = userSearchFilter;
        this.groupSearchBase = groupSearchBase;
        this.groupSearchFilter = groupSearchFilter;
    }

    public AuthenticationManager getAuthenticationManager() {
        return null;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getSslCertificate() {
        return sslCertificate;
    }

    public String getBindUserDn() {
        return bindUserDn;
    }

    public String getBindPassword() {
        return bindPassword;
    }

    public String getUserSearchBase() {
        return userSearchBase;
    }

    public String getUserSearchFilter() {
        return userSearchFilter;
    }

    public String getGroupSearchBase() {
        return groupSearchBase;
    }

    public String getGroupSearchFilter() {
        return groupSearchFilter;
    }
}
