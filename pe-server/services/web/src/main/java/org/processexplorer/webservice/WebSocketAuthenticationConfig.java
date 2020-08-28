package org.processexplorer.webservice;

import org.processexplorer.webservice.config.JwtTokenUtil;
import org.processexplorer.webservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * @author Alexander Seeliger on 28.08.2020.
 */
@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthenticationConfig.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> authorization = accessor.getNativeHeader("X-Authorization");

                    if (authorization == null || authorization.isEmpty()) {
                        return message;
                    }

                    var jwtToken = authorization.get(0);
                    String username = null;

                    if (jwtToken.startsWith("Bearer ")) {
                        jwtToken = jwtToken.substring(7);
                        try {
                            username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                        } catch (Exception ex) {
                        }
                    }

                    if (username != null) {
                        // check validity
                        UserDetails userDetails = userService.loadUserByUsername(username);
                        // if token is valid configure Spring Security to manually set
                        // authentication
                        if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                            var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                            accessor.setUser(usernamePasswordAuthenticationToken);
                        }
                    }
                }
                return message;
            }
        });
    }
}