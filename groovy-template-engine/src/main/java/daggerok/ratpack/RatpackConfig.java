package daggerok.ratpack;

import daggerok.SpringBootRatpackApplication;
import daggerok.user.User;
import daggerok.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ratpack.exec.Blocking;
import ratpack.groovy.template.MarkupTemplateModule;
import ratpack.handling.Handler;
import ratpack.spring.config.EnableRatpack;

import java.util.HashMap;

import static java.util.stream.Collectors.toList;
import static ratpack.groovy.Groovy.groovyMarkupTemplate;

@EnableRatpack
@Configuration
@ComponentScan(basePackageClasses = SpringBootRatpackApplication.class)
public class RatpackConfig {
/*
  @Bean public Handler handler() {
      return context -> context.render("Hello World");
  }
*/
  @Bean // requires to make groovyMarkupTemplate works
  MarkupTemplateModule markupTemplate() {
    return new MarkupTemplateModule();
  }

  @Bean
  Handler handler(final UserRepository userRepository) {
    //return context -> context.render("index.html");
    //val model = new HashMap<String, Object>();
    return context -> {
      Blocking.get(() -> {
        final HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("message", "This is a groovy template engine from ratpack!");
        model.put("users", userRepository.findAll()
                                         .stream()
                                         .map(User::toString)
                                         .collect(toList()));
        return model;
      }).then(model -> context.render(
          groovyMarkupTemplate(model, "index.tpl", "text/html;charset=UTF-8")
      ));
    };
  }
}
