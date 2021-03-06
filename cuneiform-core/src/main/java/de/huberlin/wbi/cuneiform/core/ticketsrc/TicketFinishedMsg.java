/*******************************************************************************
 * In the Hi-WAY project we propose a novel approach of executing scientific
 * workflows processing Big Data, as found in NGS applications, on distributed
 * computational infrastructures. The Hi-WAY software stack comprises the func-
 * tional workflow language Cuneiform as well as the Hi-WAY ApplicationMaster
 * for Apache Hadoop 2.x (YARN).
 *
 * List of Contributors:
 *
 * Jörgen Brandt (HU Berlin)
 * Marc Bux (HU Berlin)
 * Ulf Leser (HU Berlin)
 *
 * Jörgen Brandt is funded by the European Commission through the BiobankCloud
 * project. Marc Bux is funded by the Deutsche Forschungsgemeinschaft through
 * research training group SOAMED (GRK 1651).
 *
 * Copyright 2014 Humboldt-Universität zu Berlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.huberlin.wbi.cuneiform.core.ticketsrc;


import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.huberlin.wbi.cuneiform.core.actormodel.Message;
import de.huberlin.wbi.cuneiform.core.cre.BaseCreActor;
import de.huberlin.wbi.cuneiform.core.semanticmodel.JsonReportEntry;
import de.huberlin.wbi.cuneiform.core.semanticmodel.Ticket;

public class TicketFinishedMsg extends Message {

	private final Ticket ticket;
	private final Set<JsonReportEntry> reportEntrySet;

	public TicketFinishedMsg( BaseCreActor sender, Ticket ticket, Collection<JsonReportEntry> report ) {
		
		super( sender );
		
		if( ticket == null )
			throw new NullPointerException( "Ticket must not be null." );
		
		this.ticket = ticket;
		
		reportEntrySet = new HashSet<>();

		addReport( report );
	}
	
	public void addReport( Collection<JsonReportEntry> report ) {
		
		if( report == null )
			throw new NullPointerException( "JSON report entry collection must not be null." );
		
		for( JsonReportEntry entry : report )
			addReport( entry );
	}
	
	public void addReport( JsonReportEntry entry ) {
		
		if( entry == null )
			throw new NullPointerException( "JSON report entry must not be null." );
		
		reportEntrySet.add( entry );
	}
	
	public Set<JsonReportEntry> getReportEntrySet() {
		return Collections.unmodifiableSet( reportEntrySet );
	}
	
	public Ticket getTicket() {
		return ticket;
	}
	
	@Override
	public String toString() {
		return "{ ticketFinished, "+ticket.getTicketId()+" }";
	}
}
