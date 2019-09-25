REST API:

Rc1	user/recommend/create	Add a recommendation for viewing of content for a user	userID	        Integer	Yes	The user for whom the recommendation is being made
                                                                                        contentID	Integer	Yes	The content that is recommended for the user
 	 	 	                                                                data	        Integer	Yes	Other data supplied related to this recommendation (as a JSON array)
Rc2	content/getCatalog	Get the catalog of content from the server	        None	 	 	        A Json associative array of content IDs and Content Objects
Rc3	content/getMetadata	Get metadata associated with a piece of content	        contentID	Integer	Yes	A Json associative array of metadata keys and values
Rc4	user/profile/get	Get a user's complete profile	                        userID	        Integer	Yes	The User object, in JSON
Rc5	user/problems/get	Get a user's active problem list	                userID	        Integer	Yes	A Json array with problem IDs as keys, and descriptions as values
RC6	user/problems/getOld	Get a user's inactive problem list	                userID	        Integer	Yes	A Json array with problem IDs as keys, and descriptions as values

