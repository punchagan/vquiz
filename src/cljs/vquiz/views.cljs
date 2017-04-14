(ns vquiz.views
  (:require [re-frame.core :as re-frame]))

(defn start-button []
  [:button {:on-click
            (fn []
              (re-frame/dispatch [:initialize-youtube])
              (re-frame/dispatch [:initialize-quiz]))}
   "Start"])

(defn main-panel []
  (let [quiz-title (re-frame/subscribe [:quiz-title])
        quiz-intro (re-frame/subscribe [:quiz-intro])
        quiz-started (re-frame/subscribe [:quiz-started])]
    (fn []
      [:div
       [:h1 @quiz-title]
       [:div {:id :youtube-player}]
       (if (not @quiz-started)
         [:div
          [:p @quiz-intro]
          [start-button]]
         [:div
          [:p "Yay!!!!!"]])])))
