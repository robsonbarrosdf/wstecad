package br.leg.camara.wstecad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

	@Bean
	public Docket detalheApi() {
 
		Docket docket = new Docket(DocumentationType.SWAGGER_2);
 
		docket
		.select()
		.apis(RequestHandlerSelectors.basePackage("br.gov.camara.wstecad"))
		.paths(PathSelectors.any())
		.build()
		.apiInfo(this.informacoesApi().build());
 
		return docket;
	}
 
	private ApiInfoBuilder informacoesApi() {
 
		ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder();
 
		apiInfoBuilder.title("Api-wstecad");
		apiInfoBuilder.description("Api para consulta de termos do TECAD (Tesauro da Câmara dos Deputados.");
		apiInfoBuilder.version("1.0");
		apiInfoBuilder.termsOfServiceUrl("");
		apiInfoBuilder.license("");
		apiInfoBuilder.licenseUrl("");
		apiInfoBuilder.contact(this.contato());
 
		return apiInfoBuilder;
 
	}
	private Contact contato() {
 
		return new Contact(
				"COLEG",
				"http://www.camara.leg.br", 
				"ditec.coleg@camara.leg.br");
	}
	
}
