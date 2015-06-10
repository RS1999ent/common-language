package net.floodlightcontroller.commonlanguagemodule;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFFlowModCommand;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IListener.Command;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.BSN;
import net.floodlightcontroller.packet.BootstrapAdvert;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.LLDP;
import net.floodlightcontroller.packet.LLDPTLV;
import net.floodlightcontroller.routing.ForwardingBase;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.topology.NodePortTuple;
import net.floodlightcontroller.util.MatchUtils;

public class CommonLanguage implements IOFMessageListener, IFloodlightModule {

	protected IFloodlightProviderService floodlightProvider;
	protected Set<Long> macAddresses;
	protected static Logger logger;
	protected IOFSwitchService switchService;
	protected IRoutingService routeService;
	protected IStaticFlowEntryPusherService staticRulePusherService;
	protected String CLProcessIP = null;
	
	private static final byte[] BOOTSTRAP_STANDARD_MAC =
			HexString.fromHexString("11:11:11:11:11:0e");
	
	@Override
	public String getName() {
		return CommonLanguage.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class <? extends IFloodlightService>> l = 
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IOFSwitchService.class);
		l.add(IRoutingService.class);
		l.add(IStaticFlowEntryPusherService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		macAddresses = new ConcurrentSkipListSet<Long>();
		switchService = context.getServiceImpl(IOFSwitchService.class);
		routeService = context.getServiceImpl(IRoutingService.class);
		staticRulePusherService = context.getServiceImpl(IStaticFlowEntryPusherService.class);
		logger = LoggerFactory.getLogger(CommonLanguage.class);
		
		Map<String, String> configOptions = context.getConfigParams(this);
		CLProcessIP = configOptions.get("CLProcessIP");

	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		

	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		switch (msg.getType()) {
		case PACKET_IN:
	//		ctrIncoming.increment();
			return this.handlePacketIn(sw.getId(), (OFPacketIn) msg,
					cntx);
		default:
			break;
		}
		return Command.CONTINUE;

	}

	
	protected Command handlePacketIn(DatapathId sw, OFPacketIn pi,
			FloodlightContext cntx) {

		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
				IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		OFPort inPort = (pi.getVersion().compareTo(OFVersion.OF_12) < 0 ? pi
				.getInPort() : pi.getMatch().get(MatchField.IN_PORT));
		if (eth.getPayload() instanceof BSN) {
			BSN bsn = (BSN) eth.getPayload();
			if (bsn == null)
				return Command.STOP;
			if (bsn.getPayload() == null)
				return Command.STOP;
			// It could be a packet other than BSN LLDP, therefore
			// continue with the regular processing.
			if (bsn.getPayload() instanceof LLDP == false)
				return Command.CONTINUE;
			return handleLldp((LLDP) bsn.getPayload(), sw, inPort, false, cntx);
		} else if (eth.getPayload() instanceof LLDP) {
			return handleLldp((LLDP) eth.getPayload(), sw, inPort, true, cntx);
		}
		else if(eth.getPayload() instanceof BootstrapAdvert)
		{
			BootstrapAdvert advert = (BootstrapAdvert) eth.getPayload();
			return handleBootstrapAdvert((BootstrapAdvert) eth.getPayload(), sw, inPort, cntx, pi);
		}
		return Command.CONTINUE;
	}
	
	private Command handleBootstrapAdvert(BootstrapAdvert advert,  DatapathId sw, OFPort inPort, 
			FloodlightContext cntx, OFPacketIn pi)
			{
				Set<DatapathId> allSwitches = switchService.getAllSwitchDpids();
				
				for(DatapathId aSwitch : allSwitches)
				{
					if(aSwitch != sw)
					{
						Route toBorder = routeService.getRoute(aSwitch, sw, null);
						Match.Builder mb = OFFactories.getFactory(pi.getVersion()).buildMatch();
						IPv4Address address =  IPv4Address.of(CLProcessIP);
						mb.setExact(MatchField.IPV4_DST, address);						
						pushRoute(toBorder, mb.build(), pi, cntx, true, OFFlowModCommand.ADD);
						//	.setExact(MatchField.IP_PROTO, ipv4.getProtocol());
//						List<NodePortTuple> path = toBorder.getPath();
//						for(NodePortTuple noPTup : path)
//						{
//							OFFlowMod.Builder fmb;
//							DatapathId nodeID = noPTup.getNodeId();
//							fmb = 
//							
//							//staticRulePusherService.
//						}
					}
				}
				Match.Builder mb = OFFactories.getFactory(pi.getVersion()).buildMatch();
				IPv4Address address =  IPv4Address.of(CLProcessIP);
				mb.setExact(MatchField.IPV4_DST, address);
				pushOutBorder(sw, inPort, mb.build()); 
				
				return null;
		
			}
	
	private Command handleLldp(LLDP lldp, DatapathId sw, OFPort inPort,
			boolean isStandard, FloodlightContext cntx) {
		// If LLDP is suppressed on this port, ignore received packet as well
		IOFSwitch iofSwitch = switchService.getSwitch(sw);
		OFPortDesc ofpPort = iofSwitch.getPort(inPort);
//		if (!isIncomingDiscoveryAllowed(sw, inPort, isStandard))
//			return Command.STOP;


	//	long myId = ByteBuffer.wrap(controllerTLV.getValue()).getLong();
		long otherId = 0;
		boolean myLLDP = false;
		Boolean isReverse = null;
		
		ByteBuffer portBB = ByteBuffer.wrap(lldp.getPortId().getValue());
		portBB.position(1);

		OFPort remotePort = OFPort.of(portBB.getShort());		
		IOFSwitch remoteSwitch = null;
		boolean fromOutsideDomain = false;
		// Verify this LLDP packet matches what we're looking for
		for (LLDPTLV lldptlv : lldp.getOptionalTLVList()) {
			if (lldptlv.getType() == 127 && lldptlv.getLength() == 12
					&& lldptlv.getValue()[0] == 0x0
					&& lldptlv.getValue()[1] == 0x26
					&& lldptlv.getValue()[2] == (byte) 0xe1
					&& lldptlv.getValue()[3] == 0x0) {
				ByteBuffer dpidBB = ByteBuffer.wrap(lldptlv.getValue());
				remoteSwitch = switchService.getSwitch(DatapathId.of(dpidBB.getLong(4)));
				fromOutsideDomain = remoteSwitch == null;
			} 
		}
		
		if(fromOutsideDomain)
		{
						
			OFPacketOut out = generateBootstrapPacket(iofSwitch, inPort);
			iofSwitch.write(out);
			iofSwitch.flush();

		}
		return Command.CONTINUE;
	}
	
	private OFPacketOut generateBootstrapPacket(IOFSwitch iofSwitch, OFPort inPort)
	{
		
		DatapathId sw = iofSwitch.getId();
		if (logger.isTraceEnabled()) {
			logger.trace("Sending bootstrap packet out of switch: {}, port: {}",
					iofSwitch.toString(), inPort);
		}
		
		BootstrapAdvert bootstrap = new BootstrapAdvert(CLProcessIP);
		OFPortDesc ofpPort = iofSwitch.getPort(inPort);
		Ethernet ethernet;			
			ethernet = new Ethernet().setSourceMACAddress(ofpPort.getHwAddr())
					.setDestinationMACAddress(BOOTSTRAP_STANDARD_MAC);
			ethernet.setPayload(bootstrap);
			
			byte[] data = ethernet.serialize();
			OFPacketOut.Builder pob = switchService.getSwitch(sw).getOFFactory().buildPacketOut();
			pob.setBufferId(OFBufferId.NO_BUFFER);
			pob.setInPort(OFPort.ANY);
			pob.setData(data);

			List<OFAction> actions = new ArrayList<OFAction>();
			actions.add(iofSwitch.getOFFactory().actions().buildOutput().setPort(inPort).build());
			
			pob.setActions(actions);
			
			return pob.build();		
	}
	
	/**
	 * Push routes from back to front
	 * @param route Route to push
	 * @param match OpenFlow fields to match on
	 * @param srcSwPort Source switch port for the first hop
	 * @param dstSwPort Destination switch port for final hop
	 * @param cookie The cookie to set in each flow_mod
	 * @param cntx The floodlight context
	 * @param reqeustFlowRemovedNotifn if set to true then the switch would
	 * send a flow mod removal notification when the flow mod expires
	 * @param doFlush if set to true then the flow mod would be immediately
	 *        written to the switch
	 * @param flowModCommand flow mod. command to use, e.g. OFFlowMod.OFPFC_ADD,
	 *        OFFlowMod.OFPFC_MODIFY etc.
	 * @return srcSwitchIncluded True if the source switch is included in this route
	 */
	
	public boolean pushRoute(Route route, Match match, OFPacketIn pi,
			 FloodlightContext cntx,
			 boolean doFlush,
			OFFlowModCommand flowModCommand) {

		boolean srcSwitchIncluded = false;

		List<NodePortTuple> switchPortList = route.getPath();

		for (int indx = switchPortList.size() - 1; indx > 0; indx -= 2) {
			// indx and indx-1 will always have the same switch DPID.
			DatapathId switchDPID = switchPortList.get(indx).getNodeId();
			IOFSwitch sw = switchService.getSwitch(switchDPID);

			if (sw == null) {
				if (logger.isWarnEnabled()) {
					logger.warn("Unable to push route, switch at DPID {} "
							+ "not available", switchDPID);
				}
				return srcSwitchIncluded;
			}

			// need to build flow mod based on what type it is. Cannot set
			// command later
			OFFlowMod.Builder fmb;
			switch (flowModCommand) {
			case ADD:
				fmb = sw.getOFFactory().buildFlowAdd();
				break;
			case DELETE:
				fmb = sw.getOFFactory().buildFlowDelete();
				break;
			case DELETE_STRICT:
				fmb = sw.getOFFactory().buildFlowDeleteStrict();
				break;
			case MODIFY:
				fmb = sw.getOFFactory().buildFlowModify();
				break;
			default:
				logger.error("Could not decode OFFlowModCommand. Using MODIFY_STRICT. (Should another be used as the default?)");
			case MODIFY_STRICT:
				fmb = sw.getOFFactory().buildFlowModifyStrict();
				break;
			}

			OFActionOutput.Builder aob = sw.getOFFactory().actions()
					.buildOutput();
			List<OFAction> actions = new ArrayList<OFAction>();
			Match.Builder mb = MatchUtils.createRetentiveBuilder(match);

			// set input and output ports on the switch
			OFPort outPort = switchPortList.get(indx).getPortId();
			OFPort inPort = switchPortList.get(indx - 1).getPortId();
			mb.setExact(MatchField.IN_PORT, inPort);
			aob.setPort(outPort);
			aob.setMaxLen(Integer.MAX_VALUE);
			actions.add(aob.build());

			// compile
			fmb.setMatch(mb.build())
					// was match w/o modifying input port
					.setActions(actions)
					.setIdleTimeout(ForwardingBase.FLOWMOD_DEFAULT_IDLE_TIMEOUT)
					.setHardTimeout(ForwardingBase.FLOWMOD_DEFAULT_HARD_TIMEOUT)
					.setBufferId(OFBufferId.NO_BUFFER)
					// .setCookie(cookie)
					.setOutPort(outPort)
					.setPriority(ForwardingBase.FLOWMOD_DEFAULT_PRIORITY);

			try {
				if (logger.isTraceEnabled()) {
					logger.trace("Pushing Route flowmod routeIndx={} "
							+ "sw={} inPort={} outPort={}", new Object[] {
							indx, sw, fmb.getMatch().get(MatchField.IN_PORT),
							outPort });
				}
				sw.write(fmb.build());
				// messageDamper.write(sw, fmb.build());
				if (doFlush) {
					sw.flush();
				}

			} catch (Exception e) {
				logger.error("Failure writing flow mod", e);
			}
		}

		return srcSwitchIncluded;
	}
	
	public void pushOutBorder(DatapathId dpid, OFPort inPort, Match iMatch) {
		/* Insert static flows on all ports of the switch to redirect
		 * DHCP client --> DHCP DHCPServer traffic to the controller.
		 * DHCP client's operate on UDP port 67
		 */
		IOFSwitch sw = switchService.getSwitch(dpid);
		
		OFFlowAdd.Builder flow = sw.getOFFactory().buildFlowAdd();
		Match.Builder match = MatchUtils.createRetentiveBuilder(iMatch);	
		//Match.Builder match = sw.getOFFactory().buildMatch();
		ArrayList<OFAction> actionList = new ArrayList<OFAction>();
		OFActionOutput.Builder action = sw.getOFFactory().actions().buildOutput();
		
		
		for (OFPortDesc port : sw.getPorts()) {
			match.setExact(MatchField.IN_PORT, port.getPortNo());
			match.setExact(MatchField.ETH_TYPE, EthType.IPv4);
			//match.setExact(MatchField.IP_PROTO, IpProtocol.UDP);
			//match.setExact(MatchField.UDP_SRC, UDP.DHCP_CLIENT_PORT);
			action.setMaxLen(0xffFFffFF);
			action.setPort(inPort);
			actionList.add(action.build());
			flow.setBufferId(OFBufferId.NO_BUFFER);
			flow.setHardTimeout(0);
			flow.setIdleTimeout(0);
			flow.setOutPort(inPort);
			flow.setActions(actionList);
			flow.setMatch(match.build());
			flow.setPriority(32767);
			staticRulePusherService.addFlow("border-port---" + port.getPortNo().getPortNumber() + "---(" + port.getName() + ")", flow.build(), sw.getId());
		}		
	}

}
