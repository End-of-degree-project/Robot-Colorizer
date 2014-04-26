package com.pablomartinez.wave.robot.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.log.Log;
import org.waveprotocol.wave.model.id.InvalidIdException;
import org.waveprotocol.wave.model.id.ModernIdSerialiser;
import org.waveprotocol.wave.model.waveref.WaveRef;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Singleton;
import com.google.wave.api.Blip;
import com.google.wave.api.JsonRpcConstant.ParamsProperty;
import com.google.wave.api.JsonRpcResponse;
import com.google.wave.api.Wavelet;
import com.pablomartinez.wave.robot.ColorizerRobot;



@SuppressWarnings("serial")
@Singleton
public class ColorizerRobotControllerServlet extends HttpServlet implements ColorizerRobotController {

	private static final Logger LOG = Logger.getLogger(ColorizerRobotControllerServlet.class.getName());

  protected ColorizerRobot robot = null;

	public ColorizerRobotControllerServlet() {


	}



	  @Override
	  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	      throws ServletException, IOException {

		  LOG.log(Level.SEVERE, req.toString());

		String action = req.getParameter("action");


    if (action == null || !action.equals("create"))
		{
		      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		      resp.getWriter().print("Action is not valid!");
		      resp.getWriter().flush();
		      return;

		}

		if (action.equals("create")) {

			String user = req.getParameter("user");
			String title = req.getParameter("title");

			String waveRef = null;
		    try {

        waveRef = this.createWave(user, title);

		    } catch (Exception e) {

		    LOG.log(Level.SEVERE, "Failed to create wave", e);
		    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    resp.getWriter().print("Failed! " + e.getMessage());
		    resp.getWriter().flush();
		      return;
		    }

      resp.getWriter().print("Create Success! " + waveRef);
		}


  }


  protected String createWave(String user, String title) {


		Wavelet wavelet = robot.newWave(robot.WAVE_DOMAIN, ImmutableSet.of(user + "@" + robot.WAVE_DOMAIN));
		
		Log.info(user + "@" + robot.WAVE_DOMAIN);

		Blip rootBlip = wavelet.getRootBlip();
		rootBlip.append(title+"\n\n");
		String dateStr = (new Date()).toString();
		wavelet.setTitle("Wavelet: " + dateStr);
		WaveRef waveRef = null;

		try {
			List<JsonRpcResponse> responses = robot.submit(wavelet, robot.RPC_URL);

			for (JsonRpcResponse response : responses) {
				if (response != null) {

					Map<ParamsProperty, Object> data = response.getData();

					if (data.containsKey(ParamsProperty.WAVELET_ID)) {

						String waveletId = String.valueOf(data
								.get(ParamsProperty.WAVELET_ID));

						String waveId = String.valueOf(data
								.get(ParamsProperty.WAVE_ID));

						String blipId = String.valueOf(data
								.get(ParamsProperty.BLIP_ID));




						try {

							waveRef = WaveRef.of(ModernIdSerialiser.INSTANCE
									.deserialiseWaveId(waveId),
									ModernIdSerialiser.INSTANCE
											.deserialiseWaveletId(waveletId),
									blipId);

						} catch (InvalidIdException e) {
							LOG.log(Level.SEVERE,
									String.format(
											"The response contains invalid ids: waveId: %s, waveletId: %s, blipId: %s",
											waveId, waveletId, blipId), e);
							throw new RuntimeException(e);
						}
					}
				}
			}
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "", e);
		}


    return waveRef.toString();
	}





	public void register(ColorizerRobot robotInstance) {


		LOG.info("Controller has registered following robot: "+robotInstance.getServletName());
    this.robot = robotInstance;

	}


}
