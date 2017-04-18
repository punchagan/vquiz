(ns vquiz.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [vquiz.events]
              [vquiz.subs]
              [vquiz.views :as views]
              [vquiz.config :as config]
              [youtube-fx.core]
              [day8.re-frame.http-fx]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db "json/sample-db.json"])
  (dev-setup)
  (mount-root))
