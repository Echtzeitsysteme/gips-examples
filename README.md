# GIPS Examples

[**GIPS**](https://github.com/Echtzeitsysteme/gips) is an open-source framework for **G**raph-Based **I**LP **P**roblem **S**pecification.
This repository holds some GIPS example projects.


## Setup

* Install [GIPS](https://github.com/Echtzeitsysteme/gips) as described in its [repository](https://github.com/Echtzeitsysteme/gips).
* Launch a runtime workspace (while using a runtime Eclipse) as stated in the eMoflon::IBeX installation steps. (Please refer to the installation steps of GIPS above.)
* Clone this Git repository to your local machine and import it into Eclipse: *File -> Import -> General -> Existing Projects into Workspace*. Import all projects.
* Build all your projects with the black eMoflon hammer. Sometimes, it is required to trigger a cleaning in Eclipse (*Project -> Clean... -> Clean all projects*).
* You can now launch a GIPS project like `org.emoflon.gips.gipsl.examples.mdvne`:
    * Go to `org.emoflon.gips.gipsl.examples.mdvne` -> `src-gen` -> `org.emoflon.gips.gipsl.examples.mdvne.api.gips` in the project explorer.
    * Launch `MdvneLauncher.launch` with a right click -> _Run As_ -> _MdvneLauncher_.


## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE.md](LICENSE.md) file for more details.
