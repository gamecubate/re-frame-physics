# Goal
This project aims to bring a live, ClojureScript driven, physics engine rig definition and testbed to the browser.

- One or more rigs would be defined in a browser pane with ClojureScript hash maps.
- Another pane would display a physics engine driven simulation of those rigs.

# Dependencies
This project relies on two other (awesome) projects:
- the [planck.js](https://github.com/shakiba/planck.js/) physics engine
- the [re-frame](https://github.com/Day8/re-frame) framework.
Thanks to their authors for making this possible.

# Getting Started

Clone this project, open a terminal on the project root, run the ```boot dev``` commmand and point your browser to ```localhost:4000```.

If the boot command fails because that port is already taken, edit ```build.boot```, re-assigning a different (and free) port number on the ```serve {:port 4000}``` line.

In the browser, you should see a simulation of the default test rig defined and assembled in ```src/cljs/rfp/app/cljs``` . Modify that rig by, say, commenting out some of the ```_ (pl/assemble-in! ...``` lines or modifying some key values. You're on your way.

# Next Actions
This project is in flux. More to come, shortly.

# License
For now, use at will and at own risk.

# Contributing
Feel free to submit comments, suggestions, issues, etc.
