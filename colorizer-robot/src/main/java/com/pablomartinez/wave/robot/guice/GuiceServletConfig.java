package com.pablomartinez.wave.robot.guice;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.pablomartinez.wave.robot.ColorizerRobot;
import com.pablomartinez.wave.robot.controller.ColorizerRobotController;
import com.pablomartinez.wave.robot.controller.ColorizerRobotControllerServlet;

public class GuiceServletConfig extends GuiceServletContextListener {

	private static final Logger LOG = Logger.getLogger(GuiceServletConfig.class
			.getName());

	private static final String PROP_FILE_NAME = "robots.properties";

	private static final String PROP_KEY_TOKEN = "token";
	private static final String PROP_KEY_SECRET = "secret";
	private static final String PROP_KEY_NAME = "name";
	private static final String PROP_KEY_CLASS = "class";
	private static final String PROP_KEY_SERVLET_CTX = "servletcontext";

	private static final String PROP_KEY_DOMAIN = "domain";
	private static final String PROP_KEY_RPC_URL = "rpc";

	protected Properties properties;

	@Override
	protected Injector getInjector() {

		/*
		 * Loading config properties for the robot
		 */

		try {

			properties = new Properties();

			String propertyFileName = System.getProperty("file.config");

			LOG.log(Level.INFO, "Loading properties from file "
					+ propertyFileName);

			if (propertyFileName == null) {
				// Get properties from Class Loader resource
				propertyFileName = PROP_FILE_NAME;
				Class<? extends GuiceServletConfig> c = this.getClass();
				LOG.log(Level.INFO, "Got class");
				InputStream is = c.getClassLoader().getResourceAsStream(propertyFileName);
				if (is == null) {
					LOG.log(Level.SEVERE, "Could not create input stream: " + propertyFileName);
				} else {
					LOG.log(Level.INFO, "Got input stream");
				}
				if (properties == null) {
					LOG.log(Level.SEVERE, "Could not create properties");
				}
				properties.load(is);
				LOG.log(Level.INFO, "Loaded properties");
			} else {
				// Get properties from external resource
				properties.load(new FileReader(propertyFileName));
			}

		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, "Error loading properties file", e);

		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Error loading properties file", e);

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Error loading properties file", e);

		}

		ServletModule servletModule = new ServletModule() {

			@SuppressWarnings("unchecked")
			@Override
			protected void configureServlets() {
				Map<String, String> initParams = new HashMap<String, String>();
				String _servletctx_ = null;

				try {

					initParams.put(ColorizerRobot.INIT_PARAM_DOMAIN,
							properties.getProperty(PROP_KEY_DOMAIN));
					LOG.log(Level.INFO, ColorizerRobot.INIT_PARAM_DOMAIN + ": " + properties.getProperty(PROP_KEY_DOMAIN));
					initParams.put(ColorizerRobot.INIT_PARAM_RPC,
							properties.getProperty(PROP_KEY_RPC_URL));
					LOG.log(Level.INFO, ColorizerRobot.INIT_PARAM_RPC + ": " + properties.getProperty(PROP_KEY_RPC_URL));
					initParams.put(ColorizerRobot.INIT_PARAM_NAME,
							properties.getProperty(PROP_KEY_NAME));
					LOG.log(Level.INFO, ColorizerRobot.INIT_PARAM_NAME + ": " + properties.getProperty(PROP_KEY_NAME));
					initParams.put(ColorizerRobot.INIT_PARAM_TOKEN,
							properties.getProperty(PROP_KEY_TOKEN));
					LOG.log(Level.INFO, ColorizerRobot.INIT_PARAM_TOKEN + ": " + properties.getProperty(PROP_KEY_TOKEN));
					initParams.put(ColorizerRobot.INIT_PARAM_SECRET,
							properties.getProperty(PROP_KEY_SECRET));
					LOG.log(Level.INFO, ColorizerRobot.INIT_PARAM_SECRET + ": " + properties.getProperty(PROP_KEY_SECRET));

					_servletctx_ = properties.getProperty(PROP_KEY_SERVLET_CTX);
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Error initing parameters", e);
				}	

				try {

					serveRegex("\\/" + _servletctx_ + "/_wave/.*").with(
							(Class<ColorizerRobot>) Class.forName(properties
									.getProperty(PROP_KEY_CLASS)), initParams);
					LOG.log(Level.SEVERE, "Serving: \\/" + _servletctx_ + "/_wave/.*");

				} catch (ClassNotFoundException e) {

					LOG.log(Level.SEVERE, "Error loading class for Robot", e);
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Error getting property", e);

				}
			}
		};

		// Controller --------------------------------------------------

		AbstractModule controllerModule = new ServletModule() {

			@Override
			protected void configureServlets() {
				LOG.info("Configuring servlets");
				bind(ColorizerRobotController.class).to(
						ColorizerRobotControllerServlet.class);
				serve("/controller").with(ColorizerRobotControllerServlet.class);

			}
		};

		Injector injector = Guice.createInjector(servletModule,
				controllerModule);

		return injector;

	}
}
