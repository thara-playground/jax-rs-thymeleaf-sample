/*
* Copyright 2011 Tomochika Hara.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.tomochika1985.sample;

import java.util.ResourceBundle;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.bootstrap.GenericBootstrap;

import org.hibernate.validator.engine.ConfigurationImpl;
import org.hibernate.validator.engine.ResourceBundleMessageInterpolator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.tomochika1985.core.jaxrs.ThymeleafTemplateWriter;

import com.google.inject.AbstractModule;

/**
 * @author t_hara
 *
 */
public class BootstrapModule extends AbstractModule {

	@Override
	protected void configure() {
		configureCoreModules();
	}

	void configureCoreModules() {
		
		// for validation
		GenericBootstrap defaultProvider = Validation.byDefaultProvider();
		ConfigurationImpl configuration = (ConfigurationImpl) defaultProvider.configure();
		ResourceBundle validationMessages = ResourceBundle.getBundle("ValidationMessages");
		MessageInterpolator messageInterpolator = new ResourceBundleMessageInterpolator(validationMessages);
		configuration.messageInterpolator(messageInterpolator);
		
		ValidatorFactory validatorFactory = configuration.buildValidatorFactory();
		Validator validator = validatorFactory.getValidator();
		bind(Validator.class).toInstance(validator);
		
		// for JAX-RS
		String encoding = "UTF-8";
		
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
		templateResolver.setTemplateMode(TemplateMode.XHTML);
		templateResolver.setCharacterEncoding(encoding);
		templateResolver.setCacheable(false);
		
		templateResolver.setPrefix("/");
//		templateResolver.setPrefix("/WEB-INF/templates/");
//		templateResolver.setSuffix(".html");
		
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		
		bind(ThymeleafTemplateWriter.class).toInstance(new ThymeleafTemplateWriter(templateEngine, encoding));
	}
}
