package org.jboss.fuse.gateway.hl7.server.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jboss.fuse.service.health.HealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.model.v26.message.ADR_A19;
import ca.uhn.hl7v2.model.v26.message.QRY_A19;

public class A19Processor implements Processor {

	private Logger log = LoggerFactory.getLogger(A19Processor.class);

	private HealthService healthService;

	public void process(Exchange exchange) throws Exception {

		log.info("A19Processor::start");

		ADR_A19 adr_a19 = healthService.processA19Request(exchange.getIn().getBody(QRY_A19.class));

		log.info(adr_a19.printStructure());

		exchange.getIn().setBody(adr_a19);

		log.info("A19Processor::end");

	}

	public void setHealthService(HealthService healthService) {
		this.healthService = healthService;
	}

}