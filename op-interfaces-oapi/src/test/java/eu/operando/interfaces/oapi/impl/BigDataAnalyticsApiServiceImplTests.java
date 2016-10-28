package eu.operando.interfaces.oapi.impl;

import org.junit.Test;
import org.mockito.Mockito;

import eu.operando.moduleclients.ClientAuthenticationApiOperandoService;
import eu.operando.moduleclients.ClientBigDataAnalytics;

public class BigDataAnalyticsApiServiceImplTests
{
	private static final String SERVICE_ID_GET_BDA_REPORT = "GET /osp/bda/jobs/{job_id}/reports";
	ClientAuthenticationApiOperandoService mockClientAuthenticationService = Mockito.mock(ClientAuthenticationApiOperandoService.class);
	ClientBigDataAnalytics mockClientBigDataAnalytics = Mockito.mock(ClientBigDataAnalytics.class);
	
	BigDataAnalyticsApiServiceImpl implementation = new BigDataAnalyticsApiServiceImpl(mockClientAuthenticationService, mockClientBigDataAnalytics);
	
	@Test
	public void testGetBdaReport_ServiceTicketChecked()
	{
		implementation.getBdaReport(null, null, null);
		
		//Mockito.verify(mockClientAuthenticationService).isOspAuthenticatedForRequestedService(serviceTicket, SERVICE_ID_GET_BDA_REPORT);
	}
}
