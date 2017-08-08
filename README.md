# "Privacy-Preserving Intersection Management for Autonomous Vehicles"
Authors: Kokciyan, Nadin; Erdogan, Mustafa; Salih Meral, Tuna Han; Yolum, Pinar

We make use of different privacy-aware strategies to regulate the traffic at junctions. The autonomous vehicles bid with their information. An Intersection Manager decides which vehicle should move first.

# How to check the experiment results?

1. Install MongoDB and import the two databases (traffic and experiments_selected) in dumps folder. The traffic database includes all [Road Safety Accidents in 2015](https://data.gov.uk/dataset/road-accidents-safety-data). 

2. The experiments_selected database includes 4563 accidents used in our experiments. You can check the experiment results in the database. For more information about a specific accident, you can refer to the traffic database. Accident_Index can be used to identify an accident in both databases. 

# How to repeat our experiments in different settings?

1. Install MongoDB and import the traffic database in dumps folder. The traffic database includes all [Road Safety Accidents in 2015](https://data.gov.uk/dataset/road-accidents-safety-data). 

2. The main program Main.java can be executed to see the auction results when the vehicles use different strategies. Bid All, Bid Privacy Aware and Bid Privacy Incremental are the strategies being used. We run these experiments for five thresholds: 0.9, 0.8, 0.7, 0.5, and 0.3. 

3. If you want to re-run the experiments, please make sure that the traffic database is imported. Create an empty experiments_selected database. Then, change saveMongo variable to true in Main.java file. The new results will be saved into the experiments_selected database. 