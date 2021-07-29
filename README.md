# Super Street Finder

A simple app written during a weekend for fun that trains a NER (Name Entity Recognition) model using the list of streets from Madrid. Once the model is trained, the same list of streets is fed into an Elasticsearch index. 

Once a request is performed to the server, the text is analyzed using the model and its tokens are split to perform field-based search on elastic.

The app is self-contained and trains the model on startup if no model has been trained before.

Beware I'm not really experienced on ML, just learned the bare minimun to understand NER and train a model using Stanford NLP

A simple `run.sh` script is provided. The script will start elasticsearch on docker, build and run the server and perform a simple request once the server is ready. This may fail the first time as the model takes some time to train.