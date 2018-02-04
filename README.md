# chatthingie

> It is, duh, a chat thingie

Build using kotlin and spring boot in the backend and es6 and vue in the the front

## Needed tools
Have some editor with kotlin support (intellij/eclipse) and a decent javascript editor with some plugins like linter support (visual code)

Backend is build with maven (`mvn`) and the front with yarn (`yarn`)

## Build
There are two type of builds dev and prod.
First start by installing all javascript dependencies (aka downloading half the internet) by runnin `yarn`

### Dev build
Run the `DevProxyMode` main class and then run `yarn run dev`. All connections (http/ws) will now be proxied through webpack. So you get the fun of hot reload and all that, but still use the proper api calls in the back. The frontend server will run on port `8080` and the backend on port `1234`

### Prod build
First build the frontend `yarn run build`. This will produce a minified and all that file and other needed files (css, index.html,...) in the resources directory so that the spring boot app can server it. To build an executable jar run `mvn clean install`. Tada you have a jar you can deploy

## Tests
As good as none, and i am pretty sure the js one doesn't work. Feel free to mess with webpack to get it to work :D.

## Deploy
Run the jar and server ll boot. For added safety, please run over https/wss.
