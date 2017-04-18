(ns vquiz.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [vquiz.events]
              [vquiz.subs]
              [vquiz.views :as views]
              [vquiz.config :as config]
              [youtube-fx.core]
              [day8.re-frame.http-fx]
              [clojure.string :refer [split]]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn parse-params
  "Parse URL parameters into a hashmap"
  []
  (let [param-strs (-> (.-location js/window) .-href (split #"\?") last (split #"\&"))]
    (into {} (for [[k v] (map #(split % #"=") param-strs)]
               [(keyword k) v]))))

(defn db-url []
  (let [default-url "json/sample-db.json"
        params (parse-params)]
    (:url params default-url)))

(defn ^:export init []
  (dev-setup)
  (re-frame/dispatch-sync [:initialize-db (db-url)])
  (mount-root))
