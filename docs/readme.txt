IMPORTS

Imports must be fully qualified 
	Ex. import foo.*; ->import foo.Bar;

Import statements must not use line wrapping

Import statements should be done in a single block with no spaces in between

Depreciated libraries should never be used

NAMING

Non-public, non-static class member field names start with m

File and class names should be written in UpperCamelCase

Functions, parameters, and local variable names should be in lowerCamelCase 
	Ex. myString, theInt

The source file name consists of the case-sensitive name of the top-level class it contains plus the .java extension

Static field names start with s

Acronyms should be treated as words (first letter capitalized and the rest lower case) for ease of reading.
	Ex. XMLHTTPRequest –> XmlHttpRequest


INDENTATION

Indentation should be done with one tab


COMMENTS

TODO comments should be used for any temporary or short term solutions, and something that will be amended in the future.

 All classes and nontrivial functions must have Javadoc comments documenting their function and the function of all parameters and return statements
/**
* Function description
*
*@param1 param1 description
*@return  return description
*
\**


MISC

If a class has multiple constructors,  they should appear sequentially

Exceptions shouldn’t be ignored. New, custom exceptions can be thrown instead, but never suppressed.

Public static final fields (constants) should be written in all caps with underscores
	Ex.  BLUE_VALUE_ID

Horizontal alignment is never required

Braces should be put on the same line as the code before them and not on their own line
	-If the code block is empty, both braces may be written on the same line 
	Ex.  if (something) {
		body();
	       }
	     if (something) {}
	
Statements require braces around them for conditionals, unless the entire conditional may fit in one line.
	Ex. if (something) body();

Line length should be at most 100 characters long, unless it is a comment of an example command or URL, which may then exceed the limit for ease of cut and pasting
Import lines may break this rule since they are often obscured

Logs should be used sparingly in final build commits to help performance.


CODESTYLE REFERENCES:

Adapted from Android.com’s official Code Style Guide for the Android Open Source Project:
https://docs.google.com/document/d/1E0y9KAAO6jjCl7Uv-04NvRMdwVjnDDO4rmWpePXim_U/edit

Adapted from Google's Java style guide:
https://google.github.io/styleguide/
