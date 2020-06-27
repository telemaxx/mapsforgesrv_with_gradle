/*******************************************************************************
 * Copyright 2019, 2020 Thomas Theussing and Contributors
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * 
 * changelog:
 * 0.13: selectable style
 *******************************************************************************/

package com.telemaxx.mapsforgesrv;
import java.io.File;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Server;

public class MapsforgeSrv {

	public static void main(String[] args) throws Exception {
		final String VERSION = "0.13.0"; //starting with 0.13, the mapsforge version //$NON-NLS-1$
		System.out.println("MapsforgeSrv - a mapsforge tile server. " + "version: " + VERSION); //$NON-NLS-1$ //$NON-NLS-2$

		String[] mapFilePaths = null;
		String themeFilePath = null;
		String themeFileStyle = null;
		String preferredLanguage = null;
		final int DEFAULTPORT = 8080; 
		String portNumberString = "" + DEFAULTPORT; //$NON-NLS-1$


		Options options = new Options();

		Option mapfileArgument = new Option("m", "mapfiles", true, "comma-separated list of mapsforge map files (.map)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mapfileArgument.setRequired(true);
		options.addOption(mapfileArgument);

		Option themefileArgument = new Option("t", "themefile", true, "mapsforge theme file(.xml), (default: the internal OSMARENDER)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		themefileArgument.setRequired(false);
		options.addOption(themefileArgument);
		
		Option themefileStyleArgument = new Option("s", "style", true, "style from theme file(.xml), (default: default defined in xml file)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		themefileStyleArgument.setRequired(false);
		options.addOption(themefileStyleArgument);		

		Option languageArgument = new Option("l", "language", true, "preferred language (default: native language)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		languageArgument.setRequired(false);
		options.addOption(languageArgument);

		Option portArgument = new Option("p", "port", true, "port, where the server is listening(default: " + DEFAULTPORT + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		portArgument.setRequired(false);
		options.addOption(portArgument);

		Option interfaceArgument = new Option("if", "interface", true, "which interface listening [all,localhost] (default: localhost)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		interfaceArgument.setRequired(false);
		options.addOption(interfaceArgument);        

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;      

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("mapsforgesrv", options); //$NON-NLS-1$
			System.exit(1);
		}

		int portNumber = DEFAULTPORT;
		portNumberString = cmd.getOptionValue("port"); //$NON-NLS-1$
		if (portNumberString != null) {
			try {
				portNumberString = portNumberString.trim();
				//System.out.println("portString" + portNumberString);
				portNumber = Integer.parseInt(portNumberString);
				if (portNumber < 1024 || portNumber > 65535) {
					portNumber = DEFAULTPORT;
					System.out.println("portnumber not 1024-65535, exit"); //$NON-NLS-1$
					System.exit(1);
				} else {
					System.out.println("using port: " + portNumber); //$NON-NLS-1$
				}
			} catch (NumberFormatException e){
				portNumber = DEFAULTPORT;
				System.out.println("couldnt parse portnumber, using " + DEFAULTPORT); //$NON-NLS-1$
				//e.printStackTrace();
			}
		} else {
			System.out.println("no port given, using " + DEFAULTPORT); //$NON-NLS-1$
		}

		mapFilePaths = cmd.getOptionValue("mapfiles").trim().split(","); //$NON-NLS-1$ //$NON-NLS-2$
		ArrayList<File> mapFiles = new ArrayList<>();
		for (String path : mapFilePaths) {
			mapFiles.add(new File(path));
		}
		mapFiles.forEach(mapFile -> {
			System.out.println("Map file: " + mapFile); //$NON-NLS-1$
			if (!mapFile.isFile()) {
				System.err.println("ERROR: Map file does not exist!"); //$NON-NLS-1$
				System.exit(1);
			}
		});

		themeFilePath = cmd.getOptionValue("themefile"); //$NON-NLS-1$
		if (themeFilePath != null) {
			themeFilePath = themeFilePath.trim();
		}
		File themeFile = null;
		if (themeFilePath != null) {	
			themeFile = new File(themeFilePath);
			System.out.println("Theme file: " + themeFile); //$NON-NLS-1$
			if (!themeFile.isFile()) {
				System.err.println("ERROR: theme file does not exist!"); //$NON-NLS-1$
				System.exit(1);
			}
		} else {
			System.out.println("Theme: OSMARENDER"); //$NON-NLS-1$
		}
		
		themeFileStyle = cmd.getOptionValue("style"); //$NON-NLS-1$
		if (themeFileStyle != null) {
			themeFileStyle = themeFileStyle.trim();
			System.out.println("selected ThemeStyle: " + themeFileStyle); //$NON-NLS-1$
		}	
		
		preferredLanguage = cmd.getOptionValue("language"); //$NON-NLS-1$
		if (preferredLanguage != null) {
			System.out.println("preferredLanguage, using: " + preferredLanguage); //$NON-NLS-1$
		}
		
		MapsforgeHandler mapsforgeHandler = new MapsforgeHandler(mapFiles, themeFile, themeFileStyle, preferredLanguage);

		Server server = null;
		String listeningInterface = cmd.getOptionValue("interface"); //$NON-NLS-1$
		if (listeningInterface != null) {
			listeningInterface = listeningInterface.trim();
			if (listeningInterface.toLowerCase().equals("all")) { //$NON-NLS-1$
				System.out.println("listening on all interfaces, port:" + portNumber); //$NON-NLS-1$
				server = new Server(portNumber);
			} else if (listeningInterface.toLowerCase().equals("localhost")) { //$NON-NLS-1$
				//listeningInterface = "localhost";
				System.out.println("listening on localhost port:" + portNumber); //$NON-NLS-1$
				server = new Server(InetSocketAddress.createUnresolved("localhost", portNumber)); //$NON-NLS-1$
			} else {
				System.out.println("unkown Interface, only \"all\" or \"localhost\" , not " + listeningInterface ); //$NON-NLS-1$
				System.exit(1);	
			}
		} else {
			System.out.println("listening on localhost port:" + portNumber); //$NON-NLS-1$
			server = new Server(InetSocketAddress.createUnresolved("localhost", portNumber)); //$NON-NLS-1$
		}

		server.setHandler(mapsforgeHandler);
		try {
			server.start();
		} catch (BindException e) {
			System.out.println(e.getMessage());
			System.out.println("Stopping server"); //$NON-NLS-1$
			System.exit(1);
		}
		server.join();
	}

}