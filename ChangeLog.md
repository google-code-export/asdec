# 1.2.0 update 1 #
> Fixed: Reading some tags caused loading to fail
# 1.2.0 #
> New:
  * Displaying various SWF objects (shapes, sprites,...) with flash player library (Windows only, sorry).
  * Images display and export
  * One merged window for AS1/2 and 3.
  * AS2: Exporting selection
  * Progressbar during loading
  * Updated icons
Fixed:
  * AS3: xml attrib, switch in anonymous function (in AS2 too)
# 1.1.0 #
> New:
  * Checking for updates
  * AS2: Exporting
  * AS3: Decompiling whole scripts instead of just classes
  * AS3: Exporting selected scripts
  * AS3: Script search bar
  * AS3: List of DoABCTags now has default "- all -" item
  * AS3: Better imports, use namespaces
  * AS3: XML related instructions
  * AS3: Anonymous functions with names
  * AS3: Better initialization of const values
  * Logging exceptions to log.txt file

Fixed:
  * AS3: set\_local..get\_local, dup, chained assignments, highlighting, callsupervoid, typenames, with statement, loops

# 1.0.1 #
  * AS3: Runtime namespace resolving
  * AS3: Arguments variable
  * AS3: Better recognizing Pre/Post Increments/Decrements
  * AS3: Better declarations
  * AS3: Fixed static variables
# 1.0.0 #
> New:
    * Support for LZMA compressed files
    * AS3: Detecting local register types for declaration.
    * AS3: Displaying inline functions
    * AS3: Last save/open dir is remembered
    * AS3: Better usage detection for multinames
    * AS3: GUI - Constants tab moved to the top
    * AS3: Commandline arguments for exporting
    * AS3: Better chained assignments
    * AS3: Deobfuscation is now optional, can be accessed via menu
    * AS2: FSCommand2 instruction support
    * Proxy: Mimetype application/octet-stream added
    * Added executable for Windows users.
> Fixed:
    * AS3: rest parameter, for..in, fail on large classes (due to sub limiter)
    * Other minor fixes
# beta 1 #
> New:
    * AS3: Automatic computing method body parameters (EXPERIMENTAL)
    * AS3: Editing return type of methods
    * AS3: Editing type and default value for variables/constants (Slot/Const traits)
    * Gui: Updated Icons
    * AS1/2: Few enhancements
    * About dialog
> Bugs:
    * AS 1/2: Fixed large bug causing Ifs to not decompile properly
    * Proxy: Some minor fixes
# alpha 10 #
> > (All AS3 part related)


> New:
  * Highlighting actual line
  * Completing instruction names via Ctrl+Space
  * Editing method parameters, method body parameters via tab panel
  * ByteCode minor\_version 17 supported - decimal datatypes
  * Local variables and method parameters take name from debug information if present
  * Automatic renaming of classes/methods when obfuscated names
  * Better error messages (When cannot decompile obfuscated code)

> Bugs:
  * Fixed Vector datatypes (TypeName multiname, applytype instruction)
  * Hilighting fixes
  * Fixed decrement/increment statements decompilation
  * Decompiler now adds variable declarations on the beginning of decompiled method
  * Try/catch statements fixed when debug information present
  * Fixed for each statements
  * Other minor fixes

# alpha 9 #
  * AS3: Added disassembling of some new types of instructions
  * AS3: Exporting source as PCode
  * AS3: Many other bugfixes...
# alpha 8 #
  * AS1/2: Better GUI
  * AS1/2: Better decompiling of Ifs, For..in
  * AS3: Editing exceptions
  * AS3: Finding usage of multinames from constant table