package org.jboss.fuse.service.health;

import ca.uhn.hl7v2.model.v26.message.ACK;
import ca.uhn.hl7v2.model.v26.message.ADR_A19;
import ca.uhn.hl7v2.model.v26.message.QRY_A19;

public interface HealthService {

	public ADR_A19 processA19Request(QRY_A19 a19Request);

	public ACK processUnsupported();

}
