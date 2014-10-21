package de.codecentric.jbehave.admin.reporters;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.NullStoryReporter;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * A special StoryReporter which is sending E-Mails with the Report to the author of the story.
 */
public class EmailStoryReporter extends NullStoryReporter {

	private static final String AUTHOR = "author";

	private static final Log LOGGER = LogFactory.getLog(EmailStoryReporter.class);

	private String reportPath;
	private String reportBaseUrl;

	private JavaMailSender mailSender;
	private String from;

	private Story currentStory;
	private boolean storyFailed = false;

	public EmailStoryReporter(String from, String reportPath, String reportBaseUrl, JavaMailSender mailSender) {
		this.from = from;
		this.reportPath = reportPath;
		this.reportBaseUrl = reportBaseUrl;
		this.mailSender = mailSender;
	}

	@Override
	public void beforeStory(Story story, boolean givenStory) {
		this.currentStory = story;
		storyFailed = false;
	}

	@Override
	public void afterStory(boolean givenStory) {
		String author = currentStory.getMeta().getProperty(AUTHOR);
		if (storyFailed && author != null) {
			author = author.replaceAll("<at>", "@");
			LOGGER.warn("Story \"" + currentStory.getPath() + "\" failed. E-Mail will be send to " + author);
			sendMail(author, "Story \"" + currentStory.getPath() + "\" failed.", currentStory.getName());
		}
	}

	@Override
	public void failed(String step, Throwable cause) {
		LOGGER.warn("Step \"" + step + "\" in Story \"" + currentStory.getPath() + "\" failed.");
		storyFailed = true;
	}

	private void sendMail(String to, String subject, String story) {
		if (mailSender != null && StringUtils.isNotEmpty(to)) {
			MimeMessage mail = mailSender.createMimeMessage();
			MimeMessageHelper helper;
			try {
				helper = new MimeMessageHelper(mail, true);
				helper.setTo(to);
				helper.setFrom(from);
				helper.setCc(new String[] {});
				helper.setBcc(new String[] {});
				helper.setSubject(subject);
				helper.setText(reportBaseUrl + "/#/stories/" + story);
				helper.addAttachment(story + ".html", new File(reportPath + story + ".html"));
				mailSender.send(mail);
			} catch (MessagingException e) {
				LOGGER.error("MimeMessage could not be created!", e);
			} catch (MailSendException e) {
				LOGGER.error("E-Mail could not be send!", e);
			}
		} else {
			LOGGER.info("No SMTP Server or TO-Adress available!");
		}
	}

}
