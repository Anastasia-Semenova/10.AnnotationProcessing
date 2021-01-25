import com.google.auto.service.AutoService;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes(value = {"HtmlForm"})
public class HtmlProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_20);
        configuration.setDefaultEncoding("UTF-8");
        try {
            configuration.setTemplateLoader(new FileTemplateLoader(new File("src/main/resources")));

            Template template = null;

            template = configuration.getTemplate("freemarker_form.ftlh");

            // получить типы с аннотаций HtmlForm
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(HtmlForm.class);
            for (Element element : annotatedElements) {
                // получаем полный путь для генерации html
                String path = HtmlProcessor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                // User.class -> User.html
                path = path.substring(1) + element.getSimpleName().toString() + ".ftlh";
                Path out = Paths.get(path);
                Map<String, Object> attributes = new HashMap<>();
                HtmlForm annotation = element.getAnnotation(HtmlForm.class);
                attributes.put("action", annotation.action());
                attributes.put("method", annotation.method());


                Map<String, Object> inputs = new HashMap<>();
                Set<? extends Element> annotatedFields = roundEnv.getElementsAnnotatedWith(HtmlInput.class);
                for (Element elementFields : annotatedFields) {
                    HtmlInput input = elementFields.getAnnotation(HtmlInput.class);
                    inputs.put("type", input.type());
                    inputs.put("name", input.name());
                    inputs.put("placeholder", input.placeholder());
                }

                attributes.put("inputs", inputs);

                BufferedWriter writer = new BufferedWriter(new FileWriter(out.toFile()));

                template.process(attributes, writer);
                writer.close();
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }


        return true;
    }
    }

