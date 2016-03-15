package org.jboss.fuse.service.health.impl;

import java.io.IOException;
import java.util.Random;

import org.jboss.fuse.service.health.HealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v26.message.ACK;
import ca.uhn.hl7v2.model.v26.message.ADR_A19;
import ca.uhn.hl7v2.model.v26.message.QRY_A19;
import ca.uhn.hl7v2.model.v26.segment.MSA;
import ca.uhn.hl7v2.model.v26.segment.MSH;
import ca.uhn.hl7v2.model.v26.segment.QRD;

public class HealthServiceImpl implements HealthService {

	private Logger log = LoggerFactory.getLogger(HealthServiceImpl.class);

	private int _MIN = 10000;
	private int _MAX = 9999999;

	private int _YEAR_MIN = 1929;
	private int _YEAR_MAX = 1999;

	private int _MONTH_MIN = 1;
	private int _MONTH_MAX = 12;

	private int _DAY_MIN = 1;
	private int _DAY_MAX = 28;

	private int _4_MIN = 1000;
	private int _4_MAX = 9999;

	private int _3_MIN = 100;
	private int _3_MAX = 999;

	private Random random = new Random();

	@Override
	public ACK processUnsupported() {
		ACK ack = new ACK();
		return ack;
	}

	@Override
	public ADR_A19 processA19Request(QRY_A19 a19Request) {

		ADR_A19 returnable = new ADR_A19();

		if (null == a19Request) {

			log.warn("A19Request is null");

		} else if (null == a19Request.getMSH()) {

			log.warn("A19Request MSH is null");

		} else {

			try {

				processMSH(a19Request.getMSH(), returnable);
				processMSA(a19Request.getMSH(), returnable.getMSA());
				processQRD(a19Request.getQRD(), returnable.getQRD());
				processQueryResponse(a19Request, returnable);

			} catch (HL7Exception exception) {
				// TODO
				// Roll HealthService Exceptions
				log.warn("HL7Exception");
			}
		}

		return returnable;
	}

	private void processMSH(MSH requestMSH, ADR_A19 response) throws HL7Exception {

		try {
			// Setting Values in the MSH segment to standard default values.
			response.initQuickstart("ADR", "A19", "P");
		} catch (IOException ex) {
			throw new HL7Exception(ex);
		}

		// Set receiving application/facility to request MSH
		response.getMSH().getReceivingApplication().parse(requestMSH.getSendingApplication().encode());
		response.getMSH().getReceivingFacility().parse(requestMSH.getSendingApplication().encode());

		// Set sending application to inbound
		response.getMSH().getSendingApplication().parse(requestMSH.getReceivingApplication().encode());
		response.getMSH().getSendingFacility().parse(requestMSH.getSendingFacility().encode());

	}

	private void processMSA(MSH requestMSH, MSA responseMSA) throws DataTypeException {

		responseMSA.getAcknowledgmentCode().setValue("AA");
		responseMSA.getMessageControlID().setValue(requestMSH.getMessageControlID().getValue());

	}

	private void processQRD(QRD requestQRD, QRD responseQRD) throws DataTypeException {

		responseQRD.getQrd1_QueryDateTime().setValue(requestQRD.getQrd1_QueryDateTime().getValue());
		responseQRD.getQrd2_QueryFormatCode().setValue(requestQRD.getQrd2_QueryFormatCode().getValue());
		responseQRD.getQrd3_QueryPriority().setValue(requestQRD.getQrd3_QueryPriority().getValue());
		responseQRD.getQrd4_QueryID().setValue(requestQRD.getQrd4_QueryID().getValue());
		responseQRD.getQrd5_DeferredResponseType().setValue(requestQRD.getQrd5_DeferredResponseType().getValue());
		responseQRD.getQrd6_DeferredResponseDateTime()
				.setValue(requestQRD.getQrd6_DeferredResponseDateTime().getValue());
		responseQRD.getQrd7_QuantityLimitedRequest().getQuantity()
				.setValue(requestQRD.getQrd7_QuantityLimitedRequest().getQuantity().getValue());
		responseQRD.getQrd7_QuantityLimitedRequest().getUnits().getIdentifier()
				.setValue(requestQRD.getQrd7_QuantityLimitedRequest().getUnits().getIdentifier().getValue());

		int whoSize = requestQRD.getQrd8_WhoSubjectFilterReps();

		for (int i = 0; i < whoSize; i++) {

			responseQRD.getQrd8_WhoSubjectFilter(i).getFamilyName().getSurname()
					.setValue(requestQRD.getQrd8_WhoSubjectFilter(i).getFamilyName().getSurname().getValue());

			responseQRD.getQrd8_WhoSubjectFilter(i).getGivenName()
					.setValue(requestQRD.getQrd8_WhoSubjectFilter(i).getGivenName().getValue());

			responseQRD.getQrd8_WhoSubjectFilter(i).getIDNumber()
					.setValue(requestQRD.getQrd8_WhoSubjectFilter(i).getIDNumber().getValue());

			responseQRD.getQrd8_WhoSubjectFilter(i).getAssigningAuthority().getNamespaceID().setValue(
					requestQRD.getQrd8_WhoSubjectFilter(i).getAssigningAuthority().getNamespaceID().getValue());

		}

		int whatSize = responseQRD.getQrd9_WhatSubjectFilterReps();

		for (int j = 0; j < whatSize; j++) {

			responseQRD.getQrd9_WhatSubjectFilter(j).getIdentifier()
					.setValue(requestQRD.getQrd9_WhatSubjectFilter(j).getIdentifier().getValue());

		}

		int whatDeptSize = responseQRD.getQrd10_WhatDepartmentDataCodeReps();

		for (int k = 0; k < whatDeptSize; k++) {

			responseQRD.getQrd10_WhatDepartmentDataCode(k).getIdentifier()
					.setValue(requestQRD.getQrd10_WhatDepartmentDataCode(k).getIdentifier().getValue());
		}

	}

	private void processQueryResponse(QRY_A19 request, ADR_A19 response) throws DataTypeException {

		int whoSize = request.getQRD().getQrd8_WhoSubjectFilterReps();

		// Need to construct a CX per each patient
		for (int i = 0; i < whoSize; i++) {

			// Set the PID (SI)
			response.getQUERY_RESPONSE(0).getPID().getSetIDPID().setValue(String.valueOf(i));

			response.getQUERY_RESPONSE(0).getPID().getPatientIdentifierList(i).getIDNumber()
					.setValue(request.getQRD().getQrd8_WhoSubjectFilter(i).getIDNumber().getValue());

			response.getQUERY_RESPONSE(0).getPID().getPatientIdentifierList(i).getAssigningFacility().getNamespaceID()
					.setValue(request.getQRD().getQrd8_WhoSubjectFilter(i).getAssigningFacility().getNamespaceID()
							.getValue());

			// Pull Surname out of Query
			response.getQUERY_RESPONSE(0).getPID().getPid5_PatientName(i).getFamilyName().getSurname()
					.setValue(request.getQRD().getQrd8_WhoSubjectFilter(0).getFamilyName().getSurname().getValue());

			// Pull Given name out of Query
			response.getQUERY_RESPONSE(0).getPID().getPid5_PatientName(i).getGivenName()
					.setValue(request.getQRD().getQrd8_WhoSubjectFilter(0).getGivenName().getValue());

			// Randomize Sex
			response.getQUERY_RESPONSE(0).getPID().getAdministrativeSex().setValue(randomSex());

			// External Identifier
			response.getQUERY_RESPONSE(0).getPID().getPid2_PatientID().getIDNumber()
					.setValue(String.valueOf(randomInt()));

			// Randomize Birth Date
			response.getQUERY_RESPONSE(0).getPID().getPid7_DateTimeOfBirth().setDatePrecision(randomYear(),
					randomMonth(), randomDay());

			// Primary Language
			response.getQUERY_RESPONSE(0).getPID().getPid15_PrimaryLanguage().getText().setValue("ENG");

			// Address
			response.getQUERY_RESPONSE(0).getPID().getPid11_PatientAddress(0).getStreetAddress()
					.getStreetOrMailingAddress().setValue(randomThreeNumber() + " Main Street");

			response.getQUERY_RESPONSE(0).getPID().getPid11_PatientAddress(0).getOtherDesignation().setValue("");

			response.getQUERY_RESPONSE(0).getPID().getPid11_PatientAddress(0).getCity().setValue("AnyCity");

			response.getQUERY_RESPONSE(0).getPID().getPid11_PatientAddress(0).getStateOrProvince()
					.setValue("AnyStOrProv");

			response.getQUERY_RESPONSE(0).getPID().getPid11_PatientAddress(0).getZipOrPostalCode().setValue("11111");

			// Phone Number
			response.getQUERY_RESPONSE(0).getPID().getPid13_PhoneNumberHome(0).getXtn5_CountryCode().setValue("1");
			response.getQUERY_RESPONSE(0).getPID().getPid13_PhoneNumberHome(0).getXtn6_AreaCityCode()
					.setValue(String.valueOf(randomThreeNumber()));
			response.getQUERY_RESPONSE(0).getPID().getPid13_PhoneNumberHome(0).getXtn7_LocalNumber()
					.setValue(String.valueOf(randomThreeNumber()) + String.valueOf(randomFourNumber()));

			// Mother's Name
			response.getQUERY_RESPONSE(0).getPID().getPid6_MotherSMaidenName(0).getFamilyName().getSurname()
					.setValue("MotherLastName");

			response.getQUERY_RESPONSE(0).getPID().getPid6_MotherSMaidenName(0).getGivenName()
					.setValue("MotherFirstName");

		}

	}

	private int randomInt() {
		return random.nextInt((_MAX - _MIN) + 1) + _MIN;
	}

	private int randomYear() {
		return random.nextInt((_YEAR_MAX - _YEAR_MIN) + 1) + _YEAR_MIN;
	}

	private int randomMonth() {
		return random.nextInt((_MONTH_MAX - _MONTH_MIN) + 1) + _MONTH_MIN;
	}

	private int randomDay() {
		return random.nextInt((_DAY_MAX - _DAY_MIN) + 1) + _DAY_MIN;
	}

	private int randomThreeNumber() {
		return random.nextInt((_3_MAX - _3_MIN) + 1) + _3_MIN;
	}

	private int randomFourNumber() {
		return random.nextInt((_4_MAX - _4_MIN) + 1) + _4_MIN;
	}

	private String randomSex() {

		if (Math.random() > 0.5) {
			return "F";
		} else {
			return "M";
		}

	}
}
