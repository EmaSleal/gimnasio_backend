package cr.ac.backend.authentication.service.impl;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class EmailService {

    @Value("${sender.key}")
    private String key;

    private Resend resend = new Resend("re_X7qY3NFp_ETffUyjtLJpgTMcrzdhvdB4c");


    public Date sendForgotPasswordEmail(String to, String token) {

        String html = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Recuperación de Contraseña</title>" +
                "<meta charset=\"utf-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<style type=\"text/css\">" +
                "body {" +
                "font-family: Arial, sans-serif;" +
                "font-size: 16px;" +
                "line-height: 1.5;" +
                "margin: 0;" +
                "padding: 0;" +
                "background-color: #f2f2f2;" +
                "color: #333;" +
                "}" +
                ".container {" +
                "width: 100%;" +
                "max-width: 600px;" +
                "margin: 0 auto;" +
                "padding: 20px;" +
                "background-color: #fff;" +
                "box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);" +
                "}" +
                "h1 {" +
                "font-size: 24px;" +
                "margin-bottom: 20px;" +
                "color: #333;" +
                "text-align: center;" +
                "}" +
                "p {" +
                "margin-bottom: 10px;" +
                "color: #333;" +
                "}" +
                ".button__container {" +
                "text-align: center;" +
                "}" +
                ".button {" +
                "display: inline-block;" +
                "padding: 10px 20px;" +
                "background-color: #f44336;" +
                "color: #fff;" +
                "margin: 0 auto;" +
                "text-decoration: none;" +
                "text-align: center;" +
                "border-radius: 4px;" +
                "margin-bottom: 20px;" +
                "}" +
                ".button:hover {" +
                "background-color: #e53935;" +
                "}" +
                ".footer {" +
                "background-color: #333;" +
                "color: #fff;" +
                "padding: 20px;" +
                "margin-top: 40px;" +
                "font-size: 14px;" +
                "text-align: center;" +
                "}" +
                ".footer__texto {" +
                "margin: 0;" +
                "color: #fff;" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "" +
                "<div class=\"container\">" +
                "<h1>Recuperación de Contraseña</h1>" +
                "<p>Hola,</p>" +
                "<p>Hemos recibido una solicitud de recuperación de contraseña para tu cuenta. Si no has realizado esta solicitud, puedes ignorar este correo electrónico.</p>" +
                "<p>Si deseas continuar con la recuperación de contraseña, haz clic en el botón a continuación:</p>" +
                "<div class=\"button__container\">" +
                "<a href=\"http://localhost:9395/forgot-password/email?token=" + token + "\" class=\"button\">Recuperar Contraseña</a>" +
                "</div>" +
                "<p>Este enlace caducará en 24 horas.</p>" +
                "<p>Gracias,</p>" +
                "<p>El equipo de soporte</p>" +
                "</div>" +
                "" +
                "<div class=\"footer\">" +
                "<p class=\"footer__texto\">Este correo electrónico es generado automáticamente. Por favor, no respondas a este mensaje.</p>" +
                "</div>" +
                "" +
                "</body>" +
                "</html>";
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .from("emanuel.soto.leal@est.una.ac.cr")
                .to(to)
                .subject("Recuperación de Contraseña")
                .html(html)
                .build();
        try {
            resend.emails().send(sendEmailRequest);
        } catch (ResendException e) {
            e.printStackTrace();
        }
        return new Date();
    }
}
