package org.example.plastinka2.services;

import freemarker.template.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailForm;

    private final Template confirmEmailTemplate;

     MailServiceImpl() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);//обьект фримаркера
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateLoader(
                new SpringTemplateLoader(new ClassRelativeResourceLoader(this.getClass()), "/")
        );
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        try {
            this.confirmEmailTemplate = configuration.getTemplate("templates/confirm_mail.ftlh");
        } catch (TemplateNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void sendEmailForConfirm(String email, String code) {
        String mailText = getEmailText(code);
        MimeMessagePreparator messagePreparator = getEmail(email, mailText);
        javaMailSender.send(messagePreparator);
    }

    private String getEmailText(String code) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("confirm_code", code);

        StringWriter writer = new StringWriter();

        try {
            confirmEmailTemplate.process(attributes, writer);
        } catch (TemplateException | IOException e) {
            throw new IllegalArgumentException(e);
        }
        return writer.toString();
    }



    private MimeMessagePreparator getEmail(String email, String mailText) { //заполнение полей письма
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom(mailForm);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("Регистрация");
            mimeMessageHelper.setText(mailText, true);
        };
        return messagePreparator;
    }
}
