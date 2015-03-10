package org.cloudfoundry.identity.oauth2showcase;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.security.oauth2.sso.EnableOAuth2Sso;
import org.springframework.cloud.security.oauth2.sso.OAuth2SsoConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@Controller
@EnableOAuth2Sso
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping("/")
    public String index(HttpServletRequest request, Model model) {
        return "index";
    }

    @Value("${idServiceUrl}")
    private String idServiceUrl;

    @Autowired
    private OAuth2RestTemplate oauth2RestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping("/authorization_code")
    public String authCode(Model model) throws Exception {
        String response = oauth2RestTemplate.getForObject("{idServiceUrl}/userinfo", String.class,
                idServiceUrl);
        model.addAttribute("idServiceUrl",idServiceUrl);
        model.addAttribute("response",toPrettyJsonString(response));
        Map<String, ?> token = getToken(oauth2RestTemplate.getOAuth2ClientContext());
        model.addAttribute("token",toPrettyJsonString(token));
        return "authorization_code";
    }

    private Map<String, ?> getToken(OAuth2ClientContext clientContext) throws Exception {
        if (clientContext.getAccessToken() != null) {
            String tokenBase64 = clientContext.getAccessToken().getValue().split("\\.")[1];
            return objectMapper.readValue(Base64.decodeBase64(tokenBase64), new TypeReference<Map<String, ?>>() {
            });
        }
        return null;
    }

    private String toPrettyJsonString(String json) throws Exception {
        Object object = objectMapper.readValue(json, Object.class);
        return toPrettyJsonString(object);
    }
    
    private String toPrettyJsonString(Object object) throws Exception {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}