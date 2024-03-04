package cr.ac.backend.authentication.config;


import brave.Tracer;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracing;
import org.springframework.boot.actuate.autoconfigure.tracing.MicrometerTracingAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.tracing.TracingProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
