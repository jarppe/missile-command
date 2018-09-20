(defproject jarppe/missile-command "0.0.0-SNAPSHOT"
  :description "ClojureScript implementation of Missile Command (https://en.wikipedia.org/wiki/Missile_Command)"
  :license {:name "Eclipse Public License", :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [org.clojure/core.async "0.4.474"]]

  :plugins [[lein-figwheel "0.5.16"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]
  :resource-paths ["resources"]

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src"]
                        :figwheel     {:websocket-host :js-client-host}
                        :compiler     {:main                 missile-command.main
                                       :asset-path           "js"
                                       :output-to            "target/dev/resources/public/app.js"
                                       :output-dir           "target/dev/resources/public/js"
                                       :source-map-timestamp true
                                       :preloads             [devtools.preload]}}
                       {:id           "prod"
                        :source-paths ["src"]
                        :compiler     {:output-to     "docs/app.js"
                                       :main          missile-command.main
                                       :optimizations :advanced
                                       :pretty-print  false}}]}

  :figwheel {:css-dirs ["resources/public"]
             :repl false}
  
  :profiles {:dev {:dependencies  [[binaryage/devtools "0.9.10"]]
                   :source-paths  ["src"]
                   :resource-paths ["target/dev/resources"]
                   :clean-targets ^{:protect false} ["target/dev/resources/public" :target-path]}})
