# HL7 Test Server

The following is description of each module:

1. features.hl7 : A maven module that contains a Karaf Feature file to make deployments easier. The four features are
  - service-health : deployment of only the Health Service
  - service-health-client : an example client of the Health Service. It also is a basic test of the service.
  - gateway-hl7 : deployment of the HL7 Test Server.
  - gateway-hl7-client : an example client of the HL7 Test Server. It also is a basic test of the server.
  - gateway.hl7.client : A maven module that uses Blueprint to construct a Camel Context with a route to read HL7 payloads from the filesystem and send to the gateway-hl7. Of note, this client can be replaced with HAPI test panel.
2. gateway.hl7.server : A maven module that uses Blueprint to construct a Camel Context with routes to synchronously send and receive HL7 payloads.
3. service.health : A maven module that contains the Health Service Interface. Other modules depend only on this interface, and not the implementation. In addition there are no framework dependencies, allowing for highest amount of re-usability for this business function.
4. service.health.impl : A maven module that implements the service.health interface.
5. service.health.client : A maven module that interacts with the Health Service to test its function.

## Installation

To install the server:
`features:addurl mvn:org.jboss.fuse/hl7.features/0.0.1-SNAPSHOT/xml/features`
`features:install gateway-hl7`

To install the test clients:
`features:install gateway-hl7-client`
`features:install service-health-client`

The gateway-hl7-client expects that a HL7 payload is placed on the filesystem at the following location:
`${JBOSS_FUSE_HOME}/test-data/rawhl7`

and an example payload is located at https://github.com/joelicious/hl7-test-server/blob/master/gateway.hl7.client/src/test/resources/test-data/QRY_A19.hl7
