# GIPS Examples

[**GIPS**](https://github.com/Echtzeitsysteme/gips) is an open-source framework for **G**raph-Based **I**LP **P**roblem **S**pecification.
This repository holds some GIPS example projects.


## Setup

* Install [GIPS](https://github.com/Echtzeitsysteme/gips) as described in its [repository](https://github.com/Echtzeitsysteme/gips).
* Launch a runtime workspace (while using a runtime Eclipse) as stated in the eMoflon::IBeX installation steps. (Please refer to the installation steps of GIPS above.)
* Clone this Git repository to your local machine and import it into Eclipse: *File -> Import -> General -> Existing Projects into Workspace*. Import all projects.
    * As an alternative, you can use this [PSF file](https://raw.githubusercontent.com/Echtzeitsysteme/gips-examples/main/projectSet.psf) for the import.
* Build all your projects with the black eMoflon hammer. Sometimes, it is required to trigger a cleaning in Eclipse (*Project -> Clean... -> Clean all projects*).
* You can now launch a GIPS project like `org.emoflon.gips.gipsl.examples.mdvne`:
    * Go to `org.emoflon.gips.gipsl.examples.mdvne` -> `src-gen` -> `org.emoflon.gips.gipsl.examples.mdvne.api.gips` in the project explorer.
    * Launch `MdvneLauncher.launch` with a right click -> _Run As_ -> _MdvneLauncher_.
    * Please keep in mind that not every project contains a `.launch` file.


## Example Overview

| **Name**                                           | **Description**                                                                         |
| -------------------------------------------------- | --------------------------------------------------------------------------------------- |
| `org.emoflon.gips.gipsl.examples.headlessrunner.*` | Example on how to build and use a GIPS project headlessly - export it to JAR and run it |
| `org.emoflon.gips.gipsl.examples.helloworld`       | Very simple hello world example for the GIPS language - can not be executed             | 
| `org.emoflon.gips.gipsl.examples.mdvne.*`          | Model-driven Virtual Network Embedding example with and without migration functionality |
| `network.model`                                    | Network (meta)model with converters and a manipulator for the MdVNE example             |
| `org.emoflon.gips.gipsl.examples.sdr.*`            | Software-Defined Radio task scheduling on CPUs                                          |
| `PersonTaskAssignments`/`PTA*`                     | Person-to-Task Assignments for construction purposes                                    |

For more projects, refer to the [GIPS test repository](https://github.com/Echtzeitsysteme/gips-tests).


## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for more details.
