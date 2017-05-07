# Goal
Bring a live ClojureScript physics engine rig definition and simulation testbed to the browser.

# Dependencies
The current version of this project relies on other (awesome) projects:

- Ali Shakiba's [Planck.js](http://shakiba.me/planck.js), a JavaScript rewrite of Erin Catto's [Box2D](http://box2d.org/) physics engine.
- Mike Thompson's [re-frame](https://github.com/Day8/re-frame), a Reagent framework for writing SPAs.

Thanks to their authors for making this easier.

# Goal / Overview
- One or more rigs are defined with ClojureScript hash maps in a KLIPSE browser plugin.
- A physics engine driven simulation of those rigs is performed by the planck.js engine.
- Each rig component is rendered reactively with Reagent.
- Application flow is managed by re-frame.

# Getting Started
Clone this project, open a terminal on the project root, run the ```boot dev``` command and point your browser to ```localhost:4000```.

If the boot command fails because that port is already taken, edit ```build.boot```, re-assigning a different (and free) port number on the ```serve {:port 4000}``` line.

In the browser, you should see a simulation of the default test rig defined and assembled in ```src/cljs/rfp/app/cljs``` . Modify that rig by, say, commenting out some of the ```_ (pl/assemble-in! ...``` lines or modifying some key values. You're on your way.

# Next Actions
This project is in flux. More to come, shortly.

# License
For now, use at will and at own risk.

# Contributing
Feel free to submit comments, suggestions, issues, etc.
