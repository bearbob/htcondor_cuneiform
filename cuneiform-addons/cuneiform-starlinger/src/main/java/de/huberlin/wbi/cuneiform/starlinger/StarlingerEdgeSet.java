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

package de.huberlin.wbi.cuneiform.starlinger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StarlingerEdgeSet {

	private Map<String,List<String>> edgeMap;
	
	public StarlingerEdgeSet() {
		
		edgeMap = new HashMap<>();
	}
	
	public void addEdge( String src, String sink ) {
		
		List<String> sinkList;
		
		sinkList = edgeMap.get( src );
		
		if( sinkList == null ) {
			
			sinkList = new LinkedList<>();
			edgeMap.put( src, sinkList );
		}
		
		sinkList.add( sink );
	}
	
	@Override
	public String toString() {
		
		JsonMap jsonMap;
		StringBuffer buf;
		boolean comma;
		
		jsonMap = new JsonMap();
		
		for( String key : edgeMap.keySet() ) {
			
			
			buf = new StringBuffer();
			
			buf.append( '[' );
			
			comma = false;
			for( String value : edgeMap.get( key ) ) {
				
				if( comma )
					buf.append( ',' );
				comma = true;
				
				buf.append( '\'' ).append( value ).append( '\'' );
			}
			
			buf.append( ']' );
			
			jsonMap.putAttribute( key, buf.toString() );
		}
		
		return jsonMap.toString();
	}
}
