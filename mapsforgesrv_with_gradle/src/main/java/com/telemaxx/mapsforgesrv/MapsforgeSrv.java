package com.telemaxx.mapsforgesrv;
import java.io.File;
import java.net.BindException;
import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;

public class MapsforgeSrv {

	public static void main(String[] args) throws Exception {
		System.out.println("MapsforgeSrv - a mapsforge tile server");
		if (args.length < 1)
			help();
 
		File mapFile = new File(args[0]);
		System.out.println("Map file: " + mapFile);
		if (!mapFile.isFile()) {
			System.err.println("ERROR: Map file does not exist!");
			System.exit(1);
		}

		File themeFile = null;
		if (args.length > 1) {
			themeFile = new File(args[1]);
			System.out.println("Render theme file: " + themeFile);
			if (!themeFile.isFile()) {
				System.err.println("ERROR: Render theme file does not exist!");
				System.exit(1);
			}
		} else {
			System.out.println("Theme: OSMARENDER");
		}

		MapsforgeHandler mapsforgeHandler = new MapsforgeHandler(mapFile, themeFile);

		Server server = new Server(InetSocketAddress.createUnresolved("localhost", 8080));
		server.setHandler(mapsforgeHandler);
		try {
			server.start();
		} catch (BindException e) {
			System.out.println(e.getMessage());
			System.out.println("Stopping server");
			System.exit(1);
		}
		server.join();
	}

	public static void help() {
		System.out.println("java -jar MapsforgeSrv.jar <mapfile> [themefile]");
		System.exit(1);
	}
}