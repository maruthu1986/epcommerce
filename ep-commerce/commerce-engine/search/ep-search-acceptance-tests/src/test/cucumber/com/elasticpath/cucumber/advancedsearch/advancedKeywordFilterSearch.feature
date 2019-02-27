Feature: As Operations, I want Advanced Search functionality to work when doing keyword searches on text based attribute fields, so I am able to use these attributes as part of advanced search configuration.

Background:
Given the Advanced Search has been implemented for text attribute fields

Scenario Outline: Tests advanced keyword search finding matches.
And attribute <ATTR-KEY> is configured as a text attribute
And Product A attribute <ATTR-KEY> contains <PRODUCT-ATTR-VALUE>
When <SEARCH-TERM> is provided as a <ATTR-KEY> advanced search term
Then Product A is listed in the results

Examples:
	| ATTR-KEY 		| PRODUCT-ATTR-VALUE 					| SEARCH-TERM 			|
	| XX     		| Little John is big and tall         	| Little  				|
	| XX	   		| Little John is big and tall			| John is big and tall 	|
	| XX 			| McGraw Hill							| McGraw				|	
	| XX 			| McGraw Hill							| mcGraw				|
	| XX 			| McGraw Hill							| mcgraw				|	
	| XX			| Que ce réflex ou pour					| réflex				|
	| XX 			| Student, biology						| student				|
	| XX 			| Student, biology						| student and biology 	|
	| XX 			| Student, biology						| student biology		|
	| XX 			| Student, biology						| biology				|
	| XX 			| Student, biology						| Student, biology		|

Scenario Outline: Tests advanced keyword search not finding matches.
And attribute <ATTR-KEY> is configured as a text attribute
And Product A attribute <ATTR-KEY> contains <PRODUCT-ATTR-VALUE>
When <SEARCH-TERM> is provided as a <ATTR-KEY> advanced search term
Then No products are listed in the results

Examples:
	| ATTR-KEY 		| PRODUCT-ATTR-VALUE 					| SEARCH-TERM 	|
	| XX 			| Que ce réflex ou pour					| réflexsdfas	|