package com.github.fwi.sbhx;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

	final SbhxProperties sbhxProperties;
	final ServerModel serverModel;
	final SpringTemplateEngine templateEngine;

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@GetMapping("/servers")
	@ResponseBody
	public String servers(@RequestParam(value = "refresh", required = false) boolean refresh) {
		
		var servers = refresh ? serverModel.refresh() : serverModel.get();
		sleep();
		var context = new Context();
		context.setVariable("servers", servers);
		var html = templateEngine.process("servers", context);
		if (refresh) {
			// Reset the search-input to empty by adding it as a "hx-swap-oob" element.
			html += getIndexHtml("search-servers-input");
			// Reset the notifications-area by adding it as a "hx-swap-oob" element.
			html += getIndexHtml("notifications");
		}
		log.trace("Reponse after server refresh:\n{}", html);
		return html;
	}

	String getIndexHtml(String fragment) {
		// this is similar to returning "index :: fragment-name"
		return "\n" + templateEngine.process("index", Set.of(fragment), new Context());
	}

	@PostMapping(path = "/servers", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String searchServers(@RequestParam MultiValueMap<String, String> paramMap, Model model) {
		model.addAttribute("servers", serverModel.search(paramMap.getFirst("search")));
		sleep();
		return "servers";
	}

	final DateTimeFormatter REFRESH_TIME_FORMAT = DateTimeFormatter
		.ofPattern("dd/MM/yy HH:mm:ss zzz")
		.withZone(ZoneId.systemDefault());

	@PostMapping("/servers/refresh/{project}")
	@ResponseBody
	public String refreshServer(@PathVariable @NotNull String project) {
		
		var server = serverModel.get().stream().filter(s -> s.getProject().equals(project)).findAny()
			.orElseThrow(() -> new RuntimeException(project + " server not found"));
		var refreshTime = REFRESH_TIME_FORMAT.format(Instant.now());
		log.info("Updating server {} - {}", project, refreshTime);
		sleep();
		String html = StringUtils.EMPTY;
		if ("broken".equals(project)) { // Just for testing
			html = "Failed";
			html += getNotifHtml("Server " + project + " could not be refreshed.", false);
		} else {
			server.setRefreshTime(refreshTime);
			html = refreshTime;
			html += getNotifHtml("Server " + project + " refreshed at " + refreshTime, true);
		}
		log.debug("Refresh server response html:\n{}", html);
		return html;
		/*
		 * Note: table rows combined with hx-swap-oob is bugged in HTMX, see https://github.com/bigskysoftware/htmx/issues/1043
		 */
	}

	/**
	 * An "hx-swap-oob" for the notification div.
	 */
	String getNotifHtml(String message, boolean valid) {
		final var notifHtml = """
			\n<div id="notifications" hx-swap-oob="true">
				<input  type="text" placeholder="%s" aria-invalid="%b" readonly/>
			</div>
				""";
		return notifHtml.formatted(message, !valid);
	}

	void sleep() {
		if (sbhxProperties.getResponseDelay() != null) {
			try {
				Thread.sleep(sbhxProperties.getResponseDelay().toMillis());
			} catch (Exception ignored) {}
		}
	}

}
