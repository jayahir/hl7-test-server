package org.jboss.fuse.gateway.hl7.server.builders;

import org.apache.camel.builder.RouteBuilder;

public class UnhandledRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("{{unhandled.request.handler}}")
		.id("gw.hl7.unhandled")
		.log("Processing A19 Request")
		.processRef("unsupportedProcessor");

	}

}
