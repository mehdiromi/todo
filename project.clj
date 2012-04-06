(defproject todo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :plugins [[lein-cljsbuild "0.1.3"]]
  :dependencies [
                [org.clojure/clojure "1.3.0"]
                [crate "0.1.0-SNAPSHOT"]
                [jayq "0.1.0-alpha2"]
                ]
  :cljsbuild {
    :builds [{
        :source-path "src"
        :compiler {
          :output-to "js/main.js"
          ;:externs ["js/jq.mobi.min.js"]
          ;:externs ["js/jquery.mobile.min.js"]
          ;:optimizations :advanced
          :optimizations :simple
          :pretty-print true}}]})