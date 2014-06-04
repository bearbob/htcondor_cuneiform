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

package de.huberlin.wbi.cuneiform.cmdline.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.huberlin.cuneiform.dax.repl.DaxRepl;
import de.huberlin.wbi.cuneiform.core.actormodel.Actor;
import de.huberlin.wbi.cuneiform.core.cre.BaseCreActor;
import de.huberlin.wbi.cuneiform.core.cre.LocalCreActor;
import de.huberlin.wbi.cuneiform.core.repl.BaseRepl;
import de.huberlin.wbi.cuneiform.core.repl.CmdlineRepl;
import de.huberlin.wbi.cuneiform.core.ticketsrc.TicketSrcActor;

public class Main {
	
	private static final String FORMAT_CF = "cf";
	private static final String FORMAT_DAX = "dax";
	private static final String PLATFORM_LOCAL = "local";
	
	
	private static String platform;
	private static String format;
	private static File[] inputFileVector;
	

	public static void main( String[] args ) throws IOException, ParseException, InterruptedException {
		
		CommandLine cmd;
		Options opt;
		BaseRepl repl;
		BaseCreActor cre;
		File sandbox;
		ExecutorService executor;
		TicketSrcActor ticketSrc;
		
		executor = Executors.newCachedThreadPool();
		try {
		
			opt = getOptions();
			cmd = parse( args, opt );
			config( cmd );
			
			if( cmd.hasOption( 'h' ) ) {
				System.out.println(  ); // TODO
				return;
			}
						
			switch( platform ) {
			
				case PLATFORM_LOCAL :
					
					sandbox = new File( System.getProperty( "user.home" )+"/.cuneiform" );
					sandbox.mkdir();
					cre = new LocalCreActor( sandbox );
					break;
					
				default : throw new RuntimeException( "Platform not recognized." );
			}
	
			executor.submit( cre );
			ticketSrc = new TicketSrcActor( cre );
			executor.submit( ticketSrc );
			executor.shutdown();
			
			switch( format ) {
			
				case FORMAT_CF :
					repl = new CmdlineRepl( ticketSrc );
					break;
					
				case FORMAT_DAX :
					repl = new DaxRepl( ticketSrc );
					break;
					
				default :
					throw new RuntimeException( "Format not recognized." );
			}
			
			if( inputFileVector.length > 0 ) {
				
				// run in quiet mode
				
				for( File f : inputFileVector )
					repl.interpret( readFile( f ) );
				
				Thread.sleep( 3*Actor.DELAY );
				while( repl.isBusy() ) {
					
					System.out.println( repl.getRunningSet() );
					Thread.sleep( Actor.DELAY );
				}				
				return;
			}
			
			if( !format.equals( FORMAT_CF ) ) {
				
				// read from standard in
				
				repl.interpret( readStdIn() );
				return;
			}
			
			// run in interactive mode
			
			CmdlineRepl.run( repl );
		
		}
		finally {
			executor.shutdownNow();
		}

	}
	
	public static void config( CommandLine cmd ) {
		
		String[] fileVector;
		File f;
		String s;
		int i, n;
		
		fileVector = cmd.getArgs();
		n = fileVector.length;
		inputFileVector = new File[ n ];
		
		for( i = 0; i < n; i++ ) {
			
			s = fileVector[ i ];
			f = new File( s );
			
			inputFileVector[ i ] = f;
		}
		
		if( cmd.hasOption( 'p' ) )
			platform = cmd.getOptionValue( 'p' );
		else
			platform = PLATFORM_LOCAL;
		
		if( cmd.hasOption( 'f' ) )
			format = cmd.getOptionValue( 'f' );
		else
			format = FORMAT_CF;
	}
	
	public static CommandLine parse( String[] args, Options opt ) throws ParseException {
		
		GnuParser parser;
		CommandLine cmd;
		
		parser = new GnuParser();		
		cmd = parser.parse( opt, args );
		return cmd;
	}
	
	public static Options getOptions() {
		
		Options opt;
		
		opt = new Options();
		
		opt.addOption( "f", "format", true,
			"The format of the input file. Must be either '"+FORMAT_CF+"' for Cuneiform or '"+FORMAT_DAX+"' for Pegasus DAX. Default is '"+FORMAT_CF+"'." );
		
		opt.addOption( "p", "platform", true,
			"The platform to run. The only platform currently available is '"+PLATFORM_LOCAL+"'. Default is '"+PLATFORM_LOCAL+"'." );
		
		opt.addOption( "h", "help", false, "Output help." );
		
		return opt;
		
	}
	
	public static String readFile( File f ) throws FileNotFoundException, IOException {
		
		String line;
		StringBuffer buf;
		
		buf = new StringBuffer();
		try( BufferedReader reader = new BufferedReader( new FileReader( f ) ) ) {
			
			while( ( line = reader.readLine() ) != null )
				buf.append( line ).append( '\n' );
		}
		
		return buf.toString();
		
	}
	
	public static String readStdIn() throws IOException {
		
		StringBuffer buf;
		String line;
		
		buf = new StringBuffer();
		try( BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) ) ) {
			
			while( ( line = reader.readLine() ) != null )
				buf.append( line ).append( '\n' );
		}
		
		return buf.toString();
	}
}