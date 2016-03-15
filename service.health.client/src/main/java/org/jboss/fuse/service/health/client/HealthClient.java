package org.jboss.fuse.service.health.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.jboss.fuse.service.health.HealthService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v26.datatype.ST;
import ca.uhn.hl7v2.model.v26.message.ADR_A19;
import ca.uhn.hl7v2.model.v26.message.QRY_A19;
import ca.uhn.hl7v2.model.v26.segment.MSH;
import ca.uhn.hl7v2.model.v26.segment.PID;
import ca.uhn.hl7v2.model.v26.segment.QRD;

public class HealthClient implements BundleActivator {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);

	private int msgId = 1;

	private int qryId = 1;

	@Override
	public void start(BundleContext context) throws Exception {

		System.out.println("healthserviceclient::start begin");

		QRY_A19 qryA19 = new QRY_A19();

		// Will modify qryA19
		createMessageHeaderSegment(qryA19.getMSH());

		qryA19.getMSH().getMessageType().getMessageCode().setValue("QRY");
		qryA19.getMSH().getMessageType().getTriggerEvent().setValue("A19");

		// Will modify qryA19
		createMessageQueryDefinition(qryA19.getQRD());

		final String lastName = "lastName";
		final String firstName = "firstName";
		final String identifier = "identifier";
		final String assigningFacility = "assigningFacility";

		int piIndex = 0;

		createWhoSubjectFilterForSearch(qryA19.getQRD(), piIndex, lastName, firstName, identifier, assigningFacility);
		
		System.out.println(qryA19.printStructure());

		HealthService healthService = getHealthService(context);
		ADR_A19 adrA19 = healthService.processA19Request(qryA19);
		
		System.out.println(adrA19.printStructure());

		ST st = adrA19.getMSH().getMsh1_FieldSeparator();
		System.out.println("MSH1_FieldSeparator: " + st.getValue());
		System.out.println("First Name: " + parseFirstName(adrA19.getQUERY_RESPONSE().getPID()));
		System.out.println("Last Name: " + parseLastName(adrA19.getQUERY_RESPONSE().getPID()));
		System.out.println("External ID: " + parseExternalId(adrA19.getQUERY_RESPONSE().getPID()));
		System.out.println("Mother Maiden Last: " + parseMotherMaidenLastName(adrA19.getQUERY_RESPONSE().getPID()));

		System.out.println("healthserviceclient::start end");

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("healthserviceclient::stop begin");
		System.out.println("healthserviceclient::stop end");

	}

	private HealthService getHealthService(BundleContext context) {

		ServiceReference<?> ref = context.getServiceReference(HealthService.class.getName());
		return (HealthService) context.getService(ref);

	}

	private void createMessageHeaderSegment(MSH msh) {

		String formatted = String.format("%05d", msgId++);

		try {

			msh.getFieldSeparator().setValue("|");
			msh.getEncodingCharacters().setValue("^~\\&");
			msh.getSendingApplication().getNamespaceID().setValue("TEST-APP");
			msh.getSendingFacility().getNamespaceID().setValue("FUSE");
			msh.getReceivingApplication().getNamespaceID().setValue("RECEIVE-APP");
			msh.getReceivingFacility().getNamespaceID().setValue("RECEIVE-FAC");
			msh.getDateTimeOfMessage().setValue(sdf.format(Calendar.getInstance().getTime()));
			msh.getMessageControlID().setValue("T" + formatted);
			msh.getProcessingID().getProcessingID().setValue("P");
			msh.getVersionID().getVersionID().setValue("2.6");
			msh.getSequenceNumber().setValue("1");
			msh.getCountryCode().setValue("US");
			msh.getCharacterSet(0).setValue("ASCII");
			msh.getPrincipalLanguageOfMessage().getIdentifier().setValue("ENG");
			msh.getPrincipalLanguageOfMessage().getText().setValue("English");

		} catch (DataTypeException e) {
			// TODO
			e.printStackTrace();
		}

	}

	private void createMessageQueryDefinition(QRD qrd) {

		String formatted = String.format("%05d", qryId++);

		try {

			qrd.getQueryDateTime().setValue(sdf.format(Calendar.getInstance().getTime()));
			qrd.getQueryFormatCode().setValue("R");
			qrd.getQueryPriority().setValue("I");
			qrd.getQueryID().setValue("Q" + formatted);
			qrd.getQuantityLimitedRequest().getQuantity().setValue("1");
			qrd.getQuantityLimitedRequest().getUnits().getIdentifier().setValue("R");

		} catch (DataTypeException e) {
			// TODO
			e.printStackTrace();
		}

	}

	private void createWhoSubjectFilterForSearch(QRD qrd, int index, String lastName, String firstName,
			String identifier, String assigningFacility) {

		try {
			qrd.getWhoSubjectFilter(index).getFamilyName().getSurname().setValue(lastName);
			qrd.getWhoSubjectFilter(index).getGivenName().setValue(firstName);
			qrd.getWhoSubjectFilter(index).getIDNumber().setValue(identifier);
			qrd.getWhoSubjectFilter(index).getAssigningFacility().getNamespaceID().setValue(assigningFacility);
		} catch (DataTypeException e) {
			// TODO
			e.printStackTrace();
		}

	}

	private String parseFirstName(PID pid) {
		return pid.getPid5_PatientName(0).getGivenName().getValue();
	}
	
	private String parseLastName(PID pid) {
		return pid.getPid5_PatientName(0).getFamilyName().getSurname().getValue();
	}

	private String parseExternalId(PID pid) {
		return pid.getPid2_PatientID().getIDNumber().getValue();
	}

	private String parseMotherMaidenLastName(PID pid) {
		return pid.getPid6_MotherSMaidenName(0).getFamilyName().getSurname().getValue();
	}

}