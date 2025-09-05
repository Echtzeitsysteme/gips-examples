# GIPS headless runner example

Steps to run the example (headlessly):
- Import both projects into a GIPS runtime workspace:
    - `org.emoflon.gips.gipsl.examples.headlessrunner`
    - `org.emoflon.gips.gipsl.examples.headlessrunnermodel`
- Build the projects and check if any problems occur on the console.
- Run the method `main(...)` of the class `org.emoflon.gips.gipsl.examples.headlessrunner.runner.HeadlessRunner.java`
    - Important: This is exptected to **fail**! Eclipse needs to know the run configuration in order to export it later.
- Export the JAR file
    - Click on *File* -> *Export*
    - Choose *Java* -> *Runnable JAR file*
    - Click *Next*
    - Choose the *Launch Configuration*: *HeadlessRunner - org.emoflon.gips.gipsl.examples.headlessrunner*
    - Choose the *Export destination* to be *gips-examples/org.emoflon.gips.gipsl.examples.headlessrunner/scripts*
    - *Library handling* must be set to *Package requzired libraries into generated JAR*
    - Click *Finish*
    - (Ignore the upcoming warnings regarding the JAR export; it contains some Java code warnings from the projects.)
- Run the JAR file
    - Navigate/Open a console to the folder *gips-examples/org.emoflon.gips.gipsl.examples.headlessrunner/scripts*
    - Adjust the data in `env.sh` to match your Gurobi installation
        - `export GRB_LICENSE_FILE`
        - `export GUROBI_HOME`
        - `export LD_LIBRARY_PATH`
    - Finally run the Jar file with `$ ./start.sh`
