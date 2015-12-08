package cucumber.runtime.java.spring;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableMap;

@Configuration
public class CucumberSpringConfig {
    
    public static final String GLUE_SCOPE_NAME = GlueCodeScope.NAME;

    @Bean
    public GlueCodeScope getGlueCodeScope() {
        return new GlueCodeScope();
    }

    @Bean(name = "glueCodeScopeConfigurer")
    public CustomScopeConfigurer getGlueCodeScopeConfigurer() {
        CustomScopeConfigurer scopeConfigurer = new CustomScopeConfigurer();
        scopeConfigurer.setScopes(ImmutableMap.of(GLUE_SCOPE_NAME, (Object) getGlueCodeScope()));
        return scopeConfigurer;
    }

}
