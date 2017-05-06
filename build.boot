(set-env!
 :source-paths    #{"src/cljs"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs          "2.0.0"      :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.3"      :scope "test"]
                 [adzerk/boot-reload        "0.5.1"      :scope "test"]
                 [pandeiro/boot-http        "0.7.6"      :scope "test"]
                 [com.cemerick/piggieback   "0.2.1"      :scope "test"]
                 [org.clojure/tools.nrepl   "0.2.12"     :scope "test"]
                 [weasel                    "0.7.0"      :scope "test"]
                 [org.clojure/clojurescript "1.9.521"]
                 [reagent "0.6.1"]
                 [re-frame "0.9.2"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]
                 [binaryage/devtools "0.9.4" :scope "test"]
                 [powerlaces/boot-cljs-devtools "0.2.0" :scope "test"]])
(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[powerlaces.boot-cljs-devtools :refer [cljs-devtools dirac]])

(deftask build []
  (comp (speak)
        (cljs)))

(deftask run []
  (comp (serve)
        (watch)
        (cljs-repl)
        (cljs-devtools)
        (reload)
        (build)))

(deftask dev
  "Simple alias to run application in development mode"
  []
  (task-options! cljs {:optimizations :none :source-map true}
                 serve {:port 4000}
                 reload {:on-jsload 'rfp.app/run})
  (run))

(deftask release
  "Compile application for release"
  []
  (task-options! cljs {:optimizations :advanced :ids #{"js/app"}})
  (comp
    (build)
    (sift :include #{#"index.html" #"css/*" #"js/app.js"})
    (target :dir #{"release"})))
