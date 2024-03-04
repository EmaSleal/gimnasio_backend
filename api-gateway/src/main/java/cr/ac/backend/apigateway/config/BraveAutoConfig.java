package cr.ac.backend.apigateway.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.brave.ZipkinSpanHandler;

/*@AutoConfiguration(before = MicrometerTracingAutoConfiguration.class)
@ConditionalOnClass({ Tracer.class, BraveTracer.class})
@EnableConfigurationProperties(TracingProperties.class)
@ConditionalOnEnabledTracing*/
@Configuration(proxyBeanMethods = false)

//ZipkinSpanHandler
@ConditionalOnClass(ZipkinSpanHandler.class)
public class BraveAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Reporter.class)
    ZipkinSpanHandler zipkinSpanHandler(Reporter<zipkin2.Span> reporter) {
        return (ZipkinSpanHandler) ZipkinSpanHandler.newBuilder(reporter).build();
    }
}
