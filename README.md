# Roam Examples

[**Roam**](https://github.com/Echtzeitsysteme/Roam) is an open-source framework for **R**ule-based **o**ptim**a**l **m**apping.
This repository holds some Roam example projects.


## Setup

* Install [Roam](https://github.com/Echtzeitsysteme/Roam) as described in its [repository](https://github.com/Echtzeitsysteme/Roam).
* Launch a runtime workspace (while using a runtime Eclipse) as stated in the eMoflon::IBeX installation steps. (Please refer to the installation steps of Roam above.)
* Clone this Git repository to your local machine and import it into Eclipse: *File -> Import -> General -> Existing Projects into Workspace*. Import all projects.
* Build all your projects with the black eMoflon hammer. Sometimes, it is required to trigger a cleaning in Eclipse (*Project -> Clean... -> Clean all projects*).
* You can now launch a Roam project like `org.emoflon.roam.roamslang.examples.mdvne`:
    * Go to `org.emoflon.roam.roamslang.examples.mdvne` -> `src-gen` -> `org.emoflon.roam.roamslang.examples.mdvne.api.roam` in the project explorer.
    * Launch `MdvneLauncher.launch` with a right click -> _Run As_ -> _MdvneLauncher_.


## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE.md](LICENSE.md) file for more details.
