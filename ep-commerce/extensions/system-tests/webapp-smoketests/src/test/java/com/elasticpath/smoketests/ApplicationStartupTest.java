package com.elasticpath.smoketests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Test that the applications start up without error.
 */
public class ApplicationStartupTest {
	
	private static final int HTTP_STATUS_OK = 200;

	final Properties settings = new Properties();

	private JMXClient jmxClient;

	@Before
	public void before() throws Exception {
		settings.load(ApplicationStartupTest.class.getResourceAsStream("/test.properties"));
		jmxClient = new JMXClient(settings.getProperty("remote.jmx.port"));
	}

	/**
	 * Test search server.
	 *
	 * @throws com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException the failing http status code exception
	 * @throws java.net.MalformedURLException the malformed url exception
	 * @throws java.io.IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testSearchServer() throws FailingHttpStatusCodeException, IOException {

		final WebClient webClient = new WebClient();
		final Page page = webClient.getPage("http://localhost:"
				+ settings.getProperty("cargo.port") + settings.getProperty("search.context") + "/status");

		assertEquals(HTTP_STATUS_OK, page.getWebResponse().getStatusCode());
		assertTrue(page instanceof TextPage);

		webClient.closeAllWindows();
	}

	/**
	 * Test batch server.
	 *
	 * @throws com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException the failing http status code exception
	 * @throws java.net.MalformedURLException the malformed url exception
	 * @throws java.io.IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testBatchServer() throws FailingHttpStatusCodeException, IOException {
		final String QuartzInstanceMBeanName = "quartz:type=QuartzScheduler,name=BatchJmxScheduler,instanceId=NONE_CLUSTER";
		final WebClient webClient = new WebClient();
		final Page page = webClient.getPage("http://localhost:"
				+ settings.getProperty("cargo.port") + settings.getProperty("batch.context") + "/status");

		assertEquals(HTTP_STATUS_OK, page.getWebResponse().getStatusCode());
		assertTrue(page instanceof TextPage);
		assertNotNull("Quartz instance not found", jmxClient.getObjectInstance(QuartzInstanceMBeanName));

		webClient.closeAllWindows();
	}

	/**
	 * Test Commerce Manager.
	 *
	 * @throws com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException the failing http status code exception
	 * @throws java.net.MalformedURLException the malformed url exception
	 * @throws java.io.IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testCM() throws FailingHttpStatusCodeException, IOException {

		final WebClient webClient = new WebClient();

		final Page page = webClient.getPage("http://localhost:"
				+ settings.getProperty("cargo.port") + settings.getProperty("cm.context") + settings.getProperty("cm.status.url"));

		assertEquals(HTTP_STATUS_OK, page.getWebResponse().getStatusCode());
		assertTrue(page instanceof TextPage);

		webClient.closeAllWindows();
	}

	/**
	 * Test integration server.
	 *
	 * @throws FailingHttpStatusCodeException The failing http status code exception.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testIntegrationServer() throws FailingHttpStatusCodeException, IOException {

		final WebClient webClient = new WebClient();
		final Page page = webClient.getPage("http://localhost:"
				+ settings.getProperty("cargo.port") + settings.getProperty("integration.context") + "/status");

		assertEquals(HTTP_STATUS_OK, page.getWebResponse().getStatusCode());
		assertTrue(page instanceof TextPage);

		webClient.closeAllWindows();
	}

	@Test
	public void testActiveMQWebConsole() throws FailingHttpStatusCodeException, IOException {

		final WebClient webClient = new WebClient();
		webClient.setJavaScriptEnabled(false);

		final Page page = webClient.getPage("http://localhost:"
			+ settings.getProperty("cargo.port") + settings.getProperty("activemq.context"));

		assertEquals(HTTP_STATUS_OK, page.getWebResponse().getStatusCode());
		assertTrue(page instanceof HtmlPage);

		webClient.closeAllWindows();
	}

	/**
	 * Test cortex server.
	 *
	 * @throws com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException the failing http status code exception
	 * @throws java.net.MalformedURLException the malformed url exception
	 * @throws java.io.IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testCortexServer() throws FailingHttpStatusCodeException, IOException {
		final WebClient webClient = new WebClient();
		final Page page = webClient.getPage("http://localhost:"
				+ settings.getProperty("cargo.port") + settings.getProperty("cortex.context") + "/status");

		assertEquals(HTTP_STATUS_OK, page.getWebResponse().getStatusCode());
		assertTrue(page instanceof TextPage);

		webClient.closeAllWindows();
	}

}
