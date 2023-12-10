package com.github.fwi.sbhx;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerModel {

    /** Mathematical 'x' to signal unknown value. */
    static final String UNKNOWN_REFRESH_TIME = new String(Character.toChars(0x1D465));

    volatile List<ServerProperties> servers;

    List<ServerProperties> get() {
        return servers == null ? refresh() : servers;
    }

    List<ServerProperties> refresh() {
        
        log.info("Refreshing servers list.");
        servers = List.of("vlaai", "pannekoek", "pudding", "broken")
            .stream().map(p -> new ServerProperties(p)).toList();
        return servers;
    }

    List<ServerProperties> search(String search) {

        if (StringUtils.isEmpty(search)) {
            return servers;
        }
        var foundServers = servers.stream().filter(s -> s.contains(search)).toList();
        log.debug("Found {} servers for search [{}]", foundServers.size(), search);
        return foundServers;
    }

    @Data
    static class ServerProperties {

        public ServerProperties(String project) {
            this.project = project;
            this.url = "http://" + project + ".service.local";
            this.refreshTime = UNKNOWN_REFRESH_TIME;
        }

        String project;
        String url;
        String refreshTime;

        boolean contains(String search) {
            return StringUtils.containsIgnoreCase(project, search); // || StringUtils.containsIgnoreCase(url, search);
        }
    }

}
