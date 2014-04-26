package com.pablomartinez.wave.robot;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.wave.api.AbstractRobot;
import com.google.wave.api.Annotation;
import com.google.wave.api.Range;
import com.google.wave.api.event.AnnotatedTextChangedEvent;
import com.google.wave.api.event.BlipContributorsChangedEvent;
import com.google.wave.api.event.BlipSubmittedEvent;
import com.google.wave.api.event.DocumentChangedEvent;
import com.google.wave.api.event.Event;
import com.google.wave.api.event.FormButtonClickedEvent;
import com.google.wave.api.event.GadgetStateChangedEvent;
import com.google.wave.api.event.OperationErrorEvent;
import com.google.wave.api.event.WaveletBlipCreatedEvent;
import com.google.wave.api.event.WaveletBlipRemovedEvent;
import com.google.wave.api.event.WaveletCreatedEvent;
import com.google.wave.api.event.WaveletFetchedEvent;
import com.google.wave.api.event.WaveletParticipantsChangedEvent;
import com.google.wave.api.event.WaveletSelfAddedEvent;
import com.google.wave.api.event.WaveletSelfRemovedEvent;
import com.google.wave.api.event.WaveletTagsChangedEvent;
import com.google.wave.api.event.WaveletTitleChangedEvent;
import com.pablomartinez.wave.robot.controller.ColorizerRobotController;


@SuppressWarnings("serial")
@Singleton
public class ColorizerRobot extends AbstractRobot {

	public static final String INIT_PARAM_DOMAIN = "domain";
	public static final String INIT_PARAM_RPC = "rpc";

	public static final String INIT_PARAM_TOKEN = "token";
	public static final String INIT_PARAM_SECRET = "secret";
	public static final String INIT_PARAM_NAME = "name";

	private static final Logger LOG = Logger.getLogger(ColorizerRobot.class
			.getName());

	public String RPC_URL = "http://localhost:8888/robot/rpc";
	public String WAVE_DOMAIN = "localhost:8888";

	// Acquire at http://waveinabox.net/robot/register/create (or your own WIAB
	// server).
	public String CONSUMER_TOKEN = "other@localhost";
	public String CONSUMER_TOKEN_SECRET = "kumHqHuS9cbVkJDnlmVVVVWVW-J874WLftk87N6_uVTKQTkz"; // put
																								// here
																								// secret
	// provided by
	// registration

	public String ROBOT_NAME = "basicrobot";
	public String PROFILE_URL = null;

	protected ColorizerRobotController controller;

	@Inject
	public ColorizerRobot(ColorizerRobotController controller) {

		this.controller = controller;

	}

	@Override
	public void init() throws ServletException {

		this.WAVE_DOMAIN = this.getInitParameter(INIT_PARAM_DOMAIN);
		this.RPC_URL = this.getInitParameter(INIT_PARAM_RPC);
		this.CONSUMER_TOKEN = this.getInitParameter(INIT_PARAM_TOKEN);
		this.CONSUMER_TOKEN_SECRET = this.getInitParameter(INIT_PARAM_SECRET);
		this.ROBOT_NAME = this.getInitParameter(INIT_PARAM_NAME);

		setupOAuth(CONSUMER_TOKEN, CONSUMER_TOKEN_SECRET, RPC_URL);
		setAllowUnsignedRequests(true);

		controller.register(this);

		LOG.info("Robot servlet initilized: " + this.ROBOT_NAME + ", "
				+ this.CONSUMER_TOKEN);
	}

	@Override
	protected String getRobotName() {

		return ROBOT_NAME;
	}

	@Override
	protected String getRobotProfilePageUrl() {

		return PROFILE_URL;
	}

	protected void logEvent(Event event) {

		LOG.log(Level.INFO, "Received event of type = "
				+ event.getType().name() + " on wavelet = "
				+ event.getWavelet().getWaveletId().getId());

	}

	@Override
	public void onAnnotatedTextChanged(AnnotatedTextChangedEvent event) {
		
		log("----------------------------------------------------------------------------");
		try{
			for(Annotation a : event.getBlip().getAnnotations().asList()){
				log(a.toString());
//				Range range = a.getRange();
//				log("Range: " + range.getStart() + ", " + range.getEnd());
//				log(a.getValue());
//				log(a.toString());
			}
		}
		catch(Exception e){}
		log("----------------------------------------------------------------------------");

		

		// logEvent(event);
	}

	@Override
	public void onBlipContributorsChanged(BlipContributorsChangedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onBlipSubmitted(BlipSubmittedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onDocumentChanged(DocumentChangedEvent event) {
		
//		log("------------------------------" + event.getBundle().);
//		logEvent(event);
	}

	@Override
	public void onFormButtonClicked(FormButtonClickedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onGadgetStateChanged(GadgetStateChangedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onWaveletBlipCreated(WaveletBlipCreatedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onWaveletBlipRemoved(WaveletBlipRemovedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onWaveletCreated(WaveletCreatedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onWaveletFetched(WaveletFetchedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onWaveletParticipantsChanged(
			WaveletParticipantsChangedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onWaveletSelfRemoved(WaveletSelfRemovedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onWaveletTagsChanged(WaveletTagsChangedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onWaveletTitleChanged(WaveletTitleChangedEvent event) {

		// logEvent(event);
	}

	@Override
	public void onOperationError(OperationErrorEvent event) {

		// logEvent(event);
	}

	/*
	 * public void floodWave(String strWaveId, String strWaveletId, String
	 * documentId, int floodBlockSize, int floodBlockNum, int msDelay) {
	 * 
	 * WaveId waveId = null; WaveletId waveletId = null;
	 * 
	 * StringBuffer dataString = new StringBuffer(floodBlockSize*4);
	 * 
	 * String content = properties.getProperty("content",
	 * ContentGenerator.loremIpsum2);
	 * 
	 * LOG.log(Level.INFO, "Building data block from slices of  " +
	 * String.valueOf(content.length()*4)+" bytes");
	 * 
	 * for (int i=0; i<floodBlockSize; i=i+(content.length()*4)) {
	 * dataString.append(content); }
	 * 
	 * try {
	 * 
	 * waveId = ModernIdSerialiser.INSTANCE.deserialiseWaveId(strWaveId);
	 * waveletId =
	 * ModernIdSerialiser.INSTANCE.deserialiseWaveletId(strWaveletId);
	 * 
	 * } catch (InvalidIdException e) {
	 * 
	 * LOG.log(Level.SEVERE, String.format(
	 * "Wave Id can't be decoded: waveId: %s, waveletId: %s, blipId: %s",
	 * strWaveId, strWaveletId, documentId), e);
	 * 
	 * throw new RuntimeException(e); }
	 * 
	 * try {
	 * 
	 * Wavelet wavelet = fetchWavelet(waveId, waveletId, RPC_URL);
	 * 
	 * for (int i=0; i<floodBlockNum; i++) {
	 * 
	 * wavelet.getRootBlip().append(dataString.toString());
	 * wavelet.getRootBlip().append("\n\n");
	 * 
	 * submit(wavelet, RPC_URL);
	 * 
	 * LOG.log(Level.INFO, "Submitted data block #"+String.valueOf(i) +
	 * " of "+String.valueOf(floodBlockNum));
	 * 
	 * try {
	 * 
	 * Thread.sleep(msDelay);
	 * 
	 * } catch (InterruptedException e) {
	 * 
	 * LOG.log(Level.INFO, "Thread was interrupted. ",e);
	 * 
	 * } }
	 * 
	 * 
	 * // List<JsonRpcResponse> responses = submit(wavelet, RPC_URL);
	 * 
	 * 
	 * 
	 * 
	 * } catch (IOException e) {
	 * 
	 * LOG.log(Level.SEVERE, "", e); }
	 * 
	 * 
	 * }
	 */

}
