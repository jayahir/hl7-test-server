package org.jboss.fuse.gateway.hl7.server.builders;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.HL7DataFormat;

public class HL7SyncReceiveRouteBuilder extends RouteBuilder {

	private HL7DataFormat hl7 = new HL7DataFormat();

	@Override
	public void configure() throws Exception {

		from("netty4:tcp://{{server.receive.tcp.ip}}:{{server.receive.tcp.port}}?sync=true&encoder=#gw.hl7encoder&decoder=#gw.hl7decoder")
			.id("gw.hl7.syncReceiveRoute")
			.log("== HL7 Service Request: ${body}")
			.unmarshal(hl7)
			.choice()
				.when(header("CamelHL7TriggerEvent").isEqualTo("A19"))
					.to("{{a19.request.handler}}")
				.otherwise()
					.to("{{unhandled.request.handler}}")
			.end()
			.marshal(hl7)
			.log("== Service Response: ${body}");

	}
}
