package eu.operando.interfaces.rapi.configuration;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class Initializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	 
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { };
    }
  
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { SwaggerConfiguration.class};
    }
  
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/*" };
    }
    
    
    
}
