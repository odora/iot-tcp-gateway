package com.mtoliv.iot.config;

import com.mtoliv.iot.server.api.TcpReader;
import com.mtoliv.iot.server.api.TcpWirter;
import com.mtoliv.iot.server.message.ReaderIdleEvent;
import com.mtoliv.iot.server.message.ResponseMessage;
import com.mtoliv.iot.server.message.WriterIdleEvent;
import com.mtoliv.iot.server.session.Session;
import com.mtoliv.iot.server.session.SessionManager;
import com.mtoliv.iot.server.session.listener.LogSessionListener;
import com.mtoliv.iot.server.session.listener.SessionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

	@Resource
	private Environment env;

	@Bean
	public SessionManager sessionManager() {
		SessionManager sessionManager = new SessionManager() ;
		List<SessionListener> listeners = new ArrayList<>() ;
		listeners.add(new LogSessionListener()) ;
		sessionManager.setSessionListeners(listeners);
		return sessionManager ;
	}

	@Bean
	public TcpWirter tcpWirter() {
		return new TcpWirter() ;
	}

	@Bean
	public TcpReader tcpReader() {
		return new TcpReader() {
			@Override
			public Optional<Object> readerCallback(Session session, Object msg) {
				System.out.println("Got it:" + msg) ;
				//return Optional.empty();
				if ( msg instanceof ReaderIdleEvent || msg instanceof WriterIdleEvent) {
				    // 应该构造一个PING响应发向设备。这里简单处理成不发送任何响应
					return Optional.empty() ;
				} else {
					ResponseMessage responseMessage = new ResponseMessage();
					responseMessage.setIntValue(200);
					return Optional.of(responseMessage);
				}
			}
		} ;
	}
}
