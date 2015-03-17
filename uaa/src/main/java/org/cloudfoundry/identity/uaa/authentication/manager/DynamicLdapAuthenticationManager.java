package org.cloudfoundry.identity.uaa.authentication.manager;

import org.cloudfoundry.identity.uaa.config.EnvironmentPropertiesFactoryBean;
import org.cloudfoundry.identity.uaa.ldap.LdapIdentityProviderDefinition;
import org.cloudfoundry.identity.uaa.scim.ScimGroupExternalMembershipManager;
import org.cloudfoundry.identity.uaa.scim.ScimGroupProvisioning;
import org.cloudfoundry.identity.uaa.user.UaaUserDatabase;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.nio.file.ProviderNotFoundException;

public class DynamicLdapAuthenticationManager implements AuthenticationManager {
    private final LdapIdentityProviderDefinition definition;
    private AuthenticationManager manager = null;
    private ClassPathXmlApplicationContext context = null;
    private ScimGroupExternalMembershipManager scimGroupExternalMembershipManager;
    private ScimGroupProvisioning scimGroupProvisioning;
    private UaaUserDatabase userDatabase;

    public DynamicLdapAuthenticationManager(LdapIdentityProviderDefinition definition,
                                            ScimGroupExternalMembershipManager scimGroupExternalMembershipManager,
                                            ScimGroupProvisioning scimGroupProvisioning,
                                            UaaUserDatabase userDatabase) {
        this.definition = definition;
        this.scimGroupExternalMembershipManager = scimGroupExternalMembershipManager;
        this.scimGroupProvisioning = scimGroupProvisioning;
        this.userDatabase = userDatabase;
    }

    public synchronized AuthenticationManager getLdapAuthenticationManager() throws BeansException {
        if (definition==null) {
            return null;
        }
        if (context==null) {
            ConfigurableEnvironment environment = definition.getLdapConfigurationEnvironment();
            //create parent BeanFactory to inject singletons from the parent
            DefaultListableBeanFactory parentBeanFactory = new DefaultListableBeanFactory();
            parentBeanFactory.registerSingleton("externalGroupMembershipManager", scimGroupExternalMembershipManager);
            parentBeanFactory.registerSingleton("scimGroupProvisioning", scimGroupProvisioning);
            parentBeanFactory.registerSingleton("userDatabase", userDatabase);
            GenericApplicationContext parent = new GenericApplicationContext(parentBeanFactory);
            parent.refresh();

            //create the context that holds LDAP
            context = new ClassPathXmlApplicationContext(new String[] {"ldap-integration.xml"}, false, parent);
            context.setEnvironment(environment);
            EnvironmentPropertiesFactoryBean factoryBean = new EnvironmentPropertiesFactoryBean();
            factoryBean.setEnvironment(environment);
            PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
            placeholderConfigurer.setProperties(factoryBean.getObject());
            context.addBeanFactoryPostProcessor(placeholderConfigurer);
            context.refresh();
            manager = (AuthenticationManager)context.getBean("ldapAuthenticationManager");
        }
        return manager;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AuthenticationManager manager = getLdapAuthenticationManager();
        if (manager!=null) {
            return manager.authenticate(authentication);
        }
        throw new ProviderNotFoundException("LDAP provider not configured");
    }

    public void destroy() {
        ClassPathXmlApplicationContext applicationContext = context;
        if (applicationContext != null) {
            context = null;
            applicationContext.destroy();
        }
    }

}
