package com.duke.bookproject.book.service;

import com.duke.bookproject.book.model.HighlightReminder;
import com.duke.bookproject.book.model.KindleHighLight;
import com.duke.bookproject.constant.EmailConstant;
import com.duke.bookproject.template.EmailTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DailyReminderScheduler {
    private final HighlightReminderService highlightReminderService;
    private final KindleHighLightService highlightService;
    private final EmailService emailService;

    public DailyReminderScheduler(HighlightReminderService highlightReminderService,
            KindleHighLightService highlightService, EmailService emailService) {
        this.highlightReminderService = highlightReminderService;
        this.highlightService = highlightService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyReminders() throws MessagingException {
        List<HighlightReminder> reminders = highlightReminderService.findRemindersDueToday();

        if (reminders.isEmpty()) {
            return;
        }

        Map<String, List<HighlightReminder>> remindersByUser = reminders.stream()
                .collect(Collectors.groupingBy(HighlightReminder::getUserEmail));

        for (Map.Entry<String, List<HighlightReminder>> entry : remindersByUser.entrySet()) {
            String userEmail = entry.getKey();
            List<HighlightReminder> userReminders = entry.getValue();

            List<KindleHighLight> highlights = new ArrayList<>();

            for (HighlightReminder reminder : userReminders) {
                Optional<KindleHighLight> highlight = highlightService.findById(reminder.getHighlightId());
                if (highlight.isPresent()) {
                    highlights.add(highlight.get());
                }
            }

            if (!highlights.isEmpty()) {
                String username = emailService.getUsernameFromEmail(userEmail);

                List<Map<String, String>> highlightMaps = new ArrayList<>();
                for (KindleHighLight highlight : highlights) {
                    Map<String, String> highlightMap = new HashMap<>();
                    highlightMap.put("title", highlight.getTitle());
                    highlightMap.put("author", highlight.getAuthor());
                    highlightMap.put("content", highlight.getContent());
                    highlightMaps.add(highlightMap);
                }

                Map<String, Object> emailTemplate = EmailTemplate.getKindleHighlightsEmailTemplate(
                        username,
                        highlightMaps);

                emailService.sendMessageUsingThymeleafTemplate(
                        userEmail,
                        EmailConstant.KINDLE_HIGHLIGHTS_SUBJECT,
                        emailTemplate);
            }

            for (HighlightReminder reminder : userReminders) {
                highlightReminderService.updateNextReminderDate(reminder);
            }
        }
    }
}