<pre>
1) -help | --help | /?<br>
...shows commandline arguments (this help)<br>
2) infile<br>
...opens SWF file with the decompiler GUI<br>
3) -proxy (-PXXX)<br>
...auto start proxy in the tray. Optional parameter -P specifies port for proxy. Defaults to 55555.<br>
4) -export (as|pcode) outdirectory infile<br>
...export infile actionscript to outdirectory as AsctionScript code ("as" argument) or as PCode ("pcode" argument)<br>
5) -dumpSWF infile<br>
...dumps list of SWF tags to console<br>
6) -compress infile outfile<br>
...Compress SWF infile and save it to outfile<br>
7) -decompress infile outfile<br>
...Decompress infile and save it to outfile<br>
</pre>

Examples:
<pre>
java -jar ASDec.jar myfile.swf<br>
java -jar ASDec.jar -proxy<br>
java -jar ASDec.jar -proxy -P1234<br>
java -jar ASDec.jar -export as "C:\decompiled\" myfile.swf<br>
java -jar ASDec.jar -export pcode "C:\decompiled\" myfile.swf<br>
java -jar ASDec.jar -dumpSWF myfile.swf<br>
java -jar ASDec.jar -compress myfile.swf myfiledec.swf<br>
java -jar ASDec.jar -decompress myfiledec.swf myfile.swf<br>
</pre>