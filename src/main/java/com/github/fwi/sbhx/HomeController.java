package com.github.fwi.sbhx;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.NotNull;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

	final SbhxProperties sbhxProperties;
	final ServerModel serverModel;

	@GetMapping("/")
	public String home() {
		return "index"; 
	}

	@GetMapping("/servers")
	public String servers(@RequestParam(value = "refresh", required = false) boolean refresh, Model model) {
		model.addAttribute("servers", refresh ? serverModel.refresh() : serverModel.get());
		sleep();
		return "servers"; 
	}

	@PostMapping(path = "/servers", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String searchServers(@RequestParam MultiValueMap<String, String> paramMap, Model model) {
		model.addAttribute("servers", serverModel.search(paramMap.getFirst("search")));
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
			html = getNotifHtml("Server " + project + " could not be refreshed.", false);
			html += getRefreshTimeHtml(project, "Failed");
		} else {
		server.setRefreshTime(refreshTime);
			html = getNotifHtml("Server " + project + " refreshed at " + refreshTime, true);
			html += getRefreshTimeHtml(project, refreshTime);
		}
		log.debug("Refresh server response html:\n{}", html);
		return html;
		/*
		 * Note: table rows combined with hx-swap-oob is bugged in HTMX, see https://github.com/bigskysoftware/htmx/issues/1043
		 * For reference, programmatic fragment parsing can be done by injecting
		 *   	SpringTemplateEngine templateEngine;
		 * and then 
		 * 		Context context = new Context();
		 * 		context.setVariable("server", server);
		 * 		templateEngine.process("servers", Set.of("server-fresh-time"), context);
		 * This is similar to:
		 * 		return "servers.html :: server-fresh-time"; 
		 */
	}

	String getNotifHtml(String message, boolean valid) {
		final var notifHtml = """
			<input id="notifications" hx-swap-oob="true" type="text" placeholder="%s" aria-invalid="%b" readonly/>
				""";
		return notifHtml.formatted(message, !valid);
	}
	
	String getRefreshTimeHtml(String project, String time) {
		final var timeHtml = """
			<span id="refresh-server-time-%s">%s</span>
				""";
		return timeHtml.formatted(project, time);
	}

	void sleep() {

		if (sbhxProperties.getResponseDelay() != null) {
			try {
				Thread.sleep(sbhxProperties.getResponseDelay().toMillis());
			} catch (Exception ignored) {}
		}
	}

}
