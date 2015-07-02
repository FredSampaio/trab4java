package br.usp.icmc.supermercado.sendmail;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import br.usp.icmc.supermercado.user.User;
import br.usp.icmc.supermercado.product.Product;
import br.usp.icmc.supermercado.requisitado.Requisitado;
 
public class SendMail
{
    private String to;
    private Requisitado requisitado;

    public SendMail(Requisitado requisitado)
    {
        this.requisitado = requisitado;
    }
        
    public void sendTo()
    {  
        final String username = "supermercadoonlinejava@gmail.com";
        final String password = "javaehamor";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
        new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return(new PasswordAuthentication(username, password));
            }
        });

        try
        {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("freddysampaio9@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(this.requisitado.getUsuario().getEmail()));
            message.setSubject("Supermercado Online");
            message.setText(
                "\nOla sr(a). " +
                requisitado.getUsuario().getNome() + 
                ", gostariamos de informar que o produto que voce" + 
                " deseja ja encontra-se disponivel!" + 
                "\n\nProduto: " + 
                requisitado.getProduto().getNome() +
                "\n\n\n\n\n\nObrigado,\nSupermercado Online" 
                );

            Transport.send(message);
        } 
        catch(Exception e) {}
    }         
}
