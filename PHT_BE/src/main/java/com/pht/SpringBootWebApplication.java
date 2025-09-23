package com.pht;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={
		"com.pht.**"})
@MapperScan(basePackages = {"com.pht.**.mybatis" })
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
public class SpringBootWebApplication extends SpringBootServletInitializer {
	public static final Logger logger = LoggerFactory.getLogger(SpringBootWebApplication.class);

	public static void main(String[] args) {
		logger.info("START API");
		try {
			disableSSLValidation();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SpringApplication.run(SpringBootWebApplication.class, args);
	}

	@Bean
	public static Boolean disableSSLValidation() throws Exception {
		final SSLContext sslContext = SSLContext.getInstance("TLS");

		sslContext.init(null, new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		} }, null);

		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
		return true;
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootWebApplication.class);
	}
}
