/**
 * 
 */
package org.tomochika1985.core.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.Validate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.WriterException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateMode;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * @author t_hara
 *
 */
@Provider
@Produces(MediaType.TEXT_HTML)
public class ThymeleafTemplateWriter implements MessageBodyWriter<IContext> {

	private final TemplateEngine templateEngine;
	
	private String encoding = "UTF-8";
	
	public ThymeleafTemplateWriter() {
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
		templateResolver.setTemplateMode(TemplateMode.XHTML);
		templateResolver.setCharacterEncoding(encoding);
		templateResolver.setCacheable(false);
		
		templateResolver.setPrefix("/");
//		templateResolver.setPrefix("/WEB-INF/templates/");
//		templateResolver.setSuffix(".html");
		
		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
	}
	
	/**
	 * @param templateEngine
	 * @param encoding
	 */
	public ThymeleafTemplateWriter(TemplateEngine templateEngine,
			String encoding) {
		super();
		Validate.notNull(templateEngine);
		Validate.notEmpty(encoding);
		this.templateEngine = templateEngine;
		this.encoding = encoding;
	}
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return IContext.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(IContext t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	
	@Override
	public void writeTo(IContext t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		
		IWebContext context;
		
		if (t instanceof IWebContext) {
			context = (IWebContext) t;
		} else {
			HttpServletRequest request = getHttpServletRequest();
			context = new WebContext(request, request.getSession().getServletContext());
			context.getVariables().putAll(t.getVariables());
		}
		
		Template ann = this.lookupTemplate(annotations);
		if (ann == null) {
			throw new WriterException("@Template is required.");
		}
		
		try {
			String result = templateEngine.process(ann.value(), context);
			entityStream.write(result.getBytes(encoding));
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}
	
	Template lookupTemplate(Annotation[] annotations) {
		for (Annotation ann : annotations) {
			if (ann instanceof Template) {
				return (Template) ann;
			}
		}
		return null;
	}
	
	HttpServletRequest getHttpServletRequest() {
		return ResteasyProviderFactory.getContextData(HttpServletRequest.class);
	}
}
