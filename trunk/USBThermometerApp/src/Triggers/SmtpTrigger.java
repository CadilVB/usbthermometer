/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Triggers;

import Visual.Triggers.SmtpTriggerConfigurationPanel;
import Triggers.SmtpTrigger.*;
import javax.swing.JPanel;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author pawelkn
 */
public class SmtpTrigger extends Trigger {

    private SmtpTriggerConfigurationPanel c;

    private String serverName = "smtp.provider.com";
    private int serverPort = 465;
    private String senderEmail = "sender@address.com";
    private String accountName = "login";
    private String password = "password";
    private String receiverEmail = "receiver@address.com";
    private String title;
    private String message;

    public SmtpTrigger() {
        super();

        setName("SMTP Trigger");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String msgOnEnter) {
        this.message = msgOnEnter;
        c.setMessage(message);
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
        c.setReceiverEmail(receiverEmail);
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
        c.setSenderEmail(senderEmail);
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
        c.setServerName(serverName);
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
        c.setServerPort(serverPort);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titleOnEnter) {
        this.title = titleOnEnter;
        c.setTitle(title);
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
        c.setAccountName(accountName);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        c.setPassword(password);
    }

    @Override
    public void onChange() {
    }

    @Override
    public void onEnter() {
        new Thread(new Runnable() {
            public void run() {
                Properties props = new Properties();
                props.put("mail.smtp.host", serverName);
                props.put("mail.smtp.socketFactory.port", Integer.toString(serverPort));
                props.put("mail.smtp.socketFactory.class",
                        "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", Integer.toString(serverPort));

                Session session = Session.getDefaultInstance(props,
                        new javax.mail.Authenticator() {

                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(accountName, password);
                            }
                        });

                try {
                    Message msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress(senderEmail));
                    msg.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(receiverEmail));
                    msg.setSubject(title);
                    msg.setText(message);

                    Transport.send(msg);

                    System.out.println("Email send");

                } catch (MessagingException ex) {
                    Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
                }
            }
        }).start();
    }

    @Override
    public void onExit() {
    }

    @Override
    public void updateConfiguration() {
        accountName = ((SmtpTriggerConfigurationPanel)c).getAccountName();
        message = ((SmtpTriggerConfigurationPanel)c).getMessage();
        password = ((SmtpTriggerConfigurationPanel)c).getPassword();
        receiverEmail = ((SmtpTriggerConfigurationPanel)c).getReceiverEmail();
        senderEmail = ((SmtpTriggerConfigurationPanel)c).getSenderEmail();
        serverName = ((SmtpTriggerConfigurationPanel)c).getServerName();
        serverPort = ((SmtpTriggerConfigurationPanel)c).getServerPort();
        title = ((SmtpTriggerConfigurationPanel)c).getTitle();
    }

    @Override
    public synchronized JPanel getConfigurationPanel() {
        c = new SmtpTriggerConfigurationPanel(this);
        c.setAccountName(accountName);
        c.setMessage(message);
        c.setPassword(password);
        c.setReceiverEmail(receiverEmail);
        c.setSenderEmail(senderEmail);
        c.setServerName(serverName);
        c.setServerPort(serverPort);
        c.setTitle(title);
        return c;
    }
}
