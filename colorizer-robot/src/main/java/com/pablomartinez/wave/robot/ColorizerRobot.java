package com.pablomartinez.wave.robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.waveprotocol.wave.model.id.InvalidIdException;
import org.waveprotocol.wave.model.id.ModernIdSerialiser;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.wave.api.AbstractRobot;
import com.google.wave.api.Annotation;
import com.google.wave.api.Blip;
import com.google.wave.api.BlipContentRefs;
import com.google.wave.api.BlipData;
import com.google.wave.api.Element;
import com.google.wave.api.Range;
import com.google.wave.api.Wavelet;
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
import com.pablomartinez.wave.robot.util.Color;
import com.pablomartinez.wave.robot.util.RobotUtils;
import com.pablomartinez.wave.robot.util.diff_match_patch;
import com.pablomartinez.wave.robot.util.diff_match_patch.Diff;
import com.pablomartinez.wave.robot.util.diff_match_patch.Operation;
import com.pablomartinez.wave.robot.util.diff_match_patch.Patch;


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

	private Map<String, String> _blips = new HashMap<String, String>();
	
	private Blip myBlip = null;
	private Wavelet myWavelet = null;
	private String myBlipId;
	private WaveletId myWaveletId;
	private HashMap<String, Color> participants = new HashMap<String, Color>();
	
	public String RPC_URL = "http://localhost:8888/robot/rpc";
	public String WAVE_DOMAIN = "localhost:8888";

	public String CONSUMER_TOKEN = "other@localhost";
	public String CONSUMER_TOKEN_SECRET = "kumHqHuS9cbVkJDnlmVVVVWVW-J874WLftk87N6_uVTKQTkz";
																								
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
		
	private void clearBGColor(Blip blip){
		blip.range(0, blip.getContent().length()).clearAnnotation("style/backgroundColor");
	}
	
	//Only return positive differences, negative ones won't need to
	//be annotated.
	private List<Range> getDifference(String oldBlip, String newBlip){
		diff_match_patch dmp = new diff_match_patch();
		LinkedList<Diff> diffs = dmp.diff_main(oldBlip, newBlip);
		
		List<Range> rangeDiffs = new ArrayList<Range>();
		
		int index = 0;
		for(Diff d : diffs){
			int size = d.text.length();
			if(d.operation == Operation.INSERT){
				rangeDiffs.add(new Range(index, index+size));
			}
			index += size;
		}
		
		return rangeDiffs;
	}
	
	private Blip getRobotBlip(List<Blip> list){
		
		for(Blip tb : list){
			if(tb.getCreator().equals(CONSUMER_TOKEN)){
				return tb;
			}
		}
		return null;
	}
	
	private void clearIfNeeded(Event event){
		String name = CONSUMER_TOKEN.split("@")[0];
		//if(event.getBlip().getContent().contains("@" + name + " clear annotations")){
		if(event.getBlip().getContent().startsWith("\n@" + name + " clear annotations")){
			clearBGColor(event.getBlip());
		}
	}

	@Override
	public void onDocumentChanged(DocumentChangedEvent event) {
		
		clearIfNeeded(event);
		
		if(event.getModifiedBy().equals(CONSUMER_TOKEN)){
			return;
		}
		
		String b = event.getBlip().getContent();
		if(!_blips.containsKey(event.getBlip().getBlipId())){
			_blips.put(event.getBlip().getBlipId(), b);
		}
		
		List<Range> diffs = getDifference(_blips.get(event.getBlip().getBlipId()), b);
		
		_blips.put(event.getBlip().getBlipId(), b); // Update value
		
		String participant = event.getModifiedBy();
		Color color = new Color(participant);
		
		
		if(!participants.containsKey(participant)){
			addNewParticipant(event.getWavelet().getWaveId(), event.getWavelet().getWaveletId(), participant, color);
		}
		
		for(Range r : diffs){
			event.getBlip().range(r.getStart(), r.getEnd()).annotate("style/backgroundColor", color.toString());
		}
		
	}

	private void addNewParticipant(WaveId waveId, WaveletId waveletId, String participant, Color color) {
		participants.put(participant, color);
		Wavelet wavelet = null;
		try {
			wavelet = fetchWavelet(waveId, waveletId, RPC_URL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Blip b = getRobotBlip(wavelet.getRootBlip().getChildBlips());
		if(b == null){
			b = wavelet.getRootBlip().reply();			
		}
		
		b.range(0, b.getContent().length()).delete();		
		
		b.append("Tracked participants:").annotate("style/backgroundColor", "rgba(255,255,255,0)");
		int pos = "Tracked participants:".length()+1;
		for(String part : participants.keySet()){
			b.append("\n" + part);
			BlipContentRefs.range(b, pos, pos+part.length()+1).annotate("style/backgroundColor", participants.get(part).toString());
			pos += part.length()+1;
		}
		
		try {
			submit(wavelet, RPC_URL);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		_blips.put(event.getNewBlipId(), event.getNewBlip().getContent());
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
		
		myBlip = event.getWavelet().getRootBlip().reply();
		myWaveletId = event.getWavelet().getWaveletId();
		
		Wavelet wavelet = event.getWavelet();
		Map<String, Blip> blips = wavelet.getBlips();
		Set<Entry<String, Blip>> set = blips.entrySet();
		Iterator<Entry<String, Blip>> it = set.iterator();
	    while (it.hasNext()) {
	        Entry<String, Blip> pairs = it.next();
	        _blips.put(pairs.getKey(), pairs.getValue().getContent());
	        //it.remove();
	    }
		
		_blips.put(event.getBlip().getBlipId(), event.getBlip().getContent());
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
}
