package com.dragonguard.backend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author 김승진
 * @description 이메일 전송에 사용될 util 클래스
 */

@Component
@RequiredArgsConstructor
public class EmailSender {
    private static final String EMAIL_SUBJECT = "GitRank 조직 인증";
    private static final String EMAIL_CONTENT = "<html><head></head><body><div>다음 번호를 입력해주세요:\n<div><h1>%d</h1></body></html>";
    private final JavaMailSender javaMailSender;

    public void send(final String memberEmail, final int random) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        mimeMessageHelper.setTo(memberEmail);
        mimeMessageHelper.setSubject(EMAIL_SUBJECT);
        mimeMessageHelper.setText(getEmailText(random), Boolean.TRUE);
        javaMailSender.send(mimeMessage);
    }

    private String getEmailText(final int code) {
        return String.format(EMAIL_CONTENT, code);
    }
}
