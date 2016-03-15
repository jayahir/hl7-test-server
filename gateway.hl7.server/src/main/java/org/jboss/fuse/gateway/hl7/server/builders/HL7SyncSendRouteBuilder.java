package org.jboss.fuse.gateway.hl7.server.builders;

import org.apache.camel.builder.RouteBuilder;

public class HL7SyncSendRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("file://test-data/fromHL7/rawhl7?move=processed")
				.id("gw.hl7.syncSendRoute")
				.description("Sync Send request")
				.convertBodyTo(String.class)
				.log("== Service Send Request: ${body}")
				.to("netty4:tcp://{{server.send.tcp.ip}}:{{server.send.tcp.port}}?sync=true&encoder=#gw.hl7encoder&decoder=#gw.hl7decoder")
				.log("== Service Response: ${body}")
				.to("file://test-data/fromHL7/response?fileName=adt-response-${date:now:yyyyMMdd-hhmmss}");

	}
}
