package net.madz.test.annotations.processors;

import java.util.logging.Logger;

public abstract class UserCredentialInjector implements TemplateProcessor {

    private static Logger logger = Logger.getLogger(UserCredentialInjector.class.getName());

    public abstract String getUserName();

    public abstract String getPassword();

    @Override
    public String process(String template) {
        String content = template;
        logger.info("before processing: " + content);
        content = content.replaceAll("#\\{userName\\}", getUserName());
        content = content.replaceAll("#\\{password\\}", getPassword());
        logger.info("after processing: " + content);
        return content;
    }
}
