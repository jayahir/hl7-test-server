package org.jboss.fuse.gateway.hl7.server.builders;

import org.apache.camel.builder.RouteBuilder;

public class A19RequestRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("{{a19.request.handler}}")
			.id("gw.hl7.a19")
			.log("Processing A19 Request")
			.processRef("a19Processor");

	}

}
