@Echo off
rem cd /d C:\Users\top\BTSync\Exchange\gps_tools\MapsforgeSrv
rem java.exe -jar MapsforgeSrv.jar  "..\..\..\oruxmaps\Mapsforge\Germany_ML.map" "Mapsforge\ELV_320\Elevelo_Cycling_L.xml"
java.exe -jar C:\Users\top\git\mapsforgesrv_with_gradle\mapsforgesrv_with_gradle\jars_ready2use\MapsforgeSrv.jar -m "D:\OfflineMaps\mapfiles\oam\niedersachsen_oam.osm.map,D:\OfflineMaps\mapfiles\oam\nordrhein-westfalen_oam.osm.map"  -t "C:\Users\top\BTSync\oruxmaps\mapstyles\ELV4\Elements.xml" -p 8080 -if "all"  -l "EN" -s "elmt-hiking"
echo.

