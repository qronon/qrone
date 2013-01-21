package org.qrone.r7.app;

import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

public class QrONEAppLogTurboFilter extends TurboFilter{

	@Override
	public FilterReply decide(Marker arg0, Logger logger, Level lv,
			String arg3, Object[] arg4, Throwable arg5) {
		if(lv.equals(Level.DEBUG) && !logger.getName().startsWith("org.qrone")){
			return FilterReply.DENY;
		}
		return FilterReply.NEUTRAL;
	}

}
