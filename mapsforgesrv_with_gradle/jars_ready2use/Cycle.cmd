@Echo off
rem cd /d C:\Users\top\BTSync\Exchange\gps_tools\MapsforgeSrv
rem java.exe -jar MapsforgeSrv.jar  "..\..\..\oruxmaps\Mapsforge\Germany_ML.map" "Mapsforge\ELV_320\Elevelo_Cycling_L.xml"
java.exe -jar C:\Users\top\git\mapsforgesrv_with_gradle\mapsforgesrv_with_gradle\jars_ready2use\MapsforgeSrv.jar -m "D:\OfflineMaps\mapfiles\oam\Italy_ML.map" -t "C:\Users\top\BTSync\oruxmaps\mapstyles\ELV4\Elevate_hiking.xml" -p 8080 -if "all"  -l "EN"
echo.

