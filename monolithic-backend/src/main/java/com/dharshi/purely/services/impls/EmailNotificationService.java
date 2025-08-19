package com.dharshi.purely.services.impls;

import com.dharshi.purely.modals.*;
import com.dharshi.purely.repositories.ProductRepository;
import com.dharshi.purely.services.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Set;

@Component
public class EmailNotificationService implements NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void sendUserRegistrationVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = fromMail;
        String senderName = "Purely";
        String subject = "Please verify your registration";
        String content = "Dear " + user.getUsername() + ",<br><br>"
                + "<p>Thank you for joining us! We are glad to have you on board.</p><br>"
                + "<p>To complete the sign up process, enter the verification code in your device.</p><br>"
                + "<p>verification code: <strong>" + user.getVerificationCode() + "</strong></p><br>"
                + "<p><strong>Please note that the above verification code will be expired within 15 minutes.</strong></p>"
                + "<br>Thank you,<br>"
                + "Purely.";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);

        javaMailSender.send(message);
    }


    public void sendForgotPasswordVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = fromMail;
        String senderName = "Purely";
        String subject = "Forgot password - Please verify your Account";
        String content = "Dear " + user.getUsername() + ",<br><br>"
                + "<p>To change your password, enter the verification code in your device.</p><br>"
                + "<p>verification code: <strong>" + user.getVerificationCode() + "</strong></p><br>"
                + "<p><strong>Please note that the above verification code will be expired within 15 minutes.</strong></p>"
                + "<br>Thank you,<br>"
                + "Purely.";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);

        javaMailSender.send(message);
    }

    @Override
    public void sendOrderConfirmationEmail(User user, Order order) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = fromMail;
        String senderName = "Purely";
        String subject = "Purely - Order confirmation";

        StringBuilder contentBuilder = new StringBuilder("Dear " + user.getUsername() + ",<br><br>"
                + "<h2>Thank you for your order!</h2>"
                + "<p>Your order #" + order.getId() + " has been successfully placed!</p>"
                + "<h3>Order summary</h3>");
        for(CartItem orderItem: order.getOrderItems()) {

            Product product = productRepository.findById(orderItem.getProduct().getId()).orElse(null);
            String description = product.getProductName() + ": " + orderItem.getQuantity() + " x " + product.getPrice() + "<br>";
            contentBuilder.append(description);
        }

        String content = contentBuilder.toString();

        content += "<h4>Total: " + order.getOrderAmt() + "</h4>"
                + "<p>Delivery charges be will added to your total at your doorstep!</p>"
                + "<br>Thank you,<br>"
                + "Purely.";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);

        javaMailSender.send(message);
    }

}
