(defproject todo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :plugins [[lein-cljsbuild "0.1.3"]]
  :dependencies [[org.clojure/clojure "1.3.0"]
                [crate "0.1.0-SNAPSHOT"]]
  :cljsbuild {
    :builds [{
        :source-path "src"
        :compiler {
          :output-to "js/main.js"
          ;:optimizations :advanced
          :optimizations :simple
          :pretty-print true}}]})