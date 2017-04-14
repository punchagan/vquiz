(ns vquiz.views
  (:require [re-frame.core :as re-frame]))

(defn video-panel []
  (let [videos @(re-frame/subscribe [:videos])]
    (when (not-empty videos)
      [:iframe {:src (first videos)}])))

(defn main-panel []
  (let [quiz-title @(re-frame/subscribe [:quiz-title])]
    (fn []
      [:div
       [:h1 quiz-title]
       [video-panel]])))
