package org.jboss.fuse.gateway.hl7.server.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jboss.fuse.service.health.HealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.model.v26.message.ACK;

public class UnsupportedProcessor implements Processor {

	private Logger log = LoggerFactory.getLogger(A19Processor.class);

	private HealthService healthService;

	public void process(Exchange exchange) throws Exception {

		log.info("UnsupportedProcessor::start");

		ACK ack = healthService.processUnsupported();

		log.info(ack.printStructure());

		exchange.getIn().setBody(ack);

		log.info("UnsupportedProcessor::end");

	}

	public void setHealthService(HealthService healthService) {
		this.healthService = healthService;
	}
}
