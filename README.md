This project requires SBT 0.13.6 and probably any version 0.13.x works well for this system. The project was implements using Akka and tested using Akka TestKit with ScalaTest.

1) After cloning the repo, run "sbt update"
2) To Run the integration tests, run "sbt test"

3) To execute the program, I recommend just executing

git checkout 9cc2cc441ce2904a249d14fd0f4bd0e3d7b69e06
Then just run, sbt "run <starting number of consumers>", with the double quotes  
After you are finished experimenting with the program, execute "git checkout master"

Note: The latest commit uses ActorLogging in order to facilitate testing but the output is more difficult to see if we run the program manually. The best version to run the program is at the commit below: https://github.com/anthonyca7/NTPService/tree/9cc2cc441ce2904a249d14fd0f4bd0e3d7b69e06


All the requirements have been fulfilled. If there is any problems running the program contact me at anthonyka7@gmail.

