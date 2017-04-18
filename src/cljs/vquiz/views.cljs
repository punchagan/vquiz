(ns vquiz.views
  (:require [re-frame.core :as re-frame]
            [vquiz.db :as db]))

(defn initialize-quiz-ui []
  (re-frame/dispatch [:initialize-youtube])
  (re-frame/dispatch [:initialize-quiz]))

(defn start-button []
  [:button {:on-click initialize-quiz-ui}
   "Start Quiz!"])

(defn make-id [group value]
  "Return id given a group and value."
  (str group "-" value))

(defn radio-button [name value]
  ^{:key (make-id name value)}
  [:span
   [:input {:type "radio" :name name :value value}]
   [:span {:on-click (fn [e] (aset (-> e .-target .-previousSibling) "checked" true))} value]
   [:br]])

(defn verify-answer [e]
  (let [selected-answer (js/document.querySelector "input[name=answers]:checked")]
    (re-frame/dispatch [:verify-answer selected-answer])))

(defn resume-video []
  (re-frame/dispatch [:resume-video]))

(defn display [question answer-correct?]
  [:div
   [:p (:text question)]
   (let [correct-answer (db/correct-answer question)]
     (cond
       (nil? answer-correct?)
       (for [ans (:answers question)]
          (radio-button "answers" ans))
       (true? answer-correct?)
       [:p (str "Correct! The answer is " correct-answer)]
       (false? answer-correct?)
       [:p (str "Sorry! The correct answer is " correct-answer)]))

   (if (nil? answer-correct?)
     [:button {:on-click verify-answer} "Check Answer"]
     [:span
      [:button {:on-click resume-video} "Resume Video"]
      [:button "Next Question"]])])

(defn youtube-player-initialized? []
  (let [youtube-player (js/document.querySelector "#youtube-player")]
    (and youtube-player
         (= (.-tagName youtube-player) "IFRAME"))))

(defn intro-panel [intro-text]
  [:div
   [:p intro-text]
   [start-button]])

(defn main-panel []
  (let [quiz-title (re-frame/subscribe [:quiz-title])
        quiz-intro (re-frame/subscribe [:quiz-intro])
        quiz-started (re-frame/subscribe [:quiz-started])
        display-question (re-frame/subscribe [:display-question])
        answer-correct? (re-frame/subscribe [:answer-correct?])]
    (fn []
      [:div
       [:h1 @quiz-title]
       [:div {:id :youtube-player}]
       (if (not @quiz-started)
         [intro-panel @quiz-intro]
         ;; Youtube player gets initialized in an on-click method, but
         ;; developing with figwheel breaks this, and hence this hack
         (when (not (youtube-player-initialized?))
           (initialize-quiz-ui)))
       (when @display-question
         (display @display-question @answer-correct?))])))
